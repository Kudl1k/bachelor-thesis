import { ChargeRecord } from "./ChargerData";
import { Size } from "./SizeData";
import { Type } from "./TypeData";

export interface Battery {
  id: number;
  type: Type;
  size: Size;
  factory_capacity: number;
  voltage: number;
  last_charged_capacity: number | null;
  last_time_charged_at: string | null;
  created_at: string;
}

export interface BatteryInsert {
  type: string;
  size: string;
  factory_capacity: number;
  voltage: number;
}

export interface BatteryColumnType {
  id: number;
  type: string;
  size: string;
  factory_capacity: number;
  voltage: number;
  last_charged_capacity: string | null;
  last_time_charged_at: string | null;
}

export interface BatteryWithSlot {
  id: number;
  slot: number;
}

export interface BatteryInfo {
  id: number;
  type: Type;
  size: Size;
  factory_capacity: number;
  voltage: number;
  last_charged_capacity: number | null;
  last_time_charged_at: string | null;
  created_at: string;
  charge_records: ChargeRecord[];
}

export async function fetchBatteryData(
  setBatteryData: (data: Battery[]) => void
) {
  try {
    const response = await fetch("http://127.0.0.1:8080/batteries");
    if (!response.ok) {
      throw new Error("Network response was not ok");
    }
    const data: Battery[] = await response.json();
    console.log("Battery data fetched:", data);
    setBatteryData(data);
  } catch (error) {
    console.error("Failed to fetch battery data:", error);
  }
}

export async function insertBatteryData(
  batteryInsert: BatteryInsert
): Promise<Battery | null> {
  try {
    const response = await fetch("http://127.0.0.1:8080/batteries", {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify(batteryInsert),
    });

    if (!response.ok) {
      throw new Error("Network response was not ok");
    }

    const createdBattery: Battery = await response.json();
    console.log("Battery data inserted:", createdBattery);
    return createdBattery;
  } catch (error) {
    console.error("Failed to insert battery data:", error);
    return null;
  }
}

export async function fetchBatteryInfo(
  id: number,
  setBatteryData: (data: BatteryInfo) => void
) {
  try {
    const response = await fetch(`http://127.0.0.1:8080/batteries/${id}/info`);
    if (!response.ok) {
      throw new Error("Network response was not ok");
    }
    const data: BatteryInfo = await response.json();
    setBatteryData(data);
  } catch (error) {
    console.error("Failed to fetch battery info:", error);
  }
}
