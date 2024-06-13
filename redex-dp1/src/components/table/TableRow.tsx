import React, { Fragment } from "react";
import TableCell from "./TableCell";
import TableOptions from "./TableOptions";

interface TableRowProps {
  row: any[];
  options: any[];
}

const getEstadoFromRow = (row: any[]) => {
  for (let item of row) {
    if (
      typeof item.data === "object" &&
      item.data !== null &&
      item.data.props
    ) {
      const estadoTexto = item.data.props.children;
      if (typeof estadoTexto === "string") {
        return estadoTexto;
      }
    }
  }
  return null;
};

const TableRow: React.FC<TableRowProps> = ({ row, options }) => {
  const estado = getEstadoFromRow(row);
  return (
    <tr className="bg-white border-b-l-r hover:bg-gray-100">
      {row.map((cell, index) => {
        if (index === 0) return null;
        if (index === row.length - 1) {
          return (
            <Fragment key={index + row[0].data * row.length}>
              <TableCell
                data={cell.data}
                className={cell.className ? cell.className : ""}
              />
              {options.length > 0 ? (
                <TableOptions id={parseInt(row[0].data)} options={options} />
              ) : null}
            </Fragment>
          );
        } else {
          return (
            <TableCell
              key={index + row[0].data * row.length}
              data={cell.data}
              className={cell.className ? cell.className : ""}
            />
          );
        }
      })}
    </tr>
  );
};

export default TableRow;
