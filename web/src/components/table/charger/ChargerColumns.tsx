import { Checkbox } from "@/components/ui/checkbox";
import { ChargerColumnType } from "@/models/ChargerData";
import { ColumnDef } from "@tanstack/react-table";
import { DataTableColumnHeader } from "../ColumnHeader";

export const chargerColumns: ColumnDef<ChargerColumnType>[] = [
  {
    id: "select",
    cell: ({ row }) => (
      <Checkbox
        checked={row.getIsSelected()}
        onCheckedChange={(value) => row.toggleSelected(!!value)}
        aria-label="Select row"
      />
    ),
  },
  {
    accessorKey: "id",
    header: ({ column }) => (
      <DataTableColumnHeader column={column} title="ID" />
    ),
    cell: ({ row }) => {
      const value = row.original.id;

      return <div className="font-semibold justify-center">{value}</div>;
    },
    filterFn: (row, columnId, filterValue: string) => {
      const search = filterValue.toLowerCase();
      let value = row.getValue(columnId) as string;
      if (typeof value === "number") value = String(value);
      return value?.toLowerCase().includes(search);
    },
  },
  {
    accessorKey: "name",
    header: ({ column }) => (
      <DataTableColumnHeader column={column} title="Name" />
    ),
  },
  {
    accessorKey: "tty",
    header: ({ column }) => (
      <DataTableColumnHeader column={column} title="TTY" />
    ),
  },
  {
    accessorKey: "slots",
    header: ({ column }) => (
      <DataTableColumnHeader column={column} title="Slots" />
    ),
  },
  {
    accessorKey: "types",
    header: ({ column }) => (
      <DataTableColumnHeader column={column} title="Available types" />
    ),
  },
  {
    accessorKey: "sizes",
    header: ({ column }) => (
      <DataTableColumnHeader column={column} title="Available sizes" />
    ),
  },
];
