import Image from "next/image";
import React, { FC, ReactElement } from "react";

interface TitleWithIconProps {
  name: string;
  icon?: string; // Accepts string for image path
}

const TitleWithIcon: FC<TitleWithIconProps> = ({ name, icon }) => {
  return (
    <div className="flex items-center justify-between w-fit py-4">
      {icon && (
        <Image
          src={icon}
          alt={`Icon for ${name}`}
          className="mr-6"
          width={50}
          height={50}
        />
      )}
      <h1
        style={{ color: "#C80405" }}
        className={`bold ml-${icon ? "6" : "0"} text-4xl my-4`}
      >
        {name}
      </h1>
    </div>
  );
};

export default TitleWithIcon;
