"use client"

import {
    ColumnDef,
    ColumnFiltersState,
    SortingState,
    VisibilityState,
    flexRender,
    getCoreRowModel,
    getFilteredRowModel,
    getPaginationRowModel,
    getSortedRowModel,
    useReactTable,
  } from "@tanstack/react-table"
   
  import { Input } from "@/components/ui/input"


import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from "@/components/ui/table"
import { useEffect, useState } from "react"
import { DataTablePagination } from "../Pagination"
import { DataTableViewOptions } from "../ViewOptions"

interface DataTableProps<TData, TValue> {
  columns: ColumnDef<TData, TValue>[]
  data: TData[]
  setSelectedIds?: (selectedIds: number[]) => void
  setSelectedId: (selectedId: number) => void
  multiRowSelection?: boolean
}

export function DataTable<TData, TValue>({
  columns,
  data,
  setSelectedIds,
  setSelectedId,
  multiRowSelection
}: DataTableProps<TData, TValue>) {
    const [columnFilters, setColumnFilters] = useState<ColumnFiltersState>(
        []
      )
    const [sorting, setSorting] = useState<SortingState>([])
    const [columnVisibility, setColumnVisibility] = useState<VisibilityState>({})
    const [rowSelection, setRowSelection] = useState({})


    const table = useReactTable({
        autoResetAll: false,
        data,
        columns,
        onSortingChange: setSorting,
        getCoreRowModel: getCoreRowModel(),
        getPaginationRowModel: getPaginationRowModel(),
        getSortedRowModel: getSortedRowModel(),
        onColumnFiltersChange: setColumnFilters,
        getFilteredRowModel: getFilteredRowModel(),
        onColumnVisibilityChange: setColumnVisibility,
        onRowSelectionChange: setRowSelection,
        enableMultiRowSelection: multiRowSelection,
        state: {
            sorting,
            columnFilters,
            columnVisibility,
            rowSelection,
        },
    })

    useEffect(() => {
        const selectedRowIds = table.getSelectedRowModel().rows.map(row => (row.original as { id: number })["id"]);
        if (setSelectedIds) {
            setSelectedIds(selectedRowIds);
        }
        if (selectedRowIds) {
            setSelectedId(selectedRowIds[0] || -1);
        }
    });
  

  return (
    <div>
        <div className="flex items-center py-4">
            <Input
                placeholder="Filter batteries by id"
                value={
                    (table.getColumn("id")?.getFilterValue() as string) ?? ""
                }
                onChange={(event) => {
                    table.getColumn("id")?.setFilterValue(event.target.value);
                }}
                className="max-w-sm"
            />
        <DataTableViewOptions table={table} />

            </div>
        <div className="rounded-md border">
        <Table>
            <TableHeader>
            {table.getHeaderGroups().map((headerGroup) => (
                <TableRow key={headerGroup.id}>
                {headerGroup.headers.map((header) => {
                    return (
                    <TableHead key={header.id}>
                        {header.isPlaceholder
                        ? null
                        : flexRender(
                            header.column.columnDef.header,
                            header.getContext()
                            )}
                    </TableHead>
                    )
                })}
                </TableRow>
            ))}
            </TableHeader>
            <TableBody>
            {table.getRowModel().rows?.length ? (
                table.getRowModel().rows.map((row) => (
                <TableRow
                    key={row.id}
                    data-state={row.getIsSelected() && "selected"}
                >
                    {row.getVisibleCells().map((cell) => (
                    <TableCell key={cell.id}>
                        {flexRender(cell.column.columnDef.cell, cell.getContext())}
                    </TableCell>
                    ))}
                </TableRow>
                ))
            ) : (
                <TableRow>
                <TableCell colSpan={columns.length} className="h-24 text-center">
                    No results.
                </TableCell>
                </TableRow>
            )}
            </TableBody>
        </Table>
        </div>
        <div className="flex items-center justify-end">
            
            <DataTablePagination table={table} />
        </div>
    </div>
  )
}
