package src.Clases;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalTime;
import java.time.OffsetTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

public class PlanDeVuelo {
        private String codigoIATAOrigen;
        private String codigoIATADestino;
        private OffsetTime horaSalida;
        private OffsetTime horaLlegada;
        private int capacidad;
        private boolean isSameContinent;
}
