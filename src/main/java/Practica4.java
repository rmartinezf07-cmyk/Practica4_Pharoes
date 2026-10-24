import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
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
        // 1. Obtener datos
        RespuestaOpenMeteo respuesta = obtenerDatosMeteorologicos();
        List<Double> temperaturas = respuesta.daily.temperature2mMax;

        // 2. Calcular consumos
        List<Double> consumos = calcularConsumos(temperaturas);
        System.out.println("Temperaturas: " + temperaturas);
        System.out.println("Consumos calculados: " + consumos);
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

    // --- MÉTODO 2: CÁLCULO DEL CONSUMO ---
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
}