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
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/components/ui/select";
import {
  Battery,
  BatteryColumnType,
  BatteryWithSlot,
  fetchBatteryData,
} from "@/models/BatteryData";
import {
  Charger,
  ChargerColumnType,
  ChargerSearch,
  fetchPorts,
  searchChargerData,
  startTracking,
  updatePort,
} from "@/models/ChargerData";
import { ChevronsDownUpIcon } from "lucide-react";
import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";

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
  const [ports, setPorts] = useState<string[] | null>(null);
  const [port, setPort] = useState<string | null>(null);

  const [stage, setStage] = useState<Stage>(Stage.BATTERIES);

  const navigate = useNavigate();

  const [selectedSlots, setSelectedSlots] = useState<BatteryWithSlot[]>([]);

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
          ? battery.last_charged_capacity.toString()
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

  useEffect(() => {
    if (ports !== null && selectedCharger !== null) {
      setSelectedSlots([]);
      for (let index = 0; index < selectedCharger.slots; index++) {
        setSelectedSlots((prev) => [...prev, { id: 0, slot: index + 1 }]);
      }
      ports?.find((findport) => {
        console.log("Checking port", findport);
        if (selectedCharger?.tty === findport) {
          console.log("Port found", findport);
          setPort(findport);
        }
      });
    }
  }, [ports, selectedCharger]);

  function handleContinueBattery() {
    const searchCharger: ChargerSearch = {
      types: selectedBatteries?.map((battery) => battery.type.shortcut) ?? [],
      sizes: selectedBatteries?.map((battery) => battery.size.name) ?? [],
    };
    searchChargerData(searchCharger, setChargers);
  }

  async function handleContinueCharger() {
    if (!selectedCharger) {
      console.error("No charger selected");
      return;
    }
    console.log("Selected slots", selectedSlots);

    await fetchPorts(setPorts);
    console.log("Ports", ports);

    setStage(Stage.FINISH);
  }

  function setBatterySlot(value: string) {
    const newslots = selectedSlots.map((slot) => {
      const [id, slotNumber] = value.split(",");
      console.log(id, slotNumber);
      if (Number(slot.slot) === parseInt(slotNumber)) {
        return { id: Number(id), slot: slot.slot };
      }
      return slot;
    });
    console.log(newslots);
    setSelectedSlots(newslots);
    console.log(value);
  }

  function checkBatterySlotsEmpty(): boolean {
    let counter = 0;
    for (let i = 0; i < selectedSlots.length; i++) {
      if (selectedSlots[i].id !== 0) {
        counter++;
      }
    }
    return counter === selectedBatteries?.length;
  }

  function checkBatterySlotsConflicts(): boolean {
    for (let i = 0; i < selectedSlots.length; i++) {
      const slot = selectedSlots[i];
      if (slot.id === 0) {
        continue;
      }
      for (let j = 0; j < selectedSlots.length; j++) {
        if (i === j) {
          continue;
        }
        if (slot.id === selectedSlots[j].id) {
          return false;
        }
      }
    }
    return true;
  }

  async function handleStart() {
    console.log("Starting the process");
    console.log("Selected batteries:", selectedBatteries);
    console.log("Selected charger:", selectedCharger);
    console.log("Selected slots:", selectedSlots);
    if (port === null) {
      console.error("No port selected", port);
      return;
    }
    const result = checkBatterySlotsEmpty();
    if (!result) {
      console.error("Not all slots are filled");
      return;
    }
    const conflicts = checkBatterySlotsConflicts();
    if (!conflicts) {
      console.error("There are conflicts in the slots");
      return;
    }
    if (selectedCharger?.tty !== port) {
      await updatePort(selectedChargerId, port);
    }
    checkBatterySlotsConflicts();
    const filteredSlots = selectedSlots.filter((slot) => slot.id !== 0);
    console.log("Filtered slots:", filteredSlots);
    startTracking({
      id_charger: selectedChargerId,
      batteries: filteredSlots,
    });
    navigate("/");
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
                    {selectedBatteries?.map((battery) => (
                      <div
                        key={battery.id}
                        className="flex items-center justify-between"
                      >
                        <div className="flex items-center gap-2 whitespace-nowrap">
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
                        searchbarname="batteries"
                        columns={batteryColumns}
                        data={data}
                        setSelectedIds={handleBatterySelectionChange}
                        setSelectedId={() => {}}
                        multiRowSelection
                        idname="id"
                        sortiddesc={false}
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
                        searchbarname="chargers"
                        columns={chargerColumns}
                        data={chargerData || []}
                        setSelectedId={handleChargerSelectionChange}
                        setSelectedIds={() => {}}
                        multiRowSelection={false}
                        idname="id"
                        sortiddesc={false}
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
        <Card className="w-full xl:w-1/3 lg:w-1/2 md:w-1/2 ms-4 me-4 mt-4">
          <CardHeader>
            <CardTitle>
              <h4 className="font-semibold">Summary</h4>
            </CardTitle>
          </CardHeader>
          <CardContent>
            <div>
              <h3 className="font-semibold text-xl pb-4">
                {selectedCharger?.name}
              </h3>
              <div className="grid w-full justify-center">
                <div className="flex gap-1 items-center">
                  <span className="font-semibold">Port: </span>
                  <Select
                    defaultValue={
                      ports && ports.includes(selectedCharger?.tty ?? "")
                        ? selectedCharger?.tty
                        : undefined
                    }
                    onValueChange={(value) => {
                      console.log(value);
                      setPort(value);
                    }}
                  >
                    <SelectTrigger className="w-[180px]">
                      <SelectValue
                        placeholder={
                          ports && ports.length > 0
                            ? ports[0]
                            : "No ports available"
                        }
                      />
                    </SelectTrigger>
                    <SelectContent>
                      {ports?.map((port) => (
                        <SelectItem key={port} value={port}>
                          {port}
                        </SelectItem>
                      ))}
                    </SelectContent>
                  </Select>
                </div>
              </div>
            </div>
            <div>
              <h3 className="font-semibold text-xl pb-4">Batteries</h3>
              <div className="grid w-full justify-center">
                {Array.from({ length: selectedCharger?.slots ?? 0 }).map(
                  (_, index) => (
                    <div className="flex gap-1 items-center my-1">
                      <span className="font-semibold">Slot {index + 1}: </span>
                      <Select
                        defaultValue={"0," + (index + 1)}
                        onValueChange={setBatterySlot}
                        key={index + 1}
                      >
                        <SelectTrigger className="w-[180px]">
                          <SelectValue />
                        </SelectTrigger>
                        <SelectContent>
                          {selectedBatteries?.map((battery) => (
                            <SelectItem
                              key={battery.id}
                              value={battery.id.toString() + "," + (index + 1)}
                            >
                              {battery.id} - {battery.factory_capacity} mAh
                            </SelectItem>
                          ))}
                          <SelectItem value={"0," + (index + 1)}>
                            Empty
                          </SelectItem>
                        </SelectContent>
                      </Select>
                    </div>
                  )
                )}
              </div>
            </div>
            <div className="flex justify-center my-1">
              <Button onClick={() => handleStart()}>Start</Button>
            </div>
          </CardContent>
        </Card>
      )}
    </div>
  );
}
