import React, { useState, ChangeEvent, FormEvent, useContext } from "react";
import "../../styles/RegisterPage.css";
import TitleWithIcon from "./TitleWithIcon";
import { cities } from "@/app/data/cities";
import { OperationContext } from "@/app/context/operation-provider";
import { Envio } from "@/app/types/envios";
import toast, { Toaster } from "react-hot-toast";

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
  const { saveShipmentData, shipments, saveShipmentBatch } =
    useContext(OperationContext);

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

  const [filteredOriginCities, setFilteredOriginCities] =
    useState<typeof cities>(cities);
  const [filteredDestinationCities, setFilteredDestinationCities] =
    useState<typeof cities>(cities);
  const [selectedFile, setSelectedFile] = useState<File | null>(null);
  const [showConfirmationPopup, setShowConfirmationPopup] =
    useState<boolean>(false);

  const handleFileChange = (event: ChangeEvent<HTMLInputElement>) => {
    const file = event.target.files ? event.target.files[0] : null;
    setSelectedFile(file);
  };

  const handleUploadConfirmed = async (): Promise<void> => {
    if (typeof window === "undefined") {
      return;
    }
    if (!selectedFile) {
      alert("No file selected. Please select a file and try again.");
      return;
    }

    const reader = new FileReader();

    reader.onload = (e: ProgressEvent<FileReader>) => {
      const text = e.target?.result as string;
      const lines = text.split(/\r?\n/);

      const newShipments = lines
        .map((line) => {
          const parts = line.split("-");

          if (parts.length === 5) {
            const [
              codigoIATAOrigen,
              idEnvio,
              fechaStr,
              horaStr,
              codigoIATADestinoPackage,
            ] = parts as [string, string, string, string, string];

            const year = fechaStr.substring(0, 4);
            const month = fechaStr.substring(4, 6);
            const day = fechaStr.substring(6, 8);
            const codigoIATADestino = codigoIATADestinoPackage.split(":")[0];
            const packageCount = codigoIATADestinoPackage.split(":")[1];
            const date = new Date(`${year}-${month}-${day}T${horaStr}:00`);
            const fechaHoraOrigen = date.toISOString().replace("Z", "");

            return {
              idEnvio,
              fechaHoraOrigen,
              zonaHorariaGMT: formData.originGMT, // Asumiendo que el GMT no varía por envío
              codigoIATAOrigen,
              codigoIATADestino,
              cantPaquetes: parseInt(packageCount, 10),
              paquetes: [],
            } as Envio;
          }
          return null;
        })
        .filter((envio): envio is Envio => envio !== null);

      saveShipmentBatch([...shipments, ...newShipments]);
    };

    reader.readAsText(selectedFile);
    setSelectedFile(null);
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
    if (!formData.firstName.trim()) newErrors.push("El nombre es obligatorio.");
    if (!formData.email.trim())
      newErrors.push("El correo electrónico es obligatorio.");
    if (!formData.originCity.trim())
      newErrors.push("La ciudad de origen es obligatoria.");
    if (!formData.destinationCity.trim())
      newErrors.push("La ciudad de destino es obligatoria.");
    if (formData.originCity === formData.destinationCity)
      newErrors.push(
        "Las ciudades de origen y destino no pueden ser las mismas."
      );
    if (!formData.packageCount.trim())
      newErrors.push("La cantidad de paquetes es obligatoria.");
    return newErrors;
  };

  const formatDateForBackend = (dateIsoString: string): string => {
    const date = new Date(dateIsoString);
    return date.toISOString().replace("Z", "").split(".")[0];
  };

  const handleFinalSubmit = () => {
    const envio: Envio = {
      idEnvio: "",
      fechaHoraOrigen: formatDateForBackend(new Date().toISOString()),
      zonaHorariaGMT: formData.originGMT,
      codigoIATAOrigen: formData.originCity,
      codigoIATADestino: formData.destinationCity,
      cantPaquetes: parseInt(formData.packageCount),
      paquetes: [],
    };
    console.log(envio.fechaHoraOrigen);
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
    toast.success("Pedido registrado con éxito");
    setShowConfirmationPopup(false); // Close confirmation popup
  };

  const handleSubmit = (e: FormEvent) => {
    e.preventDefault();
    const formErrors = validateForm();
    if (formErrors.length > 0) {
      formErrors.forEach((error) => {
        toast.error(error);
      });
      return;
    }
    setShowConfirmationPopup(true);
  };

  const handleEnviarPedidos = async () => {
    const peticion = { envios: shipments }; // Envía los envíos almacenados en el contexto
    try {
      const response = await fetch("http://localhost:8080/back/api/diario", {
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

  const handleDeselectFile = () => {
    setSelectedFile(null);
  };

  const triggerFileInput = () => {
    document.getElementById("fileInput")?.click();
  };

  return (
    <div className="register-shipment-container">
      <TitleWithIcon name="Registrar Pedido" icon="/icons/caja.png" />
      <Toaster position="top-right" reverseOrder={false} />
      <div
        style={{
          display: "flex",
          width: "100%",
          gap: "10px",
          paddingBottom: "12px",
        }}
      >
        <input
          type="file"
          id="fileInput"
          style={{ display: "none" }}
          onChange={handleFileChange}
          accept=".txt"
        />
        <button onClick={triggerFileInput} className="select-archive-button">
          Carga Masiva
        </button>
        <button
          onClick={handleDeselectFile}
          className="deselect-archive-button"
          disabled={selectedFile == null}
        >
          Cancelar
        </button>
        <button
          onClick={() => selectedFile && handleUploadConfirmed()}
          className="register-shipment-button"
          disabled={!selectedFile}
        >
          Registro Masivo
        </button>
        <button
          onClick={handleEnviarPedidos}
          className="register-shipment-button"
        >
          Enviar Pedidos
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
        <div></div>
        <div className="form-group">
          <button className="register-shipment-button" type="submit">
            Registrar Pedido
          </button>
        </div>
      </form>
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
