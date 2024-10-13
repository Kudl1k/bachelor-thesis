import { useEffect } from "react";
import { toast } from "sonner";
import { Battery, BatteryWithSlot } from "./BatteryData";
import { DEFAULTURL } from "./Default";
import { Size } from "./SizeData";
import { Type } from "./TypeData";

export interface Charger {
  id: number;
  parser: Parser;
  name: string;
  tty: string;
  baudRate: number;
  dataBits: number;
  stopBits: number;
  parity: number;
  rts: boolean;
  dtr: boolean;
  slots: number;
  created_at: string;
  types: Type[];
  sizes: Size[];
}

export interface Parser {
  id: number;
  name: string;
}

export interface ChargerInsert {
  parser: number;
  name: string;
  tty: string;
  baudRate: number;
  dataBits: number;
  stopBits: number;
  parity: number;
  rts: boolean;
  dtr: boolean;
  slots: number;
  types: string[];
  sizes: string[];
}

export interface ChargerColumnType {
  id: number;
  name: string;
  tty: string;
  slots: number;
  types: string[];
  sizes: string[];
}

export interface ChargerSearch {
  types: string[];
  sizes: string[];
}

export interface Tracking {
  id_charger: number;
  batteries: BatteryWithSlot[];
}

export interface ChargeRecord {
  idChargeRecord: number;
  slot: number;
  startedAt: string;
  finishedAt: string;
  initialCapacity: number;
  chargedCapacity: number | null;
  dischargedCapacity: number | null;
  charger: Charger;
  battery: Battery;
  tracking: TrackingRecord[];
  cells: CellWithTracking[];
}

export interface TrackingRecord {
  timestamp: string;
  charge_record_id: number;
  charging: boolean;
  real_capacity: number;
  capacity: number;
  voltage: number;
  current: number;
}

export interface CellWithTracking {
  idChargeRecord: number;
  number: number;
  voltages: CellTrakcing[];
}

export interface Cell {
  idChargeRecord: number;
  number: number;
}

export interface CellTrakcing {
  timestamp: string;
  idChargeRecord: number;
  number: number;
  voltage: number;
}

export interface ChargeTrackingWithCellTrackings {
  formatedChargeTracking: TrackingRecord;
  formatedCellTrackings: CellTrakcing[] | null;
}

export async function fetchChargerData(
  setChargerData: (data: Charger[]) => void
) {
  try {
    const response = await fetch(`${DEFAULTURL}/chargers`);
    if (!response.ok) {
      throw new Error("Network response was not ok");
    }
    const data: Charger[] = await response.json();
    console.log("Charger data fetched:", data);
    setChargerData(data);
  } catch (error) {
    console.error("Failed to fetch charger data:", error);
  }
}

export async function insertChargerData(
  chargerInsert: ChargerInsert
): Promise<Charger | null> {
  try {
    const response = await fetch(`${DEFAULTURL}/chargers`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify(chargerInsert),
    });
    if (!response.ok) {
      toast.error("There was an error, while creating a charger :(");
      throw new Error("Network response was not ok");
    }
    const createdCharger: Charger = await response.json();
    toast("Charger has been succesfully created!");
    console.log("Charger data inserted:", createdCharger);
    return createdCharger;
  } catch (error) {
    console.error("Failed to insert charger data:", error);
    return null;
  }
}

export async function fetchChargerInfo(
  id: number,
  setCharger: (data: Charger) => void
) {
  try {
    const response = await fetch(`${DEFAULTURL}/chargers/${id}`);
    if (!response.ok) {
      throw new Error("Network response was not ok");
    }
    const data: Charger = await response.json();
    setCharger(data);
  } catch (error) {
    console.error("Failed to fetch charger info:", error);
  }
}

export async function searchChargerData(
  search: ChargerSearch,
  setChargerData: (data: Charger[]) => void
) {
  try {
    const response = await fetch(`${DEFAULTURL}/chargers/search`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify(search),
    });
    if (!response.ok) {
      throw new Error("Network response was not ok");
    }
    const data: Charger[] = await response.json();
    setChargerData(data);
  } catch (error) {
    console.error("Failed to search charger data:", error);
  }
}

