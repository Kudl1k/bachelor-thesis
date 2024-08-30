"use client";

import { zodResolver } from "@hookform/resolvers/zod";
import { useForm } from "react-hook-form";
import { z } from "zod";

import { Button } from "@/components/ui/button";

import {
  Form,
  FormControl,
  FormDescription,
  FormField,
  FormItem,
  FormLabel,
  FormMessage,
} from "@/components/ui/form";

import { SizeFormCombobox } from "@/components/comboboxes/SizeCombobox";
import { TypeFormCombobox } from "@/components/comboboxes/TypeCombobox";
import { Dialog } from "@/components/Dialog";
import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
} from "@/components/ui/card";
import { Input } from "@/components/ui/input";
import { BatteryInsert, insertBatteryData } from "@/models/BatteryData";
import { Size } from "@/models/SizeData";
import { Type } from "@/models/TypeData";
import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";

const batteryAddFormSchema = z.object({
  type: z.string(),
  size: z.string().min(1, { message: "Size is required." }),
  factory_capacity: z
    .string()
    .min(1, { message: "Factory capacity is required." }),
  voltage: z.string().min(1, { message: "Voltage is required." }),
  shoplink: z.string().optional(),
});

interface BatteryAddFormProps {
  types: Type[] | [];
  sizes: Size[] | [];
}

export function BatteryAddFormSchema({ types, sizes }: BatteryAddFormProps) {
  const [openDialog, setOpenDialog] = useState(false);
  const [battery, setBattery] = useState<BatteryInsert | null>(null);
  const navigate = useNavigate();

  const form = useForm<z.infer<typeof batteryAddFormSchema>>({
    resolver: zodResolver(batteryAddFormSchema),
  });

  async function onSubmit(data: z.infer<typeof batteryAddFormSchema>) {
    const insertBattery: BatteryInsert = {
      type: data.type,
      size: data.size,
      factory_capacity: parseInt(data.factory_capacity),
      shop_link: data.shoplink,
      voltage: parseInt(data.voltage),
    };
    setBattery(insertBattery);
  }

  async function onContinue(insertBattery: BatteryInsert) {
    console.log(insertBattery);
    const battery = await insertBatteryData(insertBattery);
    console.log(battery);
    navigate("/battery");
  }

  useEffect(() => {
    if (form.formState.isSubmitSuccessful) {
      setOpenDialog(true);
    }
  }, [form.formState.isSubmitSuccessful]);

  function handleCancel() {
    setOpenDialog(false);
    form.reset(form.getValues()); // Reset the form state to allow resubmission
  }

  return (
    <Card>
      <CardHeader>
        <CardTitle>Create a new battery</CardTitle>
        <CardDescription>
          Here you can create a new battery. If there is no types or sizes you
          like, you can create a new ones in those tabs
        </CardDescription>
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
                  description=""
                  types={types}
                  fieldValue={field.value}
                  setValue={form.setValue}
                />
              )}
            />
            <FormField
              control={form.control}
              name="size"
              render={({ field }) => (
                <SizeFormCombobox
                  fieldName="size"
                  label="Size"
                  description=""
                  sizes={sizes}
                  fieldValue={field.value}
                  setValue={form.setValue}
                />
              )}
            />
            <FormField
              control={form.control}
              name="factory_capacity"
              render={({ field }) => (
                <FormItem>
                  <FormLabel>Factory Capacity</FormLabel>
                  <FormControl>
                    <Input type="number" placeholder="2000" {...field} />
                  </FormControl>
                  <FormDescription>Factory capacity in mAh.</FormDescription>
                  <FormMessage />
                </FormItem>
              )}
            />
            <FormField
              control={form.control}
              name="voltage"
              render={({ field }) => (
                <FormItem>
                  <FormLabel>Voltage</FormLabel>
                  <FormControl>
                    <Input type="number" placeholder="1500" {...field} />
                  </FormControl>
                  <FormDescription>Voltage in mV.</FormDescription>
                  <FormMessage />
                </FormItem>
              )}
            />
            <FormField
              control={form.control}
              name="shoplink"
              render={({ field }) => (
                <FormItem>
                  <FormLabel>Shop Link</FormLabel>
                  <FormControl>
                    <Input type="text" placeholder="alza.cz" {...field} />
                  </FormControl>
                  <FormDescription>
                    Link to shop, where the battery was bought from.
                  </FormDescription>
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
              onContinue={() => onContinue(battery!)}
              onCancel={() => handleCancel()}
            />
          </form>
        </Form>
      </CardContent>
    </Card>
  );
}
