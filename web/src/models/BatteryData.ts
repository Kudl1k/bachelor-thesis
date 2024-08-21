import { Type } from "./TypeData";



export interface Battery {
  id: number;
  type: Type;
  size: string;
  factory_capacity: number;
  voltage: number;
  last_charged_capacity: null;
  last_time_charged_at: null;
  created_at: string;
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
