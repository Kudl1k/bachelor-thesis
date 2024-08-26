import { ChargeRecord } from "@/models/ChargerData";
import { useEffect } from "react";
import { CartesianGrid, Line, LineChart, XAxis, YAxis } from "recharts";
import {
  ChartConfig,
  ChartContainer,
  ChartTooltip,
  ChartTooltipContent,
} from "../ui/chart";

const chartConfig = {
  capacity: {
    label: "Capacity (mAh)",
    color: "#2563eb",
  },
  voltage: {
    label: "Voltage (V)",
    color: "#f87171",
  },
  current: {
    label: "Current (mA)",
    color: "#34d399",
  },
} satisfies ChartConfig;

export interface ChargeRecordChartProps {
  data: ChargeRecord;
}

export function ChargeRecordChart({ data }: ChargeRecordChartProps) {
  useEffect(() => {
    console.log("ChargeRecordChart data updated:", data);
  }, [data]);
  return (
    <div className="shadow-md rounded-lg p-4">
      <h1 className="text-lg font-semibold">
        Slot: {data.slot} - #{data.battery.id} - {data.battery.type.shortcut} -{" "}
        {data.battery.size.name} - {data.battery.factory_capacity}mAh
      </h1>
      <ChartContainer
        key={data.idChargeRecord}
        config={chartConfig}
        className="min-h-[200px] w-full "
      >
        <LineChart accessibilityLayer data={data.tracking}>
          <CartesianGrid vertical={false} />
          <XAxis
            dataKey="timestamp"
            tickLine={false}
            tickMargin={10}
            axisLine={false}
          />

          <YAxis tickLine={false} axisLine={false} tickMargin={20} />
          <ChartTooltip
            content={
              <ChartTooltipContent className="w-[150px]" indicator="line" />
            }
          />
          <Line
            dataKey="capacity"
            fill={chartConfig.capacity.color}
            stroke="var(--color-capacity)"
            strokeWidth={2}
            dot={false}
          />
          <Line
            dataKey="voltage"
            fill={chartConfig.voltage.color}
            stroke="var(--color-voltage)"
            strokeWidth={2}
            dot={false}
          />
          <Line
            dataKey="current"
            fill={chartConfig.current.color}
            stroke="var(--color-current)"
            strokeWidth={2}
            dot={false}
          />
        </LineChart>
      </ChartContainer>
    </div>
  );
}
