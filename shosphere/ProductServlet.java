
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/api/products")
public class ProductServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
                         throws IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try (PrintWriter out = response.getWriter();
             Connection con = DBConnection.getConnection();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(
                 "SELECT id, name, price, description FROM products")) {

            out.print("[");

            boolean first = true;

            while (rs.next()) {
                if (!first) {
                    out.print(",");
                }

                out.print("{");
                out.print("\"id\":" + rs.getInt("id") + ",");
                out.print("\"name\":\"" +
                    escape(rs.getString("name")) + "\",");
                out.print("\"price\":" + rs.getBigDecimal("price") + ",");
                out.print("\"description\":\"" +
                    escape(rs.getString("description")) + "\"");
                out.print("}");

                first = false;
            }

            out.print("]");

        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            try (PrintWriter out = response.getWriter()) {
                out.print("{\"error\":\"Unable to load products\"}");
            }
            e.printStackTrace();
        }
    }

    private String escape(String value) {
        if (value == null) return "";
        return value.replace("\\", "\\\\")
                    .replace("\"", "\\\"")
                    .replace("\n", "\\n")
                    .replace("\r", "\\r");
    }
}

