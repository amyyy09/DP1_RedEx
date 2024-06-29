import React, { useState, useRef, useEffect } from "react";
import "../../styles/Topbar.css";

interface TopbarProps {
  onSearch: (id: string) => void;
  errorMessage?: string;
}

const Topbar: React.FC<TopbarProps> = ({ onSearch, errorMessage }) => {
  const [searchTerm, setSearchTerm] = useState("");
  const [isPopupOpen, setIsPopupOpen] = useState(false);
  const searchButtonRef = useRef<HTMLDivElement>(null);
  const [popupStyle, setPopupStyle] = useState<React.CSSProperties>({});

  const handleSearch = () => {
    if (searchTerm.trim() !== "") {
      onSearch(searchTerm);
      setIsPopupOpen(false); // Close the popup after search
    }
  };

  const handleKeyDown = (event: React.KeyboardEvent<HTMLInputElement>) => {
    if (event.key === "Enter") {
      handleSearch();
    }
  };

  const openPopup = () => {
    if (searchButtonRef.current) {
      const rect = searchButtonRef.current.getBoundingClientRect();
      setPopupStyle({
        position: "absolute",
        top: `${rect.bottom + window.scrollY}px`,
        left: `${rect.left + window.scrollX}px`,
      });
    }
    setIsPopupOpen(true);
  };

  const closePopup = () => {
    setIsPopupOpen(false);
  };

  return (
    <div className="topbar">
      <div className="topbar-item-log">
        <img src="./redex.png" alt="Logo" />
      </div>
      <div 
        className="topbar-item" 
        onClick={openPopup} 
        style={{ cursor: 'pointer' }}
        ref={searchButtonRef}
      >
        <img src="./icons/buscar.png" alt="Buscar Paquete" />
        <span>Buscar Paquete</span>
      </div>
      <div className="topbar-item">
        <img src="./icons/caja.png" alt="Estado Envíos" />
        <span>Estado Envíos</span>
      </div>
      <div className="topbar-item">
        <img src="./icons/rastro.png" alt="Rastrear Vuelo" />
        <span>Rastrear Vuelo</span>
      </div>
      <div className="topbar-item">
        <img src="./icons/cajas.png" alt="Almacenes" />
        <span>Almacenes</span>
      </div>
      {isPopupOpen && (
        <div className="popup-topbar" style={popupStyle}>
          <div className="popup-header-topbar">
            <h2 style={{ color: 'black' }}>Buscar Paquete</h2>
            <button onClick={closePopup} className="close-button-topbar" style={{ color: 'black' }}>&times;</button>
          </div>
          <div className="popup-content-topbar">
            <input
              type="text"
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
              onKeyDown={handleKeyDown}
              placeholder="ID del paquete"
              className="search-input-topbar"
              style={{ color: 'black' }}
            />
            <button onClick={handleSearch} className="search-button-topbar">APLICAR</button>
          </div>
        </div>
      )}
    </div>
  );
};

export default Topbar;
