package Controller;

import java.io.*;
import java.util.ArrayList;

public class Procesos {

    final static String RUTA_ARCHIVO = "C:\\Users\\lenovo\\IdeaProjects\\Vaadin Server\\src\\main\\resources\\Productos.txt";

    public static ArrayList<String[]> obtenerProductos() throws Exception {

        try{

            ArrayList<String[]> productos = new ArrayList<>();

            String[] producto;

            String linea = "";

            FileReader archivoProductos = new FileReader(new File(RUTA_ARCHIVO));
            BufferedReader datosProductos = new BufferedReader(archivoProductos);

            while((linea = datosProductos.readLine()) != null){
                producto = linea.trim().split("\\|");
                productos.add(producto);
            }

            datosProductos.close();

            return productos;
        }
        catch (IOException io){
            throw new Exception("Error al procesar el archivo (" + io.getMessage() + ")");
        }
        catch (Exception e){
            throw new Exception("Error en la función (" + e.getMessage() + ")");
        }

    }

}
