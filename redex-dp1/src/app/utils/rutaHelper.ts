import { flightPlans } from "../data/flightPlans";

export function transformCode(ruta: string): string {
  const code = ruta.split(";").map((code, index, array) => {
    const origin = flightPlans[Number(code)-1].origin.name;
    const destination = flightPlans[Number(code)-1].destiny.name;
    if (index === array.length - 1) {
      return `${origin} - ${destination}`;
    } else {
      return `${origin}`;
    }
    // return `${origin} - ${destination}`;
  });

  return code.join(" - ");
}
