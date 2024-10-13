/* eslint-disable @typescript-eslint/no-explicit-any */
import { Button } from "@/components/ui/button";
import {
  Command,
  CommandEmpty,
  CommandGroup,
  CommandInput,
  CommandItem,
  CommandList,
} from "@/components/ui/command";
import {
  FormControl,
  FormDescription,
  FormItem,
  FormLabel,
  FormMessage,
} from "@/components/ui/form";
import {
  Popover,
  PopoverContent,
  PopoverTrigger,
} from "@/components/ui/popover";
import { cn } from "@/lib/utils";
import { Parser } from "@/models/ChargerData";
import { Check, ChevronsUpDown } from "lucide-react";
import { useState } from "react";
import { Path, UseFormSetValue } from "react-hook-form";

interface ComboboxProps<T extends { parser: number }> {
  fieldName: Path<T>;
  label: string;
  description: string;
  types: Parser[];
  fieldValue: number | undefined;
  setValue: UseFormSetValue<T>;
}

export function ParserFormCombobox<T extends { parser: number }>({
  fieldName,
  label,
  description,
  types,
  fieldValue,
  setValue,
}: ComboboxProps<T>) {
  const [open, setOpen] = useState(false);

  return (
    <FormItem className="flex flex-col">
      <FormLabel>{label}</FormLabel>
      <Popover open={open} onOpenChange={setOpen}>
        <PopoverTrigger asChild>
          <FormControl>
            <Button
              variant="outline"
              role="combobox"
              className={cn(
                "justify-between",
                !fieldValue && "text-muted-foreground"
              )}
            >
              {fieldValue
                ? types.find((type) => type.id === fieldValue)?.name
                : `Select ${label}`}
              <ChevronsUpDown className="ml-2 h-4 w-4 shrink-0 opacity-50" />
            </Button>
          </FormControl>
        </PopoverTrigger>
        <PopoverContent className="p-0">
          <Command>
            <CommandInput placeholder={`Search ${label}...`} />
            <CommandList>
              <CommandEmpty>No {label} found.</CommandEmpty>
              <CommandGroup>
                {types.map((type) => (
                  <CommandItem
                    value={type.id.toString()}
                    key={type.id}
                    onSelect={() => {
                      setValue(fieldName, type.id as any);
                      setOpen(false);
                    }}
                  >
                    <Check
                      className={cn(
                        "mr-2 h-4 w-4",
                        type.id === fieldValue
                          ? "opacity-100"
                          : "opacity-0"
                      )}
                    />
                    {type.name}
                  </CommandItem>
                ))}
              </CommandGroup>
            </CommandList>
          </Command>
        </PopoverContent>
      </Popover>
      <FormDescription>{description}</FormDescription>
      <FormMessage />
    </FormItem>
  );
}