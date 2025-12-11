import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DBUtil {
    private static final String DB_URL = "jdbc:sqlite:complaints.db";

    static {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            System.err.println("FATAL: SQLite JDBC driver not found. Please ensure sqlite-jdbc-....jar is in the classpath.");
            e.printStackTrace();
        }
        try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {
                String sql = "CREATE TABLE IF NOT EXISTS complaints (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "name TEXT NOT NULL," +
                    "contact TEXT NOT NULL," +
                    "issue TEXT NOT NULL," +
                    "status TEXT NOT NULL DEFAULT 'Pending'" +
                    ");";
            stmt.execute(sql);
        } catch (SQLException e) {
            System.err.println("Error initializing the database table.");
            e.printStackTrace();
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }
}
