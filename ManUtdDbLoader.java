import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ManUtdDbLoader {

    // --- Bagian 1: Konfigurasi Database ---
    private static final String DB_URL = "jdbc:h2:mem:manutd_db;DB_CLOSE_DELAY=-1";
    private static final String DB_USER = "sa";
    private static final String DB_PASS = "";

    public static void main(String[] args) {
        
        try {
            // --- Bagian 2: Load Driver Database ---
            // Ini adalah "Langkah 1: Load driver JDBC".
            Class.forName("org.h2.Driver");

            // --- Bagian 3: Persiapan Database ---
            setupDatabase();

            // --- Bagian 4: Membuat Thread Pool ---
            // Ini adalah "NEW THREAD MANAGEMENT : EXECUTORSERVICE".
            int JUMLAH_THREAD = 4;
            ExecutorService executor = Executors.newFixedThreadPool(JUMLAH_THREAD);

            // --- Bagian 5: Menyiapkan Data ---
            Object[][] squadToInsert = {
                {"André Onana", "Goalkeeper", 24},
                {"Diogo Dalot", "Defender", 20},
                {"Lisandro Martínez", "Defender", 6},
                {"Casemiro", "Midfielder", 18},
                {"Bruno Fernandes", "Midfielder", 8},
                {"Rasmus Hojlund", "Forward", 11},
                {"Marcus Rashford", "Forward", 10},
                {"Alejandro Garnacho", "Forward", 17},
                {"Kobbie Mainoo", "Midfielder", 37}
            };

            System.out.println("Memulai " + squadToInsert.length + " tugas pendaftaran pemain...");
            
            // --- Bagian 6: Memberikan Tugas ke Thread Pool ---
            for (Object[] player : squadToInsert) {
                
                // 1. Buat sebuah 'tugas' (Runnable) untuk setiap pemain.
                Runnable task = new PlayerInserter(
                    (String) player[0], // name
                    (String) player[1], // position
                    (Integer) player[2], // number
                    DB_URL, DB_USER, DB_PASS // Info koneksi DB
                );
                
                // 2. Serahkan tugas itu ke thread pool.
                executor.submit(task);
            }

            // --- Bagian 7: Mematikan Thread Pool ---
            // Memberi tahu pool untuk tidak menerima tugas baru lagi.
            executor.shutdown();
            
            // Menunggu semua tugas yang sedang berjalan selesai, maks 60 detik.
            if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
                // Jika 60 detik lewat dan belum selesai, paksa berhenti.
                executor.shutdownNow();
            }

            System.out.println("Semua data pemain telah dimasukkan.");
            
            // --- Bagian 8: Verifikasi Hasil ---
            verifyData();

        } catch (SQLException | ClassNotFoundException e) {
            // SQLException terjadi jika ada error SQL (misal tabel salah)
            // ClassNotFoundException terjadi jika .jar H2 tidak ditemukan
            e.printStackTrace();
        } catch (InterruptedException e) {
            // Terjadi jika 'main thread' diinterupsi saat 'awaitTermination'.
            e.printStackTrace();
        }
    }
    
    /**
     * Metode helper untuk membuat koneksi dan menyiapkan tabel 'players'.
     */
    private static void setupDatabase() throws SQLException {
        // try-with-resources: conn dan stmt akan otomatis ditutup
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
             Statement stmt = conn.createStatement()) {
            
            stmt.execute("DROP TABLE IF EXISTS players"); // Hapus tabel lama jika ada
            
            // Buat tabel baru
            String createTableSql = "CREATE TABLE players (" +
                                    " id INT AUTO_INCREMENT PRIMARY KEY," +
                                    " player_name VARCHAR(100) NOT NULL," +
                                    " position VARCHAR(50)," +
                                    " squad_number INT" +
                                    ")";
            stmt.execute(createTableSql);
            System.out.println("Database dan tabel 'players' (skuad) berhasil dibuat.");
        }
    }
    
    /**
     * Metode helper untuk membaca dan mencetak semua data dari tabel 'players'.
     */
    private static void verifyData() {
        System.out.println("\n--- Hasil Verifikasi Data Skuad di Database ---");
        String sql = "SELECT id, player_name, position, squad_number FROM players ORDER BY squad_number";
        int count = 0;
        
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            // Loop untuk membaca setiap baris hasil.
            while (rs.next()) {
                System.out.printf("ID: %d, Nama: %s, Posisi: %s, No: %d\n",
                        rs.getInt("id"),
                        rs.getString("player_name"),
                        rs.getString("position"),
                        rs.getInt("squad_number"));
                count++;
            }
            System.out.println("---------------------------------------------");
            System.out.println("Total pemain yang terdaftar: " + count);
            
        } catch (SQLException e) {
            System.err.println("Verifikasi data skuad gagal: " + e.getMessage());
        }
    }
}