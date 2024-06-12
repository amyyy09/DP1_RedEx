// use client; // Not strictly necessary in modern Next.js projects
import React, { FC, useState } from "react";
import next from "next";
import { useRouter } from "next/navigation";
import "../../styles/Sidebar.css";

const Sidebar: FC = () => {
  const router = useRouter();
  const [activeRoute, setActiveRoute] = useState("/"); // Initial active state

  const handleClick = (route: string) => {
    router.push(route);
    setActiveRoute(route); // Update active state on navigation
  };

  return (
    <div className="sidebar">
      <div
        className={`sidebar-item ${activeRoute === "/" ? "active" : ""}`}
        onClick={() => handleClick("/")}
      >
        <img src="./icons/hogar.png" alt="Home" />
      </div>
      <div
        className={`sidebar-item ${activeRoute === "/vuelos" ? "active" : ""}`}
        onClick={() => handleClick("/vuelos")}
      >
        <img src="./icons/modo-vuelo.png" alt="Plane" />
      </div>
      <div
        className={`sidebar-item ${
          activeRoute === "/simulacion" ? "active" : ""
        }`}
        onClick={() => handleClick("/simulacion")}
      >
        <img src="./icons/radar.png" alt="Power" />
      </div>
      <div
        className={`sidebar-item ${activeRoute === "/pedidos" ? "active" : ""}`}
        onClick={() => handleClick("/pedidos")}
      >
        <img src="./icons/paquete.png" alt="Pedidos" />
      </div>
      <div
        className={`sidebar-item ${
          activeRoute === "/configuracion" ? "active" : ""
        }`}
        onClick={() => handleClick("/configuracion")}
      >
        <img src="./icons/actualizacion.png" alt="Loading" />
      </div>
      <div
        className={`sidebar-item ${activeRoute === "/salir" ? "active" : ""}`}
        onClick={() => handleClick("/salir")}
      >
        <img src="./icons/salir.png" alt="Logout" />
      </div>
    </div>
  );
};

export default Sidebar;
