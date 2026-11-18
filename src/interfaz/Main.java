package interfaz;

import modelo.Agronomo;
import modelo.Terreno;
import negocio.CsvDatabase;

public class Main {
    public static void main(String[] args) {
        CsvDatabase database = new CsvDatabase("data/readings.csv");
        Terreno terreno = new Terreno("LOTE-ESM-01", "Arcillo-Limosa", 12.5, 45.0, 75.0);
        Agronomo agronomo = new Agronomo(1, "Ing. Nelson Haro", "nelson@palma.ec", "palma2026");

        database.cargarDatosEnTerreno(terreno);

        ConsoleMenu menu = new ConsoleMenu(database, terreno, agronomo);
        menu.start();
    }
}