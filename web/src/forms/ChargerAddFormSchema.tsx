import MultipleSelector from "@/components/comboboxes/MultiSelect";
import { TtysCombobox } from "@/components/comboboxes/TtysCombobox";
import { Dialog } from "@/components/Dialog";
import { Button } from "@/components/ui/button";
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card";
import { Checkbox } from "@/components/ui/checkbox";
import { Form, FormControl, FormField, FormItem, FormLabel, FormMessage } from "@/components/ui/form";
import { Input } from "@/components/ui/input";
import { ChargerInsert, insertChargerData } from "@/models/ChargerData";
import { Size } from "@/models/SizeData";
import { Type } from "@/models/TypeData";
import { zodResolver } from "@hookform/resolvers/zod";
import { useEffect, useState } from "react";
import { useForm } from "react-hook-form";
import { useNavigate } from "react-router-dom";
import { z } from "zod";



const optionSchema = z.object({
    label: z.string(),
    value: z.string(),
    disable: z.boolean().optional(),
  });

const chargerAddFormSchema = z .object({
    name: z.string().min(1, { message: "Name is required." }),
    tty: z.string().min(1, { message: "TTY is required." }),
    baudRate: z.string().min(1, { message: "Baud rate is required." }),
    dataBits: z.string().min(1, { message: "Data bits is required." }),
    stopBits: z.string().min(1, { message: "Stop bits is required." }),
    parity: z.string().min(1, { message: "Parity is required." }),
    rts: z.boolean().default(false),
    dtr: z.boolean().default(false),
    slots: z.string().min(1, { message: "Slots is required." }),
    types: z.array(optionSchema).min(1),
    sizes: z.array(optionSchema).min(1),
})

interface ChargerAddFormProps {
    ttys: string[];
    types: Type[] | [];
    sizes: Size[] | [];
}

