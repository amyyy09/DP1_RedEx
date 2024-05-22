package src.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import src.entity.AeropuertoEntity;
import src.entity.EscalasEntity;
import src.entity.PlanDeVueloEntity;
import src.entity.RutaPredefinidaEntity;
import src.model.Aeropuerto;
import src.model.PlanDeVuelo;
import src.model.RutaPredefinida;
import src.repository.AeropuertoRepository;
import src.repository.EscalasRepository;
import src.repository.RutaPredefinidaRepository;

@Service
public class RutaPredefinidaService {

    @Autowired
    private RutaPredefinidaRepository rutaPredefinidaRepository;
    private AeropuertoService aeropuertoService;
    private PlanDeVueloService planDeVueloService;
    private EscalasRepository escalasRepository;

    public RutaPredefinidaEntity register(RutaPredefinidaEntity ruta) {
        return rutaPredefinidaRepository.save(ruta);
    }

    public List<RutaPredefinidaEntity> getAll() {
        return rutaPredefinidaRepository.findAll();
    }

    public RutaPredefinidaEntity getById(Long id) {
        return rutaPredefinidaRepository.findById(id).orElse(null);
    }

    public RutaPredefinidaEntity update(RutaPredefinidaEntity ruta) {
        return rutaPredefinidaRepository.save(ruta);
    }

