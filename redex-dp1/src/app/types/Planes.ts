export interface PlaneProps {
  origin: { lat: number; lng: number };
  destination: { lat: number; lng: number };
  name: string;
  duration: number; // duration in milliseconds for the animation
}
