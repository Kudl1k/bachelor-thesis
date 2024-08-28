import { Loading } from "@/components/Loading";
import { batteryColumns } from "@/components/table/battery/BatteryColumns";
import { DataTable } from "@/components/table/battery/BatteryTable";
import { chargerColumns } from "@/components/table/charger/ChargerColumns";
import { Badge } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import {
  Collapsible,
  CollapsibleContent,
  CollapsibleTrigger,
} from "@/components/ui/collapsible";
import {
  Battery,
  BatteryColumnType,
  fetchBatteryData,
} from "@/models/BatteryData";
import {
  Charger,
  ChargerColumnType,
  ChargerSearch,
  searchChargerData,
  startTracking,
} from "@/models/ChargerData";
import { ChevronsDownUpIcon } from "lucide-react";
import { useEffect, useState } from "react";

const enum Stage {
  BATTERIES,
  CHARGER,
  FINISH,
}

export function SetupPage() {
  const [isOpenBatteries, setIsOpenBatteries] = useState(true);
  const [batteries, setBatteries] = useState<Battery[] | null>(null);
  const [data, setData] = useState<BatteryColumnType[] | null>(null);
  const [selectedBatteryIds, setSelectedBatteryIds] = useState<number[]>([]);
  const [selectedBatteries, setSelectedBatteries] = useState<Battery[] | null>(
    null
  );

  const [isOpenCharger, setIsOpenCharger] = useState(true);
  const [isDisabledCharger, setIsDisabledCharger] = useState(true);
  const [chargers, setChargers] = useState<Charger[] | null>(null);
  const [chargerData, setChargerData] = useState<ChargerColumnType[] | null>(
    null
  );
  const [selectedChargerId, setSelectedChargerId] = useState<number>(-1);
  const [selectedCharger, setSelectedCharger] = useState<Charger | null>(null);

  const [isFinish, setIsFinish] = useState(true);

  const [stage, setStage] = useState<Stage>(Stage.BATTERIES);

  useEffect(() => {
    fetchBatteryData(setBatteries);
  }, []);

  useEffect(() => {
    if (batteries) {
      const transformedData = batteries.map((battery) => ({
        id: battery.id,
        type: battery.type.shortcut,
        size: battery.size.name,
        factory_capacity: battery.factory_capacity,
        voltage: battery.voltage,
        last_charged_capacity: battery.last_charged_capacity
          ? battery.last_charged_capacity
          : "N/A",
        last_time_charged_at: battery.last_time_charged_at
          ? battery.last_time_charged_at
          : "N/A",
      }));
      setData(transformedData);
    }
  }, [batteries]);

  useEffect(() => {
    if (!selectedBatteryIds || !batteries) {
      return;
    }
    setSelectedBatteries(
      batteries.filter((battery) => selectedBatteryIds.includes(battery.id))
    );
    if (selectedBatteryIds.length === 0) {
      console.log("No batteries selected");
      setIsDisabledCharger(true);
      setChargers(null);
      return;
    } else {
      setIsDisabledCharger(false);
    }
  }, [selectedBatteryIds, batteries]);

  useEffect(() => {
    if (selectedChargerId > 0) {
      setSelectedCharger(
        chargers?.find((charger) => charger.id === selectedChargerId) ?? null
      );
    }
  }, [chargers, selectedChargerId]);

  useEffect(() => {
    if (chargers) {
      setChargerData(
        chargers.map((charger) => ({
          id: charger.id,
          name: charger.name,
          tty: charger.tty,
          slots: charger.slots,
          types: charger.types.map((type) => type.shortcut),
          sizes: charger.sizes.map((size) => size.name),
        }))
      );
      console.log(chargers);

      setStage(Stage.CHARGER);
    }
  }, [chargers, selectedChargerId]);

  const handleBatterySelectionChange = (selectedIds: number[]) => {
    setSelectedBatteryIds(selectedIds);
  };

  const handleChargerSelectionChange = (selectedId: number) => {
    if (selectedId > 0) {
      setIsFinish(false);
    } else {
      setIsFinish(true);
    }
    setSelectedChargerId(selectedId);
  };

  function handleContinueBattery() {
    const searchCharger: ChargerSearch = {
      types: selectedBatteries?.map((battery) => battery.type.shortcut) ?? [],
      sizes: selectedBatteries?.map((battery) => battery.size.name) ?? [],
    };
    searchChargerData(searchCharger, setChargers);
  }

  function handleContinueCharger() {
    if (!selectedCharger) {
      console.error("No charger selected");
      return;
    }
    setStage(Stage.FINISH);
  }

  function handleStart() {
    console.log("Starting the process");
    startTracking({
      id_charger: selectedChargerId,
      batteries: selectedBatteryIds.map((id, index) => ({
        id: id,
        slot: index + 1,
      })),
    });
  }

  if (!batteries || !data) {
    return Loading();
  }

  return (
    <div className="flex justify-center">
      {(stage === Stage.BATTERIES || stage === Stage.CHARGER) && (
        <div className="justify-center xl:w-4/6 w-full grid grid-cols-1 sm:grid-cols-1 md:grid-cols-3 lg:grid-cols-3 xl:grid-cols-3 pe-4 ps-4 pt-4 gap-3">
          <div className="flex w-full xl:col-span-1 lg:col-span-1 md:col-span-1">
            <Card className="w-full h-min">
              <CardContent>
                <Collapsible
                  open={isOpenBatteries}
                  onOpenChange={setIsOpenBatteries}
                >
                  <CollapsibleTrigger className="w-full">
                    <div className="flex w-full items-center justify-between pt-5">
                      <h4 className="font-semibold">Selected batteries</h4>
                      <div className="gap-1 flex items-center">
                        <h4 className="font-bold">
                          {selectedBatteries?.length}
                        </h4>
                        <ChevronsDownUpIcon size={16} />
                      </div>
                    </div>
                  </CollapsibleTrigger>
                  <CollapsibleContent>
                    {selectedBatteries?.map((battery, index) => (
                      <div
                        key={battery.id}
                        className="flex items-center justify-between"
                      >
                        <div className="flex items-center gap-2 whitespace-nowrap">
                          <span className="font-bold">Slot {index + 1}</span>
                          <span className="">#{battery.id}</span>
                          <Badge>{battery.type.shortcut}</Badge>
                          <Badge>{battery.size.name}</Badge>
                        </div>
                        <h4 className="whitespace-nowrap">
                          {battery.factory_capacity} mAh
                        </h4>
                      </div>
                    ))}
                  </CollapsibleContent>
                </Collapsible>

                <Collapsible
                  open={isOpenCharger}
                  onOpenChange={setIsOpenCharger}
                  disabled={isDisabledCharger}
                  className={isDisabledCharger ? "text-zinc-300" : ""}
                >
                  <CollapsibleTrigger className="w-full">
                    <div className="flex w-full items-center justify-between pt-2">
                      <h4 className="font-semibold">Selected charger</h4>
                      <div className="gap-1 flex items-center">
                        <ChevronsDownUpIcon size={16} />
                      </div>
                    </div>
                  </CollapsibleTrigger>
                  <CollapsibleContent>
                    <div className="flex items-center">
                      {selectedCharger && selectedCharger.name}
                    </div>
                  </CollapsibleContent>
                </Collapsible>
              </CardContent>
            </Card>
          </div>
          <div className="flex justify-center w-full xl:col-span-2 lg:col-span-2 md:col-span-2">
            <Card className="w-full">
              {stage === Stage.BATTERIES && (
                <>
                  <CardHeader>
                    <CardTitle>
                      <div className="flex w-full items-center justify-between">
                        <h4 className="font-semibold">Select batteries</h4>
                        <div className="gap-1 flex items-center">
                          <h4 className="font-bold">
                            {selectedBatteryIds.length} selected
                          </h4>
                        </div>
                      </div>
                    </CardTitle>
                  </CardHeader>
                  <CardContent>
                    <div className="container mx-auto">
                      <DataTable
                        columns={batteryColumns}
                        data={data}
                        setSelectedIds={handleBatterySelectionChange}
                        setSelectedId={() => {}}
                        multiRowSelection
                        idname="id"
                      />
                    </div>
                    <div className="flex w-full justify-end pe-8">
                      <Button
                        disabled={isDisabledCharger}
                        onClick={() => {
                          handleContinueBattery();
                        }}
                      >
                        Pick a charger
                      </Button>
                    </div>
                  </CardContent>
                </>
              )}
              {stage === Stage.CHARGER && (
                <>
                  <CardHeader>
                    <CardTitle>
                      <div className="flex w-full items-center justify-between">
                        <h4 className="font-semibold">Select a charger</h4>
                      </div>
                    </CardTitle>
                  </CardHeader>
                  <CardContent>
                    <div className="container mx-auto">
                      <DataTable
                        columns={chargerColumns}
                        data={chargerData || []}
                        setSelectedId={handleChargerSelectionChange}
                        setSelectedIds={() => {}}
                        multiRowSelection={false}
                        idname="id"
                      />
                    </div>
                    <div className="flex w-full justify-end ps-8 pe-8">
                      <Button
                        disabled={isFinish}
                        onClick={() => {
                          handleContinueCharger();
                        }}
                      >
                        Go to summary
                      </Button>
                    </div>
                  </CardContent>
                </>
              )}
            </Card>
          </div>
        </div>
      )}
      {stage === Stage.FINISH && (
        <Card className="w-full xl:w-1/2 ms-4 me-4 mt-4">
          <CardHeader>
            <CardTitle>
              <h4 className="font-semibold">Summary</h4>
            </CardTitle>
          </CardHeader>
          <CardContent>
            <div className="grid grid-cols-2">
              <div>
                <h3 className="font-semibold text-xl pb-4">
                  {selectedCharger?.name}
                </h3>
                <p className="font-semibold">
                  Baud rate:{" "}
                  <span className="font-normal">
                    {selectedCharger?.baudRate}
                  </span>
                </p>
                <p className="font-semibold">
                  Data bits:{" "}
                  <span className="font-normal">
                    {selectedCharger?.dataBits}
                  </span>
                </p>
                <p className="font-semibold">
                  Stop bits:{" "}
                  <span className="font-normal">
                    {selectedCharger?.stopBits}
                  </span>
                </p>
                <p className="font-semibold">
                  Parity:{" "}
                  <span className="font-normal">{selectedCharger?.parity}</span>
                </p>
                <p className="font-semibold">
                  RTS:{" "}
                  <span className="font-normal">
                    {(selectedCharger?.rts && "Yes") || "No"}
                  </span>
                </p>
                <p className="font-semibold">
                  DTR:{" "}
                  <span className="font-normal">
                    {(selectedCharger?.dtr && "Yes") || "No"}
                  </span>
                </p>
                <p className="font-semibold">
                  TTY:{" "}
                  <span className="font-normal">{selectedCharger?.tty}</span>
                </p>
              </div>
              <div>
                <h3 className="font-semibold text-xl pb-4">Batteries</h3>
                {selectedBatteries?.map((battery, index) => (
                  <div
                    key={battery.id}
                    className="flex items-center justify-between"
                  >
                    <div className="flex items-center gap-2 whitespace-nowrap">
                      <span className="font-bold">Slot {index + 1}</span>
                      <span className="">#{battery.id}</span>
                      <Badge>{battery.type.shortcut}</Badge>
                      <Badge>{battery.size.name}</Badge>
                    </div>
                    <h4 className="whitespace-nowrap">
                      {battery.factory_capacity} mAh
                    </h4>
                  </div>
                ))}
              </div>
            </div>
            <div className="flex justify-center">
              <Button onClick={() => handleStart()}>Start</Button>
            </div>
          </CardContent>
        </Card>
      )}
    </div>
  );
}
