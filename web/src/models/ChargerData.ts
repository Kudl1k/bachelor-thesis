import { BatteryWithSlot } from "./BatteryData";
import { Size } from "./SizeData";
import { Type } from "./TypeData";


export interface Charger{
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
    sizes: Size[]
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

export async function fetchChargerData(setChargerData: (data: Charger[]) => void) {
    try {
      const response = await fetch("http://127.0.0.1:8080/chargers");
        if (!response.ok) {
            throw new Error("Network response was not ok");
        }
        const data: Charger[] = await response.json();
        setChargerData(data);
    }
    catch (error) {
        console.error("Failed to fetch charger data:", error);
    }
}

export async function insertChargerData(chargerInsert: ChargerInsert): Promise<Charger | null> {
    try {
        const response = await fetch("http://127.0.0.1:8080/chargers",{
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
    }
    catch (error) {
        console.error("Failed to insert charger data:", error);
        return null;
    }
}

export async function searchChargerData(search: ChargerSearch, setChargerData: (data: Charger[]) => void) {
    try {
        const response = await fetch("http://127.0.0.1:8080/chargers/search", {
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
    }
    catch (error) {
        console.error("Failed to search charger data:", error);
    }
}

export async function fetchPorts(setTtys: (data: string[]) => void){
    try {
        const response = await fetch("http://127.0.0.1:8080/chargers/ports");
        if (!response.ok) {
            throw new Error("Network response was not ok");
        }
        const data: string[] = await response.json();
        setTtys(data);
    }
    catch (error) {
        console.error("Failed to fetch ports:", error);
    }
}

export async function startTracking(tracking: Tracking){
    try {
        const response = await fetch(`http://127.0.0.1:8080/chargers/${tracking.id_charger}/tracking/start`, {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
            },
            body: JSON.stringify(tracking),
        });
        if (!response.ok) {
            throw new Error("Network response was not ok");
        }
        console.log("Tracking started");
    }
    catch (error) {
        console.error("Failed to start tracking:", error);
    }
}

export async function stopTracking(id_charger: number){
    try {
        const response = await fetch(`http://127.0.0.1:8080/chargers/${id_charger}/tracking/start`, {
            method: "GET",
        });
        if (!response.ok) {
            throw new Error("Network response was not ok");
        }
        console.log("Tracking stopped");
    }
    catch (error) {
        console.error("Failed to stop tracking:", error);
    }
}

