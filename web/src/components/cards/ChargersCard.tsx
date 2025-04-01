import { Charger } from "@/models/ChargerData";
import { Atom, Cable, Ruler } from "lucide-react";
import { Link } from "react-router-dom";
import { Badge } from "../ui/badge";
import { buttonVariants } from "../ui/button";
import {
  Card,
  CardContent,
  CardFooter,
  CardHeader,
  CardTitle,
} from "../ui/card";

interface ChargersCardProps {
  charger: Charger;
}

export function ChargersCard({ charger }: ChargersCardProps) {
  return (
    <Card className="shadow-md">
      <CardHeader>
        <CardTitle>
          <div className="flex w-full space-x-2">
            <Cable />
            <h2 className="text-xl">{charger.name}</h2>
          </div>
        </CardTitle>
      </CardHeader>
      <CardContent>
        <div className="pb-3 flex">
          <span className="font-semibold">
            <Atom />
          </span>
          {charger.types.map((type) => (
            <Badge className="ms-1" key={type.shortcut} variant="outline">
              {type.shortcut}
            </Badge>
          ))}
        </div>
        <div className="flex">
          <span className="font-semibold">
            <Ruler />
          </span>
          {charger.sizes.map((size) => (
            <Badge className="ms-1" key={size.name} variant="outline">
              {size.name}
            </Badge>
          ))}
        </div>
      </CardContent>
      <CardFooter>
        <div className="flex justify-between w-full">
          <Link
            className={buttonVariants({ variant: "secondary" })}
            to={`/chargers/${charger.id}`}
          >
            Details
          </Link>
        </div>
      </CardFooter>
    </Card>
  );
}
