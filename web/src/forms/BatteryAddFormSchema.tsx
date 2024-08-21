"use client"

import { zodResolver } from "@hookform/resolvers/zod"
import { z } from "zod"
import { Check, ChevronsUpDown } from "lucide-react"
import { useForm } from "react-hook-form"

import { cn } from "@/lib/utils"
import { Button } from "@/components/ui/button"
import {
  Command,
  CommandEmpty,
  CommandGroup,
  CommandInput,
  CommandItem,
  CommandList,
} from "@/components/ui/command"
import {
  Form,
  FormControl,
  FormDescription,
  FormField,
  FormItem,
  FormLabel,
  FormMessage,
} from "@/components/ui/form"
import {
  Popover,
  PopoverContent,
  PopoverTrigger,
} from "@/components/ui/popover"
import { Type } from "@/models/TypeData"
import { Input } from "@/components/ui/input"

const batteryAddFormSchema = z.object({
    type: z.string(),
    size: z.string().min(1, { message: "Size is required." }),
    factory_capacity: z.number().min(1, { message: "Factory capacity is required." }),
    voltage: z.number().min(1, { message: "Voltage is required." }),
})

interface BatteryAddFormProps {
    types: Type[] | [];
    onSubmitForm: (data: z.infer<typeof batteryAddFormSchema>) => void;
}

export function BatteryAddFormSchema({types, onSubmitForm }: BatteryAddFormProps) {

    

    const form = useForm<z.infer<typeof batteryAddFormSchema>>({
        resolver: zodResolver(batteryAddFormSchema),
    })
    console.log(types)

    function onSubmit(data: z.infer<typeof batteryAddFormSchema>) {
        onSubmitForm(data)
    }

    return (
        <Form {...form}>
            <form onSubmit={form.handleSubmit(onSubmit)}>
                <FormField
                    control={form.control}
                    name="type"
                    render={({ field }) => (
                        <FormItem className="flex flex-col">
                            <FormLabel>Type</FormLabel>
                            <Popover>
                                <PopoverTrigger asChild>
                                    <FormControl>
                                        <Button
                                            variant="outline"
                                            role="combobox"
                                            className={cn(
                                                "justify-between",
                                                !field.value && "text-muted-foreground"
                                            )}
                                        >
                                            {field.value
                                                ? types.find(
                                                    (type) => type.name === field.value
                                                )?.name
                                                : "Select type"}
                                            <ChevronsUpDown className="ml-2 h-4 w-4 shrink-0 opacity-50" />
                                        </Button>
                                    </FormControl>
                                </PopoverTrigger>
                                <PopoverContent className="p-0">
                                    <Command>
                                        <CommandInput placeholder="Search language..." />
                                        <CommandList>
                                            <CommandEmpty>No language found.</CommandEmpty>
                                            <CommandGroup>
                                                {types.map((type) => (
                                                    <CommandItem
                                                        value={type.name}
                                                        key={type.shortcut}
                                                        onSelect={() => {
                                                            form.setValue("type", type.name)
                                                        } }
                                                    >
                                                        <Check
                                                            className={cn(
                                                                "mr-2 h-4 w-4",
                                                                type.name === field.value
                                                                    ? "opacity-100"
                                                                    : "opacity-0"
                                                            )} />
                                                        {type.name}
                                                    </CommandItem>
                                                ))}
                                            </CommandGroup>
                                        </CommandList>
                                    </Command>
                                </PopoverContent>
                            </Popover>
                            <FormDescription>
                                This is a combobox form field.
                            </FormDescription>
                            <FormMessage />
                        </FormItem>
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
                <div className="flex justify-center pt-4">
                    <Button type="submit">Submit</Button>
                </div>
            </form>
        </Form>

      )
}