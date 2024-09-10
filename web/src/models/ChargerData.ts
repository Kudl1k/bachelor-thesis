import { useEffect } from "react";
import { Battery, BatteryWithSlot } from "./BatteryData";
import { DEFAULTURL } from "./Default";
import { Size } from "./SizeData";
import { Type } from "./TypeData";

export interface Charger {
  id: number;
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

export interface ChargerInsert {
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
      throw new Error("Network response was not ok");
    }
    const createdCharger: Charger = await response.json();
    console.log("Charger data inserted:", createdCharger);
    return createdCharger;
  } catch (error) {
    console.error("Failed to insert charger data:", error);
    return null;
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
        return prevRecords.map((record) => {
          const chargeRecordId = Array.isArray(data)
            ? data[0].charge_record_id
            : data.charge_record_id;
          if (Number(record.idChargeRecord) === Number(chargeRecordId)) {
            console.log("Record found:", record);
            if (Array.isArray(data)) {
              console.log("Data is array");
              return {
                ...record,
                tracking: data,
              };
            } else {
              return {
                ...record,
                tracking: [...record.tracking, data],
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
