import { Size } from "./SizeData";
import { Type } from "./TypeData";



export interface Battery {
  id: number;
  type: Type;
  size: Size;
  factory_capacity: number;
  voltage: number;
  last_charged_capacity: null;
  last_time_charged_at: null;
  created_at: string;
}

export interface BatteryInsert {
  type: string,
  size: string,
  factory_capacity: number,
  voltage: number,
}


export async function fetchBatteryData(setBatteryData: (data: Battery[]) => void) {
    try {
      const response = await fetch("http://127.0.0.1:8080/batteries");
      if (!response.ok) {
        throw new Error("Network response was not ok");
      }
      const data: Battery[] = await response.json();
      setBatteryData(data);
    } catch (error) {
      console.error("Failed to fetch battery data:", error);
    }
}

export async function insertBatteryData(batteryInsert: BatteryInsert): Promise<Battery | null> {
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
