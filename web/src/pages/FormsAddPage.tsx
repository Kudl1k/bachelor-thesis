
import { Loading } from "@/components/Loading";
import {
  Tabs,
  TabsContent,
  TabsList,
  TabsTrigger,
} from "@/components/ui/tabs"
import { BatteryAddFormSchema } from "@/forms/BatteryAddFormSchema";
import { TypeAddFormSchema } from "@/forms/TypesAddFormSchema";
import { Type, fetchTypeData } from "@/models/TypeData";
import { useState, useEffect } from "react";
import { useSearchParams } from "react-router-dom";




export function FormsAddPage() {
  const [types, setTypes] = useState<Type[] | null>(null);
  const [searchParams] = useSearchParams();
  const defaultTab = searchParams.get('tab') || 'type';

  useEffect(() => {
      fetchTypeData(setTypes);
  }, []);

  if (!types) {
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
            <TypeAddFormSchema/>
        </TabsContent>
        <TabsContent value="size"> 
            
        </TabsContent>
        <TabsContent value="battery">
          <BatteryAddFormSchema types={types}/>
        </TabsContent>
        <TabsContent value="charger">
            
        </TabsContent>
      </Tabs>
    </div>
    
  )
}