import { toast } from "sonner";
import { DEFAULTURL } from "./Default";

export interface Size {
  name: string;
}

export async function fetchSizeData(setSizeData: (data: Size[]) => void) {
  try {
    const response = await fetch(`http://${DEFAULTURL}/sizes`);
    if (!response.ok) {
      throw new Error("Network response was not ok");
    }
    const data: Size[] = await response.json();
    setSizeData(data);
  } catch (error) {
    console.error("Failed to fetch size data:", error);
  }
}

export async function insertSizeData(sizeInsert: Size): Promise<Size | null> {
  try {
    const response = await fetch(`http://${DEFAULTURL}/sizes`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify(sizeInsert),
    });
    if (!response.ok) {
      toast.error("There was an error, while creating a size :(");
      throw new Error("Network response was not ok");
    }
    toast("Size has been succesfully created!");
    return response.json();
  } catch (error) {
    console.error("Failed to insert size data:", error);
    return null;
  }
}
