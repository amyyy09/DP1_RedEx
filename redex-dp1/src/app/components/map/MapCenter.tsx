import { useEffect } from "react";
import { useMap } from "react-leaflet";
import { LatLngTuple } from "leaflet";

interface MapCenterProps {
  center: LatLngTuple | null;
}

const MapCenter: React.FC<MapCenterProps> = ({ center }) => {
  const map = useMap();

  useEffect(() => {
    if (center) {
      map.setView(center, map.getZoom()); // Mantiene el nivel de zoom actual
    }
  }, [center, map]);

  return null;
};

export default MapCenter;
