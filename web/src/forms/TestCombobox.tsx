"use client"
 
import { zodResolver } from "@hookform/resolvers/zod"
import { useForm } from "react-hook-form"
import { z } from "zod"
 
import { Button } from "@/components/ui/button"

import {
  Form,
  FormField,
} from "@/components/ui/form"
import { toast } from "@/components/ui/use-toast"
import { Type } from "@/models/TypeData"
import { TypeFormCombobox } from "@/components/comboboxes/TypeCombobox"



const FormSchema = z.object({
  type: z.string({
    required_error: "Please select a type.",
  }),
})

interface ComboboxFormProps {
    types: Type[]
}

export function ComboboxForm({ types }: ComboboxFormProps) {
    console.log(types); // Add this line to check the data

    const form = useForm<z.infer<typeof FormSchema>>({
        resolver: zodResolver(FormSchema),
    });

    function onSubmit(data: z.infer<typeof FormSchema>) {
        toast({
            title: "You submitted the following values:",
            description: (
                <pre className="mt-2 w-[340px] rounded-md bg-slate-950 p-4">
                    <code className="text-white">{JSON.stringify(data, null, 2)}</code>
                </pre>
            ),
        });
    }

    return (
        <>
            <Form {...form}>
                <form onSubmit={form.handleSubmit(onSubmit)} className="space-y-6">
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
                        )}
                    />
                    <Button type="submit">Submit</Button>
                </form>
            </Form>
        </>
    );
}
