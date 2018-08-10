# MasterSlave
## JDBC 读写分离

JDBC 读写分离 ， 源码完全来自sharding-jdbc , 剥离出读写分离部分，剔除无关代码，只为研究
示例代码：
```JAVA

String sql = " select age from  t WHERE id = 4 ";

DataSource masterDataSource = null;/* 这里自行获取masterDataSource */
DataSource slave1DataSource = null;/* 这里自行获取slave1DataSource */
DataSource slave2DataSource = null;/* 这里自行获取slave2DataSource */
DataSource slave3DataSource = null;/* 这里自行获取slave3DataSource */

Map<String, DataSource> slaveDataSources = ImmutableMap.of(
        "slave1DataSource", slave1DataSource,
        "slave2DataSource", slave2DataSource,
        "slave3DataSource", slave3DataSource);

MasterSlaveRule masterSlaveRule =
        new MasterSlaveRule("masterSlaveDataSource", "masterDataSource", masterDataSource, slaveDataSources);


/* masterSlaveDataSource 会自动进行读写分离 */
DataSource masterSlaveDataSource = new MasterSlaveDataSource(masterSlaveRule);

/* 像普通的DataSource一样使用 */
Connection connection = masterSlaveDataSource.getConnection();

Statement statement = connection.createStatement();

ResultSet resultSet = statement.executeQuery(sql);
if (resultSet.next()) {
    int age = resultSet.getInt(1);
    System.out.println(age);
}


```


# 原理解析

## 一个正常的JDBC流程中核心代码如下

```java

DataSource dataSource = // 获取数据库连接池    (1)
// 获取数据库连接
Connection conn = dataSource.getConnection();  (2)
// 实例化Statement对象
Statement stmt = conn.createStatement();   (3)
String sql = "SELECT id, name, url FROM websites";

// 执行查询
ResultSet rs = stmt.executeQuery(sql);   (4)
// 或者执行更新
int rows = stmt.executeUpdate(sql);   (4)
// 展开结果集数据库
while(rs.next()){
   /* rs.getString rs.getInt ... */
}
// 完成后关闭
rs.close();
stmt.close();

```

一个最基础的事实就是 ==》 DAO框架 最底层 都是基于JDBC标准的 <br>
一个正常的读写分离流程中，在获取数据库连接的时候就已经确定好了走读库还是走写库，这正是云PMS中读写分离的实现原理
在Dao框架 （mybatis、ibatis、hibernate、spring jdbc）  中 <br> (1)步骤都是初始化配置阶段就已经完成，
一个Dao方法开始都是从步骤(2)开始的，要想在 getConnection 之前就知道使用哪个数据库
### 云PMS的解决方案(支持的操作更全面 如调用储存过程等)

云PMS的策略就是在方法名称上下功夫，
以 get find query select 等名字开头的方法走读库，使用切面编程，在方法执行前就把走读库还是写库的信息写在ThreadLocal中，
等到 Dao 框架 getConnection 的时候根据之前存的信息来判断具体给哪个数据库连接给Dao框架，这一步本来是可以在dao方法阶段做的，但是考虑到事物，
在同一个事物中要保持一直操作同一个数据库，所以将这一步前移到service中，由于依赖切面编程，所以种方法依赖spring aop,
同时所有的service方法执行之前都要被代理，性能会有一点影响

### 千里眼的解决方案(更通用)

Dao框架获取到连接之后, 在步骤 (4) 执行之前都不会真的去连接数据库，而是做一些设置操作，
判断走读库还是写库，最最根本的依据就是根据sql，
所以，一种新的方法就是再得到sql之前都不给dao 框架真正的连接,而是给一个实现了接口的包装类,核心类如下
<br>
`io.shardingjdbc.core.jdbc.core.connection.MasterSlaveConnection` 
<br>
`io.shardingjdbc.core.jdbc.core.datasource.MasterSlaveDataSource`
<br>
`io.shardingjdbc.core.jdbc.core.statement.MasterSlaveStatement`
<br>
`io.shardingjdbc.core.jdbc.core.statement.MasterSlavePreparedStatement`
<br>

同时记录下真正得到连接前，做的一些设置，等到真正得到连接的时候，在通过反射，把记录下的设置操作设置到真实的数据库连接上

对Dao框架而言，根本无法感知到这个连接不是真实的连接，还像往常一样操作即可，悄悄地就完成了读写分离