export function ChargerAddFormSchema({ttys, types, sizes}: ChargerAddFormProps) {
    const [charger, setCharger] = useState<ChargerInsert | null>(null)
    const [openDialog, setOpenDialog] = useState(false)

    const navigate = useNavigate()


    async function onContinue(chargerInsert: ChargerInsert) {
        const charger = await insertChargerData(chargerInsert)
        console.log(charger)
        navigate("/chargers")
    }

    const form = useForm<z.infer<typeof chargerAddFormSchema>>({
        resolver: zodResolver(chargerAddFormSchema),
    })

    useEffect(() => {
        if (form.formState.isSubmitSuccessful) {
            setOpenDialog(true)
        }
    }, [form.formState.isSubmitSuccessful])

    function onSubmit(data: z.infer<typeof chargerAddFormSchema>) {
        const insertedCharger: ChargerInsert = {
            name: data.name,
            tty: data.tty,
            baudRate: parseInt(data.baudRate),
            dataBits: parseInt(data.dataBits),
            stopBits: parseInt(data.stopBits),
            parity: parseInt(data.parity),
            rts: data.rts,
            dtr: data.dtr,
            slots: parseInt(data.slots),
            types: data.types.map((type) => type.value),
            sizes: data.sizes.map((type) => type.value),
        }
        console.log("Insert charger:", insertedCharger)
        setCharger(insertedCharger)
    }

    function handleCancel() {
        setOpenDialog(false)
        form.reset(form.getValues())
    }

    return (
        <Card className="mb-10">
            <CardHeader>
                <CardTitle>Create a new charger</CardTitle>
                <CardDescription>
                    Here you can create a new charger, that will be use for charging batteries
                </CardDescription>
            </CardHeader>
            <CardContent>
            <Form {...form}>
                <form onSubmit={form.handleSubmit(onSubmit)}>

                    <FormField
                        control={form.control}
                        name="name"
                        render={({ field }) => (
                            <FormItem>
                                <FormLabel>Name</FormLabel>
                                <FormControl>
                                    <Input {...field} placeholder="Enter the name of the charger..." />
                                </FormControl>
                                <FormMessage />
                            </FormItem>
                        )}
                    />

                    <FormField
                        control={form.control}
                        name="tty"
                        render={({ field }) => (
                            <TtysCombobox
                            fieldName="tty"
                            label="TTY"
                            description=""
                            ttys={ttys}
                            fieldValue={field.value}
                            setValue={form.setValue}
                            />
                    )} />

                    <FormField
                        control={form.control}
                        name="baudRate"
                        render={({ field }) => (
                            <FormItem>
                                <FormLabel>Baud rate</FormLabel>
                                <FormControl>
                                    <Input {...field} placeholder="Enter the baud rate of the charger..." />
                                </FormControl>
                                <FormMessage />
                            </FormItem>
                        )}
                    />

                    <FormField
                        control={form.control}
                        name="dataBits"
                        render={({ field }) => (
                            <FormItem>
                                <FormLabel>Data bits</FormLabel>
                                <FormControl>
                                    <Input {...field} placeholder="Enter the data bits of the charger..." />
                                </FormControl>
                                <FormMessage />
                            </FormItem>
                        )}
                    />

                    <FormField
                        control={form.control}
                        name="stopBits"
                        render={({ field }) => (
                            <FormItem>
                                <FormLabel>Stop bits</FormLabel>
                                <FormControl>
                                    <Input {...field} placeholder="Enter the stop bits of the charger..." />
                                </FormControl>
                                <FormMessage />
                            </FormItem>
                        )}
                    />

                    <FormField
                        control={form.control}
                        name="parity"
                        render={({ field }) => (
                            <FormItem>
                                <FormLabel>Parity</FormLabel>
                                <FormControl>
                                    <Input {...field} placeholder="Enter the parity of the charger..." />
                                </FormControl>
                                <FormMessage />
                            </FormItem>
                        )}
                    />

                    <FormField
                        control={form.control}
                        name="rts"
                        render={({ field }) => (
                            <FormItem className="flex flex-row items-start space-x-3 space-y-0 rounded-md border p-4 mt-4">
                                <FormLabel>RTS</FormLabel>
                                <FormControl>
                                    <Checkbox
                                    checked={field.value}
                                    onCheckedChange={field.onChange}
                                    />
                                </FormControl>
                                
                            </FormItem>
                        )}
                    />

                    <FormField
                        control={form.control}
                        name="dtr"
                        render={({ field }) => (
                            <FormItem className="flex flex-row items-start space-x-3 space-y-0 rounded-md border p-4 mt-4">
                                <FormLabel>DTR</FormLabel>
                                <FormControl>
                                    <Checkbox
                                    checked={field.value}
                                    onCheckedChange={field.onChange}
                                    />
                                </FormControl>
                                
                            </FormItem>
                        )}
                    />

                    <FormField
                        control={form.control}
                        name="slots"
                        render={({ field }) => (
                            <FormItem>
                                <FormLabel>Slots</FormLabel>
                                <FormControl>
                                    <Input {...field} placeholder="Enter the slots of the charger..." />
                                </FormControl>
                                <FormMessage />
                            </FormItem>
                        )}
                    />

                    <FormField
                    control={form.control}
                    name="types"
                    render={({ field }) => (
                        <FormItem>
                        <FormLabel>Types</FormLabel>
                        <FormControl>
                            <MultipleSelector
                            {...field}
                            defaultOptions={types.map((type) => ({
                                label: type.shortcut,
                                value: type.name,
                            }))}
                            placeholder="Select types you want to add..."
                            emptyIndicator={
                                <p className="text-center text-lg leading-10 text-gray-600 dark:text-gray-400">
                                no results found.
                                </p>
                            }
                            />
                        </FormControl>
                        <FormMessage />
                        </FormItem>
                    )}
                    />
                    <FormField
                    control={form.control}
                    name="sizes"
                    render={({ field }) => (
                        <FormItem>
                        <FormLabel>Sizes</FormLabel>
                        <FormControl>
                            <MultipleSelector
                            {...field}
                            defaultOptions={sizes.map((size) => ({
                                label: size.name,
                                value: size.name,
                            }))}
                            placeholder="Select sizes you want to add..."
                            emptyIndicator={
                                <p className="text-center text-lg leading-10 text-gray-600 dark:text-gray-400">
                                no results found.
                                </p>
                            }
                            />
                        </FormControl>
                        <FormMessage />
                        </FormItem>
                    )}
                    />
                    <Dialog
                        open={openDialog}
                        trigger={
                            <div className="flex justify-center pt-4">
                                <Button type="submit">Submit</Button>
                            </div>
                        }
                        title="Submit battery"
                        description="Do you want to submit the battery?"
                        onContinue={() => onContinue(charger!)}
                        onCancel={() => handleCancel()}
                    />
                    
                </form>
            </Form>
            </CardContent>
        </Card>
    );
}




