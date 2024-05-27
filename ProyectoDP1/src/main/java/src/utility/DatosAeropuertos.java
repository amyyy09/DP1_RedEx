package src.utility;

import src.entity.AeropuertoEntity;
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
<<<<<<< HEAD
        public static List<Aeropuerto> getAeropuertosInicializados() {
                List<Aeropuerto> aeropuertos = new ArrayList<>();
                aeropuertos.add(new Aeropuerto("SKBO", "Bogota", "Colombia", "America del Sur", "bogo", -5,
                                new Almacen(30, 0, new ArrayList<>()), 0, 0));
                aeropuertos.add(new Aeropuerto("SEQM", "Quito", "Ecuador", "America del Sur", "quit", -5,
                                new Almacen(30, 0, new ArrayList<>()), 0, 0));
                aeropuertos.add(new Aeropuerto("SVMI", "Caracas", "Venezuela", "America del Sur", "cara", -4,
                                new Almacen(30, 0, new ArrayList<>()), 0, 0));
                aeropuertos.add(new Aeropuerto("SBBR", "Brasilia", "Brasil", "America del Sur", "bras", -3,
                                new Almacen(30, 0, new ArrayList<>()), 0, 0));
                aeropuertos.add(new Aeropuerto("SPIM", "Lima", "Perú", "America del Sur", "lima", -5,
                                new Almacen(30, 0, new ArrayList<>()), 0, 0));
                aeropuertos.add(new Aeropuerto("SLLP", "La Paz", "Bolivia", "America del Sur", "lapa", -4,
                                new Almacen(30, 0, new ArrayList<>()), 0, 0));
                aeropuertos.add(new Aeropuerto("SCEL", "Santiago de Chile", "Chile", "America del Sur", "sant", -3,
                                new Almacen(30, 0, new ArrayList<>()), 0, 0));
                aeropuertos.add(new Aeropuerto("SABE", "Buenos Aires", "Argentina", "America del Sur", "buen", -3,
                                new Almacen(30, 0, new ArrayList<>()), 0, 0));
                aeropuertos.add(new Aeropuerto("SGAS", "Asunción", "Paraguay", "America del Sur", "asun", -4,
                                new Almacen(30, 0, new ArrayList<>()), 0, 0));
                aeropuertos.add(new Aeropuerto("SUAA", "Montevideo", "Uruguay", "America del Sur", "mont", -3,
                                new Almacen(30, 0, new ArrayList<>()), 0, 0));

                aeropuertos.add(new Aeropuerto("LATI", "Tirana", "Albania", "Europa", "tira", 2,
                                new Almacen(30, 0, new ArrayList<>()), 0, 0));
                aeropuertos.add(new Aeropuerto("EDDI", "Berlin", "Alemania", "Europa", "berl", 2,
                                new Almacen(30, 0, new ArrayList<>()), 0, 0));
                aeropuertos.add(new Aeropuerto("LOWW", "Viena", "Austria", "Europa", "vien", 2,
                                new Almacen(30, 0, new ArrayList<>()), 0, 0));
                aeropuertos.add(new Aeropuerto("EBCI", "Bruselas", "Belgica", "Europa", "brus", 2,
                                new Almacen(30, 0, new ArrayList<>()), 0, 0));
                aeropuertos.add(new Aeropuerto("UMMS", "Minsk", "Bielorrusia", "Europa", "mins", 3,
                                new Almacen(30, 0, new ArrayList<>()), 0, 0));
                aeropuertos.add(new Aeropuerto("LBSF", "Sofia", "Bulgaria", "Europa", "sofi", 3,
                                new Almacen(30, 0, new ArrayList<>()), 0, 0));
                aeropuertos.add(new Aeropuerto("LKPR", "Praga", "Checa", "Europa", "prag", 2,
                                new Almacen(30, 0, new ArrayList<>()), 0, 0));
                aeropuertos.add(new Aeropuerto("LDZA", "Zagreb", "Croacia", "Europa", "zagr", 2,
                                new Almacen(30, 0, new ArrayList<>()), 0, 0));
                aeropuertos.add(new Aeropuerto("EKCH", "Copenhague", "Dinamarca", "Europa", "cope", 2,
                                new Almacen(30, 0, new ArrayList<>()), 0, 0));
                aeropuertos.add(new Aeropuerto("EHAM", "Amsterdam", "Holanda", "Europa", "amst", 2,
                                new Almacen(30, 0, new ArrayList<>()), 0, 0));

                aeropuertos.add(new Aeropuerto("VIDP", "Delhi", "India", "Asia", "delh", 5,
                                new Almacen(30, 0, new ArrayList<>()), 0, 0));
                aeropuertos.add(new Aeropuerto("RKSI", "Seul", "Corea del Sur", "Asia", "seul", 9,
                                new Almacen(30, 0, new ArrayList<>()), 0, 0));
                aeropuertos.add(new Aeropuerto("VTBS", "Bangkok", "Tailandia", "Asia", "bang", 7,
                                new Almacen(30, 0, new ArrayList<>()), 0, 0));
                aeropuertos.add(new Aeropuerto("OMDB", "Dubai", "Emiratos A.U", "Asia", "emir", 4,
                                new Almacen(30, 0, new ArrayList<>()), 0, 0));
                aeropuertos.add(new Aeropuerto("ZBAA", "Beijing", "China", "Asia", "beij", 8,
                                new Almacen(480, 0, new ArrayList<>()), 0, 0));
                aeropuertos.add(new Aeropuerto("RJTT", "Tokyio", "Japon", "Asia", "toky", 9,
                                new Almacen(30, 0, new ArrayList<>()), 0, 0));
                aeropuertos.add(new Aeropuerto("WMKK", "Kuala", "Malasia", "Asia", "kual", 8,
                                new Almacen(30, 0, new ArrayList<>()), 0, 0));
                aeropuertos.add(new Aeropuerto("WSSS", "Singapure", "Singapure", "Asia", "sing", 8,
                                new Almacen(30, 0, new ArrayList<>()), 0, 0));
                aeropuertos.add(new Aeropuerto("WIII", "Jakarta", "Indonesia", "Asia", "jaka", 7,
                                new Almacen(30, 0, new ArrayList<>()), 0, 0));
                aeropuertos.add(new Aeropuerto("RPLL", "Manila", "Filipinas", "Asia", "mani", 8,
                                new Almacen(30, 0, new ArrayList<>()), 0, 0));

                return aeropuertos;
        }

        public static List<AeropuertoEntity> getAeropuertosSinInicializar() {
                List<AeropuertoEntity> aeropuertos = new ArrayList<>();

                aeropuertos.add(new AeropuertoEntity("SKBO", "Bogota", "Colombia", "America del Sur", "bogo", -5,
                                4.701389,
                                -74.146944, 430, 0));
                aeropuertos.add(new AeropuertoEntity("SEQM", "Quito", "Ecuador", "America del Sur", "quit", -5,
                                0.113333,
                                -78.358611, 410, 0));
                aeropuertos.add(new AeropuertoEntity("SVMI", "Caracas", "Venezuela", "America del Sur", "cara", -4,
                                10.603056,
                                -66.990556, 400, 0));
                aeropuertos.add(new AeropuertoEntity("SBBR", "Brasilia", "Brasil", "America del Sur", "bras", -3,
                                -15.869167,
                                -47.917778, 480, 0));
                aeropuertos.add(new AeropuertoEntity("SPIM", "Lima", "Perú", "America del Sur", "lima", -5, -12.021944,
                                -77.114444, 440, 0));
                aeropuertos.add(new AeropuertoEntity("SLLP", "La Paz", "Bolivia", "America del Sur", "lapa", -4,
                                -16.513339,
                                -68.192223, 400, 0));
                aeropuertos.add(new AeropuertoEntity("SCEL", "Santiago de Chile", "Chile", "America del Sur", "sant",
                                -3, -33.392778,
                                -70.785556, 470, 0));
                aeropuertos.add(new AeropuertoEntity("SABE", "Buenos Aires", "Argentina", "America del Sur", "buen", -3,
                                -34.559167,
                                -58.415833, 430, 0));
                aeropuertos.add(new AeropuertoEntity("SGAS", "Asunción", "Paraguay", "America del Sur", "asun", -4,
                                -25.239722,
                                -57.519167, 370, 0));
                aeropuertos.add(new AeropuertoEntity("SUAA", "Montevideo", "Uruguay", "America del Sur", "mont", -3,
                                -34.838333,
                                -56.030833, 320, 0));

                aeropuertos.add(
                                new AeropuertoEntity("LATI", "Tirana", "Albania", "Europa", "tira", 2, 41.414722,
                                                19.720556, 410, 0));
                aeropuertos.add(
                                new AeropuertoEntity("EDDI", "Berlin", "Alemania", "Europa", "berl", 2, 52.474722,
                                                13.401389, 480, 0));
                aeropuertos.add(
                                new AeropuertoEntity("LOWW", "Viena", "Austria", "Europa", "vien", 2, 48.110278,
                                                16.569722, 390, 0));
                aeropuertos.add(
                                new AeropuertoEntity("EBCI", "Bruselas", "Belgica", "Europa", "brus", 2, 50.901389,
                                                4.484444, 200, 0));
                aeropuertos.add(
                                new AeropuertoEntity("UMMS", "Minsk", "Bielorrusia", "Europa", "mins", 3, 53.882469,
                                                28.032939, 210, 0));
                aeropuertos.add(
                                new AeropuertoEntity("LBSF", "Sofia", "Bulgaria", "Europa", "sofi", 3, 42.695333,
                                                23.401061, 500, 0));
                aeropuertos.add(
                                new AeropuertoEntity("LKPR", "Praga", "Checa", "Europa", "prag", 2, 50.100833,
                                                14.260000, 380, 0));
                aeropuertos.add(
                                new AeropuertoEntity("LDZA", "Zagreb", "Croacia", "Europa", "zagr", 2, 45.742931,
                                                16.068778, 350, 0));
                aeropuertos.add(
                                new AeropuertoEntity("EKCH", "Copenhague", "Dinamarca", "Europa", "cope", 2, 55.617917,
                                                12.655972, 10, 0));
                aeropuertos.add(
                                new AeropuertoEntity("EHAM", "Amsterdam", "Holanda", "Europa", "amst", 2, 52.308613,
                                                4.763889, -3, 0));

                aeropuertos.add(new AeropuertoEntity("VIDP", "Delhi", "India", "Asia", "delh", 5, 28.568052, 77.117658,
                                230, 0));
                aeropuertos.add(new AeropuertoEntity("RKSI", "Seul", "Corea del Sur", "Asia", "seul", 9, 37.469075,
                                126.450517, 20,
                                0));
                aeropuertos.add(
                                new AeropuertoEntity("VTBS", "Bangkok", "Tailandia", "Asia", "bang", 7, 13.681108,
                                                100.747283, 5, 0));
                aeropuertos.add(
                                new AeropuertoEntity("OMDB", "Dubai", "Emiratos A.U", "Asia", "emir", 4, 25.253056,
                                                55.364444, 60, 0));
                aeropuertos.add(new AeropuertoEntity("ZBAA", "Beijing", "China", "Asia", "beij", 8, 40.080111,
                                116.584556, 35, 0));
                aeropuertos.add(
                                new AeropuertoEntity("RJTT", "Tokyio", "Japon", "Asia", "toky", 9, 35.552258,
                                                139.779694, 7, 0));
                aeropuertos.add(
                                new AeropuertoEntity("WMKK", "Kuala", "Malasia", "Asia", "kual", 8, 2.745578,
                                                101.709917, 26, 0));
                aeropuertos.add(
                                new AeropuertoEntity("WSSS", "Singapure", "Singapure", "Asia", "sing", 8, 1.364447,
                                                103.991146, 22, 0));
                aeropuertos.add(
                                new AeropuertoEntity("WIII", "Jakarta", "Indonesia", "Asia", "jaka", 7, -6.125567,
                                                106.655897, 8, 0));
                aeropuertos.add(
                                new AeropuertoEntity("RPLL", "Manila", "Filipinas", "Asia", "mani", 8, 14.508647,
                                                121.019581, 16, 0));

                return aeropuertos;
        }
