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
        height: "100vh",
        width: "100vw",
        justifyContent: "space-between",
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
