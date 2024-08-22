import { Menubar } from "@/components/ui/menubar";
import { NavBarButton } from "./buttons/NavbarButton";
import { useEffect, useState } from "react";
import { Battery, Cable, Zap } from "lucide-react";
import { useLocation } from "react-router-dom";
import { SettingsComponent } from "./settings/Settings";
import { HamburgerNavbarButton } from "./buttons/HamburgerNavbarButton";

export function CustomMenubar() {
    const location = useLocation();
    const [activeButton, setActiveButton] = useState<string>("");
    const [isSmallScreen, setIsSmallScreen] = useState<boolean>(false);

    useEffect(() => {
        const path = location.pathname;
        if (path === "/") {
            setActiveButton("Start");
        } else if (path.startsWith("/battery")) {
            setActiveButton("Baterie");
        } else if (path.startsWith("/chargers")) {
            setActiveButton("Akumulatory");
        } else {
            setActiveButton("Start");
        }
    }, [location.pathname]);

    useEffect(() => {
        const handleResize = () => {
            setIsSmallScreen(window.innerWidth < 640);
        };

        window.addEventListener("resize", handleResize);
        handleResize();

        return () => window.removeEventListener("resize", handleResize);
    }, []);

    return (
        <nav className="sticky top-0 mt-4 ms-4 me-4">
            <div className="flex justify-center">
                <Menubar className="flex justify-between w-full xl:w-4/6 h-13 shadow-lg">
                    {isSmallScreen ? (
                        <HamburgerNavbarButton activeButton={activeButton}/>
                    ) : (
                        <div className="flex">
                            <NavBarButton
                                text="Dashboard"
                                link="/"
                                active={activeButton === "Start"}
                                icon={Zap}
                            />
                            <NavBarButton
                                text="Battery"
                                link="/battery"
                                active={activeButton === "Baterie"}
                                icon={Battery}
                            />
                            <NavBarButton
                                text="Chargers"
                                link="/chargers"
                                active={activeButton === "Akumulatory"}
                                icon={Cable}
                            />
                        </div>
                    )}
                    <div className="p-2">
                        <SettingsComponent />
                    </div>
                </Menubar>
            </div>
        </nav>
    );
}