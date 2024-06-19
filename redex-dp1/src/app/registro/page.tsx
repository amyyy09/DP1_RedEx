"use client";

import React, { useMemo, useState, useEffect, useRef } from "react";
import dynamic from "next/dynamic";
import Topbar from "../components/layout/Topbar";
import Sidebar from "../components/layout/Sidebar";
import CurrentTimeDisplay from "../components/map/CurrentTimeDisplay"; // Import the new component
import { Vuelo } from "../types/Planes";
import "../styles/SimulatedTime.css";

const Registro: React.FC = () => {

    return (
        <div style={{ display: "flex", flexDirection: "column" }}>
            <div style={{ display: "flex", flex: 1 }}>
                <Sidebar />
                <div>
                <h1>Registro de Envíos</h1>
                
                </div>
                
            </div>
        </div>
    );
};

export default Registro;
