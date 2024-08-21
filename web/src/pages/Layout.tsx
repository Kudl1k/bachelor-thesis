import { CustomMenubar } from "@/components/custom-menubar"
import { Outlet } from "react-router-dom"


const Layout = () => {
    return (
        <>
            <CustomMenubar/>
            <Outlet />
        </>
    )    
}

export default Layout