"use client";
import React, { useState } from "react";
import RegisterPage from "../components/registro/RegisterPage";
import Image from "next/image";
import Sidebar from "../components/layout/Sidebar";
import "../styles/loading.css";

const pedidos: React.FC = () => {
  const [loading, setLoading] = useState(false);

  return (
    <>
      {loading && (
        <div className="loading-overlay">
          <div className="loading-spinner">Cargando...</div>
        </div>
      )}
      <div
        style={{
          display: "flex",
          flexDirection: "row",
          height: "100vh",
          width: "100vw",
          justifyContent: "space-between",
        }}
      >
        <Sidebar />
        <div style={{ flex: 1 }}>
          <RegisterPage loading={loading} setLoading={setLoading} />
        </div>
        <Image
          src="/cajitas.png"
          alt="Side Visual"
          width={300} // Puedes ajustar esto según necesites
          height={500} // Puedes ajustar esto según necesites
          layout="intrinsic"
        />
      </div>
    </>
  );
};

export default pedidos;
