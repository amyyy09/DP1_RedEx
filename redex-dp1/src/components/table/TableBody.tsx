import React from "react";
import TableRow from "./TableRow";

interface TableBodyProps {
  data: any[];
  options: any;
}

const TableBody: React.FC<TableBodyProps> = ({ data, options }) => {
  return (
    <tbody className="border-2 border-gray-200">
      {data.map((row, index) => (
        <TableRow key={index} row={row} options={options} />
      ))}
    </tbody>
  );
};

export default TableBody;
