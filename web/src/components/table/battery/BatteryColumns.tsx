import { BatteryColumnType } from "@/models/BatteryData"
import { ColumnDef } from "@tanstack/react-table"

 


import { Checkbox } from "@/components/ui/checkbox"
import { DataTableColumnHeader } from "../ColumnHeader"

export const batteryColumns: ColumnDef<BatteryColumnType>[] = [
    {
        id: "select",
        header: ({ table }) => (
          <Checkbox
            checked={
              table.getIsAllPageRowsSelected() ||
              (table.getIsSomePageRowsSelected() && "indeterminate")
            }
            onCheckedChange={(value) => table.toggleAllPageRowsSelected(!!value)}
            aria-label="Select all"
          />
        ),
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
            const value = row.original.id
       
            return <div className="font-semibold justify-center">{value}</div>
        },
        filterFn: (row, columnId, filterValue: string) => {
            const search = filterValue.toLowerCase();
            let value = row.getValue(columnId) as string;
            if (typeof value === "number") value = String(value);
            return value?.toLowerCase().includes(search);
        },
        
    },
    {
        accessorKey: "type",
        header: ({ column }) => (
            <DataTableColumnHeader column={column} title="Type" />
          ),
    },
    {
        accessorKey: "size",
        header: ({ column }) => (
            <DataTableColumnHeader column={column} title="Size" />
        ),
    },
    {
        accessorKey: "factory_capacity",
        header: ({ column }) => (
            <DataTableColumnHeader column={column} title="Capacity" />
        ),
        cell: ({ row }) => {
            const value = row.original.factory_capacity
       
            return <div className="">{value}mAh</div>
        },
    },
    {
        accessorKey: "voltage",
        header: ({ column }) => (
            <DataTableColumnHeader column={column} title="Voltage" />
        ),
        cell: ({ row }) => {
            const value = row.original.voltage
       
            return <div className="">{value}mV</div>
        },
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
]