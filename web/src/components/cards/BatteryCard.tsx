import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
} from "@/components/ui/card";
import { Battery, truncateText } from "@/models/BatteryData";

import JsBarcode from "jsbarcode";
import {
  Archive,
  Atom,
  BatteryCharging,
  BatteryFull,
  CalendarClock,
  CalendarPlus,
  Hash,
  LucideLink,
  Ruler,
  Zap,
} from "lucide-react";
import { useCallback, useState } from "react";
import { Link } from "react-router-dom";
import { Badge } from "../ui/badge";
import { buttonVariants } from "../ui/button";
import { Label } from "../ui/label";
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
  const [barcodeUrl, setBarcodeUrl] = useState<string | null>(null);
  const setCanvasRef = useCallback(
    (node: HTMLCanvasElement | null) => {
      if (node) {
        JsBarcode(node, battery.id, { format: "CODE128", height: 40 });
        const url = node.toDataURL("image/png");
        setBarcodeUrl(url);
      }
    },
    [battery.id]
  );

  return (
    <Card className="shadow-md">
      <CardHeader>
        <CardTitle>
          <div className="flex justify-between items-center w-full gap-2">
            <TooltipProvider>
              <Tooltip>
                <TooltipTrigger>
                  <h1 className="text-2xl font-bold hover:shadow-lg border-2 border-background hover:border-primary ease-in-out duration-150 rounded-2xl p-1 flex items-center gap-2">
                    <Hash className="size-7" />
                    {battery.id}
                  </h1>
                </TooltipTrigger>
                <TooltipContent>
                  {barcodeUrl ? (
                    <img src={barcodeUrl} alt="Barcode" height={40} />
                  ) : (
                    <canvas ref={setCanvasRef} style={{ display: "none" }} />
                  )}
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
          <span className="flex items-center gap-2">
            <CalendarClock />
            {battery.last_time_charged_at || "N/A"}
          </span>
          <span className="flex items-center gap-2">
            <BatteryCharging />
            {battery.last_charged_capacity || "N/A"} mAh
          </span>
        </CardDescription>
      </CardHeader>
      <CardContent>
        <h2 className="text-lg font-semibold flex items-center gap-2">
          <Ruler />
          {battery.size.name}
        </h2>
        <h2 className="text-lg font-semibold flex items-center gap-2">
          <BatteryFull />
          {battery.factory_capacity} mAh
        </h2>
        <h2 className="text-lg font-semibold flex items-center gap-2">
          <Zap />
          {battery.voltage} V
        </h2>
        <h2 className="text-lg font-semibold flex items-center gap-2">
          <Atom />
          {battery.type.shortcut}
        </h2>
        <div className="flex justify-between w-full">
          <div className="flex items-center space-x-2 text-red-500">
            {battery.archived && (
              <>
                <Archive />
                <Label htmlFor="archived">Archived battery</Label>
              </>
            )}
          </div>
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
