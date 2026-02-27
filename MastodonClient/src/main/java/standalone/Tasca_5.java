package standalone;

import org.apache.hc.client5.http.fluent.Request;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ResourceBundle;

public class Tasca_5 {

    private static final String ACCOUNT_ID = "109862447110628983"; // ID del compte fib_asw

    public static void main(String[] args) {
        String TOKEN = ResourceBundle.getBundle("token").getString("token");

        try {
            // Obtenir els últims tuts del compte fib_asw
            String statusesURI = "https://mastodont.cat/api/v1/accounts/" + ACCOUNT_ID + "/statuses";

            String statusesResponse = Request.get(statusesURI)
                    .addHeader("Authorization", "Bearer " + TOKEN)
                    .execute()
                    .returnContent()
                    .asString();

            JSONArray statuses = new JSONArray(statusesResponse);

            if (statuses.length() > 0) {
                // Obtenir el primer (més recent) tut
                JSONObject latestStatus = statuses.getJSONObject(0);
                String statusId = latestStatus.getString("id");
                String statusText = latestStatus.getString("content");

                // Eliminar etiquetes HTML per a més llegibilitat
                String cleanedText = statusText.replaceAll("<[^>]*>", "");

                System.out.println("=== Darrer tut de fib_asw ===");
                System.out.println("Text: " + cleanedText);
                System.out.println("ID: " + statusId);
                System.out.println();

                // Fer reblog (boost) del tut
                String boostURI = "https://mastodont.cat/api/v1/statuses/" + statusId + "/reblog";

                String boostResponse = Request.post(boostURI)
                        .addHeader("Authorization", "Bearer " + TOKEN)
                        .execute()
                        .returnContent()
                        .asString();

                JSONObject boostResult = new JSONObject(boostResponse);
                boolean reblogged = boostResult.getBoolean("reblogged");

                if (reblogged) {
                    System.out.println("✓ Tut impulsat correctament!");
                } else {
                    System.out.println("El tut ja havia estat impulsat o hi ha hagut un problema.");
                }
            } else {
                System.out.println("No hi ha tuts disponibles per al compte fib_asw.");
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
