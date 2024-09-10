import { Checkbox } from "@/components/ui/checkbox";
import { ChargeRecord } from "@/models/ChargerData";
import { ColumnDef } from "@tanstack/react-table";
import { DataTableColumnHeader } from "../ColumnHeader";

export const ChargeRecordColumns: ColumnDef<ChargeRecord>[] = [
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
    accessorKey: "idChargeRecord",
    header: ({ column }) => (
      <DataTableColumnHeader column={column} title="ID" />
    ),
    cell: ({ row }) => {
      const value = row.original.idChargeRecord;

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
    accessorKey: "charger",
    header: ({ column }) => (
      <DataTableColumnHeader column={column} title="Charger" />
    ),
    cell: ({ row }) => {
      const value = row.original.charger.name;

      return <div className="font-semibold justify-center">{value}</div>;
    },
  },
  {
    accessorKey: "slot",
    header: ({ column }) => (
      <DataTableColumnHeader column={column} title="Slot" />
    ),
  },
  {
    accessorKey: "startedAt",
    header: ({ column }) => (
      <DataTableColumnHeader column={column} title="Started at" />
    ),
  },
  {
    accessorKey: "finishedAt",
    header: ({ column }) => (
      <DataTableColumnHeader column={column} title="Finished at" />
    ),
  },
  {
    accessorKey: "chargedCapacity",
    header: ({ column }) => (
      <DataTableColumnHeader column={column} title="Charged capacity" />
    ),
    cell: ({ row }) => {
      const value = row.original.chargedCapacity;
      if (value === null) return <div className="">-</div>;
      return <div className="">{value} mAh</div>;
    },
  },
  {
    accessorKey: "dischargedCapacity",
    header: ({ column }) => (
      <DataTableColumnHeader column={column} title="Discharged capacity" />
    ),
    cell: ({ row }) => {
      const value = row.original.dischargedCapacity;
      if (value === null) return <div className="">-</div>;
      return <div className="">{value} mAh</div>;
    },
  },
];
