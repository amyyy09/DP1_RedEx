import React from "react";
import "../../styles/Notification.css";

interface NotificationProps {
  message: string;
  onClose: () => void;
}

const Notification: React.FC<NotificationProps> = ({ message, onClose }) => {
  return (
    <div className="notification">
      <p>{message}</p>
      <button onClick={onClose}>Cerrar</button>
    </div>
  );
};

export default Notification;
