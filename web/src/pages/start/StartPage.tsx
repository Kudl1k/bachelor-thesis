import { buttonVariants } from "@/components/ui/button"
import { Link } from "react-router-dom"

export function StartPage() {

    return (
        <>
            <div className="flex justify-center items-center min-h-screen min-w-screen">
                <Link className={buttonVariants({ variant: "default" })} to={"/setup"}>Setup</Link>
            </div>
        </>
    )
 
}