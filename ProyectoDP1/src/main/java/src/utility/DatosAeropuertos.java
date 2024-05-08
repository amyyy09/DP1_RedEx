package src.utility;

import src.model.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class DatosAeropuertos {
        public static List<Aeropuerto> getAeropuertosInicializados() {
                List<Aeropuerto> aeropuertos = new ArrayList<>();
                aeropuertos.add(new Aeropuerto("SKBO", "Bogota", "Colombia", "America del Sur", "bogo", -5, new Almacen(20, 0, new ArrayList<>())));
                aeropuertos.add(new Aeropuerto("SEQM", "Quito", "Ecuador", "America del Sur", "quit", -5, new Almacen(20, 0, new ArrayList<>())));
                aeropuertos.add(new Aeropuerto("SVMI", "Caracas", "Venezuela", "America del Sur", "cara", -4, new Almacen(20, 0, new ArrayList<>())));
                aeropuertos.add(new Aeropuerto("SBBR", "Brasilia", "Brasil", "America del Sur", "bras", -3, new Almacen(20, 0, new ArrayList<>())));
                aeropuertos.add(new Aeropuerto("SPIM", "Lima", "Perú", "America del Sur", "lima", -5, new Almacen(20, 0, new ArrayList<>())));
                aeropuertos.add(new Aeropuerto("SLLP", "La Paz", "Bolivia", "America del Sur", "lapa", -4, new Almacen(20, 0, new ArrayList<>())));
                aeropuertos.add(new Aeropuerto("SCEL", "Santiago de Chile", "Chile", "America del Sur", "sant", -3, new Almacen(20, 0, new ArrayList<>())));
                aeropuertos.add(new Aeropuerto("SABE", "Buenos Aires", "Argentina", "America del Sur", "buen", -3, new Almacen(20, 0, new ArrayList<>())));
                aeropuertos.add(new Aeropuerto("SGAS", "Asunción", "Paraguay", "America del Sur", "asun", -4, new Almacen(20, 0, new ArrayList<>())));
                aeropuertos.add(new Aeropuerto("SUAA", "Montevideo", "Uruguay", "America del Sur", "mont", -3, new Almacen(20, 0, new ArrayList<>())));
                
                aeropuertos.add(new Aeropuerto("LATI", "Tirana", "Albania", "Europa", "tira", 2, new Almacen(20, 0, new ArrayList<>())));
                aeropuertos.add(new Aeropuerto("EDDI", "Berlin", "Alemania", "Europa", "berl", 2, new Almacen(20, 0, new ArrayList<>())));
                aeropuertos.add(new Aeropuerto("LOWW", "Viena", "Austria", "Europa", "vien", 2, new Almacen(20, 0, new ArrayList<>())));
                aeropuertos.add(new Aeropuerto("EBCI", "Bruselas", "Belgica", "Europa", "brus", 2, new Almacen(20, 0, new ArrayList<>())));
                aeropuertos.add(new Aeropuerto("UMMS", "Minsk", "Bielorrusia", "Europa", "mins", 3, new Almacen(20, 0, new ArrayList<>())));
                aeropuertos.add(new Aeropuerto("LBSF", "Sofia", "Bulgaria", "Europa", "sofi", 3, new Almacen(20, 0, new ArrayList<>())));
                aeropuertos.add(new Aeropuerto("LKPR", "Praga", "Checa", "Europa", "prag", 2, new Almacen(20, 0, new ArrayList<>())));
                aeropuertos.add(new Aeropuerto("LDZA", "Zagreb", "Croacia", "Europa", "zagr", 2, new Almacen(20, 0, new ArrayList<>())));
                aeropuertos.add(new Aeropuerto("EKCH", "Copenhague", "Dinamarca", "Europa", "cope", 2, new Almacen(20, 0, new ArrayList<>())));
                aeropuertos.add(new Aeropuerto("EHAM", "Amsterdam", "Holanda", "Europa", "amst", 2, new Almacen(20, 0, new ArrayList<>())));
                
                aeropuertos.add(new Aeropuerto("VIDP", "Delhi", "India", "Asia", "delh", 5, new Almacen(20, 0, new ArrayList<>())));
                aeropuertos.add(new Aeropuerto("RKSI", "Seul", "Corea del Sur", "Asia", "seul", 9, new Almacen(20, 0, new ArrayList<>())));
                aeropuertos.add(new Aeropuerto("VTBS", "Bangkok", "Tailandia", "Asia", "bang", 7, new Almacen(20, 0, new ArrayList<>())));
                aeropuertos.add(new Aeropuerto("OMDB", "Dubai", "Emiratos A.U", "Asia", "emir", 4, new Almacen(20, 0, new ArrayList<>())));
                aeropuertos.add(new Aeropuerto("ZBAA", "Beijing", "China", "Asia", "beij", 8, new Almacen(20, 0, new ArrayList<>())));
                aeropuertos.add(new Aeropuerto("RJTT", "Tokyio", "Japon", "Asia", "toky", 9, new Almacen(20, 0, new ArrayList<>())));
                aeropuertos.add(new Aeropuerto("WMKK", "Kuala", "Malasia", "Asia", "kual", 8, new Almacen(20, 0, new ArrayList<>())));
                aeropuertos.add(new Aeropuerto("WSSS", "Singapure", "Singapure", "Asia", "sing", 8, new Almacen(20, 0, new ArrayList<>())));
                aeropuertos.add(new Aeropuerto("WIII", "Jakarta", "Indonesia", "Asia", "jaka", 7, new Almacen(20, 0, new ArrayList<>())));
                aeropuertos.add(new Aeropuerto("RPLL", "Manila", "Filipinas", "Asia", "mani", 8, new Almacen(20, 0, new ArrayList<>())));
                
                return aeropuertos;
        }

        public static List<Aeropuerto> leerAeropuertos() {
                List<Aeropuerto> aeropuertos = new ArrayList<>();
                String filePath = "src/main/resources/Aeropuerto.husos.v2.txt";
                String continent = "";
                try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
                    String line;
                    while ((line = br.readLine()) != null) {
                        if (!line.startsWith(" ")) {
                            continent = line.trim();
                        } else {
                            String[] values = line.trim().split("\\s+");
                            if (values.length >= 7) {
                                aeropuertos.add(new Aeropuerto(values[1], values[2], values[3], continent, values[4], Integer.parseInt(values[5]), new Almacen(Integer.parseInt(values[6]), 0, new ArrayList<>())));
                            }
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return aeropuertos;
            }
}