"use client"

import { zodResolver } from "@hookform/resolvers/zod"
import { z } from "zod"
import { useForm } from "react-hook-form"

import { Button } from "@/components/ui/button"

import {
  Form,
  FormControl,
  FormDescription,
  FormField,
  FormItem,
  FormLabel,
  FormMessage,
} from "@/components/ui/form"

import { Type } from "@/models/TypeData"
import { Input } from "@/components/ui/input"
import { BatteryInsert, insertBatteryData } from "@/models/BatteryData"
import { Dialog } from "@/components/Dialog"
import { useEffect, useState } from "react"
import { useNavigate } from "react-router-dom"
import { TypeFormCombobox } from "@/components/comboboxes/TypeCombobox"
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card"

const batteryAddFormSchema = z.object({
    type: z.string(),
    size: z.string().min(1, { message: "Size is required." }),
    factory_capacity: z.string().min(1, { message: "Factory capacity is required." }),
    voltage: z.string().min(1, { message: "Voltage is required." }),
})


interface BatteryAddFormProps {
    types: Type[] | [];
}

export function BatteryAddFormSchema({types}: BatteryAddFormProps) {
    const [openDialog, setOpenDialog] = useState(false)
    const [battery, setBattery] = useState<BatteryInsert | null>(null)
    const navigate = useNavigate()
    

    const form = useForm<z.infer<typeof batteryAddFormSchema>>({
        resolver: zodResolver(batteryAddFormSchema),
    })

    async function onSubmit(data: z.infer<typeof batteryAddFormSchema>) {
        const insertBattery: BatteryInsert = {
            type: data.type,
            size: data.size,
            factory_capacity: parseInt(data.factory_capacity),
            voltage: parseInt(data.voltage),
        }
        setBattery(insertBattery)
    }

    async function onContinue(insertBattery: BatteryInsert) {
        const battery = await insertBatteryData(insertBattery)
        console.log(battery)
        navigate("/battery")
    }


    useEffect(() => {
        if (form.formState.isSubmitSuccessful) {
            setOpenDialog(true)
        }
    }, [form.formState.isSubmitSuccessful])

    function handleCancel() {
        setOpenDialog(false)
        form.reset(form.getValues()) // Reset the form state to allow resubmission
    }

    return (
        <Card>
          <CardHeader>
            <CardTitle>Create a new battery</CardTitle>
            <CardDescription>Here you can create a new battery. If there is no types or sizes you like, you can create a new ones in those tabs</CardDescription>
          </CardHeader>
          <CardContent>
            <Form {...form}>
                <form onSubmit={form.handleSubmit(onSubmit)}>
                    <FormField
                        control={form.control}
                        name="type"
                        render={({ field }) => (
                            <TypeFormCombobox
                                fieldName="type"
                                label="Type"
                                description="This is a combobox form field."
                                types={types}
                                fieldValue={field.value}
                                setValue={form.setValue}
                            />
                        )} />
                    <FormField
                        control={form.control}
                        name="size"
                        render={({ field }) => (
                            <FormItem>
                                <FormLabel>Size</FormLabel>
                                <FormControl>
                                    <Input type="text" placeholder="AA" {...field} />
                                </FormControl>
                                <FormDescription>
                                    Battery size in format AA, AAA, etc.
                                </FormDescription>
                                <FormMessage />
                            </FormItem>
                        )} />
                    <FormField
                        control={form.control}
                        name="factory_capacity"
                        render={({ field }) => (
                            <FormItem>
                                <FormLabel>Factory Capacity</FormLabel>
                                <FormControl>
                                    <Input type="number" placeholder="2000" {...field} />
                                </FormControl>
                                <FormDescription>
                                    Factory capacity in mAh.
                                </FormDescription>
                                <FormMessage />
                            </FormItem>
                        )} />
                    <FormField
                        control={form.control}
                        name="voltage"
                        render={({ field }) => (
                            <FormItem>
                                <FormLabel>Voltage</FormLabel>
                                <FormControl>
                                    <Input type="number" placeholder="1500" {...field} />
                                </FormControl>
                                <FormDescription>
                                    Voltage in mV.
                                </FormDescription>
                                <FormMessage />
                            </FormItem>
                        )} />
                    <Dialog
                        open={openDialog}
                        trigger={
                            <div className="flex justify-center pt-4">
                                <Button type="submit">Submit</Button>
                            </div>
                        }
                        title="Submit battery"
                        description="Do you want to submit the battery?"
                        onContinue={() => onContinue(battery!)}
                        onCancel={() => handleCancel()}
                    />
                    
                </form>
            </Form>
          </CardContent>
        </Card>
        
      )
}