export async function fetchPorts(setTtys: (data: string[]) => void) {
  try {
    const response = await fetch(`${DEFAULTURL}/chargers/ports`);
    if (!response.ok) {
      throw new Error("Network response was not ok");
    }
    const data: string[] = await response.json();
    console.log("Ports fetched:", data);
    setTtys(data);
  } catch (error) {
    console.error("Failed to fetch ports:", error);
  }
}

export async function startTracking(tracking: Tracking) {
  try {
    const response = await fetch(
      `${DEFAULTURL}/chargers/${tracking.id_charger}/tracking/start`,
      {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify(tracking),
      }
    );
    if (!response.ok) {
      toast.error("Tracking can`t start.");
      throw new Error("Network response was not ok");
    }
    console.log("Tracking started");
  } catch (error) {
    console.error("Failed to start tracking:", error);
  }
}

export async function stopTracking(id_charger: number) {
  try {
    const response = await fetch(
      `${DEFAULTURL}/chargers/${id_charger}/tracking/start`,
      {
        method: "GET",
      }
    );
    if (!response.ok) {
      throw new Error("Network response was not ok");
    }
    console.log("Tracking stopped");
  } catch (error) {
    console.error("Failed to stop tracking:", error);
  }
}

export async function fetchNotEndedChargeRecord(
  setChargeRecord: (data: ChargeRecord[]) => void
) {
  try {
    const response = await fetch(`${DEFAULTURL}/chargers/records/notended`);
    {
      if (!response.ok) {
        throw new Error("Network response was not ok");
      }
      const data: ChargeRecord[] = await response.json();
      setChargeRecord(data);
    }
  } catch (error) {
    console.error("Failed to fetch not ended charge record:", error);
  }
}

export async function fetchTrackingRecord(
  id_charge_record: number,
  setTrackingRecord: (data: TrackingRecord[]) => void
) {
  try {
    const response = await fetch(
      `${DEFAULTURL}/chargers/records/${id_charge_record}/tracking`
    );
    if (!response.ok) {
      throw new Error("Network response was not ok");
    }
    const data: TrackingRecord[] = await response.json();
    setTrackingRecord(data);
  } catch (error) {
    console.error("Failed to fetch tracking record:", error);
  }
}

export interface useWebSocketTrackingProps {
  id_charger: number;
  setChargeRecords: React.Dispatch<React.SetStateAction<ChargeRecord[]>>;
}

export function useWebSocketTracking({
  id_charger,
  setChargeRecords,
}: useWebSocketTrackingProps) {
  useEffect(() => {
    const ws = new WebSocket(
      `ws://localhost:8080/chargers/${id_charger}/tracking/last`
    );

    ws.onopen = () => {
      console.log("Connected to websocket");
    };

    ws.onmessage = (event) => {
      const data = JSON.parse(event.data);
      console.log("Received data:", data);
      setChargeRecords((prevRecords) => {
        if (prevRecords.length === 0 && data != null) {
          window.location.reload();
        }
        return prevRecords.map((record) => {
          const chargeRecordId = Array.isArray(data.formatedChargeTracking)
            ? data.formatedChargeTracking[0].charge_record_id
            : data.formatedChargeTracking.charge_record_id;
          if (Number(record.idChargeRecord) === Number(chargeRecordId)) {
            console.log("Record found:", record);
            if (Array.isArray(data.formatedChargeTracking)) {
              console.log("formatedChargeTracking is array");
              return {
                ...record,
                tracking: [...record.tracking, ...data.formatedChargeTracking],
                cells: record.cells.map((cell) => {
                  const updatedVoltages: CellTrakcing[] =
                    data.formatedCellTrackings
                      ?.filter(
                        (cellTracking: CellTrakcing) =>
                          cellTracking.number === cell.number
                      )
                      .map((cellTracking: CellTrakcing) => ({
                        ...cellTracking,
                      })) || [];
                  return {
                    ...cell,
                    voltages: [...cell.voltages, ...updatedVoltages],
                  };
                }),
              };
            } else {
              return {
                ...record,
                tracking: [...record.tracking, data.formatedChargeTracking],
                cells: record.cells.map((cell) => {
                  const updatedVoltages: CellTrakcing[] =
                    data.formatedCellTrackings
                      ?.filter(
                        (cellTracking: CellTrakcing) =>
                          cellTracking.number === cell.number
                      )
                      .map((cellTracking: CellTrakcing) => ({
                        ...cellTracking,
                      })) || [];
                  return {
                    ...cell,
                    voltages: [...cell.voltages, ...updatedVoltages],
                  };
                }),
              };
            }
          }
          return record;
        });
      });
    };

    ws.onclose = () => {
      console.log("Disconnected from websocket");
    };

    ws.onerror = (error) => {
      console.error("Websocket error:", error);
    };

    return () => {
      ws.close();
    };
  }, [id_charger, setChargeRecords]);
}

