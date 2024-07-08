import { flightPlans } from "../data/flightPlans";

export function transformCode(ruta: string): string {
  const code = ruta.split(";").map((code, index, array) => {
    const origin = flightPlans[Number(code)-1].origin.name;
    const destination = flightPlans[Number(code)-1].destiny.name;
    const salida = flightPlans[Number(code)-1].departureTime;
    const llegada = flightPlans[Number(code)-1].arrivalTime;
    if (index === array.length - 1) {
      return `${origin} - ${destination} (${llegada})`;
    } else {
      if (index === 0) {
        return `(${salida}) ${origin}`;
      }
      return `${origin}`;
    }
    // return `${origin} - ${destination}`;
  });

  return code.join(" - ");
}
