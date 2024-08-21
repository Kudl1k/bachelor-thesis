import { buttonVariants } from "@/components/ui/button";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { BatteryAddFormSchema } from "@/forms/BatteryAddFormSchema";
import { fetchTypeData, Type } from "@/models/TypeData";
import { useState, useEffect } from "react";
import { Link } from "react-router-dom";

 



export function BatteryAddPage() {
    
    const [types, setTypes] = useState<Type[] | null>(null);

    useEffect(() => {
        fetchTypeData(setTypes);
    }, []);

    if (!types) {
        return <div>Loading...</div>;
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
                    <Card>
                        <CardHeader>
                            <CardTitle>Create a new battery</CardTitle>
                        </CardHeader>
                        <CardContent>
                            <BatteryAddFormSchema types={types} onSubmitForm={() => {}} />
                        </CardContent>
                    </Card>
                </div>
            </div>
        </>
    )

}

