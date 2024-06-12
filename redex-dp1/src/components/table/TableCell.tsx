const TableCell = ({ data, className }) => {
  return (
    <td scope="row" className={`px-6 py-4`}>
      {className != "" ? (
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
