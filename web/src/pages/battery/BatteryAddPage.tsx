// src/pages/battery/BatteryAddPage.tsx
import { buttonVariants } from "@/components/ui/button";
import { fetchTypeData, Type } from "@/models/TypeData";
import { useState, useEffect } from "react";
import { Link } from "react-router-dom";
import { BatteryAddFormSchema } from "@/forms/BatteryAddFormSchema";
import { Loading } from "@/components/Loading";


export function BatteryAddPage() {
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
                <div className="flex justify-start w-full xl:w-4/6 ps-4 pe-4 pt-4">
                    <Link className={buttonVariants()} to={"/battery"}>Go Back</Link>
                </div>
            </div>
            <div className="flex justify-center">
                <div className="w-full xl:w-1/4 lg:w-3/6 md:w-4/6 ps-4 pe-4 pt-4">
                  <BatteryAddFormSchema types={types}/>

                </div>
            </div>
        </>
    );
}