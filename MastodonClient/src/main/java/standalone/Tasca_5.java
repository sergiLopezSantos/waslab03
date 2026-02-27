package standalone;

import org.apache.hc.client5.http.fluent.Request;
import org.apache.hc.core5.http.ContentType;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ResourceBundle;

public class Tasca_5 {

    private static final String BASE_URI = "https://mastodont.cat/api/v1";
    private static final String TOKEN = ResourceBundle.getBundle("token").getString("token");

    public static void main(String[] args) {

        try {
            // ID del compte fib_asw (hardcodejat)
            String accountId = "109862447110628983";

            // Obtenir el darrer tut (status) del compte fib_asw
            String statusUri = BASE_URI + "/accounts/" + accountId + "/statuses";
            String output = Request.get(statusUri)
                    .addHeader("Authorization", "Bearer " + TOKEN)
                    .execute()
                    .returnContent()
                    .asString();

            // Processar la resposta per obtenir el darrer tut
            JSONArray statuses = new JSONArray(output);
            if (statuses.length() > 0) {
                JSONObject lastStatus = statuses.getJSONObject(0); // El darrer tut
                String statusText = lastStatus.getString("content"); // El text del tut
                String statusId = lastStatus.getString("id"); // ID del tut

                // Imprimir el text del darrer tut
                System.out.println("Darrer tut publicat:\n" + statusText);

                // Impulsar (boost) el tut
                String boostUri = BASE_URI + "/statuses/" + statusId + "/reblog";
                String boostResponse = Request.post(boostUri)
                        .addHeader("Authorization", "Bearer " + TOKEN)
                        .execute()
                        .returnContent()
                        .asString();

                // Confirmar que el tut ha estat impulsat
                JSONObject boostResult = new JSONObject(boostResponse);
                System.out.println("\nTut impulsat amb èxit! ID del tut impulsat: " + boostResult.getString("id"));
            } else {
                System.out.println("No hi ha tuts disponibles per mostrar.");
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
