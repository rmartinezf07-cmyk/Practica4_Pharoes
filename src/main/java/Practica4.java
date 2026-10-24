import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class Practica4 {

    static class RespuestaOpenMeteo {
        Daily daily;
    }

    static class Daily {
        List<String> time;
        @SerializedName("temperature_2m_max")
        List<Double> temperature2mMax;
    }

    public static void main(String[] args) throws Exception {
        RespuestaOpenMeteo respuesta = obtenerDatosMeteorologicos();
        List<String> dias = respuesta.daily.time;
        List<Double> temperaturas = respuesta.daily.temperature2mMax;

        List<Double> consumos = calcularConsumos(temperaturas);

        // 3. Escribir CSV
        escribirCSV(dias, temperaturas, consumos);
        System.out.println("¡Archivo datos.csv generado correctamente!");
    }

    static RespuestaOpenMeteo obtenerDatosMeteorologicos() throws Exception {
        String url = "https://api.open-meteo.com/v1/forecast"
                + "?latitude=40.4168&longitude=-3.7038"
                + "&daily=temperature_2m_max&timezone=auto&forecast_days=7";

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Gson gson = new Gson();
        return gson.fromJson(response.body(), RespuestaOpenMeteo.class);
    }

    static List<Double> calcularConsumos(List<Double> temperaturas) {
        List<Double> consumos = new ArrayList<>();
        for (double temp : temperaturas) {
            if (temp >= 25) {
                consumos.add((temp - 25) * 10);
            } else {
                consumos.add(0.0);
            }
        }
        return consumos;
    }

    // --- MÉTODO 3: GUARDAR CSV ---
    static void escribirCSV(List<String> dias, List<Double> temps, List<Double> consumos) throws Exception {
        StringBuilder sb = new StringBuilder("Dia,Temperatura_maxima,Consumo_kWh\n");
        for (int i = 0; i < dias.size(); i++) {
            sb.append(dias.get(i)).append(",")
                    .append(temps.get(i)).append(",")
                    .append(consumos.get(i)).append("\n");
        }
        Files.writeString(Path.of("datos.csv"), sb.toString());
    }
}