import React, { FC, ReactElement } from "react";

// Define la estructura de las props con TypeScript
interface TitleWithIconProps {
  name: string; // 'name' debe ser un string
  Icon?: () => ReactElement; // 'Icon' es opcional y debe ser un componente que retorna un elemento React
}

const TitleWithIcon: FC<TitleWithIconProps> = ({ name, Icon }) => {
  return (
    <div className="flex items-center justify-between w-fit py-4">
      {Icon && <Icon />}
      <h1
        style={{ color: "#28539E" }}
        className={`font-bold ml-${Icon ? "6" : "0"} text-6xl my-4`}
      >
        {name}
      </h1>
    </div>
  );
};

export default TitleWithIcon;
