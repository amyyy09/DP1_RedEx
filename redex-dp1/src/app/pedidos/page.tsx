"use client";
import React from "react";
import RegisterPage from "@/components/RegisterPage";
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
        <RegisterPage />
      </div>
      <Image
        src="/image.png"
        alt="Side Visual"
        width={500} // Puedes ajustar esto según necesites
        height={500} // Puedes ajustar esto según necesites
        layout="intrinsic"
      />
    </div>
  );
};

export default pedidos;
