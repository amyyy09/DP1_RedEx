// components/CurrentTimeDisplay.tsx
"use client";

import React, { useState, useEffect, useContext } from "react";
import "../../styles/CurrentTimeDisplay.css"; // Add styles for the time display
import { OperationContext } from "@/app/context/operation-provider";

interface CurrentTimeDisplayProps {
  startTime: any;
}

const CurrentTimeDisplay: React.FC<CurrentTimeDisplayProps> = ({
  startTime,
}) => {
  const [currentTime, setCurrentTime] = useState<string | null>(null);

  const { referenceTime, referenceRef } = useContext(OperationContext);

  useEffect(() => {
    if (startTime === null) {
      return; // Don't do anything if the start time is not set
    }

    const updateCurrentTime = () => {
      let customDate = referenceTime ? new Date(referenceTime) : referenceRef.current ? new Date(referenceRef.current): null ;
      console.log("referenceTime in display:", referenceTime);
      console.log("referenceRef in display:", referenceRef.current);

      if (customDate === null) {
        return; // Don't do anything if the reference time is not set
      }

      const current = new Date();
    //   console.log("Start time:", startTime); // "2021-07-22T06:00:00.000Z"
      const start = new Date(startTime);
    //   console.log("Start in props:", start);
    //   console.log("Current time:", current);

    //   console.log("Custom date:", customDate);

      // add to customDate the difference between the current time and the start time
      customDate.setMinutes(
        customDate.getMinutes() + current.getMinutes() - start.getMinutes()
      );

      customDate.setSeconds(
        customDate.getSeconds() + current.getSeconds() - start.getSeconds()
      );
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

  return <div className="current-time-display">{currentTime}</div>;
};

export default CurrentTimeDisplay;
