import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
} from "@/components/ui/card";
import { Battery, truncateText } from "@/models/BatteryData";

import {
  BatteryCharging,
  BatteryFull,
  Biohazard,
  CalendarClock,
  CalendarPlus,
  Hash,
  LucideLink,
  Ruler,
  Zap,
} from "lucide-react";
import Barcode from "react-barcode";
import { Link } from "react-router-dom";
import { Badge } from "../ui/badge";
import { buttonVariants } from "../ui/button";
import {
  Tooltip,
  TooltipContent,
  TooltipProvider,
  TooltipTrigger,
} from "../ui/tooltip";

interface BatteryCardProps {
  battery: Battery;
}

export function BatteryCard({ battery }: BatteryCardProps) {
  return (
    <Card className="shadow-md">
      <CardHeader>
        <CardTitle>
          <div className="flex justify-between items-center w-full gap-2">
            <TooltipProvider>
              <Tooltip>
                <TooltipTrigger>
                  <h1 className="text-2xl font-bold hover:shadow-lg border-2 border-background hover:border-primary ease-in-out duration-150 rounded-2xl p-1">
                    <div className="flex items-center gap-2">
                      <Hash className="size-7" />
                      {battery.id}
                    </div>
                  </h1>
                </TooltipTrigger>
                <TooltipContent>
                  <Barcode value={battery.id} height={40} />
                </TooltipContent>
              </Tooltip>
            </TooltipProvider>
            <div className="flex items-center gap-1">
              <Badge variant="outline" className="gap-2">
                <CalendarPlus size={14} />
                <p className="text-sm">{battery.created_at}</p>
              </Badge>
              <div className="ml-auto items-center">
                <TooltipProvider>
                  <Tooltip>
                    <TooltipTrigger>
                      <a href={battery.shop_link || ""} target="_blank">
                        <LucideLink size={14} />
                      </a>
                    </TooltipTrigger>
                    <TooltipContent>
                      <a href={battery.shop_link || ""} target="_blank">
                        {truncateText(battery.shop_link || "", 30)}
                      </a>
                    </TooltipContent>
                  </Tooltip>
                </TooltipProvider>
              </div>
            </div>
          </div>
        </CardTitle>
        <CardDescription className="space-y-1">
          <div className="flex items-center gap-2">
            <CalendarClock />
            {battery.last_time_charged_at || "N/A"}
          </div>
          <div className="flex items-center gap-2">
            <BatteryCharging />
            {battery.last_charged_capacity || "N/A"} mAh
          </div>
        </CardDescription>
      </CardHeader>
      <CardContent>
        <h2 className="text-lg font-semibold">
          <div className="flex items-center gap-2">
            <Ruler />
            {battery.size.name}
          </div>
        </h2>
        <h2 className="text-lg font-semibold">
          <div className="flex items-center gap-2">
            <BatteryFull />
            {battery.factory_capacity} mAh
          </div>
        </h2>
        <h2 className="text-lg font-semibold">
          <div className="flex items-center gap-2">
            <Zap />
            {battery.voltage} V
          </div>
        </h2>
        <h2 className="text-lg font-semibold">
          <div className="flex items-center gap-2">
            <Biohazard />
            {battery.type.shortcut}
          </div>
        </h2>
        <div className="flex justify-end w-full">
          <Link
            className={buttonVariants({ variant: "secondary" })}
            to={`/battery/${battery.id}`}
          >
            Details
          </Link>
        </div>
      </CardContent>
    </Card>
  );
}
