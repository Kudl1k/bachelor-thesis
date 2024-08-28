import { ChargeRecord } from "@/models/ChargerData";
import { useEffect, useState } from "react";
import { CartesianGrid, Line, LineChart, XAxis, YAxis } from "recharts";
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
  capacity: {
    label: "Capacity (mAh)",
    color: "#2563eb",
  },
  voltage: {
    label: "Voltage (V)",
    color: "#f87171",
  },
  current: {
    label: "Current (A)",
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
    <div className="rounded-lg p-4">
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
      <div className="flex w-full justify-between">
        <div className="">
          <p className="font-semibold">
            ID: <span>{data.battery.id}</span>
          </p>
          <p className="font-semibold">
            <span>{getProgram(data.program)}</span>
          </p>
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

function getProgram(program: string): string {
  switch (program) {
    case "C":
      return "Charging";
    case "discharge":
      return "Discharging";
    case "storage":
      return "Storage";
    default:
      return "Unknown";
  }
}

function parseCustomDate(dateString: string): Date {
  const [datePart, timePart] = dateString.split(" ");
  const [day, month, year] = datePart.split(".").map(Number);
  const [hours, minutes, seconds] = timePart.split(":").map(Number);
  return new Date(year, month - 1, day, hours, minutes, seconds);
}
