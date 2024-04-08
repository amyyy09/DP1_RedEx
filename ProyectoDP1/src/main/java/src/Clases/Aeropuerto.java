package src.Clases;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

public class Aeropuerto {
    private int idAeropuerto;
    private String nombre;
    private String codAeropuerto;

    private Ciudad ciudad;

    private Almacen almacen;
    
    public static List<Aeropuerto> leerAeropuertos() {
        List<Aeropuerto> aeropuertos = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader("ProyectoDP1/src/main/resources/Aeropuerto.husos.v1.incompleto.txt"))) {
            String line;
            int lineCount = 0;
            Continente continente = null;
            while ((line = reader.readLine()) != null) {
                lineCount++;
                if (lineCount <= 4  || line.trim().isEmpty()) continue;  // Skip the first 4 lines
                if (line.trim().length() > 0 && Character.isLetter(line.trim().charAt(0)) && !Character.isDigit(line.trim().charAt(0))) {  // This line is a continent name
                    String currentContinent = line.trim().split("\\.")[0];
                    continente = new Continente(lineCount, currentContinent, null); 
                } else if (Character.isDigit(line.charAt(0))) {  // This line is an airport
                    String[] parts = line.trim().split("\\s{2,}");
                    int idAeropuerto = Integer.parseInt(parts[0]);
                    String codAeropuerto = parts[1];
                    String nombre = parts[2];

                    Pais pais = new Pais(lineCount, parts[3], null, null, continente); 
                    pais.setCiudades(new ArrayList<>());

                    Ciudad ciudad = new Ciudad(lineCount, nombre, parts[4], ZoneOffset.ofHours(Integer.parseInt(parts[5])), pais);
                    pais.getCiudades().add(ciudad);

                    Aeropuerto aeropuerto = new Aeropuerto(idAeropuerto, nombre, codAeropuerto, ciudad, null);

                    Almacen almacen = new Almacen(idAeropuerto, Integer.parseInt(parts[6]), 0, aeropuerto, null);

                    aeropuerto.setAlmacen(almacen);

                    aeropuertos.add(aeropuerto);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return aeropuertos;
    }
}
