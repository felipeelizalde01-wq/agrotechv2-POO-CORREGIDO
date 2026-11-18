package negocio;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import modelo.Lectura;
import modelo.Sensor;
import modelo.Terreno;

public class CsvDatabase {

    private String filePath;

    public CsvDatabase(String filePath) {
        this.filePath = filePath;
        ensureFileExists();
    }

    public void cargarDatosEnTerreno(Terreno terreno) {
        File file = new File(this.filePath);
        if (!file.exists()) {
            return;
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line = reader.readLine(); // Saltar cabecera
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;

                String[] parts = line.split(",");
                if (parts.length >= 4) {
                    try {
                        String id = parts[0].trim();
                        String fecha = parts[1].trim();
                        double humedad = Double.parseDouble(parts[2].trim());
                        String idSensor = parts[3].trim();

                        Sensor sensor = terreno.buscarSensor(idSensor);
                        if (sensor == null) {
                            sensor = new Sensor(idSensor, "Parcela-" + idSensor, 2.8, 1.2);
                            terreno.agregarSensor(sensor);
                        }
                        sensor.registrarLectura(new Lectura(id, fecha, humedad));
                    } catch (NumberFormatException e) {
                        // Ignorar fila corrupta
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Error al leer CSV: " + e.getMessage());
        }
    }

    public boolean guardarTerrenoEnCsv(Terreno terreno) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(this.filePath, false))) {
            writer.println("id,date,humidityLevel,sensorId");
            Sensor[] sensores = terreno.getSensores();
            for (int i = 0; i < sensores.length; i++) {
                Lectura[] lecturas = sensores[i].getLecturas();
                for (int j = 0; j < lecturas.length; j++) {
                    writer.println(lecturas[j].toCsvLine(sensores[i].getIdSensor()));
                }
            }
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public String generarSiguienteId(Terreno terreno) {
        int max = 0;
        Sensor[] sensores = terreno.getSensores();
        for (int i = 0; i < sensores.length; i++) {
            Lectura[] lecturas = sensores[i].getLecturas();
            for (int j = 0; j < lecturas.length; j++) {
                String id = lecturas[j].getId();
                if (id.startsWith("REC-")) {
                    try {
                        int num = Integer.parseInt(id.substring(4));
                        if (num > max) max = num;
                    } catch (NumberFormatException e) {}
                }
            }
        }
        return String.format("REC-%03d", max + 1);
    }

    private void ensureFileExists() {
        File file = new File(this.filePath);
        File parent = file.getParentFile();
        if (parent != null && !parent.exists()) {
            parent.mkdirs();
        }
    }
}