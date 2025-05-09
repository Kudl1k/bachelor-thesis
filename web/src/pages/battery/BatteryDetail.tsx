import { ChargeRecordChart } from "@/components/charts/ChargeRecordChart";
import { LastRecordsChart } from "@/components/charts/LastRecordsChart";
import { Loading } from "@/components/Loading";
import { ChargeRecordColumns } from "@/components/table/chargerecords/ChargeRecordColumns";
import { DataTable } from "@/components/table/DataTable";
import { AspectRatio } from "@/components/ui/aspect-ratio";
import { Button } from "@/components/ui/button";
import { Card, CardContent, CardHeader } from "@/components/ui/card";
import { Label } from "@/components/ui/label";
import {
  Tooltip,
  TooltipContent,
  TooltipProvider,
  TooltipTrigger,
} from "@/components/ui/tooltip";
import {
  BatteryInfo,
  fetchBatteryInfo,
  toggleArchived,
  truncateText,
} from "@/models/BatteryData";
import JsBarcode from "jsbarcode";
import {
  Archive,
  ArchiveRestore,
  BatteryMedium,
  Biohazard,
  Hash,
  Link,
  Ruler,
  Zap,
} from "lucide-react";
import { useCallback, useEffect, useRef, useState } from "react";
import { useParams } from "react-router-dom";

export default function BatteryDetail() {
  const { id } = useParams<{ id: string }>();
  const [batteryData, setBatteryData] = useState<BatteryInfo | null>(null);
  const [selectedCharger, setSelectedCharger] = useState<number | null>(null);
  const [barcodeUrl, setBarcodeUrl] = useState<string | null>(null);
  const setCanvasRef = useCallback(
    (node: HTMLCanvasElement | null) => {
      if (node && batteryData) {
        JsBarcode(node, batteryData.id, { format: "CODE128", height: 40 });
        const url = node.toDataURL("image/png");
        setBarcodeUrl(url);
      }
    },
    [batteryData]
  );

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

  function onArchivedButtonClick() {
    if (!batteryData) return;
    toggleArchived(batteryData.id);
    window.location.reload();
  }

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
                    <TooltipProvider>
                      <Tooltip>
                        <TooltipTrigger>
                          <h1 className="text-2xl font-bold hover:shadow-lg border-2 border-background hover:border-primary ease-in-out duration-150 rounded-2xl p-1">
                            <div className="flex items-center gap-2">
                              <Hash className="size-7" />
                              {batteryData.id}
                            </div>
                          </h1>
                        </TooltipTrigger>
                        <TooltipContent>
                          {barcodeUrl ? (
                            <img src={barcodeUrl} alt="Barcode" height={40} />
                          ) : (
                            <canvas
                              ref={setCanvasRef}
                              style={{ display: "none" }}
                            />
                          )}
                        </TooltipContent>
                      </Tooltip>
                    </TooltipProvider>
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
                <h2 className="text-lg font-semibold">
                  <div className="flex items-center space-x-2 text-red-500">
                    {batteryData.archived && (
                      <>
                        <Archive />
                        <Label htmlFor="archived">Archived battery</Label>
                      </>
                    )}
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
            <div className="w-full flex justify-center">
              <Button onClick={onArchivedButtonClick}>
                <ArchiveRestore className=" mr-2 h-4 w-4" /> Archive
              </Button>
            </div>
          </CardContent>
        </Card>
      </div>
    </div>
  );
}
