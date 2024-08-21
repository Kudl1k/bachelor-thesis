import {
    DropdownMenu,
    DropdownMenuContent,
    DropdownMenuItem,
    DropdownMenuLabel,
    DropdownMenuSeparator,
    DropdownMenuTrigger,
  } from "@/components/ui/dropdown-menu"
import { Settings } from "lucide-react";
import { Button } from "../ui/button";
import { ModeToggle } from "./mode-toggle";
import { Link } from "react-router-dom";




export function SettingsComponent() {
    return (
        <DropdownMenu>
            <DropdownMenuTrigger>
                <Button variant={"ghost"}>
                    <Settings/>
                </Button>
            </DropdownMenuTrigger>
            <DropdownMenuContent align="end">
                <DropdownMenuLabel>
                    <div className="flex max-w justify-between items-center">
                        Settings
                        <ModeToggle/>
                    </div>
                </DropdownMenuLabel>
                <DropdownMenuSeparator />
                
                <DropdownMenuItem>
                    <Link to={""}>Add more types</Link>
                </DropdownMenuItem>
                
            </DropdownMenuContent>
        </DropdownMenu>
    )
}