import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuLabel,
  DropdownMenuTrigger,
} from "@/components/ui/dropdown-menu";
import { Cable, Settings } from "lucide-react";
import { Button } from "../ui/button";
import { ModeToggle } from "./mode-toggle";
import { endGroup, useChargerStore } from "@/models/ChargerData";

export function SettingsComponent() {
  const groupId = useChargerStore((state) => state.groupId);

  return (
    <DropdownMenu>
      <DropdownMenuTrigger asChild>
        <Button variant={"ghost"}>
          <Settings />
        </Button>
      </DropdownMenuTrigger>
      <DropdownMenuContent align="end">
        <DropdownMenuLabel>
          <ModeToggle />
        </DropdownMenuLabel>
        {groupId && (
          <DropdownMenuLabel>
            <Button
              variant="destructive"
              onClick={() => {
                endGroup(groupId);
              }}
            >
              End charging
              <Cable className="ps-2" />
            </Button>
          </DropdownMenuLabel>
        )}
      </DropdownMenuContent>
    </DropdownMenu>
  );
}
