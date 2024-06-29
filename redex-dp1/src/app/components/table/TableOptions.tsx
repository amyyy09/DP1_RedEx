import React, { useRef, useState, useEffect } from "react";
import Link from "next/link";
import "font-awesome/css/font-awesome.min.css";

interface Option {
  link: string;
  icon: string;
  text: string;
}

interface TableOptionsProps {
  id: number;
  options: Option[];
}

const TableOptions: React.FC<TableOptionsProps> = ({ id, options }) => {
  const [showOptions, setShowOptions] = useState(false);
  const dropdownRef = useRef<HTMLDivElement>(null);

  const handleDropdown = () => {
    setShowOptions(!showOptions);
  };

  const handleClickOutside = (event: MouseEvent) => {
    if (
      dropdownRef.current &&
      !dropdownRef.current.contains(event.target as Node)
    ) {
      setShowOptions(false);
    }
  };

  useEffect(() => {
    document.addEventListener("mousedown", handleClickOutside);
    return () => {
      document.removeEventListener("mousedown", handleClickOutside);
    };
  }, []);

  return (
    <div
      className="px-6 py-4 text-gray-900 whitespace-nowrap dark:text-white"
      ref={dropdownRef}
    >
      <ul
        className="text-sm text-gray-700 dark:text-gray-200"
        aria-labelledby="options-dropdown-button"
      >
        {options.map((option, index) => (
          <li key={index}>
            <Link
              href={`${option.link}/${id}`}
              className="w-fit py-2 hover:text-blue-500 hover:underline"
            >
              <i className={`${option.icon} px-2`} aria-hidden="true"></i>
              <span className="px-2">{option.text}</span>
            </Link>
          </li>
        ))}
      </ul>
    </div>
  );
};

export default TableOptions;
