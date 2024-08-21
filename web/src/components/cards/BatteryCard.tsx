import {
    Card,
    CardContent,
    CardDescription,
    CardFooter,
    CardHeader,
    CardTitle,
  } from "@/components/ui/card"
import { Button } from "../ui/button"
import { Battery } from "@/models/BatteryData"

interface BatteryCardProps {
    battery: Battery;
}


export function BatteryCard({battery}: BatteryCardProps) {
    

    return (
        <Card className="shadow-md">
            <CardHeader>
                <CardTitle>#{battery.id}</CardTitle>
                <CardDescription>
                    <p>Last time charged: <span> {battery.last_time_charged_at}</span></p>
                    <p>Last charged capacity: {battery.last_charged_capacity}</p>
                </CardDescription>
            </CardHeader>
            <CardContent>
                <p><span className="font-semibold">Type:</span> {battery.type.shortcut}</p>
                <p><span className="font-semibold">Size:</span> {battery.size}</p>
                <p><span className="font-semibold">Capacity:</span> {battery.factory_capacity} mAh</p>
                <p><span className="font-semibold">Voltage:</span> {battery.voltage} mV</p>
                <p><span className="font-semibold">Inserted:</span> {battery.created_at}</p>
            </CardContent>
            <CardFooter>
                <div className="flex justify-between w-full">
                    <Button variant={"secondary"} >Details</Button>
                </div>
            </CardFooter>
        </Card>
    )
}