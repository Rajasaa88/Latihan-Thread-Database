import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * [KONSEP EXTENDS]
 * Ini adalah 'abstract class' yang akan kita 'extends'.
 * Tujuannya adalah untuk menyediakan fungsionalitas dasar database 
 * (info koneksi dan cara mendapatkan koneksi) ke semua kelas turunannya.
 */
public abstract class DatabaseTask {
    
    // Properti ini akan diwarisi oleh semua child class
    protected final String dbUrl;
    protected final String dbUser;
    protected final String dbPass;

    /**
     * Constructor yang akan dipanggil oleh child class menggunakan 'super()'.
     */
    public DatabaseTask(String dbUrl, String dbUser, String dbPass) {
        this.dbUrl = dbUrl;
        this.dbUser = dbUser;
        this.dbPass = dbPass;
    }

    /**
     * Metode 'utility' yang bisa digunakan oleh child class.
     * Setiap thread akan mendapatkan koneksinya sendiri.
     */
    protected Connection getConnection() throws SQLException {
        return DriverManager.getConnection(dbUrl, dbUser, dbPass);
    }
}