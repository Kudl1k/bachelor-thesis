import MultipleSelector from "@/components/comboboxes/MultiSelect";
import { Loading } from "@/components/Loading";
import { Badge } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";
import { Card, CardContent, CardHeader } from "@/components/ui/card";
import { Form, FormControl, FormField, FormItem, FormMessage } from "@/components/ui/form";
import { Tooltip, TooltipContent, TooltipProvider, TooltipTrigger } from "@/components/ui/tooltip";
import { addChargerSize, addChargerType, Charger, fetchChargerInfo, removeChargerSize, removeChargerType } from "@/models/ChargerData";
import { fetchSizeData, Size } from "@/models/SizeData";
import { fetchTypeData, Type } from "@/models/TypeData";
import { zodResolver } from "@hookform/resolvers/zod";
import { Atom, Cable, Ruler } from "lucide-react";
import { useEffect, useRef, useState } from "react";
import { useForm } from "react-hook-form";
import { useParams } from "react-router-dom";
import { z } from "zod";

const optionSchema = z.object({
  label: z.string(),
  value: z.string(),
  disable: z.boolean().optional(),
});

const typeEditFormSchema = z.object({
  types: z.array(optionSchema).min(1),
});

const sizeEditFormSchema = z.object({
  sizes: z.array(optionSchema).min(1),
});

