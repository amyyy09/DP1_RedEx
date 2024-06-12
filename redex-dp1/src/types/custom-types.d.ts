// En algún lugar de tus definiciones globales, por ejemplo en un archivo custom-types.d.ts
import 'leaflet';

declare module 'leaflet' {
  export interface MarkerOptions {
    rotationAngle?: number;
    rotationOrigin?: string;
  }

  export interface Marker {
    setRotationAngle(angle: number): void;
    setRotationOrigin(origin: string): void;
  }
}
