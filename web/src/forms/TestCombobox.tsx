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
import { toast } from "@/components/ui/use-toast"
import { Type } from "@/models/TypeData"



const FormSchema = z.object({
  type: z.string({
    required_error: "Please select a type.",
  }),
})

interface ComboboxFormProps {
    types: Type[]
}

export function ComboboxForm({types}: ComboboxFormProps) {
  const form = useForm<z.infer<typeof FormSchema>>({
    resolver: zodResolver(FormSchema),
  })

  function onSubmit(data: z.infer<typeof FormSchema>) {
    toast({
      title: "You submitted the following values:",
      description: (
        <pre className="mt-2 w-[340px] rounded-md bg-slate-950 p-4">
          <code className="text-white">{JSON.stringify(data, null, 2)}</code>
        </pre>
      ),
    })
  }

  return (
    <>
    <Form {...form}>
          <form onSubmit={form.handleSubmit(onSubmit)} className="space-y-6">
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
                                              "w-[200px] justify-between",
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
                              <PopoverContent className="w-[200px] p-0">
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
              <Button type="submit">Submit</Button>
          </form>
      </Form>
      </>
  )
}
