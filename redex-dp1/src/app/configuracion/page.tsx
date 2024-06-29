"use client";
import React from "react";
import Image from "next/image";
import ConfigPage from "../components/configurationPage";

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
        <ConfigPage />
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
