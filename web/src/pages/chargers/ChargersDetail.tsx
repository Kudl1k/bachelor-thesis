import { Loading } from "@/components/Loading";
import { Badge } from "@/components/ui/badge";
import { Card, CardContent, CardHeader } from "@/components/ui/card";
import { Charger, fetchChargerInfo } from "@/models/ChargerData";
import { Atom, Cable, Ruler } from "lucide-react";
import { useEffect, useRef, useState } from "react";
import { useParams } from "react-router-dom";

export default function ChargersDetail() {
  const { id } = useParams<{ id: string }>();
  const [charger, setCharger] = useState<Charger | null>(null);

  const hasFetched = useRef(false);

  useEffect(() => {
    if (!hasFetched.current) {
      fetchChargerInfo(Number(id), setCharger);
      hasFetched.current = true;
    }
  }, [id]);

  if (!charger) {
    return Loading();
  }

  return (
    <>
      <div className="flex justify-center w-full">
        <div className="xl:w-4/6 p-4 w-full">
          <Card className="shadow-md">
            <CardHeader>
              <div className="flex items-center gap-2">
                <Cable />
                <h2 className="text-2xl font-semibold">{charger.name}</h2>
              </div>
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
          </Card>
        </div>
      </div>
    </>
  );
}
