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

        // 4. Generar Dashboard HTML
        generarDashboardDesdeCSV();

        System.out.println("¡Listo! Se han generado datos.csv y dashboard.html correctamente.");
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

    // --- MÉTODO 4: LEER CSV Y GENERAR EL HTML ---
    static void generarDashboardDesdeCSV() throws Exception {
        List<String> lineas = Files.readAllLines(Path.of("datos.csv"));

        List<String> dias = new ArrayList<>();
        List<String> temps = new ArrayList<>();
        List<String> consumos = new ArrayList<>();

        // Saltar la cabecera (i = 1)
        for (int i = 1; i < lineas.size(); i++) {
            String[] partes = lineas.get(i).split(",");
            dias.add("'" + partes[0] + "'"); // comillas simples para JS
            temps.add(partes[1]);
            consumos.add(partes[2]);
        }

        String diasJS = String.join(", ", dias);
        String tempsJS = String.join(", ", temps);
        String consumosJS = String.join(", ", consumos);

        // Plantilla HTML con los gráficos de ECharts
        String html = """
                <!DOCTYPE html>
                <html lang="es">
                <head>
                    <meta charset="UTF-8">
                    <title>Dashboard Consumo Refrigeración</title>
                    <script src="https://cdn.jsdelivr.net/npm/echarts@5/dist/echarts.min.js"></script>
                </head>
                <body>
                    <h2 style="text-align:center">Temperatura máxima diaria</h2>
                    <div id="grafico-temperatura" style="width:100%%; height:400px;"></div>

                    <h2 style="text-align:center">Consumo estimado de refrigeración</h2>
                    <div id="grafico-consumo" style="width:100%%; height:400px;"></div>

                    <script>
                        var dias = [%s];
                        var temperaturas = [%s];
                        var consumos = [%s];

                        var chartTemp = echarts.init(document.getElementById('grafico-temperatura'));
                        chartTemp.setOption({
                            title: { text: 'Temperatura máxima (°C)' },
                            tooltip: {},
                            xAxis: { type: 'category', data: dias, name: 'Día' },
                            yAxis: { type: 'value', name: '°C' },
                            series: [{ type: 'line', data: temperaturas, name: 'Temp. máx' }]
                        });

                        var chartConsumo = echarts.init(document.getElementById('grafico-consumo'));
                        chartConsumo.setOption({
                            title: { text: 'Consumo de refrigeración (kWh)' },
                            tooltip: {},
                            xAxis: { type: 'category', data: dias, name: 'Día' },
                            yAxis: { type: 'value', name: 'kWh' },
                            series: [{ type: 'bar', data: consumos, name: 'Consumo' }]
                        });
                    </script>
                </body>
                </html>
                """.formatted(diasJS, tempsJS, consumosJS);

        Files.writeString(Path.of("dashboard.html"), html);
    }
}