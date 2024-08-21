export interface Type {
    id: number;
    shortcut: string;
    name: string;
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