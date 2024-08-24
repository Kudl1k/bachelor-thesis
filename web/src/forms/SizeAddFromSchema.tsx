import { Dialog } from "@/components/Dialog";
import { Button } from "@/components/ui/button";
import { Card, CardHeader, CardTitle, CardDescription, CardContent } from "@/components/ui/card";
import { FormField, FormControl, FormLabel, FormItem, Form } from "@/components/ui/form";
import { Input } from "@/components/ui/input";
import { insertSizeData, Size } from "@/models/SizeData";
import { zodResolver } from "@hookform/resolvers/zod";
import { useEffect, useState } from "react";
import { useForm } from "react-hook-form";
import { useNavigate } from "react-router-dom";
import { z } from "zod";




const sizeAddFromSchema = z.object({
    name: z.string().min(1, { message: "Name is required." }),  
})

export function SizeAddFormSchema() {
    const [openDialog, setOpenDialog] = useState(false)
    const [size, setSize] = useState<Size | null>(null)
    const navigate = useNavigate()
    
    const form = useForm<z.infer<typeof sizeAddFromSchema>>({
        resolver: zodResolver(sizeAddFromSchema),
    })

    async function onSubmit(data: z.infer<typeof sizeAddFromSchema>) {
        console.log(data)
        setSize(data)
    }

    async function onContinue(size: Size) {
        const insertedSize = await insertSizeData(size)
        console.log(insertedSize)
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
                <CardTitle>Create a new size</CardTitle>
                <CardDescription>
                Here you can create a new size, that will be use for creating and filtering batteries and chargers
                </CardDescription>
            </CardHeader>
            <CardContent>
                <Form {...form}>
                    <form onSubmit={form.handleSubmit(onSubmit)}>
                        <FormField
                            control={form.control}
                            name="name"
                            render={({ field }) => (
                                <FormControl>
                                    <FormItem>
                                        <FormLabel htmlFor="name">Name</FormLabel>
                                        <FormControl>
                                            <Input type="text" placeholder="AAA" {...field} />
                                        </FormControl>
                                    </FormItem>
                                </FormControl>
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
                            onContinue={() => onContinue(size!)}
                            onCancel={() => handleCancel()}
                        />
                    </form>
                </Form>
            </CardContent>
        </Card>
    )

}