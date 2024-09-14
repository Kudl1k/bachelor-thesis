import { BatteryCard } from "@/components/cards/BatteryCard";
import { InfoBox } from "@/components/InfoBox";
import { Loading } from "@/components/Loading";
import { PaggingMenu } from "@/components/PaggingMenu";
import { buttonVariants } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Switch } from "@/components/ui/switch";
import { Battery, fetchBatteryData } from "@/models/BatteryData";
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

  function onSearchQueryChange(event: React.ChangeEvent<HTMLInputElement>) {
    setSearchQuery(event.target.value);
  }

  if (!batteryData || !filteredData) {
    return Loading();
  }

  const start = (Number(page) - 1) * Number(perPage);
  const end = start + Number(perPage);

  const paginatedData = filteredData.slice(start, end);

  return (
    <>
      <div className="flex justify-center">
        <div className="flex justify-between w-full xl:w-4/6 ps-4 pe-4 pt-4">
          <div className="flex items-center space-x-2">
            <Input
              type="text"
              placeholder="Search for battery id"
              onChange={onSearchQueryChange}
            />
            <Switch onCheckedChange={onArchivedSwitchChange} />
            <Label htmlFor="">Archived</Label>
          </div>
          <div>
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

      <div className="flex justify-center">
        <div className="w-full xl:w-4/6 grid grid-cols-1 sm:grid-cols-1 md:grid-cols-2 lg:grid-cols-2 xl:grid-cols-2 2xl:grid-cols-3 gap-4 p-4">
          {paginatedData.map((battery) => (
            <BatteryCard key={battery.id} battery={battery} />
          ))}
        </div>
      </div>
      {batteryData.length > 0 && (
        <PaggingMenu
          totalPosts={filteredData.length}
          postsPerPage={perPage}
          currentPage={page}
          setCurrentPage={setPage}
        />
      )}
    </>
  );
}
