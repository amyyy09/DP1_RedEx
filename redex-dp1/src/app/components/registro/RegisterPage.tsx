import React, { useState, ChangeEvent, FormEvent, useContext } from "react";
import "../../styles/RegisterPage.css";
import TitleWithIcon from "./TitleWithIcon";
import { cities } from "@/app/data/cities";
import { OperationContext } from "@/app/context/operation-provider";
import { Envio } from "@/app/types/envios";

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
  const { saveShipmentData, shipments } = useContext(OperationContext);

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

  const [errors, setErrors] = useState<string[]>([]);
  const [filteredOriginCities, setFilteredOriginCities] =
    useState<typeof cities>(cities);
  const [filteredDestinationCities, setFilteredDestinationCities] =
    useState<typeof cities>(cities);

  const handleChange = (
    e: ChangeEvent<HTMLInputElement | HTMLTextAreaElement | HTMLSelectElement>
  ) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
  };

  const handleCityChange = (
    e: ChangeEvent<HTMLInputElement>,
    type: "origin" | "destination"
  ) => {
    const value = e.target.value;
    if (type === "origin") {
      setFormData({ ...formData, originCity: value });
      setFilteredOriginCities(
        cities.filter((city) =>
          city.name.toLowerCase().includes(value.toLowerCase())
        )
      );
    } else {
      setFormData({ ...formData, destinationCity: value });
      setFilteredDestinationCities(
        cities.filter((city) =>
          city.name.toLowerCase().includes(value.toLowerCase())
        )
      );
    }
  };

  const handleCitySelect = (
    cityName: string,
    type: "origin" | "destination"
  ) => {
    const selectedCity = cities.find((city) => city.name === cityName);
    if (selectedCity) {
      if (type === "origin") {
        setFormData({ ...formData, originCity: selectedCity.code });
      } else {
        setFormData({ ...formData, destinationCity: selectedCity.code });
      }
    }
  };

  const validateForm = () => {
    const newErrors = [];
    if (!formData.firstName.trim()) newErrors.push("First name is required.");
    if (!formData.email.trim()) newErrors.push("Email is required.");
    if (!formData.originCity.trim()) newErrors.push("Origin city is required.");
    if (!formData.destinationCity.trim())
      newErrors.push("Destination city is required.");
    if (formData.originCity === formData.destinationCity)
      newErrors.push("Origin and destination cities cannot be the same.");
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

    const envio: Envio = {
      idEnvio: "",
      fechaHoraOrigen: new Date().toISOString(),
      zonaHorariaGMT: 0, // Ejemplo: zona horaria GMT
      codigoIATAOrigen: formData.originCity,
      codigoIATADestino: formData.destinationCity,
      cantPaquetes: parseInt(formData.packageCount),
      paquetes: [], // Puedes dejarlo vacío si no tienes la información de los paquetes
    };
    console.log(envio);
    saveShipmentData(envio);
  };

  const handleEnviarPedidos = async () => {
    const peticion = { envios: shipments }; // Envía los envíos almacenados en el contexto
    try {
      const response = await fetch("http://localhost:8080/api/diario", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify(peticion),
      });
      const data = await response.json();
      console.log("Respuesta del servidor:", data);
    } catch (error) {
      console.error("Error al enviar los pedidos:", error);
    }
  };

  const handleMostrarPedidos = () => {
    console.log("Lista de pedidos:", formData);
  };

  return (
    <div className="register-shipment-container">
      <div style={{ display: "flex", justifyContent: "space-between" }}>
        <TitleWithIcon name="Registrar Pedido" icon="/icons/caja.png" />
        <div className="error-messages">
          <button
            className="register-shipment-button"
            onClick={handleEnviarPedidos}
          >
            Enviar Pedidos
          </button>
        </div>
      </div>

      <form onSubmit={handleSubmit}>
        <div className="form-group">
          <label htmlFor="firstName">
            Nombres de cliente <span className="required">*</span>
          </label>
          <input
            type="text"
            id="firstName"
            name="firstName"
            placeholder="Nombres de cliente"
            value={formData.firstName}
            onChange={handleChange}
          />
        </div>
        <div className="form-group">
          <label htmlFor="lastName">
            Apellidos de cliente <span className="required">*</span>
          </label>
          <input
            type="text"
            id="lastName"
            name="lastName"
            placeholder="Apellidos de cliente"
            value={formData.lastName}
            onChange={handleChange}
          />
        </div>
        <div className="form-group">
          <label htmlFor="email">
            Correo Electrónico <span className="required">*</span>
          </label>
          <input
            type="email"
            id="email"
            name="email"
            placeholder="Correo Electrónico"
            value={formData.email}
            onChange={handleChange}
          />
        </div>
        <div className="form-group">
          <label htmlFor="phoneNumber">
            Número Telefónico <span className="required">*</span>
          </label>
          <input
            type="text"
            id="phoneNumber"
            name="phoneNumber"
            placeholder="Número Telefónico"
            value={formData.phoneNumber}
            onChange={handleChange}
          />
        </div>
        <div className="form-group">
          <label htmlFor="dniPassport">
            DNI/Pasaporte <span className="required">*</span>
          </label>
          <input
            type="text"
            id="dniPassport"
            name="dniPassport"
            placeholder="DNI/Pasaporte"
            value={formData.dniPassport}
            onChange={handleChange}
          />
        </div>
        <div className="form-group">
          <label htmlFor="originCity">
            Ciudad Origen <span className="required">*</span>
          </label>
          <input
            type="text"
            id="originCity"
            name="originCity"
            placeholder="Ciudad Origen"
            value={formData.originCity}
            onChange={(e) => handleCityChange(e, "origin")}
            list="origin-city-options"
          />
          <datalist id="origin-city-options">
            {filteredOriginCities.map((city) => (
              <option
                key={city.code}
                value={city.name}
                onClick={() => handleCitySelect(city.name, "origin")}
              />
            ))}
          </datalist>
        </div>
        <div className="form-group">
          <label htmlFor="destinationCity">
            Ciudad Destino <span className="required">*</span>
          </label>
          <input
            type="text"
            id="destinationCity"
            name="destinationCity"
            placeholder="Ciudad Destino"
            value={formData.destinationCity}
            onChange={(e) => handleCityChange(e, "destination")}
            list="destination-city-options"
          />
          <datalist id="destination-city-options">
            {filteredDestinationCities.map((city) => (
              <option
                key={city.code}
                value={city.name}
                onClick={() => handleCitySelect(city.name, "destination")}
              />
            ))}
          </datalist>
        </div>
        <div className="form-group">
          <label htmlFor="packageCount">
            Cantidad de paquetes <span className="required">*</span>
          </label>
          <input
            type="number"
            id="packageCount"
            name="packageCount"
            placeholder="Cantidad de paquetes"
            value={formData.packageCount}
            onChange={handleChange}
          />
        </div>
        <div className="form-group">
          <label htmlFor="contentDescription">Descripción de contenido</label>
          <textarea
            id="contentDescription"
            name="contentDescription"
            placeholder="Descripción de contenido"
            value={formData.contentDescription}
            onChange={handleChange}
          ></textarea>
        </div>
        <button className="register-shipment-button" type="submit">
          Registrar Pedido
        </button>
        {errors.length > 0 && (
          <div className="error-messages">
            {errors.map((error, index) => (
              <p key={index} className="error-text">
                {error}
              </p>
            ))}
          </div>
        )}
      </form>
    </div>
  );
};

export default RegisterPage;
