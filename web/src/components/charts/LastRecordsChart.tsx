import { ChargeRecord } from "@/models/ChargerData";
import { Bar, BarChart, CartesianGrid, XAxis, YAxis } from "recharts";
import { AspectRatio } from "../ui/aspect-ratio";
import {
  ChartConfig,
  ChartContainer,
  ChartTooltip,
  ChartTooltipContent,
} from "../ui/chart";

const LastRecordsChartConfig = {
  chargedCapacity: {
    label: "Charged",
    color: "hsl(var(--chart-1))",
  },
  dischargedCapacity: {
    label: "Discharged",
    color: "hsl(var(--chart-2))",
  },
} satisfies ChartConfig;

export interface LastRecordsChartProps {
  data: ChargeRecord[];
}

export function LastRecordsChart({ data }: LastRecordsChartProps) {
  if (!data || data.length === 0) {
    return (
      <div className="rounded-lg full-w shadow-md min-h-[200px] p-4">
        <AspectRatio ratio={16 / 9}>
          <div className="flex h-full w-full items-center justify-center">
            <h4 className="italic">
              This battery does not have any charge records.
            </h4>
          </div>
        </AspectRatio>
      </div>
    );
  }
  console.log("LastRecordsChart data updated:", data);

  return (
    <ChartContainer config={LastRecordsChartConfig}>
      <BarChart accessibilityLayer data={data}>
        <CartesianGrid vertical={false} />
        <XAxis
          dataKey="idChargeRecord"
          tickLine={false}
          tickMargin={10}
          axisLine={false}
        />
        <YAxis
          tickLine={false}
          tickMargin={10}
          axisLine={false}
          allowDecimals={false}
        />
        <ChartTooltip
          content={
            <ChartTooltipContent
              hideLabel
              formatter={(value, name) => {
                console.log("Tooltip formatter called with:", { value, name });
                if (!value || !name) {
                  console.error("Invalid value or name in tooltip formatter:", {
                    value,
                    name,
                  });
                  return null;
                }
                return (
                  <>
                    <div
                      className="h-2.5 w-2.5 shrink-0 rounded-[2px] bg-[--color-bg]"
                      style={
                        {
                          "--color-bg": `var(--color-${name})`,
                        } as React.CSSProperties
                      }
                    />
                    <div className="flex min-w-[130px] items-center text-xs text-muted-foreground">
                      {LastRecordsChartConfig[
                        name as keyof typeof LastRecordsChartConfig
                      ]?.label || name}
                      <div className="ml-auto flex items-baseline gap-0.5 font-mono font-medium tabular-nums text-foreground">
                        {value}
                        <span className="font-normal text-muted-foreground">
                          mAh
                        </span>
                      </div>
                    </div>
                  </>
                );
              }}
            />
          }
          cursor={false}
        />
        <Bar
          dataKey="chargedCapacity"
          stackId="a"
          fill="var(--color-chargedCapacity)"
          radius={[0, 0, 4, 4]}
        />
        <Bar
          dataKey="dischargedCapacity"
          stackId="a"
          fill="var(--color-dischargedCapacity)"
          radius={[4, 4, 0, 0]}
        />
      </BarChart>
    </ChartContainer>
  );
}
