
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class ProductTest {
    public static void main(String[] args) {

        String sql = "SELECT * FROM products";

        try (
            Connection con = DBConnection.getConnection();
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(sql)
        ) {
            System.out.println("=== ShopSphere Products ===");

            while (rs.next()) {
                System.out.println(
                    rs.getInt("id") + ". " +
                    rs.getString("name") + " - Rs. " +
                    rs.getDouble("price")
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

