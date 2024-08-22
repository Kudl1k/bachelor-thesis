import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card";
import { insertTypeData, TypeInsert } from "@/models/TypeData";
import { zodResolver } from "@hookform/resolvers/zod"
import { z } from "zod"
import { useForm } from "react-hook-form"
import { Form, FormControl, FormField, FormItem, FormLabel } from "@/components/ui/form";
import { Input } from "@/components/ui/input";
import { Dialog } from "@/components/Dialog";
import { Button } from "@/components/ui/button";
import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom"




const typeAddFormSchema = z.object({
    name: z.string().min(1, { message: "Name is required." }),
    shortcut: z.string().min(1, { message: "Shortcut is required." }),
})


export function TypeAddFormSchema() {
    const [openDialog, setOpenDialog] = useState(false)
    const [type, setType] = useState<TypeInsert | null>(null)
    const navigate = useNavigate()


    const form = useForm<z.infer<typeof typeAddFormSchema>>({
        resolver: zodResolver(typeAddFormSchema),
    })

    async function onSubmit(data: z.infer<typeof typeAddFormSchema>) {
        const insertType: TypeInsert = {
            name: data.name,
            shortcut: data.shortcut,
        }
        setType(insertType)
    }

    async function onContinue(insertType: TypeInsert) {
        const value = insertTypeData(insertType)
        console.log(value)
        navigate('/')
    }

    function handleCancel() {
        setOpenDialog(false)
        form.reset(form.getValues())
    }

    useEffect(() => {
        if (form.formState.isSubmitSuccessful) {
            setOpenDialog(true)
        }
    }, [form.formState.isSubmitSuccessful])
    

    return (
      <Card>
        <CardHeader>
          <CardTitle>Create a new type</CardTitle>
          <CardDescription>
            Here you can create a new type, that will be use for creating and filtering batteries and chargers
          </CardDescription>
        </CardHeader>
        <CardContent>
            <Form {...form}>
                <form onSubmit={form.handleSubmit(onSubmit)}>
                    <FormField
                        control={form.control}
                        name="name"
                        render={({field}) => (
                            <FormItem>
                                <FormLabel>Name</FormLabel>
                                <FormControl>
                                    <Input type="text" placeholder="Nikl-metal hydridový" {...field}/>
                                </FormControl>
                            </FormItem>
                        )}
                    />
                    <FormField
                        control={form.control}
                        name="shortcut"
                        render={({field}) => (
                            <FormItem>
                                <FormLabel>Shortcut</FormLabel>
                                <FormControl>
                                    <Input type="text" placeholder="Ni-MH" {...field}/>
                                </FormControl>
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
                        onContinue={() => onContinue(type!)}
                        onCancel={() => handleCancel()}
                    />

                </form>
            </Form>

        </CardContent>
          

      </Card>
    )
}