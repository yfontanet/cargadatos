package bbdd;
import loader.CSVLoader;

// Punto de entrada de la aplicación
public class App {
    public static void main(String[] args) {
        CSVLoader loader = new CSVLoader();

        // Ruta relativa desde el directorio del proyecto (donde está pom.xml)
        loader.processFile("datos/resul_mar.csv", "marítimo");
        loader.processFile("datos/resul_t.csv", "terrestre");
    }
}