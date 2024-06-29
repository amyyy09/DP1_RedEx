import React, { useState } from "react";
import AdvancedTable from "./table/Table";
import { columns } from "../utils/columns";
import { flightPlans } from "../data/flightPlans";
import "../styles/Flightplans.css";
import TitleWithIcon from "./registro/TitleWithIcon";

const FlightPlansPage: React.FC = () => {
  const [loadingTable, setLoadingTable] = useState(false);
  return (
    <div className="flightplans-container">
      <TitleWithIcon
        name="Planes de Vuelo"
        icon="/icons/torre-de-control.png"
      />
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
