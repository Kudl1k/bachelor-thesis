import { toast } from "sonner";
import { ChargeRecord } from "./ChargerData";
import { DEFAULTURL } from "./Default";
import { Size } from "./SizeData";
import { Type } from "./TypeData";

export interface Battery {
  id: string;
  type: Type;
  size: Size;
  cells: number;
  factory_capacity: number;
  voltage: number;
  shop_link: string | null;
  last_charged_capacity: number | null;
  archived: boolean;
  last_time_charged_at: string | null;
  created_at: string;
}

export interface BatteryInsert {
  id: string;
  type: string;
  size: string;
  cells: number;
  factory_capacity: number;
  voltage: number;
  shop_link?: string | null;
}

export interface BatteryColumnType {
  id: string;
  type: string;
  size: string;
  cells: number;
  factory_capacity: number;
  voltage: number;
  last_charged_capacity: string;
  archived: boolean;
  last_time_charged_at: string;
}

export interface BatteryWithSlot {
  id: string;
  slot: number;
}

export interface BatteryInfo {
  id: string;
  type: Type;
  size: Size;
  cells: number;
  factory_capacity: number;
  voltage: number;
  shop_link: string | null;
  last_charged_capacity: number | null;
  archived: boolean;
  last_time_charged_at: string | null;
  created_at: string;
  charge_records: ChargeRecord[];
}

export const truncateText = (text: string, maxLength: number) => {
  if (text.length <= maxLength) {
    return text;
  }
  return text.substring(0, maxLength) + "...";
};

export async function fetchBatteryData(
  setBatteryData: (data: Battery[]) => void
) {
  try {
    const response = await fetch(`${DEFAULTURL}/batteries`);
    if (!response.ok) {
      throw new Error("Network response was not ok");
    }
    const data: Battery[] = await response.json();
    console.log("Battery data fetched:", data);
    setBatteryData(data);
  } catch (error) {
    toast("Server error", {
      description: "Please make sure that the server is on.",
    });
    console.error("Failed to fetch battery data:", error);
  }
}

export async function insertBatteryData(
  batteryInsert: BatteryInsert
): Promise<Battery | null> {
  try {
    const response = await fetch(`${DEFAULTURL}/batteries`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify(batteryInsert),
    });

    if (!response.ok) {
      toast.error("There was an error, while creating a battery :(");
      throw new Error("Network response was not ok");
    }

    const createdBattery: Battery = await response.json();
    console.log("Battery data inserted:", createdBattery);
    toast("Battery has been succesfully created!");
    return createdBattery;
  } catch (error) {
    console.error("Failed to insert battery data:", error);
    return null;
  }
}

export async function fetchBatteryInfo(
  id: string,
  setBatteryData: (data: BatteryInfo) => void
) {
  try {
    const response = await fetch(`${DEFAULTURL}/batteries/${id}/info`);
    if (!response.ok) {
      throw new Error("Network response was not ok");
    }
    const data: BatteryInfo = await response.json();
    setBatteryData(data);
  } catch (error) {
    console.error("Failed to fetch battery info:", error);
  }
}

export async function fetchNewId(setNewId: (id: string) => void) {
  try {
    const response = await fetch(`${DEFAULTURL}/batteries/newId`);
    if (!response.ok) {
      throw new Error("Network response was not ok");
    }

    const responseText = await response.text();
    console.log("Raw response text:", responseText);

    setNewId(responseText);
  } catch (error) {
    console.error("Failed to fetch new battery id:", error);
  }
}

export async function toggleArchived(id: string) {
  try {
    const response = await fetch(`${DEFAULTURL}/batteries/${id}/toggleArchive`);
    if (!response.ok) {
      throw new Error("Network response was not ok");
    }
    const responseText = await response.text();
    console.log("Succesfully toggled:", responseText);
  } catch (error) {
    console.log("Failed to toggle archive with id:", error);
  }
}
