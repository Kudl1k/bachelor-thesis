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

interface ComboboxProps<T extends { tty: string }> {
    fieldName: Path<T>;
    label: string;
    description: string;
    ttys: string[];
    fieldValue: string | undefined;
    setValue: UseFormSetValue<T>;
}

export function TtysCombobox<T extends { tty: string }>({
    fieldName,
    label,
    description,
    ttys,
    fieldValue,
    setValue
}: ComboboxProps<T>) {
    const [open, setOpen] = useState(false);

  return (
    <FormItem className="flex flex-col pt-2">
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
                ? ttys.find((tty) => tty === fieldValue)
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
                {ttys.map((tty) => (
                  <CommandItem
                    value={tty}
                    key={tty}
                    onSelect={() => {
                      setValue(fieldName, tty as any);
                      setOpen(false);
                    }}
                  >
                    <Check
                      className={cn(
                        "mr-2 h-4 w-4",
                        tty === fieldValue ? "opacity-100" : "opacity-0"
                      )}
                    />
                    {tty}
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