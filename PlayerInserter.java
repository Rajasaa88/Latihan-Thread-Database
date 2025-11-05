import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * [KONSEP EXTENDS & IMPLEMENTS]
 * 1. 'extends DatabaseTask'   : Mewarisi semua properti dan metode dari DatabaseTask.
 * 2. 'implements Runnable'   : Berjanji untuk menyediakan metode 'run()' agar bisa 
 * dieksekusi oleh Thread Pool.
 */
public class PlayerInserter extends DatabaseTask implements Runnable {

    // Properti
    private final String playerName;
    private final String position;
    private final int squadNumber;

    /**
     * Constructor untuk PlayerInserter
     */
    public PlayerInserter(String playerName, String position, int squadNumber, 
                          String dbUrl, String dbUser, String dbPass) {
        
        // 'super()' memanggil constructor dari parent class (DatabaseTask)
        // untuk meng-set dbUrl, dbUser, dan dbPass
        super(dbUrl, dbUser, dbPass); 
        
        // Set properti milik kelas ini sendiri
        this.playerName = playerName;
        this.position = position;
        this.squadNumber = squadNumber;
    }

    /**
     * Metode ini wajib ada karena kita 'implements Runnable'.
     * Logika ini yang akan dieksekusi di dalam thread.
     */
    @Override
    public void run() {
        String threadName = Thread.currentThread().getName();
        System.out.println(threadName + " memulai proses insert untuk: " + playerName);

        String sql = "INSERT INTO players (player_name, position, squad_number) VALUES (?, ?, ?)";

        // Kita bisa langsung memanggil 'getConnection()' karena kita mewarisinya (extends)
        // dari DatabaseTask.
        try (Connection conn = getConnection(); 
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // Set parameter
            pstmt.setString(1, playerName);
            pstmt.setString(2, position);
            pstmt.setInt(3, squadNumber);
            
            // Eksekusi
            pstmt.executeUpdate();

            System.out.println(threadName + " BERHASIL mendaftarkan: " + playerName + " (#" + squadNumber + ")");

        } catch (SQLException e) {
            System.err.println(threadName + " GAGAL mendaftarkan " + playerName + ": " + e.getMessage());
        }
    }
}