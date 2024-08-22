import { Charger } from "@/models/ChargerData";
import { Button } from "../ui/button";
import { Card, CardHeader, CardTitle, CardContent, CardFooter } from "../ui/card";
import { Badge } from "../ui/badge";



interface ChargersCardProps {
    charger: Charger
}

export function ChargersCard({charger}: ChargersCardProps) {

    return (
        <Card className="shadow-md">
            <CardHeader>
                <CardTitle>{charger.name}</CardTitle>
            </CardHeader>
            <CardContent>
                <div className="pb-3">
                    <span className="font-semibold">Types: </span>
                    {charger.types.map((type) => (
                        <Badge className="ms-1" key={type.shortcut} variant="outline">{type.shortcut}</Badge>
                    ))}
                </div>
                <div>
                    <span className="font-semibold">Sizes:</span>
                    {charger.sizes.map((size) => (
                        <Badge className="ms-1" key={size.name} variant="outline">{size.name}</Badge>
                    ))}
                </div>
            </CardContent>
            <CardFooter>
                <div className="flex justify-between w-full">
                    <Button variant={"secondary"} >Details</Button>
                </div>
            </CardFooter>
        </Card>
    )

}