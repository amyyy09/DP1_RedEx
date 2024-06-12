// components/modal/EndModal.tsx

import React from "react";

interface EndModalProps {
  onClose: () => void;
  simulatedStartDate: string;
  simulatedStartHour: string;
  simulatedEndDate: string;
}

const EndModal: React.FC<EndModalProps> = ({
  onClose,
  simulatedStartDate,
  simulatedStartHour,
  simulatedEndDate,
}) => {
  const date = new Date(simulatedEndDate);
  const hours = date.getHours().toString().padStart(2, "0");
  const minutes = date.getMinutes().toString().padStart(2, "0");
  const timeString = `${hours}:${minutes}`;

  const endDate = date.toLocaleDateString('en', {
    month: "2-digit",
    day: "2-digit",
    year: "numeric",
  });

  return (
    <div className="modal-overlay">
      <div className="modal-content">
        <div className="modal-header">
          <h2>Fin de la simulación</h2>
          <button className="close-button" onClick={onClose}>
            &times;
          </button>
        </div>
        <div className="modal-body">
          <p>
            La simulación inició el{" "}
            <strong>
              {new Date(simulatedStartDate).toLocaleDateString(undefined, {
                day: "2-digit",
                month: "2-digit",
                year: "numeric",
              })}
            </strong>{" "}
            a las <strong>{simulatedStartHour}</strong>.
          </p>
          <p>
            La simulación finalizó el <strong>{endDate}</strong> a las{" "}
            <strong>{timeString}</strong>.
          </p>
        </div>
      </div>
    </div>
  );
};

export default EndModal;
