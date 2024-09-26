"use client";

import {
  ColumnDef,
  ColumnFiltersState,
  RowSelectionState,
  SortingState,
  VisibilityState,
  flexRender,
  getCoreRowModel,
  getFilteredRowModel,
  getPaginationRowModel,
  getSortedRowModel,
  useReactTable,
} from "@tanstack/react-table";

import { Input } from "@/components/ui/input";

import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from "@/components/ui/table";
import { useEffect, useState } from "react";
import { DataTablePagination } from "./Pagination";
import { DataTableViewOptions } from "./ViewOptions";

interface DataTableProps<TData, TValue> {
  searchbarname: string;
  columns: ColumnDef<TData, TValue>[];
  data: TData[];
  setSelectedIds?: (selectedIds: string[]) => void;
  setSelectedId: (selectedId: number) => void;
  multiRowSelection?: boolean;
  idname: string;
  sortiddesc: boolean;
}

export function DataTable<TData, TValue>({
  searchbarname,
  columns,
  data,
  setSelectedIds,
  setSelectedId,
  multiRowSelection,
  idname,
  sortiddesc,
}: DataTableProps<TData, TValue>) {
  const [columnFilters, setColumnFilters] = useState<ColumnFiltersState>([]);
  const [sorting, setSorting] = useState<SortingState>([
    { id: idname, desc: sortiddesc },
  ]);
  const [columnVisibility, setColumnVisibility] = useState<VisibilityState>({});
  const [rowSelection, setRowSelection] = useState<RowSelectionState>({});

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
  });

  useEffect(() => {
    const selectedRowIds = table
      .getSelectedRowModel()
      .rows.map((row) =>
        (row.original as { id: number; [key: string]: number })[
          idname
        ].toString()
      );
    if (setSelectedIds) {
      setSelectedIds(selectedRowIds);
    }
    if (selectedRowIds) {
      setSelectedId(Number(selectedRowIds[0]) || -1);
    }
  });

  return (
    <div>
      <div className="flex items-center py-4">
        <Input
          placeholder={`Filter ${searchbarname} by id...`}
          value={(table.getColumn(idname)?.getFilterValue() as string) ?? ""}
          onChange={(event) => {
            table.getColumn(idname)?.setFilterValue(event.target.value);
          }}
          onKeyDown={(event) => {
            if (event.key === "Enter") {
              const filterValue = table
                .getColumn(idname)
                ?.getFilterValue()
                ?.toString();
              if (filterValue && filterValue.length === 8) {
                const firstRow = table.getRowModel().rows[0];
                if (firstRow) {
                  setRowSelection((prev) => ({
                    ...prev,
                    [firstRow.id]: true,
                  }));
                  table.getColumn(idname)?.setFilterValue("");
                }
              }
            }
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
                  );
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
                      {flexRender(
                        cell.column.columnDef.cell,
                        cell.getContext()
                      )}
                    </TableCell>
                  ))}
                </TableRow>
              ))
            ) : (
              <TableRow>
                <TableCell
                  colSpan={columns.length}
                  className="h-24 text-center"
                >
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
  );
}