=======
    public static List<Aeropuerto> getAeropuertosInicializados() {
        List<Aeropuerto> aeropuertos = new ArrayList<>();
        aeropuertos.add(new Aeropuerto("SKBO", "Bogota", "Colombia", "America del Sur", "bogo", -5, 430, 0));
        aeropuertos.add(new Aeropuerto("SEQM", "Quito", "Ecuador", "America del Sur", "quit", -5, 410, 0));
        aeropuertos.add(new Aeropuerto("SVMI", "Caracas", "Venezuela", "America del Sur", "cara", -4, 400, 0));
        aeropuertos.add(new Aeropuerto("SBBR", "Brasilia", "Brasil", "America del Sur", "bras", -3, 480, 0));
        aeropuertos.add(new Aeropuerto("SPIM", "Lima", "Perú", "America del Sur", "lima", -5, 440, 0));
        aeropuertos.add(new Aeropuerto("SLLP", "La Paz", "Bolivia", "America del Sur", "lapa", -4, 420, 0));
        aeropuertos.add(new Aeropuerto("SCEL", "Santiago de Chile", "Chile", "America del Sur", "sant", -3, 460, 0));
        aeropuertos.add(new Aeropuerto("SABE", "Buenos Aires", "Argentina", "America del Sur", "buen", -3, 460, 0));
        aeropuertos.add(new Aeropuerto("SGAS", "Asunción", "Paraguay", "America del Sur", "asun", -4, 400, 0));
        aeropuertos.add(new Aeropuerto("SUAA", "Montevideo", "Uruguay", "America del Sur", "mont", -3, 400, 0));
        
        aeropuertos.add(new Aeropuerto("LATI", "Tirana", "Albania", "Europa", "tira", 2, 410, 0));
        aeropuertos.add(new Aeropuerto("EDDI", "Berlin", "Alemania", "Europa", "berl", 2, 480, 0));
        aeropuertos.add(new Aeropuerto("LOWW", "Viena", "Austria", "Europa", "vien", 2, 430, 0));
        aeropuertos.add(new Aeropuerto("EBCI", "Bruselas", "Belgica", "Europa", "brus", 2, 440, 0));
        aeropuertos.add(new Aeropuerto("UMMS", "Minsk", "Bielorrusia", "Europa", "mins", 3, 400, 0));
        aeropuertos.add(new Aeropuerto("LBSF", "Sofia", "Bulgaria", "Europa", "sofi", 3, 400, 0));
        aeropuertos.add(new Aeropuerto("LKPR", "Praga", "Checa", "Europa", "prag", 2, 400, 0));
        aeropuertos.add(new Aeropuerto("LDZA", "Zagreb", "Croacia", "Europa", "zagr", 2, 420, 0));
        aeropuertos.add(new Aeropuerto("EKCH", "Copenhague", "Dinamarca", "Europa", "cope", 2, 480, 0));
        aeropuertos.add(new Aeropuerto("EHAM", "Amsterdam", "Holanda", "Europa", "amst", 2, 480, 0));
        
        aeropuertos.add(new Aeropuerto("VIDP", "Delhi", "India", "Asia", "delh", 5, 480, 0));
        aeropuertos.add(new Aeropuerto("RKSI", "Seul", "Corea del Sur", "Asia", "seul", 9, 400, 0));
        aeropuertos.add(new Aeropuerto("VTBS", "Bangkok", "Tailandia", "Asia", "bang", 7, 420, 0));
        aeropuertos.add(new Aeropuerto("OMDB", "Dubai", "Emiratos A.U", "Asia", "emir", 4, 420, 0));
        aeropuertos.add(new Aeropuerto("ZBAA", "Beijing", "China", "Asia", "beij", 8, 480, 0));
        aeropuertos.add(new Aeropuerto("RJTT", "Tokyio", "Japon", "Asia", "toky", 9, 460, 0));
        aeropuertos.add(new Aeropuerto("WMKK", "Kuala", "Malasia", "Asia", "kual", 8, 420, 0));
        aeropuertos.add(new Aeropuerto("WSSS", "Singapure", "Singapure", "Asia", "sing", 8, 400, 0));
        aeropuertos.add(new Aeropuerto("WIII", "Jakarta", "Indonesia", "Asia", "jaka", 7, 400, 0));
        aeropuertos.add(new Aeropuerto("RPLL", "Manila", "Filipinas", "Asia", "mani", 8, 400, 0));
        
        return aeropuertos;
    }
