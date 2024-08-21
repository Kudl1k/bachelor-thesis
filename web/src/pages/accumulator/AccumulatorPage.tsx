import { ComboboxForm } from "@/forms/TestCombobox";
import { fetchTypeData, Type } from "@/models/TypeData";
import { useEffect, useState } from "react";



export function AccumulatorPage() {
    const [types, setTypes] = useState<Type[] | null>(null);
    
    useEffect(() => {
        fetchTypeData(setTypes);
    }, []);


    if (!types) {
        return <div>Loading...</div>;
    }

    return (
        <>
            <div className="flex justify-center items-center min-h-screen min-w-screen">
                <ComboboxForm types={types}/>
            </div>
        </>
    )

}