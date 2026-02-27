package servlet;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.hc.client5.http.fluent.Request;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ResourceBundle;

@WebServlet(value = "/")
public class Tasca_6 extends HttpServlet {

    private static final String BASE_URI = "https://mastodont.cat/api/v1";
    private static final String TOKEN = ResourceBundle.getBundle("token").getString("token");

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            // ID del compte fib_asw (hardcodejat)
            String accountId = "109862447110628983";

            // Obtenir el darrer tut (status) del compte fib_asw
            String statusUri = BASE_URI + "/accounts/" + accountId + "/followers";
            String output = Request.get(statusUri)
                    .addHeader("Authorization", "Bearer " + TOKEN)
                    .execute()
                    .returnContent()
                    .asString();


        } catch (Exception ex) {
            ex.printStackTrace();
        }

        response.setContentType ("text/html");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        out.println("""
        <!DOCTYPE html>
        <html lang='ca'>
        <head>
            <meta charset='UTF-8'>
            <title>Tasca 6</title>
        </head>
        <body>
        """);

        out.println("<h1>Encara no faig res!</h1>"); //TODO TasK#6

        out.println("""
        </body>
        </html>
        """);
    }
}