export async function updatePort(charge_id: number, port: string) {
  try {
    const response = await fetch(`${DEFAULTURL}/chargers/${charge_id}/port`, {
      method: "PUT",
      headers: {
        "Content-Type": "application/json",
      },
      body: port,
    });
    if (!response.ok) {
      throw new Error("Network response was not ok");
    }
    console.log("Port updated");
  } catch (error) {
    console.error("Failed to update port:", error);
  }
}

export async function addChargerSize(
  charge_id: number,
  size: string
): Promise<Charger | null> {
  try {
    const response = await fetch(`${DEFAULTURL}/chargers/${charge_id}/size/${size}`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: size,
    });
    if (!response.ok) {
      throw new Error("Network response was not ok");
    }
    const updatedCharger: Charger = await response.json();
    console.log("Charger size updated:", updatedCharger);
    return updatedCharger;
  } catch (error) {
    console.error("Failed to update charger size:", error);
    return null;
  }
}

export async function addChargerType(
  charge_id: number,
  type: string
): Promise<Charger | null> {
  try {
    const response = await fetch(`${DEFAULTURL}/chargers/${charge_id}/type/${type}`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: type,
    });
    if (!response.ok) {
      throw new Error("Network response was not ok");
    }
    const updatedCharger: Charger = await response.json();
    console.log("Charger type updated:", updatedCharger);
    return updatedCharger;
  } catch (error) {
    console.error("Failed to update charger type:", error);
    return null;
  }
}

export async function removeChargerSize(
  charge_id: number,
  size: string
): Promise<Charger | null> {
  try {
    const response = await fetch(
      `${DEFAULTURL}/chargers/${charge_id}/size/${size}`,
      {
        method: "DELETE",
      }
    );
    if (!response.ok) {
      throw new Error("Network response was not ok");
    }
    const updatedCharger: Charger = await response.json();
    console.log("Charger size removed:", updatedCharger);
    return updatedCharger;
  } catch (error) {
    console.error("Failed to remove charger size:", error);
    return null;
  }
}

export async function removeChargerType(
  charge_id: number,
  type: string
): Promise<Charger | null> {
  try {
    const response = await fetch(
      `${DEFAULTURL}/chargers/${charge_id}/type/${type}`,
      {
        method: "DELETE",
      }
    );
    if (!response.ok) {
      throw new Error("Network response was not ok");
    }
    const updatedCharger: Charger = await response.json();
    console.log("Charger type removed:", updatedCharger);
    return updatedCharger;
  } catch (error) {
    console.error("Failed to remove charger type:", error);
    return null;
  }
}

export async function fetchParsers(
  setParsers: (data: Parser[]) => void
) {
  try {
    const response = await fetch(`${DEFAULTURL}/chargers/parsers`);
    if (!response.ok) {
      throw new Error("Network response was not ok");
    }
    const data: Parser[] = await response.json();
    console.log("Parsers fetched:", data);
    setParsers(data);
  } catch (error) {
    console.error("Failed to fetch parsers:", error);
  }
}