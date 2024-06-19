// components/CurrentTimeDisplay.tsx
"use client";

import React, { useState, useEffect } from "react";
import "../../styles/CurrentTimeDisplay.css"; // Add styles for the time display

const CurrentTimeDisplay: React.FC = () => {
    const [currentTime, setCurrentTime] = useState<string | null>(null);

    useEffect(() => {
        const updateCurrentTime = () => {
            const now = new Date();
            const formattedTime = now.toLocaleString(undefined, {
                day: "2-digit",
                month: "2-digit",
                year: "numeric",
                hour: "2-digit",
                minute: "2-digit",
                second: "2-digit",
                hour12: false,
            });
            setCurrentTime(formattedTime);
        };

        updateCurrentTime();
        const intervalId = setInterval(updateCurrentTime, 1000);

        return () => clearInterval(intervalId);
    }, []);

    if (currentTime === null) {
        return null; // Prevent server-side rendering mismatch by not rendering until the time is set
    }

    return (
        <div className="current-time-display">
            {currentTime}
        </div>
    );
};

export default CurrentTimeDisplay;