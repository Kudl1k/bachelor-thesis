import { toast } from "sonner";

export interface Type {
  shortcut: string;
  name: string;
}

export interface TypeInsert {
  name: string;
  shortcut: string;
}

export async function fetchTypeData(setTypeData: (data: Type[]) => void) {
  try {
    const response = await fetch("http://127.0.0.1:8080/types");
    if (!response.ok) {
      throw new Error("Network response was not ok");
    }
    const data: Type[] = await response.json();
    setTypeData(data);
  } catch (error) {
    console.error("Failed to fetch type data:", error);
  }
}

export async function insertTypeData(
  typeInsert: TypeInsert
): Promise<Type | null> {
  try {
    const response = await fetch("http://127.0.0.1:8080/types", {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify(typeInsert),
    });

    if (!response.ok) {
      toast.error("There was an error, while creating a type :(");
      throw new Error("Network response was not ok");
    }

    const createdBattery: Type = await response.json();
    toast("Type has been succesfully created!");
    return createdBattery;
  } catch (error) {
    console.error("Failed to insert battery data:", error);
    return null;
  }
}
