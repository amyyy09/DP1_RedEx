import React from "react";

interface Column {
  name: string;
  sortable: boolean;
  sortKey: string;
  visible?: boolean;
}

interface SortConfig {
  key: string;
  direction: "ascending" | "descending";
}

interface TableHeaderProps {
  columns: Column[];
  requestSort: (sortKey: string) => void;
  sortConfig: SortConfig;
}

const TableHeader: React.FC<TableHeaderProps> = ({
  columns,
  requestSort,
  sortConfig,
}) => {
  return (
    <thead className="border-gray-200 border-2 text-left text-md tracking-wider text-blue-50 uppercase bg-[#28539E]">
      <tr>
        {columns.map((column, index) => {
          if (column.visible != null && !column.visible) return null;
          return column.sortable ? (
            <th
              key={index}
              scope="col"
              className="px-6 py-6 cursor-pointer"
              onClick={() => requestSort(column.sortKey)}
            >
              {column.name}
              {sortConfig.key === column.sortKey
                ? sortConfig.direction === "ascending"
                  ? "↓"
                  : "↑"
                : null}
            </th>
          ) : (
            <th key={index} scope="col" className="px-6 py-6">
              {column.name}
            </th>
          );
        })}
      </tr>
    </thead>
  );
};

export default TableHeader;
