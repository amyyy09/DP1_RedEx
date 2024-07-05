import React, { useState, useRef, useEffect } from "react";
import "../../styles/Topbar.css";

interface TopbarProps {
  onSearch: (id: string) => void;
  envioSearch: (id: string) => void;
  vueloSearch: (id: string) => void;
  errorMessage?: string;
}

const Topbar: React.FC<TopbarProps> = ({
  onSearch,
  envioSearch,
  vueloSearch,
  errorMessage,
}) => {
  const [searchTerm, setSearchTerm] = useState("");
  const [isPopupOpen, setIsPopupOpen] = useState(false);
  const searchButtonRef = useRef<HTMLDivElement>(null);
  const envioButtonRef = useRef<HTMLDivElement>(null);
  const vueloButtonRef = useRef<HTMLDivElement>(null);
  const [popupStyle, setPopupStyle] = useState<React.CSSProperties>({});
  const [popupEnvio, setPopupEnvio] = useState(false);
  const [popupVuelo, setPopupVuelo] = useState(false);

  const handleSearch = () => {
    if (searchTerm.trim() !== "") {
      if (isPopupOpen) {
        onSearch(searchTerm);
        setIsPopupOpen(false); // Close the popup after search
      } else if (popupEnvio) {
        envioSearch(searchTerm);
        setPopupEnvio(false); // Close the popup after search
      } else if (popupVuelo) {
        vueloSearch(searchTerm);
        setPopupVuelo(false); // Close the popup after search
      }
      setSearchTerm(""); // Clear the search term after search
    }
  };

  const handleKeyDown = (event: React.KeyboardEvent<HTMLInputElement>) => {
    if (event.key === "Enter") {
      handleSearch();
    }
  };

  const openPopup = (tipo: string) => {
    let rect: DOMRect | null = null;
    // console.log(tipo);
    // console.log(searchButtonRef);
    if (tipo === "paquete" && searchButtonRef.current) {
      rect = searchButtonRef.current.getBoundingClientRect();
      // console.log(rect);
      setIsPopupOpen(true);
      setPopupEnvio(false);
      setPopupVuelo(false);
    } else if (tipo === "envio" && envioButtonRef.current) {
      rect = envioButtonRef.current.getBoundingClientRect();
      setPopupEnvio(true);
      setIsPopupOpen(false);
      setPopupVuelo(false);
    } else if (tipo === "vuelo" && vueloButtonRef.current) {
      rect = vueloButtonRef.current.getBoundingClientRect();
      setPopupVuelo(true);
      setIsPopupOpen(false);
      setPopupEnvio(false);
    }

    if (rect) {
      setPopupStyle({
        position: "absolute",
        top: `${rect.bottom + window.scrollY}px`,
        left: `${rect.left + window.scrollX}px`,
      });
    }
    setSearchTerm(""); // Clear the search term when opening the popup
  };

  const closePopup = () => {
    if (isPopupOpen) {
      setIsPopupOpen(false);
    } else if (popupEnvio) {
      setPopupEnvio(false);
    } else if (popupVuelo) {
      setPopupVuelo(false);
    }
  };

  return (
    <div className="topbar">
      <div className="topbar-item-log">
        <img src="./redex.png" alt="Logo" />
      </div>
      <div
        className="topbar-item"
        onClick={() => openPopup("paquete")}
        style={{ cursor: "pointer" }}
        ref={searchButtonRef}
      >
        <img src="./icons/buscar.png" alt="Buscar Paquete" />
        <span>Buscar Paquete</span>
      </div>
      <div
        className="topbar-item"
        onClick={() => openPopup("envio")}
        style={{ cursor: "pointer" }}
        ref={envioButtonRef}
      >
        <img src="./icons/caja.png" alt="Buscar Envíos" />
        <span>Estado Envíos</span>
      </div>
      {/* <div className="topbar-item">
        <img src="./icons/rastro.png" alt="Rastrear Vuelo" />
        <span>Rastrear Vuelo</span>
      </div>
      <div className="topbar-item">
        <img src="./icons/cajas.png" alt="Almacenes" />
        <span>Almacenes</span>
      </div> */}
      <div
        className="topbar-item"
        onClick={() => openPopup("vuelo")}
        style={{ cursor: "pointer" }}
        ref={vueloButtonRef}
      >
        <img src="./icons/modo-vuelo.png" alt="Buscar Vuelo" />
        <span>Buscar Vuelo</span>
      </div>
      {isPopupOpen && (
        <div className="popup-topbar" style={popupStyle}>
          <div className="popup-header-topbar">
            <h2 style={{ color: "black" }}>Buscar Paquete</h2>
            <button
              onClick={closePopup}
              className="close-button-topbar"
              style={{ color: "black" }}
            >
              &times;
            </button>
          </div>
          <div className="popup-content-topbar">
            <input
              type="text"
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
              onKeyDown={handleKeyDown}
              placeholder="ID del paquete"
              className="search-input-topbar"
              style={{ color: "black" }}
            />
            <button onClick={handleSearch} className="search-button-topbar">
              APLICAR
            </button>
          </div>
        </div>
      )}
      {popupEnvio && (
        <div className="popup-topbar" style={popupStyle}>
          <div className="popup-header-topbar">
            <h2 style={{ color: "black" }}>Buscar Envío</h2>
            <button
              onClick={closePopup}
              className="close-button-topbar"
              style={{ color: "black" }}
            >
              &times;
            </button>
          </div>
          <div className="popup-content-topbar">
            <input
              type="text"
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
              onKeyDown={handleKeyDown}
              placeholder="ID del envío"
              className="search-input-topbar"
              style={{ color: "black" }}
            />
            <button onClick={handleSearch} className="search-button-topbar">
              APLICAR
            </button>
          </div>
        </div>
      )}
      {popupVuelo && (
        <div className="popup-topbar" style={popupStyle}>
          <div className="popup-header-topbar">
            <h2 style={{ color: "black" }}>Buscar Vuelo</h2>
            <button
              onClick={closePopup}
              className="close-button-topbar"
              style={{ color: "black" }}
            >
              &times;
            </button>
          </div>
          <div className="popup-content-topbar">
            <input
              type="text"
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
              onKeyDown={handleKeyDown}
              placeholder="ID del vuelo"
              className="search-input-topbar"
              style={{ color: "black" }}
            />
            <button onClick={handleSearch} className="search-button-topbar">
              APLICAR
            </button>
          </div>
        </div>
      )}
    </div>
  );
};

export default Topbar;
