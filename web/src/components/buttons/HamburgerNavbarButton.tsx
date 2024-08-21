import {
    DropdownMenu,
    DropdownMenuContent,
    DropdownMenuItem,
    DropdownMenuTrigger,
  } from "@/components/ui/dropdown-menu"
import { Battery, Cable, Menu, Zap } from "lucide-react"
import { Button } from "../ui/button"
import { NavBarButton } from "./NavbarButton"


interface HamburgerNavbarButtonProps {
    activeButton: string;
}

export function HamburgerNavbarButton({activeButton}: HamburgerNavbarButtonProps) {
    return (
        <DropdownMenu>
            <DropdownMenuTrigger>
                <Button variant={"ghost"} className="shadow-md m-1">
                    <Menu />
                </Button>
            </DropdownMenuTrigger>
            <DropdownMenuContent align="start">
                <DropdownMenuItem>
                    <NavBarButton
                    text="Dashboard"
                    link="/"
                    active={activeButton === "Start"}
                    icon={Zap}
                />
                </DropdownMenuItem>
                <DropdownMenuItem>
                    <NavBarButton
                        text="Battery"
                        link="/battery"
                        active={activeButton === "Baterie"}
                        icon={Battery}
                    />
                </DropdownMenuItem>
                <DropdownMenuItem>
                    <NavBarButton
                        text="Chargers"
                        link="/chargers"
                        active={activeButton === "Akumulatory"}
                        icon={Cable}
                    />
                </DropdownMenuItem>

            
            
            </DropdownMenuContent>
        </DropdownMenu>
    )
}