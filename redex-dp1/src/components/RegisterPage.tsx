// components/RegisterShipment.tsx
import React, { useState, ChangeEvent, FormEvent } from "react";
import "@/styles/RegisterPage.css";
import TitleWithIcon from "./TitleWithIcon";

interface FormData {
  firstName: string;
  lastName: string;
  email: string;
  phoneNumber: string;
  dniPassport: string;
  originCity: string;
  destinationCity: string;
  packageCount: string;
  contentDescription: string;
}

const RegisterPage: React.FC = () => {
  const [formData, setFormData] = useState<FormData>({
    firstName: "",
    lastName: "",
    email: "",
    phoneNumber: "",
    dniPassport: "",
    originCity: "",
    destinationCity: "",
    packageCount: "",
    contentDescription: "",
  });

  const handleChange = (
    e: ChangeEvent<HTMLInputElement | HTMLTextAreaElement>
  ) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
  };

  const [errors, setErrors] = useState<string[]>([]);

  const validateForm = () => {
    const newErrors = [];
    if (!formData.firstName.trim()) newErrors.push("First name is required.");
    if (!formData.email.trim()) newErrors.push("Email is required.");
    if (!formData.originCity.trim()) newErrors.push("Origin city is required.");
    if (!formData.destinationCity.trim())
      newErrors.push("Destination city is required.");
    if (!formData.packageCount.trim())
      newErrors.push("Package count is required.");
    return newErrors;
  };

  const handleSubmit = (e: FormEvent) => {
    e.preventDefault();
    const formErrors = validateForm();
    if (formErrors.length > 0) {
      setErrors(formErrors);
      return;
    }
    setErrors([]);
    console.log("Form Data:", formData);
    saveShipmentData(formData);
  };

  const saveShipmentData = async (data: FormData) => {
    try {
      const response = await fetch("http://localhost:8080/envio/save", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify(data),
      });
      const responseData = await response.json();
      console.log(responseData);
      alert("Shipment registered successfully!");
    } catch (error) {
      console.error("Failed to save shipment:", error);
      alert("Failed to register shipment.");
    }
  };

  return (
    <div>
      <TitleWithIcon name="Registrar Pedido" icon="/icons/caja.png" />
      <div className="register-shipment-container">
        <form onSubmit={handleSubmit}>
          <input
            type="text"
            name="firstName"
            placeholder="Nombres de cliente"
            value={formData.firstName}
            onChange={handleChange}
          />
          <input
            type="text"
            name="lastName"
            placeholder="Apellidos de cliente"
            value={formData.lastName}
            onChange={handleChange}
          />
          <input
            type="email"
            name="email"
            placeholder="Correo Electrónico"
            value={formData.email}
            onChange={handleChange}
          />
          <input
            type="text"
            name="phoneNumber"
            placeholder="Número Telefónico"
            value={formData.phoneNumber}
            onChange={handleChange}
          />
          <input
            type="text"
            name="dniPassport"
            placeholder="DNI/Pasaporte"
            value={formData.dniPassport}
            onChange={handleChange}
          />
          <input
            type="text"
            name="originCity"
            placeholder="Ciudad Origen"
            value={formData.originCity}
            onChange={handleChange}
          />
          <input
            type="text"
            name="destinationCity"
            placeholder="Ciudad Destino"
            value={formData.destinationCity}
            onChange={handleChange}
          />
          <input
            type="number"
            name="packageCount"
            placeholder="Cantidad de paquetes"
            value={formData.packageCount}
            onChange={handleChange}
          />
          <textarea
            name="contentDescription"
            placeholder="Descripción de contenido"
            value={formData.contentDescription}
            onChange={handleChange}
          ></textarea>
          <button className="register-shipment-button" type="submit">
            Registrar Pedido
          </button>
        </form>
      </div>
    </div>
  );
};

export default RegisterPage;
