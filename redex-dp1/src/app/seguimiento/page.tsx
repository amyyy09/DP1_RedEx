"use client";
import React from "react";
import VuelosPage from "@/components/VuelosPage";
import Image from "next/image";

const pedidos: React.FC = () => {
  return (
    <div
      style={{
        display: "flex",
        flexDirection: "row",
        height: "100vh", // Asegura que ocupa toda la altura de la ventana
        width: "100vw", // Asegura que ocupa todo el ancho de la ventana
        justifyContent: "space-between", // Alinea el primer elemento a la izquierda y el segundo a la derecha
      }}
    >
      <div style={{ flex: 1 }}>
        <VuelosPage />
      </div>
      <Image
        src="/image.png"
        alt="Side Visual"
        width={500}
        height={500}
        layout="intrinsic"
      />
    </div>
  );
};

export default pedidos;
