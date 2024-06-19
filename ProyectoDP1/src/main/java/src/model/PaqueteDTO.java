package src.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PaqueteDTO {
    private String IdPaquete;
    private String codigoIATAOrigen;
    private String codigoIATADestino;
    private String vuelos;

    public static List<PaqueteDTO> fromMap(Map<Paquete, Resultado> map) {
        List<PaqueteDTO> dtoList = new ArrayList<>();

        for (Map.Entry<Paquete, Resultado> entry : map.entrySet()) {
            Paquete paquete = entry.getKey();
            Resultado resultado = entry.getValue();

            PaqueteDTO dto = new PaqueteDTO();
            dto.setIdPaquete(paquete.getID());
            dto.setCodigoIATAOrigen(resultado.getAeropuertoOrigen());
            dto.setCodigoIATADestino(resultado.getAeropuertoDestino());

            // Unir todos los IdVuelo de la lista de vuelos de Resultado, unidos por "->"
            String vuelos = resultado.getVuelos().stream()
                                      .map(Vuelo::getIdVuelo)
                                      .collect(Collectors.joining("->"));
            dto.setVuelos(vuelos);

            dtoList.add(dto);
        }

        return dtoList;
    }
}
