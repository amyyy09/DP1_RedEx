import TableRow from "./TableRow";

const TableBody = ({ data, options }) => {
  return (
    <tbody className="border-2 border-gray-200">
      {data.map((row, index) => {
        console.log(row);
        return <TableRow key={index} row={row} options={options} />;
      })}
    </tbody>
  );
};

export default TableBody;
