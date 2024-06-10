// components/CurrentTimeDisplay.tsx
"use client";

import React, { useState, useEffect } from "react";
import "../../styles/CurrentTimeDisplay.css"; // Add styles for the time display

const CurrentTimeDisplay: React.FC = () => {
    const [currentTime, setCurrentTime] = useState(new Date());

    useEffect(() => {
        const intervalId = setInterval(() => {
            setCurrentTime(new Date());
        }, 1000);

        return () => clearInterval(intervalId);
    }, []);

    const formatTime = (date: Date) => {
        return date.toLocaleString(undefined, {
            day: "2-digit",
            month: "2-digit",
            year: "numeric",
            hour: "2-digit",
            minute: "2-digit",
            second: "2-digit",
            hour12: false,
        });
    };

    return (
        <div className="current-time-display">
            {formatTime(currentTime)}
        </div>
    );
};

export default CurrentTimeDisplay;
