# MasterSlave
JDBC 读写分离 ， 源码完全来自sharding-jdbc , 剥离出读写分离部分，剔除无关代码，只为研究

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
        Connection connection = masterDataSource.getConnection();

        PreparedStatement preparedStatement = connection.prepareStatement(sql);

        ResultSet resultSet = preparedStatement.executeQuery();

        int age = resultSet.getInt("age");


```
