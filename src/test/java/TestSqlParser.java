import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.util.DruidDataSourceUtils;
import io.shardingjdbc.core.constant.SQLType;
import io.shardingjdbc.core.parsing.SQLJudgeEngine;

public class TestSqlParser {
    public static void main(String[] args) {
        String sql = " UPDATE T SET ID = 2 WHERE ID = 4 ";
        SQLJudgeEngine judgeEngine = new SQLJudgeEngine(sql);

        SQLType type = judgeEngine.judge();

        System.out.println(type);
    }
}
