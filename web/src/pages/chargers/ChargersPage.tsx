import { ChargersCard } from "@/components/cards/ChargersCard";
import { InfoBox } from "@/components/InfoBox";
import { Loading } from "@/components/Loading";
import { PaggingMenu } from "@/components/PaggingMenu";
import { buttonVariants } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Separator } from "@/components/ui/separator";
import { Switch } from "@/components/ui/switch";
import { Charger, fetchChargerData } from "@/models/ChargerData";
import { PlugZap } from "lucide-react";
import { useEffect, useRef, useState } from "react";
import { Link } from "react-router-dom";

export function ChargersPage() {
  const [chargers, setChargers] = useState<Charger[] | null>(null);
  const [page, setPage] = useState(1);
  const [perPage] = useState(9);
  const hasFetched = useRef(false);
  const [viewMode, setViewMode] = useState<"table" | "card">("card");

  useEffect(() => {
    if (!hasFetched.current) {
      fetchChargerData(setChargers);

      hasFetched.current = true;
    }
  }, []);

  useEffect(() => {
    const savedViewMode = localStorage.getItem("charger-viewMode");
    if (savedViewMode) {
      if (savedViewMode === "table" || savedViewMode === "card") {
        setViewMode(savedViewMode);
      }
    }
  }, []);

  useEffect(() => {
    const params = new URLSearchParams(location.search);
    const pageParam = params.get("page");
    if (pageParam) {
      setPage(Number(pageParam));
    }
  }, []);

  function onViewModeSwitchChange() {
    const newViewMode = viewMode === "card" ? "table" : "card";
    setViewMode(newViewMode);
    localStorage.setItem("charger-viewMode", newViewMode);
  }

  if (!chargers) {
    return Loading();
  }

  console.log(chargers);

  const start = (Number(page) - 1) * Number(perPage);
  const end = start + Number(perPage);

  const paginatedData = chargers.slice(start, end);

  return (
    <>
      <div className="flex justify-center">
        <div className="grid 2xl:grid-cols-3 xl:grid-cols-3 lg:grid-cols-3 md:grid-cols-3 sm:grid-cols-2 grid-cols-2 w-full xl:w-4/6 gap-2 ps-4 pe-4 pt-4 ">
          <Input
            className="2xl:col-span-1 xl:col-span-1 lg:col-span-1 md:col-span-1 sm:col-span-2 col-span-2"
            type="text"
            placeholder="Search for battery id"
            onChange={() => {}}
          />
          <div className="flex items-center gap-2">
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
      {chargers.length === 0 && (
        <div className="flex justify-center">
          <div className="w-full xl:w-1/4 lg:w-3/6 md:w-4/6 ps-4 pe-4 pt-4">
            <InfoBox
              alertTitle="No batteries found"
              alertDescription="There are no batteries found in the database. Please add a new battery."
              icon={PlugZap}
            />
          </div>
        </div>
      )}
      <div className="flex justify-center">
        <div className="w-full xl:w-4/6 grid grid-cols-1 sm:grid-cols-1 md:grid-cols-2 lg:grid-cols-2 xl:grid-cols-2 2xl:grid-cols-2 gap-4 p-4">
          {paginatedData.map((charger) => (
            <ChargersCard key={charger.id} charger={charger} />
          ))}
        </div>
      </div>
      {chargers.length > 0 && (
        <PaggingMenu
          totalPosts={chargers.length}
          postsPerPage={perPage}
          currentPage={page}
          setCurrentPage={setPage}
        />
      )}
    </>
  );
}
