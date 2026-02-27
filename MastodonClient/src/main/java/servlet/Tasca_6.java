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
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
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

            // Generar timestamp
            SimpleDateFormat sdf = new SimpleDateFormat("EEEE, dd MMMM 'de' yyyy 'a les' HH:mm:ss", Locale.forLanguageTag("ca"));
            String timestamp = sdf.format(new Date());

            // Generar header
            htmlContent.append("<div class='header'>\n");
            htmlContent.append("  <h1>Seguidors del compte fib_asw</h1>\n");
            htmlContent.append("  <p>").append(timestamp).append("</p>\n");
            htmlContent.append("</div>\n");

            if (followers.length() > 0) {
                for (int i = 0; i < followers.length(); i++) {
                    JSONObject follower = followers.getJSONObject(i);

                    // Obtenir dades del seguidor
                    String avatar = follower.optString("avatar", "");
                    String displayName = follower.optString("display_name", "");
                    String acct = follower.optString("acct", ""); // Username complet amb domini
                    String username = follower.optString("username", "");
                    int followersCount = follower.optInt("followers_count", 0);
                    String followerId = follower.optString("id", "");

                    htmlContent.append("<div class='account'>\n");

                    // Avatar i informació del seguidor
                    htmlContent.append("  <h2>");
                    if (!avatar.isEmpty()) {
                        htmlContent.append("<img src='").append(avatar)
                                .append("' alt='Avatar' class='account-avatar'> ");
                    }
                    htmlContent.append(escapeHtml(displayName)).append(" (@").append(escapeHtml(acct)).append(")</h2>\n");
                    htmlContent.append("  <p>Nombre de seguidors: ").append(followersCount).append("</p>\n");

                    // Obtenir els cinc tuts més recents del seguidor
                    try {
                        String statusesURI = BASE_URI + "/accounts/" + followerId + "/statuses?limit=5";
                        String statusesResponse = Request.get(statusesURI)
                                .addHeader("Authorization", "Bearer " + TOKEN)
                                .execute()
                                .returnContent()
                                .asString();

                        JSONArray statuses = new JSONArray(statusesResponse);

                        if (statuses.length() > 0) {
                            htmlContent.append("  <div class='tuts'>\n");
                            for (int j = 0; j < statuses.length(); j++) {
                                JSONObject status = statuses.getJSONObject(j);
                                String content = status.optString("content", "");
                                String createdAt = status.optString("created_at", "");

                                // Formatar la data
                                String formattedDate = formatDate(createdAt);

                                htmlContent.append("    <div class='tut'>\n");
                                htmlContent.append("      <p class='timestamp'>").append(formattedDate).append("</p>\n");
                                htmlContent.append("      <div class='content'>").append(content).append("</div>\n");
                                htmlContent.append("    </div>\n");
                            }
                            htmlContent.append("  </div>\n");
                        }
                    } catch (Exception ex) {
                        // Si no es poden obtenir els tuts, continuar amb el següent seguidor
                        System.err.println("Error obtenint tuts per a " + acct + ": " + ex.getMessage());
                    }

                    htmlContent.append("</div>\n");
                }
            } else {
                htmlContent.append("<p>No hi ha seguidors disponibles per mostrar.</p>\n");
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            htmlContent.append("<p style='color: var(--danger);'>Error en carregar els seguidors: ").append(escapeHtml(ex.getMessage())).append("</p>\n");
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
            <link href="waslab03-t6.css" rel="stylesheet" type="text/css">
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

    private String formatDate(String isoDate) {
        try {
            // L'API retorna dates en format ISO 8601: "2025-09-26T10:36:25.000Z"
            java.text.SimpleDateFormat isoFormat = new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            isoFormat.setTimeZone(java.util.TimeZone.getTimeZone("UTC"));
            java.util.Date date = isoFormat.parse(isoDate.substring(0, 19));

            // Formatar a "yyyy-MM-dd HH:mm:ss"
            java.text.SimpleDateFormat outputFormat = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            return outputFormat.format(date);
        } catch (Exception e) {
            return isoDate; // Si falla la conversió, retornar la data original
        }
    }
}
