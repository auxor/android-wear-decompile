package javax.sql;

import java.io.PrintWriter;
import java.sql.SQLException;

public interface CommonDataSource {
    PrintWriter getLogWriter() throws SQLException;

    int getLoginTimeout() throws SQLException;

    void setLogWriter(PrintWriter printWriter) throws SQLException;

    void setLoginTimeout(int i) throws SQLException;
}
