import { Button } from "../ui/button";
import { Card, CardHeader, CardTitle, CardDescription, CardContent, CardFooter } from "../ui/card";





export function ChargersCard() {

    return (
        <Card className="shadow-md">
            <CardHeader>
                <CardTitle>Conrad Charge Manager 2010</CardTitle>
                <CardDescription>
                    <p>Charges 4 batteries at once</p>
                </CardDescription>
            </CardHeader>
            <CardContent>
                <p className="font-semibold">Supported types</p>
                <p className="italic">Ni-MH,Li-Ion</p>
                <p className="font-semibold">Supported sizes</p>
                <p className="italic">AA,AAA,18650</p>
            </CardContent>
            <CardFooter>
                <div className="flex justify-between w-full">
                    <Button variant={"secondary"} >Details</Button>
                </div>
            </CardFooter>
        </Card>
    )

}