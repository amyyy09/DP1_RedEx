package src.utility;

import src.model.*;
import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class DatosAeropuertos {    
    public static List<Aeropuerto> getAeropuertosInicializados() {
        List<Aeropuerto> aeropuertos = new ArrayList<>();
        aeropuertos.add(new Aeropuerto("SKBO", "Bogota", "Colombia", "America del Sur", "bogo", -5, new Almacen(1430, 0, new ArrayList<>()),"04° 42' 05\" N","74° 08' 49\" W"));
        aeropuertos.add(new Aeropuerto("SEQM", "Quito", "Ecuador", "America del Sur", "quit", -5, new Almacen(1410, 0, new ArrayList<>()),"00° 06' 48\" N","78° 21' 31\" W"));
        aeropuertos.add(new Aeropuerto("SVMI", "Caracas", "Venezuela", "America del Sur", "cara", -4, new Almacen(1400, 0, new ArrayList<>()),"10° 36' 11\" N","66° 59' 26\" W"));
        aeropuertos.add(new Aeropuerto("SBBR", "Brasilia", "Brasil", "America del Sur", "bras", -3, new Almacen(1480, 0, new ArrayList<>()),"15° 51' 53\" S","47° 55' 05\" W"));
        aeropuertos.add(new Aeropuerto("SPIM", "Lima", "Perú", "America del Sur", "lima", -5, new Almacen(1440, 0, new ArrayList<>()),"12° 01' 19\" S","77° 06' 52\" W"));
        aeropuertos.add(new Aeropuerto("SLLP", "La Paz", "Bolivia", "America del Sur", "lapa", -4, new Almacen(1420, 0, new ArrayList<>()),"16° 30' 47\" S","68° 11' 32\" W"));
        aeropuertos.add(new Aeropuerto("SCEL", "Santiago de Chile", "Chile", "America del Sur", "sant", -3, new Almacen(1460, 0, new ArrayList<>()),"33° 23' 47\" S","70° 47' 41\" W"));
        aeropuertos.add(new Aeropuerto("SABE", "Buenos Aires", "Argentina", "America del Sur", "buen", -3, new Almacen(1460, 0, new ArrayList<>()),"34° 34' 33\" S","58° 24' 56\" W"));
        aeropuertos.add(new Aeropuerto("SGAS", "Asunción", "Paraguay", "America del Sur", "asun", -4, new Almacen(1400, 0, new ArrayList<>()),"25° 14' 24\" S","57° 31' 12\" W"));
        aeropuertos.add(new Aeropuerto("SUAA", "Montevideo", "Uruguay", "America del Sur", "mont", -3, new Almacen(1400, 0, new ArrayList<>()),"34° 47' 21\" S","56° 15' 53\" W"));
        
        aeropuertos.add(new Aeropuerto("LATI", "Tirana", "Albania", "Europa", "tira", 2, new Almacen(1410, 0, new ArrayList<>()),"41° 24' 53\" N","19° 43' 14\" E"));
        aeropuertos.add(new Aeropuerto("EDDI", "Berlin", "Alemania", "Europa", "berl", 2, new Almacen(1480, 0, new ArrayList<>()),"52° 28' 25\" N","13° 24' 06\" E"));
        aeropuertos.add(new Aeropuerto("LOWW", "Viena", "Austria", "Europa", "vien", 2, new Almacen(1430, 0, new ArrayList<>()),"48° 06' 39\" N","16° 34' 15\" E"));
        aeropuertos.add(new Aeropuerto("EBCI", "Bruselas", "Belgica", "Europa", "brus", 2, new Almacen(1440, 0, new ArrayList<>()),"50° 27' 33\" N","04° 27' 13\" E"));
        aeropuertos.add(new Aeropuerto("UMMS", "Minsk", "Bielorrusia", "Europa", "mins", 3, new Almacen(1400, 0, new ArrayList<>()),"53° 52' 57\" N","28° 01' 57\" E"));
        aeropuertos.add(new Aeropuerto("LBSF", "Sofia", "Bulgaria", "Europa", "sofi", 3, new Almacen(1400, 0, new ArrayList<>()),"42° 41' 25\" N","23° 24' 17\" E"));
        aeropuertos.add(new Aeropuerto("LKPR", "Praga", "Checa", "Europa", "prag", 2, new Almacen(1400, 0, new ArrayList<>()),"50° 06' 05\" N","14° 15' 56\" E"));
        aeropuertos.add(new Aeropuerto("LDZA", "Zagreb", "Croacia", "Europa", "zagr", 2, new Almacen(1420, 0, new ArrayList<>()),"45° 44' 34\" N","16° 04' 07\" E"));
        aeropuertos.add(new Aeropuerto("EKCH", "Copenhague", "Dinamarca", "Europa", "cope", 2, new Almacen(1480, 0, new ArrayList<>()),"55° 37' 05\" N","12° 39' 22\" E"));
        aeropuertos.add(new Aeropuerto("EHAM", "Amsterdam", "Holanda", "Europa", "amst", 2, new Almacen(1480, 0, new ArrayList<>()),"52° 18' 00\" N","04° 45' 54\" E"));
        
        aeropuertos.add(new Aeropuerto("VIDP", "Delhi", "India", "Asia", "delh", 5, new Almacen(1480, 0, new ArrayList<>()),"28° 33' 59\" N","77° 06' 11\" E"));
        aeropuertos.add(new Aeropuerto("OSDI", "Damasco", "Siria", "Asia", "dama", 3, new Almacen(1400, 0, new ArrayList<>()),"33° 24' 41\" N","36° 30' 56\" E"));
        aeropuertos.add(new Aeropuerto("OERK", "Riad", "Arabia Saudita", "Asia", "riad", 3, new Almacen(1420, 0, new ArrayList<>()),"24° 57' 28\" N","46° 41' 56\" E"));
        aeropuertos.add(new Aeropuerto("OMDB", "Dubai", "Emiratos Árabes Unidos", "Asia", "emir", 4, new Almacen(1420, 0, new ArrayList<>()),"25° 15' 10\" N","55° 21' 52\" E"));
        aeropuertos.add(new Aeropuerto("OAKB", "Kabul", "Afganistán", "Asia", "kabu", 4, new Almacen(1480, 0, new ArrayList<>()),"34° 33' 56\" N","69° 12' 39\" E"));
        aeropuertos.add(new Aeropuerto("OOMS", "Mascate", "Omán", "Asia", "masc", 4, new Almacen(1460, 0, new ArrayList<>()),"23° 35' 22\" N","58° 17' 03\" E"));
        aeropuertos.add(new Aeropuerto("OYSN", "Sana", "Yemen", "Asia", "sana", 3, new Almacen(1420, 0, new ArrayList<>()),"15° 28' 34\" N","44° 13' 11\" E"));
        aeropuertos.add(new Aeropuerto("OPKC", "Karachi", "Pakistán", "Asia", "kara", 5, new Almacen(1410, 0, new ArrayList<>()),"24° 54' 00\" N","67° 09' 00\" E"));
        aeropuertos.add(new Aeropuerto("UBBB", "Bakú", "Azerbaiyán", "Asia", "baku", 2, new Almacen(1400, 0, new ArrayList<>()),"40° 28' 02\" N", "50° 02' 48\" E"));
        aeropuertos.add(new Aeropuerto("OJAI", "Amán", "Jordania", "Asia", "aman", 3, new Almacen(1400, 0, new ArrayList<>()),"31° 43' 21\" N", "35° 59' 36\" E"));
        return aeropuertos;
    }

        // public static List<Aeropuerto> leerAeropuertos() {
        //         List<Aeropuerto> aeropuertos = new ArrayList<>();
        //         String filePath =  GlobalVariables.PATH + "Aeropuerto.husos.v2.txt";
        //         String continent = "";
        //         try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
        //             String line;
        //             while ((line = br.readLine()) != null) {
        //                 if (!line.startsWith(" ")) {
        //                     continent = line.trim();
        //                 } else {
        //                     String[] values = line.trim().split("\\s+");
        //                     if (values.length >= 7) {
        //                         aeropuertos.add(new Aeropuerto(values[1], values[2], values[3], continent, values[4], Integer.parseInt(values[5]), new Almacen(Integer.parseInt(values[6]), 0, new ArrayList<>())));
        //                     }
        //                 }
        //             }
        //         } catch (IOException e) {
        //             e.printStackTrace();
        //         }
        //         return aeropuertos;
        //     }
}