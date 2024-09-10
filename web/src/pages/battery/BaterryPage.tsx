import { BatteryCard } from "@/components/cards/BatteryCard";
import { InfoBox } from "@/components/InfoBox";
import { Loading } from "@/components/Loading";
import { PaggingMenu } from "@/components/PaggingMenu";
import { buttonVariants } from "@/components/ui/button";
import { Battery, fetchBatteryData } from "@/models/BatteryData";
import { BatteryWarning } from "lucide-react";

import { useEffect, useRef, useState } from "react";
import { Link } from "react-router-dom";

export function BaterryPage() {
  const [batteryData, setBatteryData] = useState<Battery[] | null>(null);
  const [page, setPage] = useState(1);
  const [perPage] = useState(9);
  const hasFetched = useRef(false);

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

  if (!batteryData) {
    return Loading();
  }

  const start = (Number(page) - 1) * Number(perPage);
  const end = start + Number(perPage);

  const paginatedData = batteryData.slice(start, end);

  return (
    <>
      <div className="flex justify-center">
        <div className="flex justify-end w-full xl:w-4/6 ps-4 pe-4 pt-4">
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
        <div className="w-full xl:w-4/6 grid grid-cols-1 sm:grid-cols-1 md:grid-cols-2 lg:grid-cols-2 xl:grid-cols-3 gap-4 p-4">
          {paginatedData.map((battery) => (
            <BatteryCard key={battery.id} battery={battery} />
          ))}
        </div>
      </div>
      {batteryData.length > 0 && (
        <PaggingMenu
          totalPosts={batteryData.length}
          postsPerPage={perPage}
          currentPage={page}
          setCurrentPage={setPage}
        />
      )}
    </>
  );
}
