import { BatteryCard } from "@/components/cards/BatteryCard";
import { InfoBox } from "@/components/InfoBox";
import { Loading } from "@/components/Loading";
import { PaggingMenu } from "@/components/PaggingMenu";
import { batteryPageColumns } from "@/components/table/battery/BatteryPageColumns";
import { DataTablePage } from "@/components/table/DataTablePage";
import { buttonVariants } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Separator } from "@/components/ui/separator";
import { Switch } from "@/components/ui/switch";
import { Battery, fetchBatteryData } from "@/models/BatteryData";
import {
  ColumnFiltersState,
  getCoreRowModel,
  getFilteredRowModel,
  getPaginationRowModel,
  getSortedRowModel,
  RowSelectionState,
  SortingState,
  useReactTable,
  VisibilityState,
} from "@tanstack/react-table";
import { BatteryWarning } from "lucide-react";

import { useEffect, useRef, useState } from "react";
import { Link } from "react-router-dom";

export function BaterryPage() {
  const [batteryData, setBatteryData] = useState<Battery[] | null>(null);
  const [filteredData, setFilteredData] = useState<Battery[] | null>(null);
  const [page, setPage] = useState(1);
  const [perPage] = useState(9);
  const hasFetched = useRef(false);

  const [archived, setArchived] = useState(false);
  const [searchQuery, setSearchQuery] = useState("");

  const [viewMode, setViewMode] = useState<"table" | "card">("card");

  const [columnFilters, setColumnFilters] = useState<ColumnFiltersState>([]);
  const [sorting, setSorting] = useState<SortingState>([
    { id: "id", desc: false },
  ]);
  const [columnVisibility, setColumnVisibility] = useState<VisibilityState>({});
  const [rowSelection, setRowSelection] = useState<RowSelectionState>({});

  useEffect(() => {
    const savedViewMode = localStorage.getItem("battery-viewMode");
    if (savedViewMode) {
      if (savedViewMode === "table" || savedViewMode === "card") {
        setViewMode(savedViewMode);
      }
    }
  }, []);

  useEffect(() => {
    if (!hasFetched.current) {
      fetchBatteryData(setBatteryData);
      hasFetched.current = true;
    }
  }, []);

  useEffect(() => {
    const params = new URLSearchParams(location.search);
    const pageParam = params.get("page");
    if (pageParam) {
      setPage(Number(pageParam));
    }
  }, []);

  useEffect(() => {
    if (batteryData === null) return;
    let filtered = batteryData;

    if (searchQuery.trim() !== "") {
      filtered = filtered.filter((battery) =>
        battery.id.includes(searchQuery.trim())
      );
    }

    if (!archived) {
      filtered = filtered.filter((battery) => !battery.archived);
    }

    setFilteredData(filtered);
  }, [batteryData, searchQuery, archived]);

  function onArchivedSwitchChange() {
    setArchived(!archived);
  }

  function onViewModeSwitchChange() {
    const newViewMode = viewMode === "card" ? "table" : "card";
    setViewMode(newViewMode);
    localStorage.setItem("battery-viewMode", newViewMode);
  }

  function onSearchQueryChange(event: React.ChangeEvent<HTMLInputElement>) {
    setSearchQuery(event.target.value);
  }

  const table = useReactTable({
    autoResetAll: false,
    columns: batteryPageColumns,
    data: filteredData
      ? filteredData.slice(
          (Number(page) - 1) * Number(perPage),
          (Number(page) - 1) * Number(perPage) + Number(perPage)
        )
      : [],
    onSortingChange: setSorting,
    getCoreRowModel: getCoreRowModel(),
    getPaginationRowModel: getPaginationRowModel(),
    getSortedRowModel: getSortedRowModel(),
    onColumnFiltersChange: setColumnFilters,
    getFilteredRowModel: getFilteredRowModel(),
    onColumnVisibilityChange: setColumnVisibility,
    onRowSelectionChange: setRowSelection,
    enableMultiRowSelection: false,
    state: {
      sorting,
      columnFilters,
      columnVisibility,
      rowSelection,
    },
  });

  if (!batteryData || !filteredData) {
    return Loading();
  }

  const start = (Number(page) - 1) * Number(perPage);
  const end = start + Number(perPage);

  const paginatedData = filteredData.slice(start, end);

  return (
    <>
      <div className="flex justify-center">
        <div className="grid 2xl:grid-cols-3 xl:grid-cols-3 lg:grid-cols-3 md:grid-cols-3 sm:grid-cols-2 grid-cols-2 w-full xl:w-4/6 gap-2 ps-4 pe-4 pt-4 ">
          <Input
            className="2xl:col-span-1 xl:col-span-1 lg:col-span-1 md:col-span-1 sm:col-span-2 col-span-2"
            type="text"
            placeholder="Search for battery id"
            onChange={onSearchQueryChange}
          />
          <div className="flex items-center gap-2">
            <Separator orientation="vertical" />
            <Switch
              onCheckedChange={onArchivedSwitchChange}
              checked={archived}
            />
            <Label>Archived</Label>
            <Separator orientation="vertical" />
            <Switch
              onCheckedChange={onViewModeSwitchChange}
              checked={viewMode === "table"}
            />
            <Label>Table</Label>
          </div>
          <div className="flex w-full items-center justify-end">
            <Link
              className={buttonVariants({ variant: "default" })}
              to={"/add?tab=battery"}
            >
              Add new battery
            </Link>
          </div>
        </div>
      </div>
      {batteryData.length === 0 && (
        <div className="flex justify-center">
          <div className="w-full xl:w-1/4 lg:w-3/6 md:w-4/6 ps-4 pe-4 pt-4">
            <InfoBox
              alertTitle="No batteries found"
              alertDescription="There are no batteries found in the database. Please add a new battery."
              icon={BatteryWarning}
            />
          </div>
        </div>
      )}
      
      {batteryData.length > 0 && (
        <>
        <div className="flex justify-center">
        {viewMode === "card" && (
          <div className="w-full xl:w-4/6 grid grid-cols-1 sm:grid-cols-1 md:grid-cols-2 lg:grid-cols-2 xl:grid-cols-2 2xl:grid-cols-3 gap-4 p-4">
            {paginatedData.map((battery) => (
              <BatteryCard key={battery.id} battery={battery} />
            ))}
          </div>
        )}
        {viewMode === "table" && (
          <div className="w-full xl:w-4/6  gap-4 p-4">
            <DataTablePage table={table} />
          </div>
        )}
      </div>
        <PaggingMenu
          totalPosts={filteredData.length}
          postsPerPage={perPage}
          currentPage={page}
          setCurrentPage={setPage}
        />
        </>
      )}
    </>
  );
}