>>>>>>> origin/Amy

        public static List<Aeropuerto> leerAeropuertos() {
                List<Aeropuerto> aeropuertos = new ArrayList<>();
                String filePath = "src/main/resources/Aeropuerto.husos.v2.txt";
                String continent = "";
                try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
<<<<<<< HEAD
                        String line;
                        while ((line = br.readLine()) != null) {
                                if (!line.startsWith(" ")) {
                                        continent = line.trim();
                                } else {
                                        String[] values = line.trim().split("\\s+");
                                        if (values.length >= 7) {
                                                aeropuertos.add(new Aeropuerto(values[1], values[2], values[3],
                                                                continent, values[4],
                                                                Integer.parseInt(values[5]),
                                                                new Almacen(Integer.parseInt(values[6]), 0,
                                                                                new ArrayList<>()),
                                                                0, 0));
                                        }
                                }
=======
                    String line;
                    while ((line = br.readLine()) != null) {
                        if (!line.startsWith(" ")) {
                            continent = line.trim();
                        } else {
                            String[] values = line.trim().split("\\s+");
                            if (values.length >= 7) {
                                aeropuertos.add(new Aeropuerto(values[1], values[2], values[3], continent, values[4], Integer.parseInt(values[5]), Integer.parseInt(values[6]), 0));
                            }
>>>>>>> origin/Amy
                        }
                } catch (IOException e) {
                        e.printStackTrace();
                }
                return aeropuertos;
        }
}
