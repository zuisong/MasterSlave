package cn.mmooo;

import com.alibaba.druid.pool.DruidDataSource;
import com.google.common.collect.ImmutableMap;
import com.mysql.jdbc.Driver;
import io.shardingjdbc.core.constant.SQLType;
import io.shardingjdbc.core.jdbc.core.datasource.MasterSlaveDataSource;
import io.shardingjdbc.core.parsing.SQLJudgeEngine;
import io.shardingjdbc.core.rule.MasterSlaveRule;
import org.junit.Test;

import javax.sql.DataSource;
import java.sql.*;

public class TestSqlParser {
    @Test
    public void testSqlParse() {
        String sql = " UPDATE T SET ID = 2 WHERE ID = 4 ";
        SQLJudgeEngine judgeEngine = new SQLJudgeEngine(sql);

        SQLType type = judgeEngine.judge();

        System.out.println(type);
    }

    public static DataSource getDataSource(String url, String username, String password) {
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setUrl(url);
        dataSource.setDriverClassName(Driver.class.getName());
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        return dataSource;
    }

    @Test
    public void testMaterSlave() throws SQLException {
        String sql = "  SELECT count(*) FROM tuser";
        String sql2 = "UPDATE tuser  SET  loginName='chenjian' WHERE id = 142";

        DataSource masterDataSource = getDataSource("jdbc:mysql://192.168.10.203:3306/jte253test?useUnicode=true", "root", "xyz11111111");
        DataSource slave1DataSource = getDataSource("jdbc:mysql://192.168.10.203:3306/jte253?useUnicode=true", "root", "xyz11111111");/* 这里自行获取slave1DataSource */
        /* 这里自行获取slave3DataSource */

        ImmutableMap<String, DataSource> slaveDataSources = ImmutableMap.of(
                "slave1DataSource", slave1DataSource
        );
        MasterSlaveRule masterSlaveRule =
                new MasterSlaveRule("masterSlaveDataSource", "masterDataSource", masterDataSource, slaveDataSources);


        /* masterSlaveDataSource 会自动进行读写分离 */
        DataSource masterSlaveDataSource = new MasterSlaveDataSource(masterSlaveRule);

        /* 像普通的DataSource一样使用 */
        Connection connection = masterSlaveDataSource.getConnection();

        Statement statement = connection.createStatement();


        int update = statement.executeUpdate(sql2);
        System.out.println(update);
        ResultSet resultSet = statement.executeQuery(sql);
        if (resultSet.next()) {
            int age = resultSet.getInt(1);
            System.out.println(age);
        }
    }
}
