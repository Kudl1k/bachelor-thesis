import { ChargeRecord } from "@/models/ChargerData";
import { Battery, BatteryCharging, Hash } from "lucide-react";
import { useEffect, useState } from "react";
import { CartesianGrid, Line, LineChart, XAxis, YAxis } from "recharts";
import { AspectRatio } from "../ui/aspect-ratio";
import { Badge } from "../ui/badge";
import {
  ChartConfig,
  ChartContainer,
  ChartTooltip,
  ChartTooltipContent,
} from "../ui/chart";
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "../ui/select";

const chartConfig = {
  real_capacity: {
    label: "Capacity",
    color: "#2563eb",
  },
  voltage: {
    label: "Voltage",
    color: "#f87171",
  },
  current: {
    label: "Current",
    color: "#34d399",
  },
} satisfies ChartConfig;

export interface ChargeRecordChartProps {
  data: ChargeRecord;
  className?: string;
}

export function ChargeRecordChart({ data, className }: ChargeRecordChartProps) {
  useEffect(() => {
    console.log("ChargeRecordChart data updated:", data);
  }, [data]);

  const [timeRange, setTimeRange] = useState("full");

  if (data.tracking.length === 0) {
    return (
      <div className="rounded-lg full-w shadow-md min-h-[200px] p-4">
        <AspectRatio ratio={16 / 9}>
          <div className="flex h-full w-full items-center justify-center">
            <h4 className="italic">
              This record does not have any tracking data.
            </h4>
          </div>
        </AspectRatio>
      </div>
    );
  }

  const filteredData = data.tracking.filter((record) => {
    if (timeRange === "full") {
      return true;
    }
    const lastRecordTime =
      data.tracking.length > 0
        ? parseCustomDate(data.tracking[data.tracking.length - 1].timestamp)
        : new Date();
    const recordTime = parseCustomDate(record.timestamp);
    console.log("recordTime", recordTime);
    const diff = lastRecordTime.getTime() - recordTime.getTime();
    if (timeRange === "5m") {
      return diff <= 5 * 60 * 1000;
    }
    if (timeRange === "15m") {
      return diff <= 15 * 60 * 1000;
    }
    if (timeRange === "30m") {
      return diff <= 30 * 60 * 1000;
    }
    if (timeRange === "1h") {
      return diff <= 60 * 60 * 1000;
    }
    if (timeRange === "2h") {
      return diff <= 2 * 60 * 60 * 1000;
    }
    return true;
  });

  return (
    <div className="rounded-lg p-4 mt-4 shadow-md">
      <div className="flex w-full justify-between">
        <h1 className="text-xl font-bold">Slot {data.slot}</h1>
        <Select value={timeRange} onValueChange={setTimeRange}>
          <SelectTrigger
            className="w-[160px] rounded-lg sm:ml-auto"
            aria-label="Select a value"
          >
            <SelectValue placeholder="Full view" />
          </SelectTrigger>
          <SelectContent className="rounded-xl">
            <SelectItem value="full" className="rounded-lg">
              Full view
            </SelectItem>
            <SelectItem value="5m" className="rounded-lg">
              Last 5 minutes
            </SelectItem>
            <SelectItem value="15m" className="rounded-lg">
              Last 15 minutes
            </SelectItem>
            <SelectItem value="30m" className="rounded-lg">
              Last 30 minutes
            </SelectItem>
            <SelectItem value="1h" className="rounded-lg">
              Last 1 hour
            </SelectItem>
            <SelectItem value="2h" className="rounded-lg">
              Last 2 hours
            </SelectItem>
          </SelectContent>
        </Select>
      </div>
      <div className="flex w-full justify-between text-start">
        <div className="">
          <div className="flex items-center gap-1">
            <Hash className="" />
            <h2 className="text-xl font-semibold">{data.battery.id}</h2>
          </div>
          <div className="flex items-center gap-1">
            {data.tracking[data.tracking.length - 1].charging ? (
              <>
                <BatteryCharging />
                <p className="font-semibold">Charging</p>
              </>
            ) : (
              <>
                <Battery />
                <p className="font-semibold">Discharging</p>
              </>
            )}
          </div>
          <div>
            <Badge className="me-1">{data.battery.type.shortcut}</Badge>
            <Badge className="me-1">{data.battery.size.name}</Badge>
            <Badge className="me-1">{data.battery.factory_capacity} mAh</Badge>
          </div>
        </div>
        <div className="text-end">
          <p className="text-sm text-gray-500 ml-2">
            {data.startedAt.split(" ")[1]} -{" "}
            {data.tracking[data.tracking.length - 1].timestamp.split(" ")[1]}
          </p>
          <p className="text-sm text-gray-500 ml-2">
            {data.tracking[data.tracking.length - 1].capacity} mAh
          </p>
          <p className="text-sm text-gray-500 ml-2">
            {data.tracking[data.tracking.length - 1].voltage} V
          </p>
          <p className="text-sm text-gray-500 ml-2">
            {data.tracking[data.tracking.length - 1].current} A
          </p>
        </div>
      </div>
      <ChartContainer
        key={data.idChargeRecord}
        config={chartConfig}
        className={`min-h-[200px] w-full ${className}`}
      >
        <LineChart accessibilityLayer data={filteredData}>
          <CartesianGrid vertical={false} />
          <XAxis
            dataKey="timestamp"
            tickLine={false}
            tickMargin={10}
            axisLine={false}
            tickFormatter={(value) =>
              value.split(" ")[1].split(":").slice(0, 3).join(":")
            }
          />

          <YAxis tickLine={false} axisLine={false} tickMargin={20} />
          <ChartTooltip
            content={
              <ChartTooltipContent
                hideLabel
                formatter={(value, name) => (
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
                      {chartConfig[name as keyof typeof chartConfig]?.label ||
                        name}
                      <div className="ml-auto flex items-baseline gap-0.5 font-mono font-medium tabular-nums text-foreground">
                        {value}
                        <span className="font-normal text-muted-foreground">
                          {name === "real_capacity"
                            ? " mAh"
                            : name === "voltage"
                            ? " V"
                            : " A"}
                        </span>
                      </div>
                    </div>
                  </>
                )}
              />
            }
            cursor={true}
          />
          <Line
            dataKey="real_capacity"
            fill={chartConfig.real_capacity.color}
            stroke="var(--color-real_capacity)"
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

function parseCustomDate(dateString: string): Date {
  const [datePart, timePart] = dateString.split(" ");
  const [day, month, year] = datePart.split(".").map(Number);
  const [hours, minutes, seconds] = timePart.split(":").map(Number);
  return new Date(year, month - 1, day, hours, minutes, seconds);
}
