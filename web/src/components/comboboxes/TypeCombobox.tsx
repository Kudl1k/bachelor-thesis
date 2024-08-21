/* eslint-disable @typescript-eslint/no-explicit-any */
import { useState } from "react";
import { Check, ChevronsUpDown } from "lucide-react";
import { cn } from "@/lib/utils";
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
  Popover,
  PopoverContent,
  PopoverTrigger,
} from "@/components/ui/popover";
import { FormControl, FormItem, FormLabel, FormDescription, FormMessage } from "@/components/ui/form";
import { Path, UseFormSetValue } from "react-hook-form";
import { Type } from "@/models/TypeData";

interface ComboboxProps<T extends { type: string }> {
  fieldName: Path<T>;
  label: string;
  description: string;
  types: Type[];
  fieldValue: string | undefined;
  setValue: UseFormSetValue<T>;
}

export function TypeFormCombobox<T extends { type: string }>({
  fieldName,
  label,
  description,
  types,
  fieldValue,
  setValue
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
                ? types.find((type) => type.name === fieldValue)?.name
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
                    value={type.name}
                    key={type.shortcut}
                    onSelect={() => {
                      setValue(fieldName, type.name as any);
                      setOpen(false);
                    }}
                  >
                    <Check
                      className={cn(
                        "mr-2 h-4 w-4",
                        type.name === fieldValue ? "opacity-100" : "opacity-0"
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
