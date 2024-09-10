import { ChargeRecordChart } from "@/components/charts/ChargeRecordChart";
import { LastRecordsChart } from "@/components/charts/LastRecordsChart";
import { Loading } from "@/components/Loading";
import { DataTable } from "@/components/table/battery/BatteryTable";
import { ChargeRecordColumns } from "@/components/table/chargerecords/ChargeRecordColumns";
import { AspectRatio } from "@/components/ui/aspect-ratio";
import { Card, CardContent, CardHeader } from "@/components/ui/card";
import {
  BatteryInfo,
  fetchBatteryInfo,
  truncateText,
} from "@/models/BatteryData";
import { BatteryMedium, Biohazard, Hash, Link, Ruler, Zap } from "lucide-react";
import { useEffect, useRef, useState } from "react";
import { useParams } from "react-router-dom";

export default function BatteryDetail() {
  const { id } = useParams<{ id: string }>();
  const [batteryData, setBatteryData] = useState<BatteryInfo | null>(null);
  const [selectedCharger, setSelectedCharger] = useState<number | null>(null);

  const hasFetched = useRef(false);

  useEffect(() => {
    if (!hasFetched.current) {
      fetchBatteryInfo(String(id), setBatteryData);
      hasFetched.current = true;
    }
  }, [id]);

  const handleChargerSelectionChange = (id: number) => {
    if (Number(id) === -1) {
      setSelectedCharger(null);
      console.log("Selected charger: none");
    } else {
      console.log("Selected charger:", id);
      setSelectedCharger(id);
    }
  };

  if (!batteryData) {
    return Loading();
  }

  console.log(batteryData);

  return (
    <div className="flex justify-center w-full">
      <div className="xl:w-4/6 p-4 w-full">
        <Card className="shadow-md">
          <CardHeader>
            <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-2 lg:grid-cols-2 xl:grid-cols-2">
              <div className="col-span-1 space-y-1">
                <h1 className="text-2xl font-bold">
                  <div className="flex items-center gap-2">
                    <Hash />
                    {batteryData.id}
                  </div>
                </h1>
                <h2 className="text-lg font-semibold">
                  <div className="flex items-center gap-2">
                    <Ruler />
                    {batteryData.size.name}
                  </div>
                </h2>
                <h2 className="text-lg font-semibold">
                  <div className="flex items-center gap-2">
                    <BatteryMedium />
                    {batteryData.factory_capacity} mAh
                  </div>
                </h2>
                <h2 className="text-lg font-semibold">
                  <div className="flex items-center gap-2">
                    <Zap />
                    {batteryData.voltage} V
                  </div>
                </h2>
                <h2 className="text-lg font-semibold">
                  <div className="flex items-center gap-2">
                    <Biohazard />
                    {batteryData.type.shortcut}
                  </div>
                </h2>
                <h2 className="text-lg font-semibold ">
                  <div className="flex items-center gap-2">
                    <Link />
                    <a
                      href={batteryData.shop_link || ""}
                      target="_blank"
                      rel="noreferrer"
                      className="text-blue-500"
                    >
                      {truncateText(batteryData.shop_link || "", 30)}
                    </a>
                  </div>
                </h2>
              </div>
              <div className="col-span-1">
                <LastRecordsChart data={batteryData.charge_records} />
              </div>
            </div>
            <div className="col-span-2 w-full justify-end sm:text-end md:text-end lg:text-end xl:text-end">
              {selectedCharger && (
                <ChargeRecordChart
                  data={
                    batteryData.charge_records.find(
                      (record) => record.idChargeRecord === selectedCharger
                    ) || batteryData.charge_records[0]
                  }
                  className="max-h-[400px]"
                />
              )}
              {!selectedCharger && (
                <div className="rounded-lg full-w shadow-md min-h-[200px] p-4">
                  <AspectRatio ratio={16 / 9}>
                    <div className="flex h-full w-full items-center justify-center">
                      <h4 className="italic">
                        Please select an record in the table to view a chart of
                        the record.
                      </h4>
                    </div>
                  </AspectRatio>
                </div>
              )}
            </div>
          </CardHeader>
          <CardContent>
            <div className="container mx-auto">
              <DataTable
                searchbarname="charge records"
                columns={ChargeRecordColumns}
                data={batteryData.charge_records || []}
                setSelectedId={handleChargerSelectionChange}
                setSelectedIds={() => {}}
                multiRowSelection={false}
                idname="idChargeRecord"
                sortiddesc={true}
              />
            </div>
          </CardContent>
        </Card>
      </div>
    </div>
  );
}
