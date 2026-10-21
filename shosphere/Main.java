
import java.sql.Connection;
import java.sql.SQLException;

public class Main {
    public static void main(String[] args) {

        Connection con = null;

        try {
            con = DBConnection.getConnection();

            System.out.println(
                "ShopSphere Database Connected!"
            );

        } catch (SQLException e) {
            e.printStackTrace();

        } finally {
            try {
                if (con != null) {
                    con.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
