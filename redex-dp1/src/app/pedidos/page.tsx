"use client";
import React from "react";
import RegisterPage from "@/components/RegisterPage";
import Image from "next/image";

const SimulacionPage: React.FC = () => {
  return (
    <div style={{ display: "flex", flexDirection: "row", height: "100-%" }}>
      <div style={{ flex: 1 }}>
        <RegisterPage />
      </div>
      <div>
        <Image
          src="/image.png" // Ensure the path is correct and accessible by Next.js public folder or a valid external URL
          alt="Side Visual"
          width={500} // Set width as needed
          height={500} // Maintain aspect ratio or adjust as needed
          layout="intrinsic" // This will maintain the image's aspect ratio
        />
      </div>
    </div>
  );
};

export default SimulacionPage;
