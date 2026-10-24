import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

public class Practica4 {

    // Clases para mapear el JSON de Open-Meteo
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
        System.out.println("Días obtenidos: " + respuesta.daily.time);
        System.out.println("Temperaturas: " + respuesta.daily.temperature2mMax);
    }

    // --- MÉTODO 1: LLAMADA A LA API ---
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
}