    @Transactional
    public boolean delete(Long id) {
        try {
            rutaPredefinidaRepository.deleteById(id);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Transactional
    public void generarRutasPredefinidas() {
        List<AeropuertoEntity> aeropuertosEntities = aeropuertoService.getAll();
        List<PlanDeVueloEntity> planesDeVueloEntities = planDeVueloService.getAll();

        // Conversion from Entity to Model
        List<Aeropuerto> aeropuertos = aeropuertosEntities.stream()
                .map(Aeropuerto::convertirAeropuetoFromEntity)
                .collect(Collectors.toList());
        List<PlanDeVuelo> planes = planesDeVueloEntities.stream()
                .map(PlanDeVuelo::convertirPlanDeVueloFromEntity)
                .collect(Collectors.toList());

        List<RutaPredefinida> rutas = generarRutas(aeropuertos, planes);
        List<RutaPredefinidaEntity> rutasEntities = rutas.stream()
                .map(RutaPredefinida::convertirARutaPredefinidaEntity) // agregar esto
                .collect(Collectors.toList());

        rutaPredefinidaRepository.saveAll(rutasEntities);

        for (int i = 0; i < rutas.size(); i++) {
            RutaPredefinida ruta = rutas.get(i);
            RutaPredefinidaEntity rutaEntity = rutasEntities.get(i);

            for (PlanDeVuelo plan : ruta.getEscalas()) {
                EscalasEntity escala = new EscalasEntity();
                escala.setRutaPredefinida(rutaEntity);
                escala.setPlanDeVuelo(planDeVueloService.convertToEntity(plan));
                escalasRepository.save(escala);
            }
        }
    }

    private List<RutaPredefinida> generarRutas(List<Aeropuerto> aeropuertos, List<PlanDeVuelo> planes) {
        List<RutaPredefinida> rutas = new ArrayList<>();
        Aeropuerto origen = aeropuertos.stream()
                .filter(a -> a.getCodigoIATA().equals("ZBAA"))
                .findFirst()
                .orElse(null);
        if (origen == null)
            return rutas;

        for (Aeropuerto destino : aeropuertos) {
            if (!origen.equals(destino)) {
                List<Integer> daysm = new ArrayList<>();
                Boolean sameContinent = origen.getContinente().equals(destino.getContinente());
                List<List<PlanDeVuelo>> planesRutas = generarEscalas(origen, destino, planes, daysm, sameContinent);
                for (int i = 0; i < planesRutas.size(); i++) {
                    List<PlanDeVuelo> planRuta = planesRutas.get(i);
                    RutaPredefinida ruta = new RutaPredefinida(
                            origen.getCodigoIATA(),
                            destino.getCodigoIATA(),
                            planRuta.get(0).getHoraSalida(),
                            planRuta.get(planRuta.size() - 1).getHoraLlegada(),
                            planRuta,
                            daysm.get(i), // get the corresponding value from the daysm array
                            sameContinent);
                    rutas.add(ruta);
                }
            }
        }
        return rutas;
    }

    private static List<List<PlanDeVuelo>> generarEscalas(Aeropuerto origen, Aeropuerto destino,
            List<PlanDeVuelo> planes, List<Integer> daysm, Boolean sameContinent) {
        List<List<PlanDeVuelo>> allRoutes = new ArrayList<>();
        List<PlanDeVuelo> currentRoute = new ArrayList<>();
        Set<String> visited = new HashSet<>();

        dfs(origen.getCodigoIATA(), destino.getCodigoIATA(), currentRoute, allRoutes, planes, daysm, 0, sameContinent,
                visited);
        return allRoutes;
    }

    private static void dfs(String current, String destination, List<PlanDeVuelo> currentRoute,
            List<List<PlanDeVuelo>> allRoutes, List<PlanDeVuelo> planes, List<Integer> daysm,
            int totalDays, boolean sameContinent, Set<String> visited) {

        if (current.equals(destination)) {
            if (!currentRoute.isEmpty() && !allRoutes.contains(currentRoute)) {
                List<PlanDeVuelo> routeToAdd = new ArrayList<>(currentRoute);
                allRoutes.add(routeToAdd);
                daysm.add(totalDays);
                // print the route for debugging with the respective horas de salida y llegada
                // System.out.println("Ruta encontrada: en " + totalDays + " días");
                // for (PlanDeVuelo plan : routeToAdd) {
                // System.out.println(plan.getCodigoIATAOrigen() + " " + plan.getHoraSalida() +
                // " -> " + plan.getCodigoIATADestino() + " " + " " + plan.getHoraLlegada());
                // }
                return;
            }
        }
        if (currentRoute.size() > 3 || visited.contains(current)) {
            return; // Limit recursion depth and prevent visiting the same airport in one route
        }

        visited.add(current);
        List<PlanDeVuelo> filteredPlanes = planes.stream()
                .filter(plan -> plan.getCodigoIATAOrigen().equals(current))
                .collect(Collectors.toList());

        for (PlanDeVuelo plan : filteredPlanes) {
            if (!visited.contains(plan.getCodigoIATADestino())) {

                if (currentRoute.isEmpty() || currentRoute.get(currentRoute.size() - 1).getHoraLlegada().plusMinutes(5)
                        .isBefore(plan.getHoraSalida())) { // Ensure at least 5 minutes between flights
                    currentRoute.add(plan);
                    int newTotalDays = totalDays;
                    if (plan.getHoraLlegada().isBefore(plan.getHoraSalida())
                            || (currentRoute.size() > 1 &&
                                    plan.getHoraSalida()
                                            .isBefore(currentRoute.get(currentRoute.size() - 2).getHoraLlegada()))) {

                        newTotalDays++;
                    }

                    if (sameContinent && (newTotalDays > 1 || (newTotalDays > 0 && (currentRoute.size() > 1 &&
                            plan.getHoraLlegada().toLocalTime()
                                    .isAfter(currentRoute.get(0).getHoraSalida().toLocalTime()))))) {
                        currentRoute.remove(currentRoute.size() - 1);
                        visited.remove(current);
                        return; // Abort the route if it takes more than 1 day in the same continent
                    }

                    if (!sameContinent && (newTotalDays > 2 || (newTotalDays > 1 &&
                            plan.getHoraLlegada().toLocalTime()
                                    .isAfter(currentRoute.get(0).getHoraSalida().toLocalTime())))) {
                        currentRoute.remove(currentRoute.size() - 1);
                        visited.remove(current);
                        return; // Abort the route if it exceeds 2 days and not in the same continent
                    }

                    dfs(plan.getCodigoIATADestino(), destination, currentRoute, allRoutes, planes, daysm, newTotalDays,
                            sameContinent, visited);
                    currentRoute.remove(currentRoute.size() - 1);
                }
            }
        }
        visited.remove(current);
    }
}
