// components/CurrentTimeDisplay.tsx
"use client";

import React, { useState, useEffect, use, useRef } from "react";
import "../../styles/CurrentTimeDisplay.css"; // Add styles for the time display

interface CurrentTimeDisplayProps {
    startTime: any;
  }

const CurrentTimeDisplay: React.FC<CurrentTimeDisplayProps> = ({ startTime }) => {
    const [currentTime, setCurrentTime] = useState<string | null>(null);

    useEffect(() => {
        if (startTime === null) {
            return; // Don't do anything if the start time is not set
        }

        let customDate = new Date();

        const current = new Date();
        console.log("Start time:", startTime); // "2021-07-22T06:00:00.000Z"
        const start = new Date(startTime);
        console.log("Start in props:", start);
        console.log("Current time:", current);

        // add to customDate the difference between the current time and the start time
        customDate.setMonth(6);
        customDate.setDate(22);
        customDate.setFullYear(2024);
        customDate.setHours(6);
        customDate.setMinutes(current.getMinutes() - start.getMinutes());

        const updateCurrentTime = () => {
            customDate = new Date(customDate.getTime() + 1000); // Increment by one second
            const formattedTime = customDate.toLocaleString(undefined, {
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
    
        updateCurrentTime(); // Update immediately
        const intervalId = setInterval(updateCurrentTime, 1000); // Then update every second
    
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