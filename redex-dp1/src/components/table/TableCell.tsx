import React from "react";

interface TableCellProps {
  data: any;
  className?: string;
}

const TableCell: React.FC<TableCellProps> = ({ data, className }) => {
  return (
    <td className={`px-6 py-4`}>
      {className ? (
        <span className={`${className} p-2 rounded-lg inline-block`}>
          {data}
        </span>
      ) : (
        data
      )}
    </td>
  );
};

export default TableCell;
