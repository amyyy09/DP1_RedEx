import React from 'react';
import { Polyline } from 'react-leaflet';
import { LatLngTuple } from 'leaflet';
import { Vuelo } from '@/app/types/Planes';

// Dummy function to calculate intermediate points
// This is a placeholder. For real applications, use a geodesic calculation library.
function calculateGeodesicPoints(start: LatLngTuple, end: LatLngTuple): LatLngTuple[] {
    // Adjusting to create four intermediate points
    const mid1: LatLngTuple = [
      (start[0] * 4 + end[0]) / 5,
      (start[1] * 4 + end[1]) / 5,
    ];
    const mid2: LatLngTuple = [
      (start[0] * 3 + end[0] * 2) / 5,
      (start[1] * 3 + end[1] * 2) / 5,
    ];
    const mid3: LatLngTuple = [
      (start[0] * 2 + end[0] * 3) / 5,
      (start[1] * 2 + end[1] * 3) / 5,
    ];
    const mid4: LatLngTuple = [
      (start[0] + end[0] * 4) / 5,
      (start[1] + end[1] * 4) / 5,
    ];
    return [start, mid1, mid2, mid3, mid4, end];
  }

const GeodesicLine = ({ isVisible, citiesByCode, vuelo }: { isVisible: boolean, citiesByCode: any, vuelo: Vuelo }) => {
  const startPos: LatLngTuple = [
    citiesByCode[vuelo.aeropuertoOrigen].coords.lat,
    citiesByCode[vuelo.aeropuertoOrigen].coords.lng,
  ];
  const endPos: LatLngTuple = [
    citiesByCode[vuelo.aeropuertoDestino].coords.lat,
    citiesByCode[vuelo.aeropuertoDestino].coords.lng,
  ];

  const positions = calculateGeodesicPoints(startPos, endPos);

  return (
    isVisible && (
      <Polyline
        positions={positions}
        pathOptions={{ color: "black", weight: 0.75, dashArray: "5,10" }}
      />
    )
  );
};

export default GeodesicLine;