import React, { useState, ChangeEvent, FormEvent, useContext } from "react";
import "../../styles/RegisterPage.css";
import TitleWithIcon from "./TitleWithIcon";
import { cities } from "@/app/data/cities";
import { OperationContext } from "@/app/context/operation-provider";
import { Envio } from "@/app/types/envios";
import toast, { Toaster } from "react-hot-toast";
import Modal from "react-modal";
import { convertDateTimeToArray } from "@/app/utils/timeHelper";

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
  startDate: string;
  startTime: string;
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
    startDate: "2024-07-22",
    startTime: "06:00",
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
    if (file && file.type === "text/plain") {
      setSelectedFile(file);
    } else {
      toast.error(
        "Formato de archivo no válido. Seleccione un archivo de texto."
      );
    }
    event.target.value = "";
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
      try {
        const text = e.target?.result as string;
        const lines = text.split(/\r?\n/);

        let shipmentCounter = 0;
        const newShipments = lines
          .map((line) => {
            try {
              const parts = line.split("-");
              if (parts.length === 5) {
                const [
                  codigoIATAOrigen, // idEnvio is not needed anymore
                  correlativo,
                  fechaStr,
                  horaStr,
                  codigoIATADestinoPackage,
                ] = parts as [string, string, string, string, string];

                const year = fechaStr.substring(0, 4);
                const month = fechaStr.substring(4, 6);
                const day = fechaStr.substring(6, 8);
                const codigoIATADestino =
                  codigoIATADestinoPackage.split(":")[0];
                const packageCount = codigoIATADestinoPackage.split(":")[1];
                const fechaHoraOrigen = formatDateForBackend(
                  new Date(
                    `${year}-${month}-${day}T${horaStr}:00`
                  ).toISOString()
                );
                const idEnvio = `${codigoIATAOrigen}${correlativo}`;

                // shipmentCounter += 1;
                // const idEnvio = generateUniqueIdMasiv(
                //   codigoIATAOrigen,
                //   codigoIATADestino,
                //   fechaHoraOrigen,
                //   shipmentCounter
                // );

                const paquetes = [];
                const hora = convertDateTimeToArray(fechaHoraOrigen);

                for (let i = 0; i < parseInt(packageCount, 10); i++) {
                  paquetes.push({
                    id: `${idEnvio}-${i + 1}`,
                    status: 0,
                    horaInicio: hora,
                    aeropuertoOrigen: codigoIATAOrigen,
                    aeropuertoDestino: codigoIATADestino,
                    ruta: "No asignada",
                    ubicacion: codigoIATAOrigen,
                  });
                }

                return {
                  idEnvio,
                  fechaHoraOrigen,
                  zonaHorariaGMT: formData.originGMT,
                  codigoIATAOrigen,
                  codigoIATADestino,
                  cantPaquetes: parseInt(packageCount, 10),
                  paquetes: paquetes,
                } as Envio;
              } else {
                throw new Error("Formato incorrecto");
              }
            } catch (error) {
              toast.error("Error en el formato del archivo");
              return null;
            }
          })
          .filter((envio): envio is Envio => envio !== null);

        saveShipmentBatch([...shipments, ...newShipments]);
        console.log("Envíos:", newShipments);
        toast.success("Registro por Archivo Exitoso!");
      } catch (error) {
        toast.error("Error al procesar el archivo");
      }
    };

    try {
      reader.readAsText(selectedFile);
      setSelectedFile(null);
    } catch (error) {
      toast.error("Error al leer el archivo");
    }
  };

  const generateUniqueIdMasiv = (
    codigoIATAOrigen: string,
    codigoIATADestino: string,
    fechaHoraOrigen: string,
    counter: number
  ): string => {
    return `${codigoIATAOrigen}-${counter}`;
    // return `${codigoIATAOrigen}-${codigoIATADestino}-${fechaHoraOrigen}-${counter}`;
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
    if (!formData.startDate.trim())
      newErrors.push("La fecha de envío es obligatoria.");
    if (!formData.startTime.trim())
      newErrors.push("La hora de envío es obligatoria.");
    if(!formData.dniPassport.trim())
      newErrors.push("El N° correlativo es obligatorio.");
    return newErrors;
  };

  const formatDateForBackend = (dateIsoString: string): string => {
    const date = new Date(dateIsoString);
    return date.toISOString().replace("Z", "").split(".")[0];
  };

  const handleFinalSubmit = () => {
    const envio: Envio = {
      idEnvio: "",
      fechaHoraOrigen: `${formData.startDate}T${formData.startTime}:00`,
      zonaHorariaGMT: formData.originGMT,
      codigoIATAOrigen: formData.originCity,
      codigoIATADestino: formData.destinationCity,
      cantPaquetes: parseInt(formData.packageCount),
      paquetes: [],
    };

    envio.idEnvio = `${envio.codigoIATAOrigen}${formData.dniPassport}`;

    console.log("Envío", envio);

    const hora = convertDateTimeToArray(envio.fechaHoraOrigen);
    // create paquetes array
    for (let i = 0; i < envio.cantPaquetes; i++) {
      envio.paquetes.push({
        id: `${envio.idEnvio}-${i + 1}`,
        status: 0,
        horaInicio: hora,
        aeropuertoOrigen: envio.codigoIATAOrigen,
        aeropuertoDestino: envio.codigoIATADestino,
        ruta: "No asignada",
        ubicacion: envio.codigoIATAOrigen,
      });
    }

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
      startDate: "2024-07-22",
      startTime: "12:00",
    });
    toast.success(
      "Envío registrado con éxito. El identificador es: " + envio.idEnvio
    );
    setShowConfirmationPopup(false);
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
    const peticion = { envios: shipments };
    try {
      const response = await fetch(`${process.env.BACKEND_URL}diario`, {
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

  const generateUniqueId = (envio: Envio): string => {
    const { codigoIATAOrigen, codigoIATADestino, fechaHoraOrigen } = envio;
    const hashString = `${codigoIATAOrigen}-${fechaHoraOrigen}`;
    let hash = 0;

    for (let i = 0; i < hashString.length; i++) {
      const chr = hashString.charCodeAt(i);
      hash = (hash << 5) - hash + chr;
      hash |= 0;
    }

    return hashString;
  };

  return (
    <div className="register-shipment-container">
      <TitleWithIcon name="Registrar Envío" icon="/icons/caja.png" />
      <Toaster position="top-right" reverseOrder={false} />
      <div
        style={{
          display: "flex",
          width: "100%",
          gap: "10px",
          paddingBottom: "10px",
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
          Cargar por Archivo
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
          Registro por Archivo
        </button>
        {/* <button
          onClick={handleEnviarPedidos}
          className="register-shipment-button"
        >
          Enviar Envíos
        </button> */}
      </div>
      <form onSubmit={handleSubmit}>
        <div className="form-group">
          <label htmlFor="firstName">Nombres de cliente</label>
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
          <label htmlFor="lastName">Apellidos de cliente</label>
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
          <label htmlFor="email">Correo Electrónico</label>
          <input
            type="email"
            id="email"
            name="email"
            placeholder="Correo Electrónico"
            value={formData.email}
            onChange={handleChange}
          />
        </div>
        {/*<div className="form-group">
          <label htmlFor="phoneNumber">Número Telefónico</label>
          <input
            type="text"
            id="phoneNumber"
            name="phoneNumber"
            placeholder="Número Telefónico"
            value={formData.phoneNumber}
            onChange={handleChange}
          />
        </div>*/}
        <div className="form-group">
          <label htmlFor="dniPassport">
            N° Correlativo <span className="required">*</span>
          </label>
          <input
            type="number"
            id="dniPassport"
            name="dniPassport"
            placeholder="N° correlativo del envío"
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
          <input
            type="text"
            id="contentDescription"
            name="contentDescription"
            placeholder="Descripción de contenido"
            value={formData.contentDescription}
            onChange={handleChange}
          />
        </div>
        <div className="form-group">
          <label htmlFor="startDate">
            Fecha de Envio <span className="required">*</span>
          </label>
          <input
            type="date"
            id="startDate"
            name="startDate"
            value={formData.startDate}
            onChange={handleChange}
          />
        </div>
        <div className="form-group">
          <label htmlFor="startTime">
            Hora de Envio <span className="required">*</span>
          </label>
          <input
            type="time"
            id="startTime"
            name="startTime"
            value={formData.startTime}
            onChange={handleChange}
          />
        </div>
        <div className="form-group">
          <button className="register-shipment-button" type="submit">
            Registrar Envío
          </button>
        </div>
      </form>
      <Modal
        isOpen={showConfirmationPopup}
        onRequestClose={() => setShowConfirmationPopup(false)}
        contentLabel="Confirmación de Registro"
        className="Modal"
        overlayClassName="Overlay"
        ariaHideApp={false}
      >
        <label>¿Está seguro que desea registrar el envío?</label>
        <div
          style={{
            display: "flex",
            width: "100%",
            gap: "10px",
            paddingBottom: "6px",
            paddingTop: "12px",
            alignItems: "center",
            justifyContent: "center",
          }}
        >
          <button onClick={handleFinalSubmit} className="select-archive-button">
            Confirmar
          </button>
          <button
            onClick={() => setShowConfirmationPopup(false)}
            className="deselect-archive-button"
          >
            Cancelar
          </button>
        </div>
      </Modal>
    </div>
  );
};

export default RegisterPage;
