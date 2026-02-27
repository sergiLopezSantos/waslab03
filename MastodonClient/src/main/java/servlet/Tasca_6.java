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
        StringBuilder htmlContent = new StringBuilder();

        try {
            // ID del compte fib_asw (hardcodejat)
            String accountId = "109862447110628983";

            // Obtenir els seguidors del compte fib_asw
            String followersURI = BASE_URI + "/accounts/" + accountId + "/followers";
            String output = Request.get(followersURI)
                    .addHeader("Authorization", "Bearer " + TOKEN)
                    .execute()
                    .returnContent()
                    .asString();

            // Processar la resposta per mostrar els seguidors
            JSONArray followers = new JSONArray(output);

            htmlContent.append("<h1>Seguidors de fib_asw</h1>\n");
            htmlContent.append("<div class='followers-container'>\n");

            if (followers.length() > 0) {
                for (int i = 0; i < followers.length(); i++) {
                    JSONObject follower = followers.getJSONObject(i);

                    // Obtenir dades del seguidor
                    String avatar = follower.optString("avatar", "");
                    String displayName = follower.optString("display_name", "");
                    String username = follower.optString("username", "");
                    String acct = follower.optString("acct", ""); // Username complet amb domini

                    htmlContent.append("<div class='follower-card'>\n");

                    // Mostrar avatar si està disponible
                    if (!avatar.isEmpty()) {
                        htmlContent.append("  <div class='avatar'>\n");
                        htmlContent.append("    <img src='").append(avatar).append("' alt='Avatar de ").append(username).append("' />\n");
                        htmlContent.append("  </div>\n");
                    }

                    // Mostrar nom complet (display name)
                    if (!displayName.isEmpty()) {
                        htmlContent.append("  <div class='display-name'>").append(escapeHtml(displayName)).append("</div>\n");
                    }

                    // Mostrar username complet (amb domini si no és @mastodont.cat)
                    htmlContent.append("  <div class='username'>@").append(escapeHtml(acct)).append("</div>\n");

                    htmlContent.append("</div>\n");
                }
            } else {
                htmlContent.append("<p>No hi ha seguidors disponibles per mostrar.</p>\n");
            }

            htmlContent.append("</div>\n");

        } catch (Exception ex) {
            ex.printStackTrace();
            htmlContent.append("<p style='color: red;'>Error en carregar els seguidors: ").append(ex.getMessage()).append("</p>\n");
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

        out.println(htmlContent.toString());

        out.println("""
        </body>
        </html>
        """);
    }

    private String escapeHtml(String text) {
        if (text == null) return "";
        return text.replace("&", "&amp;")
                   .replace("<", "&lt;")
                   .replace(">", "&gt;")
                   .replace("\"", "&quot;")
                   .replace("'", "&#39;");
    }
}
