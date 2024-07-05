import { flightPlans } from "../data/flightPlans";

export function transformCode(ruta: string): string {
  const code = ruta.split(";").map((code, index, array) => {
    const origin = flightPlans[Number(code)].origin.name;
    const destination = flightPlans[Number(code)].destiny.name;
    if (index === array.length - 1) {
      return `${destination} - ${origin}`;
    } else {
      return `${destination}`;
    }
  });

  return code.join(" - ");
}
