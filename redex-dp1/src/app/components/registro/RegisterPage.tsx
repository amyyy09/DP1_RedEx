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
  originCityName: string;
  destinationCityName: string;
  originGMT: number;
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
    originCityName: "",
    destinationCityName: "",
    originGMT: 0,
    packageCount: "",
    contentDescription: "",
  });

  const [errors, setErrors] = useState<string[]>([]);
  const [popupMessage, setPopupMessage] = useState<string>("");
  const [showPopup, setShowPopup] = useState<boolean>(false);
  const [filteredOriginCities, setFilteredOriginCities] =
    useState<typeof cities>(cities);
  const [filteredDestinationCities, setFilteredDestinationCities] =
    useState<typeof cities>(cities);
  const [selectedFile, setSelectedFile] = useState<File | null>(null);
  const [showConfirmationPopup, setShowConfirmationPopup] =
    useState<boolean>(false);
  const [showUploadConfirmationPopup, setShowUploadConfirmationPopup] =
    useState<boolean>(false);

  const handleFileChange = (event: ChangeEvent<HTMLInputElement>) => {
    const file = event.target.files ? event.target.files[0] : null;
    setSelectedFile(file);
    if (file) {
      setShowUploadConfirmationPopup(true); // Show confirmation when file is selected
    }
  };

  const handleUploadConfirmed = async () => {
    if (!selectedFile) {
      alert("No file selected. Please select a file and try again.");
      return;
    }

    const formData = new FormData();
    formData.append("file", selectedFile);
    setSelectedFile(null); // Reset file selection after upload
    setShowUploadConfirmationPopup(false); // Close the confirmation popup
  };

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
    const filteredCities = cities.filter((city) =>
      city.name.toLowerCase().includes(value.toLowerCase())
    );

    if (type === "origin") {
      setFilteredOriginCities(filteredCities);
      const selectedCity = cities.find(
        (city) => city.name.toLowerCase() === value.toLowerCase()
      );
      setFormData((formData) => ({
        ...formData,
        originCityName: value,
        originCity: selectedCity ? selectedCity.code : "",
        originGMT: selectedCity ? selectedCity.GMT : 0,
      }));
    } else {
      setFilteredDestinationCities(filteredCities);
      const selectedCity = cities.find(
        (city) => city.name.toLowerCase() === value.toLowerCase()
      );
      setFormData((formData) => ({
        ...formData,
        destinationCityName: value,
        destinationCity: selectedCity ? selectedCity.code : "",
      }));
    }
  };

  const handleCitySelect = (
    cityName: string,
    type: "origin" | "destination"
  ) => {
    const selectedCity = cities.find((city) => city.name === cityName);
    if (selectedCity) {
      if (type === "origin") {
        setFormData({
          ...formData,
          originCity: selectedCity.code,
          originCityName: selectedCity.name,
          originGMT: selectedCity.GMT,
        });
      } else {
        setFormData({
          ...formData,
          destinationCity: selectedCity.code,
          destinationCityName: selectedCity.name,
        });
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

  const handleFinalSubmit = () => {
    const envio: Envio = {
      idEnvio: "",
      fechaHoraOrigen: new Date().toISOString(),
      zonaHorariaGMT: formData.originGMT,
      codigoIATAOrigen: formData.originCity,
      codigoIATADestino: formData.destinationCity,
      cantPaquetes: parseInt(formData.packageCount),
      paquetes: [],
    };
    console.log(envio);
    saveShipmentData(envio);

    setFormData({
      firstName: "",
      lastName: "",
      email: "",
      phoneNumber: "",
      dniPassport: "",
      originCity: "",
      destinationCity: "",
      originCityName: "",
      destinationCityName: "",
      originGMT: 0,
      packageCount: "",
      contentDescription: "",
    });
    setPopupMessage("Pedido registrado con éxito.");
    setShowPopup(true);
    setShowConfirmationPopup(false); // Close confirmation popup
  };

  const handleSubmit = (e: FormEvent) => {
    e.preventDefault();
    const formErrors = validateForm();
    if (formErrors.length > 0) {
      setErrors(formErrors);
      return;
    }
    setErrors([]);
    setShowConfirmationPopup(true); // Show confirmation popup instead of submitting directly
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

  const closePopup = () => {
    setShowPopup(false);
  };

  const handleDeselectFile = () => {
    setSelectedFile(null);
    setShowUploadConfirmationPopup(false); // Show confirmation when file is selected
  };

  return (
    <div className="register-shipment-container">
      <TitleWithIcon name="Registrar Pedido" icon="/icons/caja.png" />

      <div style={{ display: "flex", gap: "10px", alignItems: "center" }}>
        <input
          type="file"
          id="fileInput"
          style={{ display: "none" }}
          onChange={handleFileChange}
          accept=".txt"
        />
        <div>
          <label htmlFor="fileInput" className="register-shipment-button">
            Carga Masiva
          </label>
          <button
            onClick={handleDeselectFile}
            className="register-shipment-button"
            style={{ marginLeft: "10px" }}
            disabled={selectedFile == null}
          >
            X
          </button>
        </div>
        <button
          onClick={() => selectedFile && handleUploadConfirmed()}
          className="register-shipment-button"
          disabled={!selectedFile}
        >
          Registro Masivo
        </button>
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
            value={formData.originCityName}
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
            value={formData.destinationCityName}
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
      {showPopup && (
        <div className="popup">
          <div className="popup-content">
            <p>{popupMessage}</p>
            <button onClick={closePopup}>Cerrar</button>
          </div>
        </div>
      )}
      {showConfirmationPopup && (
        <div className="popup">
          <div className="popup-content">
            <p>¿Está seguro que desea registrar el pedido?</p>
            <button onClick={handleFinalSubmit}>Confirmar</button>
            <button onClick={() => setShowConfirmationPopup(false)}>
              Cancelar
            </button>
          </div>
        </div>
      )}
    </div>
  );
};

export default RegisterPage;
