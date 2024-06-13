import React, { useState } from "react";
import AdvancedTable from "@/components/table/Table";
import { columns } from "@/utils/columns"; // Ajusta la ruta según la ubicación de tu archivo columns.ts
import { flightPlans } from "@/utils/data/flightPlans"; // Ajusta la ruta según la ubicación de tu archivo flightPlans
import "@/styles/Flightplans.css";
import TitleWithIcon from "./TitleWithIcon";

const FlightPlansPage: React.FC = () => {
  const [loadingTable, setLoadingTable] = useState(false); // Si necesitas un estado para manejar la carga de datos

  return (
    <div className="flightplans-container">
      <TitleWithIcon name="Planes de Vuelo" icon="/icons/rastro.png" />
      <AdvancedTable
        data={flightPlans}
        id="code"
        columns={columns}
        loadingTable={loadingTable}
      />
    </div>
  );
};

export default FlightPlansPage;
