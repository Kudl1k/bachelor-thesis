import { Loading } from "@/components/Loading";
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs";
import { BatteryAddFormSchema } from "@/forms/BatteryAddFormSchema";
import { ChargerAddFormSchema } from "@/forms/ChargerAddFormSchema";
import { SizeAddFormSchema } from "@/forms/SizeAddFromSchema";
import { TypeAddFormSchema } from "@/forms/TypesAddFormSchema";
import { fetchNewId } from "@/models/BatteryData";
import { fetchPorts } from "@/models/ChargerData";
import { fetchSizeData, Size } from "@/models/SizeData";
import { fetchTypeData, Type } from "@/models/TypeData";
import { useEffect, useState } from "react";
import { useSearchParams } from "react-router-dom";

export function FormsAddPage() {
  const [types, setTypes] = useState<Type[] | null>(null);
  const [sizes, setSizes] = useState<Size[] | null>(null);
  const [ttys, setTtys] = useState<string[] | null>(null);
  const [newId, setNewId] = useState<string | null>(null);
  const [searchParams] = useSearchParams();
  const defaultTab = searchParams.get("tab") || "type";

  useEffect(() => {
    fetchTypeData(setTypes);
    fetchSizeData(setSizes);
    fetchPorts(setTtys);
    fetchNewId(setNewId);
  }, []);

  if (!types || !sizes || !ttys || !newId) {
    return Loading();
  }

  return (
    <div className="flex justify-center w-screen pt-4">
      <Tabs defaultValue={defaultTab} className="w-[400px]">
        <TabsList className="grid w-full grid-cols-4">
          <TabsTrigger value="type">Type</TabsTrigger>
          <TabsTrigger value="size">Size</TabsTrigger>
          <TabsTrigger value="battery">Battery</TabsTrigger>
          <TabsTrigger value="charger">Charger</TabsTrigger>
        </TabsList>
        <TabsContent value="type">
          <TypeAddFormSchema />
        </TabsContent>
        <TabsContent value="size">
          <SizeAddFormSchema />
        </TabsContent>
        <TabsContent value="battery">
          <BatteryAddFormSchema types={types} sizes={sizes} newId={newId} />
        </TabsContent>
        <TabsContent value="charger">
          <ChargerAddFormSchema types={types} sizes={sizes} ttys={ttys} />
        </TabsContent>
      </Tabs>
    </div>
  );
}
