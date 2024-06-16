import React, { useState, useRef, useEffect } from "react";
import "../../styles/Topbar.css";

const Topbar: React.FC<{ onSearch: (id: string) => void }> = ({ onSearch }) => {
  const [searchTerm, setSearchTerm] = useState("");
  const [isPopupOpen, setIsPopupOpen] = useState(false);
  const searchButtonRef = useRef<HTMLDivElement>(null);
  const [popupStyle, setPopupStyle] = useState<React.CSSProperties>({});

  const handleSearch = () => {
    onSearch(searchTerm);
    setIsPopupOpen(false); // Close the popup after search
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
        <div className="popup" style={popupStyle}>
          <div className="popup-header">
            <h2 style={{ color: 'black' }}>Buscar Paquete</h2>
            <button onClick={closePopup} className="close-button" style={{ color: 'black' }}>&times;</button>
          </div>
          <div className="popup-content">
            <input
              type="text"
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
              placeholder="ID del paquete"
              className="search-input"
              style={{ color: 'black' }}
            />
            <button onClick={handleSearch} className="search-button">APLICAR</button>
          </div>
        </div>
      )}
    </div>
  );
};

export default Topbar;
