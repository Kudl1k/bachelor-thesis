import { ChargeRecord } from "@/models/ChargerData";
import { Battery, BatteryCharging, BatteryFull, Hash } from "lucide-react";
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
    yAxisId: "farRight", // Changed to use a separate axis
  },
} satisfies ChartConfig;

const cellChartConfig = {
  voltage: {
    label: "Voltage",
    color: "#2563eb",
    unit: "V",
    yAxisId: "left",
  },
};

export interface ChargeRecordChartProps {
  data: ChargeRecord;
  className?: string;
}

export function ChargeRecordChart({ data, className }: ChargeRecordChartProps) {
  useEffect(() => {
    console.log("ChargeRecordChart data updated:", data);
  }, [data]);

  const [timeRange, setTimeRange] = useState("full");

  const maxCellVoltage = (() => {
    try {
      if (data.cells && data.cells.length > 0) {
        const voltages = data.cells.flatMap((cell) =>
          cell.voltages.map((v) => {
            const voltage = parseFloat(String(v.voltage));
            return !isNaN(voltage) ? voltage : 0;
          })
        );

        const validVoltages = voltages.filter((v) => v > 0 && v < 10);

        if (validVoltages.length > 0) {
          const max = Math.max(...validVoltages);
          return max * 2;
        }
      }
      return 4.2;
    } catch (error) {
      console.error("Error calculating max cell voltage:", error);
      return 4.2;
    }
  })();

  const minCellVoltage = 0;

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

  function filterDataByTimeRange(
    voltages: import("@/models/ChargerData").CellTrakcing[],
    timeRange: string
  ) {
    const lastRecordTime =
      voltages.length > 0
        ? parseCustomDate(voltages[voltages.length - 1].timestamp)
        : new Date();

    return voltages.filter((record) => {
      const recordTime = parseCustomDate(record.timestamp);
      const diff = lastRecordTime.getTime() - recordTime.getTime();

      if (timeRange === "full") {
        return true;
      }
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
  }

  return (
    <div className="rounded-lg p-4 mt-4 shadow-md">
      <div className="flex w-full justify-between">
        <h1 className="text-xl font-bold">Slot {data.slot}</h1>
        <div>
          <Select value={timeRange} onValueChange={setTimeRange}>
            <SelectTrigger
              className="w-[160px] rounded-lg sm:ml-auto"
              aria-label="Select a time range"
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
      </div>
      <div className="flex w-full justify-between text-start">
        <div className="">
          <div className="flex items-center gap-1">
            <Hash className="" />
            <h2 className="text-xl font-semibold">{data.battery.id}</h2>
          </div>
          <div className="flex items-center gap-1">
            {data.finishedAt ? (
              <>
                <BatteryFull />
                <p className="font-semibold">Finished</p>
              </>
            ) : data.tracking[data.tracking.length - 1].charging ? (
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
          <p className="text-sm ml-2">
            <span style={{ color: chartConfig.real_capacity.color }}>
              {data.tracking[data.tracking.length - 1].capacity} mAh
            </span>
          </p>
          <p className="text-sm ml-2">
            <span style={{ color: chartConfig.voltage.color }}>
              {data.tracking[data.tracking.length - 1].voltage} V
            </span>
          </p>
          <p className="text-sm ml-2">
            <span style={{ color: chartConfig.current.color }}>
              {data.tracking[data.tracking.length - 1].current} A
            </span>
          </p>
        </div>
      </div>
      <ChartContainer
        key={data.idChargeRecord}
        config={chartConfig}
        className={`min-h-[200px] max-h-[400px] w-full ${className}`}
      >
        <LineChart
          accessibilityLayer
          data={filteredData}
          syncId={1}
          margin={{ right: 40, left: 5 }}
        >
          <CartesianGrid vertical={false} />
          <XAxis
            dataKey="timestamp"
            tickLine={false}
            tickMargin={10}
            axisLine={false}
            tickFormatter={(value) =>
              value.split(" ")[1].split(":").slice(0, 3).join(":")
            }
            minTickGap={20}
          />

          {/* Right Y-axis for voltage */}
          <YAxis
            yAxisId="right"
            orientation="right"
            tickLine={false}
            axisLine={false} // Remove colored line
            tickMargin={20}
            domain={["auto", "auto"]}
            label={{
              value: "V",
              angle: -90,
              dx: 30,
              fill: chartConfig.voltage.color,
              style: { fontWeight: "bold" },
            }}
            tick={{
              stroke: chartConfig.voltage.color,
            }}
            tickFormatter={(value) => value.toFixed(2)}
            stroke={chartConfig.voltage.color} // Color the line itself
          />

          {/* Far right Y-axis for current */}
          <YAxis
            yAxisId="farRight"
            orientation="right"
            tickLine={false}
            axisLine={false} // Remove colored line
            tickMargin={40}
            domain={["auto", "auto"]}
            label={{
              value: "A",
              angle: -90,
              dx: 50,
              fill: chartConfig.current.color,
              style: { fontWeight: "bold" },
            }}
            tick={{
              stroke: chartConfig.current.color,
            }}
            tickFormatter={(value) => value.toFixed(2)}
            stroke={chartConfig.current.color} // Color the line itself
          />

          {/* Left Y-axis for capacity */}
          <YAxis
            yAxisId="left"
            tickLine={false}
            axisLine={false} // Remove colored line
            tickMargin={20}
            domain={["auto", "auto"]}
            label={{
              value: "mAh",
              angle: -90,
              dx: -30,
              fill: chartConfig.real_capacity.color,
              style: { fontWeight: "bold" },
            }}
            tick={{
              stroke: chartConfig.real_capacity.color,
            }}
            stroke={chartConfig.real_capacity.color} // Color the line itself
          />

          <ChartTooltip
            content={
              <ChartTooltipContent
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
                      <div className="ml-auto flex items-baseline gap-0.5 font-mono font-medium tabular-nums">
                        <span
                          style={{
                            color:
                              chartConfig[name as keyof typeof chartConfig]
                                ?.color || "inherit",
                          }}
                        >
                          {value}
                          <span className="font-normal">
                            {chartConfig[name as keyof typeof chartConfig]
                              ?.unit || ""}
                          </span>
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
            dataKey="voltage"
            yAxisId="right"
            fill={chartConfig.voltage.color}
            stroke={chartConfig.voltage.color} // Use direct color reference instead of var
            strokeWidth={2}
            dot={false}
          />
          <Line
            dataKey="current"
            yAxisId="farRight" // Use the far right axis
            fill={chartConfig.current.color}
            stroke={chartConfig.current.color} // Use direct color reference instead of var
            strokeWidth={2}
            dot={false}
          />
          <Line
            dataKey="real_capacity"
            yAxisId="left"
            fill={chartConfig.real_capacity.color}
            stroke={chartConfig.real_capacity.color} // Use direct color reference instead of var
            strokeWidth={2}
            dot={false}
          />
        </LineChart>
      </ChartContainer>
      <div className="grid grid-cols-1 sm:grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-3">
        {data.battery.cells > 0 &&
          data.cells.map((cell, index) => {
            const filteredCellData = filterDataByTimeRange(
              cell.voltages,
              timeRange
            );
            return (
              <div key={index}>
                <div className="rounded-lg p-4 shadow-md">
                  <div className="flex w-full justify-between">
                    <h1 className="text-xl font-bold">Cell {cell.number}</h1>
                    <div className="text-end">
                      <p className="text-sm ml-2">
                        <span style={{ color: cellChartConfig.voltage.color }}>
                          {cell.voltages[cell.voltages.length - 1].voltage} V
                        </span>
                      </p>
                    </div>
                  </div>

                  <ChartContainer
                    config={cellChartConfig}
                    className={`min-h-[200px] max-h-[400px] w-full`}
                  >
                    <LineChart data={filteredCellData} syncId={1}>
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

                      <YAxis
                        tickLine={false}
                        axisLine={false} // Remove colored line
                        tickMargin={20}
                        domain={[minCellVoltage, maxCellVoltage]}
                        tick={{
                          fill: cellChartConfig.voltage.color,
                          fontWeight: "bold",
                        }}
                        stroke={cellChartConfig.voltage.color}
                        label={{
                          value: "V",
                          position: "insideLeft",
                          angle: -90,
                          dy: -10,
                          fill: cellChartConfig.voltage.color,
                          style: { fontWeight: "bold" },
                        }}
                      />
                      <ChartTooltip
                        content={
                          <ChartTooltipContent
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
                                  {cellChartConfig[
                                    name as keyof typeof cellChartConfig
                                  ]?.label || name}
                                  <div className="ml-auto flex items-baseline gap-0.5 font-mono font-medium tabular-nums">
                                    <span
                                      style={{
                                        color:
                                          cellChartConfig[
                                            name as keyof typeof cellChartConfig
                                          ]?.color || "inherit",
                                      }}
                                    >
                                      {value}
                                      <span className="font-normal">
                                        {cellChartConfig[
                                          name as keyof typeof cellChartConfig
                                        ]?.unit || ""}
                                      </span>
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
                        dataKey="voltage"
                        fill={cellChartConfig.voltage.color}
                        stroke={cellChartConfig.voltage.color} // Use direct color reference
                        strokeWidth={2}
                        dot={false}
                      />
                    </LineChart>
                  </ChartContainer>
                </div>
              </div>
            );
          })}
      </div>
    </div>
  );
}

function parseCustomDate(dateString: string): Date {
  if (dateString && typeof dateString === "string") {
    const [datePart, timePart] = dateString.split(" ");
    const [day, month, year] = datePart.split(".").map(Number);
    const [hours, minutes, seconds] = timePart.split(":").map(Number);
    return new Date(year, month - 1, day, hours, minutes, seconds);
  } else {
    console.error("Expected a string but got:", dateString);
    return new Date(); // Return a default date or handle the error as needed
  }
}
