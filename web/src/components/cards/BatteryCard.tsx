import {
  Card,
  CardContent,
  CardDescription,
  CardFooter,
  CardHeader,
  CardTitle,
} from "@/components/ui/card";
import { Battery } from "@/models/BatteryData";
import { CalendarPlus } from "lucide-react";
import { Link } from "react-router-dom";
import { Badge } from "../ui/badge";
import { buttonVariants } from "../ui/button";

interface BatteryCardProps {
  battery: Battery;
}

export function BatteryCard({ battery }: BatteryCardProps) {
  return (
    <Card className="shadow-md">
      <CardHeader>
        <CardTitle>
          <div className="flex justify-between items-center w-full">
            <div>#{battery.id}</div>
            <div>
              <Badge variant="outline">
                <CalendarPlus size={12} />
                {battery.created_at}
              </Badge>
            </div>
          </div>
        </CardTitle>
        <CardDescription>
          <p>
            Last time charged:{" "}
            <span> {battery.last_time_charged_at || "N/A"}</span>
          </p>
          <p>
            Last charged capacity:{" "}
            {battery.last_charged_capacity || "N/A" + " mAh"}
          </p>
        </CardDescription>
      </CardHeader>
      <CardContent>
        <p>
          <span className="font-semibold">Type:</span> {battery.type.shortcut}
        </p>
        <p>
          <span className="font-semibold">Size:</span> {battery.size.name}
        </p>
        <p>
          <span className="font-semibold">Capacity:</span>{" "}
          {battery.factory_capacity} mAh
        </p>
        <p>
          <span className="font-semibold">Voltage:</span> {battery.voltage} mV
        </p>
      </CardContent>
      <CardFooter>
        <div className="flex justify-between w-full">
          <Link
            className={buttonVariants({ variant: "secondary" })}
            to={`/battery/${battery.id}`}
          >
            Details
          </Link>
        </div>
      </CardFooter>
    </Card>
  );
}
