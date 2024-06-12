import React, { useEffect, useRef } from "react";
import { useMap } from "react-leaflet";
import L from "leaflet";
import "leaflet-rotatedmarker";
import ReactDOMServer from "react-dom/server";

interface RotatedMarkerProps {
  position: L.LatLngExpression;
  icon: L.Icon;
  rotationAngle: number;
  popupContent?: JSX.Element;
}

const RotatedMarker: React.FC<RotatedMarkerProps> = ({
  position,
  icon,
  rotationAngle,
  popupContent,
}) => {
  const markerRef = useRef<L.Marker | null>(null);
  const map = useMap();

  useEffect(() => {
    if (!map) return;

    if (markerRef.current) {
      map.removeLayer(markerRef.current);
    }

    const marker = L.marker(position, {
      icon,
      rotationAngle,
      rotationOrigin: "center",
    }).addTo(map);

    if (popupContent) {
      const popupHtml = ReactDOMServer.renderToString(popupContent);
      marker.bindPopup(popupHtml);
    }

    markerRef.current = marker;

    return () => {
      if (markerRef.current) {
        map.removeLayer(markerRef.current);
      }
    };
  }, [map, position, icon, rotationAngle, popupContent]);

  return null;
};

export default RotatedMarker;