export default function ChargersDetail() {
  const { id } = useParams<{ id: string }>();
  const [charger, setCharger] = useState<Charger | null>(null);
  const [types, setTypes] = useState<Type[] | null>(null);
  const [sizes, setSizes] = useState<Size[] | null>(null);
  const [initialTypes, setInitialTypes] = useState<string[]>([]);
  const [initialSizes, setInitialSizes] = useState<string[]>([]);
  const [editTypes, setEditTypes] = useState<Boolean>(false);
  const [editSizes, setEditSizes] = useState<Boolean>(false);

  const hasFetched = useRef(false);

  const typeForm = useForm<z.infer<typeof typeEditFormSchema>>({
    resolver: zodResolver(typeEditFormSchema),
    defaultValues: {
      types: [],
    },
  });
  const sizeForm = useForm<z.infer<typeof sizeEditFormSchema>>({
    resolver: zodResolver(sizeEditFormSchema),
    defaultValues: {
      sizes: [],
    },
  });

  useEffect(() => {
    if (!hasFetched.current) {
      fetchChargerInfo(Number(id), setCharger);
      fetchTypeData(setTypes);
      fetchSizeData(setSizes);
      hasFetched.current = true;
    }
  }, [id]);

  useEffect(() => {
    if (charger) {
      const initialTypeShortcuts = charger.types.map((type) => type.shortcut);
      const initialSizeNames = charger.sizes.map((size) => size.name);

      setInitialTypes(initialTypeShortcuts);
      setInitialSizes(initialSizeNames);

      typeForm.reset({
        types: initialTypeShortcuts.map((shortcut) => ({
          label: shortcut,
          value: shortcut,
          disable: false,
        })),
      });
      sizeForm.reset({
        sizes: initialSizeNames.map((name) => ({
          label: name,
          value: name,
          disable: false,
        })),
      });
    }
  }, [charger, typeForm, sizeForm]);

  function handleEditTypes() {
    setEditTypes(!editTypes);
  }

  function handleEditSizes() {
    setEditSizes(!editSizes);
  }

  async function onSubmitType(data: z.infer<typeof typeEditFormSchema>) {

    const currentTypes = data.types.map((type) => type.value);
    const addedTypes = currentTypes.filter((type) => !initialTypes.includes(type));
    const removedTypes = initialTypes.filter((type) => !currentTypes.includes(type));

    console.log("Current Types:", currentTypes);
    console.log("Added Types:", addedTypes);
    console.log("Removed Types:", removedTypes);

    // Call your API to update the types

    for (const type of addedTypes) {
      await addChargerType(charger!.id, type);
    }
    for (const type of removedTypes) {
      await removeChargerType(charger!.id, type);
    }

    setEditTypes(false);
    window.location.reload();
  }

  async function onSubmitSize(data: z.infer<typeof sizeEditFormSchema>) {

    const currentSizes = data.sizes.map((size) => size.value);
    const addedSizes = currentSizes.filter((size) => !initialSizes.includes(size));
    const removedSizes = initialSizes.filter((size) => !currentSizes.includes(size));

    console.log("Current Sizes:", currentSizes);
    console.log("Added Sizes:", addedSizes);
    console.log("Removed Sizes:", removedSizes);

    // Call your API to update the sizes

    for (const size of addedSizes) {
      await addChargerSize(charger!.id, size);
    }
    for (const size of removedSizes) {
      await removeChargerSize(charger!.id, size);
    }

    setEditSizes(false);
    window.location.reload();

  }

  if (!charger || !types || !sizes) {
    return Loading();
  }

  return (
    <>
      <div className="flex justify-center w-full">
        <div className="xl:w-4/6 p-4 w-full">
          <Card className="shadow-md">
            <CardHeader>
              <div className="flex items-center gap-2">
                <Cable />
                <h2 className="text-2xl font-semibold">{charger.name}</h2>
              </div>
            </CardHeader>
            <CardContent>
              <div className="w-full">
                <div className="pb-3 flex w-full ">
                  <TooltipProvider>
                    <Tooltip>
                      <TooltipTrigger>
                        <h1 className="text-2xl font-bold shadow-lg border-2 border-background hover:border-primary ease-in-out duration-150 rounded-xl my-1">
                          <div className="flex items-center gap-2" onClick={handleEditTypes}>
                            <Atom className="" />
                          </div>
                        </h1>
                      </TooltipTrigger>
                      <TooltipContent>
                        <h1>Click to edit types</h1>
                      </TooltipContent>
                    </Tooltip>
                  </TooltipProvider>
                  {!editTypes && (
                    charger.types.map((type) => (
                      <Badge className="ms-1 my-1" key={type.shortcut} variant="outline">
                        {type.shortcut}
                      </Badge>
                    ))
                  )}
                  {editTypes && (
                    <div className="w-full">
                      <Form {...typeForm}>
                        <form onSubmit={typeForm.handleSubmit(onSubmitType)} className="flex gap-4 mx-4 my-1">
                          <FormField
                            control={typeForm.control}
                            name="types"
                            render={({ field }) => (
                              <FormItem className="flex-grow">
                                <FormControl>
                                  <MultipleSelector
                                    {...field}
                                    defaultOptions={types.map((type) => ({
                                      label: type.shortcut,
                                      value: type.shortcut,
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
                          <div className="flex items-center">
                            <Button type="submit">Edit</Button>
                          </div>
                        </form>
                      </Form>
                    </div>
                  )}
                </div>
                <div className="pb-3 flex ">
                  <TooltipProvider>
                    <Tooltip>
                      <TooltipTrigger>
                        <h1 className="text-2xl font-bold shadow-lg border-2 border-background hover:border-primary ease-in-out duration-150 rounded-xl my-1">
                          <div className="flex items-center gap-2" onClick={handleEditSizes}>
                            <Ruler className="" />
                          </div>
                        </h1>
                      </TooltipTrigger>
                      <TooltipContent>
                        <h1>Click to edit sizes</h1>
                      </TooltipContent>
                    </Tooltip>
                  </TooltipProvider>
                  {!editSizes && (
                    charger.sizes.map((size) => (
                      <Badge className="ms-1 my-1" key={size.name} variant="outline">
                        {size.name}
                      </Badge>
                    ))
                  )}
                  {editSizes && (
                    <div className="w-full">
                      <Form {...sizeForm}>
                        <form onSubmit={sizeForm.handleSubmit(onSubmitSize)} className="flex gap-4 mx-4 my-1">
                          <FormField
                            control={sizeForm.control}
                            name="sizes"
                            render={({ field }) => (
                              <FormItem className="flex-grow">
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
                          <div className="flex items-center">
                            <Button type="submit">Edit</Button>
                          </div>
                        </form>
                      </Form>
                    </div>
                  )}
                </div>
              </div>
              <div className="w-full">
                {/* Additional content */}
              </div>
            </CardContent>
          </Card>
        </div>
      </div>
    </>
  );
}