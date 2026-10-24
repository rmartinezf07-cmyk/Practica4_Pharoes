# Prueba 4: Estimación del consumo de refrigeración y dashboard

## Descripción

Aplicación desarrollada en Java que estima el consumo diario de refrigeración a partir de la temperatura máxima prevista para los próximos siete días en Madrid. Los resultados se almacenan en un archivo CSV y se visualizan en un dashboard web generado automáticamente con Apache ECharts.

## Metodología

El programa ejecuta de forma secuencial las siguientes fases:

1. **Obtención de datos.** Consulta a la API pública Open-Meteo para recuperar la temperatura máxima diaria de los próximos siete días.
2. **Cálculo del consumo.** Para cada día, si la temperatura máxima (T) es igual o superior a 25 °C, el consumo es (T − 25) × 10 kWh; en caso contrario, es 0 kWh.
3. **Almacenamiento.** Los resultados se guardan en `datos.csv` con las columnas *Día*, *Temperatura máxima (°C)* y *Consumo de refrigeración (kWh)*.
4. **Visualización.** El programa lee `datos.csv` y genera `dashboard.html` a partir de una plantilla, con dos gráficas que comparten el eje temporal: temperatura máxima (superior) y consumo estimado (inferior).

Dado que el HTML se genera a partir del CSV, cualquier cambio en los datos se refleja volviendo a ejecutar el programa, sin editar el HTML manualmente.

## Tecnologías

- **Java**: lógica de la aplicación.
- **Maven**: gestión de dependencias.
- **Gson**: deserialización de la respuesta JSON de la API.
- **Apache ECharts**: representación gráfica de los datos.

## Ejecución

1. Abrir el proyecto en un IDE compatible con Maven (por ejemplo, IntelliJ IDEA).
2. Esperar a que se descarguen las dependencias.
3. Ejecutar el método `main` de la clase `Practica4`.
4. Abrir el archivo `dashboard.html`, generado en la raíz del proyecto, en un navegador.

## Resultados

| Archivo          | Contenido                                              |
|------------------|--------------------------------------------------------|
| `datos.csv`      | Temperatura máxima y consumo estimado por día          |
| `dashboard.html` | Dashboard con las gráficas de temperatura y consumo    |

## Autor

Rafael Martínez Fernández · [GitHub]https://github.com/rmartinezf07-cmyk
