import { ChargersCard } from "@/components/cards/ChargersCard";
import { InfoBox } from "@/components/InfoBox";
import { Loading } from "@/components/Loading";
import { PaggingMenu } from "@/components/PaggingMenu";
import { buttonVariants } from "@/components/ui/button";
import { Charger, fetchChargerData } from "@/models/ChargerData";
import { PlugZap } from "lucide-react";
import { useEffect, useRef, useState } from "react";
import { Link } from "react-router-dom";



export function ChargersPage() {
    const [chargers, setChargers] = useState<Charger[] | null>(null);
    const [page, setPage] = useState(1);
    const [perPage] = useState(9);
    const hasFetched = useRef(false);


    useEffect(() => {
        if (!hasFetched.current) {
            fetchChargerData(setChargers);

            hasFetched.current = true;
        }
    }, []);

    useEffect(() => {
        const params = new URLSearchParams(location.search);
        const pageParam = params.get('page');
        if (pageParam) {
            setPage(Number(pageParam));
        }
    }, []);


    if (!chargers) {
        return Loading();
    }

    console.log(chargers);

    const start = (Number(page) - 1) * Number(perPage)
    const end = start + Number(perPage)

    const paginatedData = chargers.slice(start, end)


    return (
        <>
            <div className="flex justify-center">
                <div className="flex justify-end w-full xl:w-4/6 ps-4 pe-4 pt-4">
                <div>
                    <Link className={buttonVariants({ variant: "default" })} to={"/add?tab=charger"} >Add new charger</Link>
                </div>
                </div>
            </div>
            {chargers.length === 0 && 
                <div className="flex justify-center">
                    <div className="w-full xl:w-1/4 lg:w-3/6 md:w-4/6 ps-4 pe-4 pt-4" >
                        <InfoBox
                            alertTitle="No batteries found"
                            alertDescription="There are no batteries found in the database. Please add a new battery."
                            icon={PlugZap}
                        />
                    </div>
                </div>  
            }
            <div className="flex justify-center">
                <div className="w-full xl:w-4/6 grid grid-cols-1 sm:grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4 p-4">
                    {paginatedData.map((charger) => (
                        <ChargersCard key={charger.id} charger={charger}/>    
                    ))}
                </div>
            </div>
            {chargers.length > 0 && 
                <PaggingMenu totalPosts={chargers.length} postsPerPage={perPage} currentPage={page} setCurrentPage={setPage} />  
            }
        </>
    )

}