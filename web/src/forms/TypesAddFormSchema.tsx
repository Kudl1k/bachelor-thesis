import { Dialog } from "@/components/Dialog";
import { Button } from "@/components/ui/button";
import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
} from "@/components/ui/card";
import {
  Form,
  FormControl,
  FormField,
  FormItem,
  FormLabel,
} from "@/components/ui/form";
import { Input } from "@/components/ui/input";
import { insertTypeData, TypeInsert } from "@/models/TypeData";
import { zodResolver } from "@hookform/resolvers/zod";
import { useEffect, useState } from "react";
import { useForm } from "react-hook-form";
import { z } from "zod";

const typeAddFormSchema = z.object({
  name: z.string().min(1, { message: "Name is required." }).default(""),
  shortcut: z.string().min(1, { message: "Shortcut is required." }).default(""),
});

export function TypeAddFormSchema() {
  const [openDialog, setOpenDialog] = useState(false);
  const [type, setType] = useState<TypeInsert | null>(null);

  const form = useForm<z.infer<typeof typeAddFormSchema>>({
    resolver: zodResolver(typeAddFormSchema),
    defaultValues: {
      name: "",
      shortcut: "",
    },
  });

  async function onSubmit(data: z.infer<typeof typeAddFormSchema>) {
    const insertType: TypeInsert = {
      name: data.name,
      shortcut: data.shortcut,
    };
    setType(insertType);
    setOpenDialog(true);
  }

  async function onContinue(insertType: TypeInsert) {
    const value = insertTypeData(insertType);
    setOpenDialog(false);
    form.reset();
    console.log(value);
  }

  function handleCancel() {
    setOpenDialog(false);
    form.reset();
  }

  useEffect(() => {
    if (form.formState.isSubmitSuccessful) {
      setOpenDialog(true);
    }
  }, [form.formState.isSubmitSuccessful]);

  return (
    <Card>
      <CardHeader>
        <CardTitle>Create a new type</CardTitle>
        <CardDescription>
          Here you can create a new type, that will be used for creating and
          filtering batteries and chargers
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
                    <Input
                      type="text"
                      placeholder="Nikl-metal hydridový"
                      {...field}
                    />
                  </FormControl>
                </FormItem>
              )}
            />
            <FormField
              control={form.control}
              name="shortcut"
              render={({ field }) => (
                <FormItem>
                  <FormLabel>Shortcut</FormLabel>
                  <FormControl>
                    <Input type="text" placeholder="Ni-MH" {...field} />
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
  );
}
