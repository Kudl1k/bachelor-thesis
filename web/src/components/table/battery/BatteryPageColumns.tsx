import { Battery } from "@/models/BatteryData";
import { ColumnDef } from "@tanstack/react-table";
import { DataTableColumnHeader } from "../ColumnHeader";

export const batteryPageColumns: ColumnDef<Battery>[] = [
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
      if (typeof value === "string") value = String(value);
      return value?.toLowerCase().includes(search);
    },
  },
  {
    accessorKey: "type",
    header: ({ column }) => (
      <DataTableColumnHeader column={column} title="Type" />
    ),
    cell: ({ row }) => {
      const value = row.original.type;

      return <div className="">{value.shortcut}</div>;
    },
  },
  {
    accessorKey: "size",
    header: ({ column }) => (
      <DataTableColumnHeader column={column} title="Size" />
    ),
    cell: ({ row }) => {
      const value = row.original.size;

      return <div className="">{value.name}</div>;
    },
  },
  {
    accessorKey: "factory_capacity",
    header: ({ column }) => (
      <DataTableColumnHeader column={column} title="Capacity" />
    ),
    cell: ({ row }) => {
      const value = row.original.factory_capacity;

      return <div className="">{value}mAh</div>;
    },
  },
  {
    accessorKey: "voltage",
    header: ({ column }) => (
      <DataTableColumnHeader column={column} title="Voltage" />
    ),
    cell: ({ row }) => {
      const value = row.original.voltage;

      return <div className="">{value}mV</div>;
    },
  },
  {
    accessorKey: "shop_link",
    header: ({ column }) => (
      <DataTableColumnHeader column={column} title="Shop link" />
    ),
    cell: ({ row }) => {
      const value = row.original.shop_link;

      if (!value) {
        return <div className="text-gray-400">No link</div>;
      }

      return (
        <div className="flex items-center">
          <a href={value} target="_blank">
            {value}
          </a>
        </div>
      );
    }
  },
  {
    accessorKey: "last_charged_capacity",
    header: ({ column }) => (
      <DataTableColumnHeader column={column} title="Last charged capacity" />
    ),
  },
  {
    accessorKey: "last_time_charged_at",
    header: ({ column }) => (
      <DataTableColumnHeader column={column} title="Last charged at" />
    ),
  },
  {
    accessorKey: "archived",
    header: ({ column }) => (
      <DataTableColumnHeader column={column} title="Archived" />
    ),
    cell: ({ row }) => {
      const value = row.original.archived;

      return <div className="">{value ? "Yes" : ""}</div>;
    },
  },
  {
    accessorKey: "created_at",
    header: ({ column }) => (
      <DataTableColumnHeader column={column} title="Created at" />
    ),
  },
];
