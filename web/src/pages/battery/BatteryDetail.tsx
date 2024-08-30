import { ChargeRecordChart } from "@/components/charts/ChargeRecordChart";
import { Loading } from "@/components/Loading";
import { DataTable } from "@/components/table/battery/BatteryTable";
import { ChargeRecordColumns } from "@/components/table/chargerecords/ChargeRecordColumns";
import { Card, CardContent, CardHeader } from "@/components/ui/card";
import { BatteryInfo, fetchBatteryInfo } from "@/models/BatteryData";
import { useEffect, useRef, useState } from "react";
import { useParams } from "react-router-dom";

export default function BatteryDetail() {
  const { id } = useParams<{ id: string }>();
  const [batteryData, setBatteryData] = useState<BatteryInfo | null>(null);
  const [selectedCharger, setSelectedCharger] = useState<number | null>(null);

  const hasFetched = useRef(false);

  useEffect(() => {
    if (!hasFetched.current) {
      fetchBatteryInfo(Number(id), setBatteryData);
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
              <div className="col-span-1">
                <h1 className="text-2xl font-bold">#{batteryData.id}</h1>
                <h2 className="text-lg font-semibold">
                  {batteryData.size.name}
                </h2>
                <h2 className="text-lg font-semibold">
                  {batteryData.factory_capacity} mAh
                </h2>
                <h2 className="text-lg font-semibold">
                  {batteryData.voltage} mV
                </h2>
                <h2 className="text-lg font-semibold">
                  {batteryData.type.shortcut}
                </h2>
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
              </div>
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
