import { ColumnDef } from "@tanstack/react-table";

export const columns: ColumnDef<any, any>[] = [
  {
    header: "Origin",
    accessorKey: "origin.name",
    cell: (info) => info.getValue(),
  },
  {
    header: "Origin Code",
    accessorKey: "origin.code",
    cell: (info) => info.getValue(),
  },
  {
    header: "Destination",
    accessorKey: "destiny.name",
    cell: (info) => info.getValue(),
  },
  {
    header: "Destination Code",
    accessorKey: "destiny.code",
    cell: (info) => info.getValue(),
  },
  {
    header: "Departure Time",
    accessorKey: "departureTime",
    cell: (info) => info.getValue(),
  },
  {
    header: "Arrival Time",
    accessorKey: "arrivalTime",
    cell: (info) => info.getValue(),
  },
  {
    header: "Capacity",
    accessorKey: "capacidad",
    cell: (info) => info.getValue(),
  },
  {
    header: "Code",
    accessorKey: "code",
    cell: (info) => info.getValue(),
  },
];
