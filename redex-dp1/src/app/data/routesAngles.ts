interface Route {
    origin: string;
    destination: string;
    angle: number;
  }
  
  export const routesAngles: Route[] = [
    { origin: "JFK", destination: "LAX", angle: 270 },
    { origin: "LAX", destination: "JFK", angle: 90 },
    // Añade todas las rutas necesarias con sus ángulos predefinidos
    // ...
  ];