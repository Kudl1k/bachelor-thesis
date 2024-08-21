import { ChargersCard } from "@/components/cards/ChargersCard";
import { Loading } from "@/components/Loading";
import { fetchTypeData, Type } from "@/models/TypeData";
import { useEffect, useState } from "react";



export function ChargersPage() {
    const [types, setTypes] = useState<Type[] | null>(null);
    
    useEffect(() => {
        fetchTypeData(setTypes);
    }, []);


    if (!types) {
        return Loading();
    }

    return (
        <>
            <div className="flex justify-center">
                <div className="w-full xl:w-4/6 grid grid-cols-1 sm:grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4 p-4">
                    <ChargersCard/>
                </div>
            </div>
        </>
    )

}