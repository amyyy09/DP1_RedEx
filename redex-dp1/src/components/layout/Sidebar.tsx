"use client";
import React from "react";
import next from "next";
import { useRouter } from "next/navigation";
import "../../styles/Sidebar.css";

const Sidebar: React.FC = () => {
  const router = useRouter();

  return (
    <div className="sidebar">
      <div className="sidebar-item" onClick={() => router.push("/")}>
        <img src="./icons/hogar.png" alt="Home" />
      </div>
      <div
        className="sidebar-item active"
        onClick={() => router.push("/flights")}
      >
        <img src="./icons/modo-vuelo.png" alt="Plane" />
      </div>
      <div className="sidebar-item" onClick={() => router.push("/simulacion")}>
        <img src="./icons/radar.png" alt="Power" />
      </div>
      <div className="sidebar-item" onClick={() => router.push("/pedidos")}>
        <img src="./icons/paquete.png" alt="Pedidos" />
      </div>
      <div
        className="sidebar-item"
        onClick={() => router.push("/configuracion")}
      >
        <img src="./icons/actualizacion.png" alt="Loading" />
      </div>
      <div className="sidebar-item" onClick={() => router.push("/salir")}>
        <img src="./icons/salir.png" alt="Logout" />
      </div>
    </div>
  );
};

export default Sidebar;
