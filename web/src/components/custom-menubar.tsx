import { Menubar } from "@/components/ui/menubar"
import { ModeToggle } from "./mode-toggle";
import { NavBarButton } from "./buttons/NavbarButton";
import { useState } from "react";
import { Battery, Cable, Zap } from "lucide-react";


export function CustomMenubar() {
    const [activeButton, setActiveButton] = useState<string>("Start");

    const handleButtonClick = (buttonText: string) => {
        setActiveButton(buttonText);
    };

    return (
        <nav className="sticky top-0 mt-4 ms-4 me-4 ">
            <div className="flex justify-center">           
                <Menubar className="flex justify-between w-full xl:w-4/6 h-13 shadow-lg">
                    <div>
                        <NavBarButton 
                            text="Charge" 
                            link="/" 
                            active={activeButton === "Start"}
                            icon={Zap}
                            onClick={() => handleButtonClick("Start")} 
                        />
                        <NavBarButton 
                            text="Battery" 
                            link="/battery" 
                            active={activeButton === "Baterie"} 
                            icon={Battery}
                            onClick={() => handleButtonClick("Baterie")}
                        />
                        <NavBarButton 
                            text="Accumulators" 
                            link="/accumulators" 
                            active={activeButton === "Akumulatory"} 
                            icon={Cable}
                            onClick={() => handleButtonClick("Akumulatory")}
                        />
                    </div>
                    <ModeToggle />
                </Menubar>
            </div>
        </nav>
    );
}