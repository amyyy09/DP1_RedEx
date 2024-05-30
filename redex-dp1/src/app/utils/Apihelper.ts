import { Vuelo } from "@/app/types/Planes";

export function vuelosWithCapacity(paquete: any, vuelosRef: React.RefObject<Vuelo[]>){

  paquete.vuelos.forEach((vueloData: Vuelo) => {
    const vuelo = new Vuelo({
      ...vueloData,
      aeropuertoOrigen: paquete.aeropuertoOrigen,
      aeropuertoDestino: paquete.aeropuertoDestino,
      cantPaquetes: 1,
    });

    if (vuelosRef.current) {
      const existingVueloIndex = vuelosRef.current.findIndex(v => v.idVuelo === vuelo.idVuelo);
      if (existingVueloIndex !== -1) {
        vuelosRef.current[existingVueloIndex].cantPaquetes += 1;
      } else {
        vuelosRef.current.push(vuelo);
      }
    } else {
      console.log("Vuelos.current is null");
    }
  });
}
