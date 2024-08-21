import { BatteryCard } from "@/components/cards/BatteryCard";
import { PaggingMenu } from "@/components/PaggingMenu";
import { buttonVariants } from "@/components/ui/button";
import { Battery, fetchBatteryData } from "@/models/BatteryData";
import { useState, useEffect, useRef } from "react";
import { Link } from "react-router-dom";



export function BaterryPage() {
    const [batteryData, setBatteryData] = useState<Battery[] | null>(null);
    const [page, setPage] = useState(1);
    const [perPage] = useState(9);
    const hasFetched = useRef(false);

    useEffect(() => {
        if (!hasFetched.current) {
            fetchBatteryData(setBatteryData);
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

    if (!batteryData) {
        return <div>Loading...</div>;
    }
    if (batteryData.length < 1) {
        return <div>No battery data available</div>;
    }
    

    const start = (Number(page) - 1) * Number(perPage)
    const end = start + Number(perPage)

    const paginatedData = batteryData.slice(start, end)

    

    return (
        <>
            <div className="flex justify-center">
                <div className="flex justify-end w-full xl:w-4/6 ps-4 pe-4 pt-4">
                <Link className={buttonVariants({ variant: "default" })} to={"/battery/add"} >Add new battery</Link>
                </div>
            </div>
            <div className="flex justify-center">
                <div className="w-full xl:w-4/6 grid grid-cols-1 sm:grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4 p-4">
                    {paginatedData.map((battery) => (
                        <BatteryCard key={battery.id} battery={battery} />
                    ))}
                </div>
            </div>
            <PaggingMenu totalPosts={batteryData.length} postsPerPage={perPage} currentPage={page} setCurrentPage={setPage} />  
        </>
    );
}
