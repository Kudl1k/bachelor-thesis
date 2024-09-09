import { ChargeRecordChart } from "@/components/charts/ChargeRecordChart";
import { buttonVariants } from "@/components/ui/button";
import { Card, CardContent, CardHeader } from "@/components/ui/card";
import {
  Carousel,
  CarouselContent,
  CarouselItem,
  CarouselNext,
  CarouselPrevious,
} from "@/components/ui/carousel";
import {
  ChargeRecord,
  fetchNotEndedChargeRecord,
  useWebSocketTracking,
} from "@/models/ChargerData";
import { useEffect, useRef, useState } from "react";
import { Link } from "react-router-dom";

export function StartPage() {
  const [chargeRecords, setChargeRecords] = useState<ChargeRecord[]>([]);
  const [chargerId, setChargerId] = useState<number | null>(null);
  const hasFetched = useRef(false);

  useEffect(() => {
    if (!hasFetched.current) {
      fetchNotEndedChargeRecord(setChargeRecords);
      hasFetched.current = true;
    }
  }, []);

  useEffect(() => {
    if (chargeRecords.length > 0) {
      setChargerId(chargeRecords[0].charger.id);
    }
  }, [chargeRecords]);

  useWebSocketTracking({
    id_charger: chargerId ?? 0,
    setChargeRecords,
  });

  return (
    <>
      {(!chargeRecords || chargeRecords.length === 0) && (
        <div className="flex justify-center items-center min-h-screen min-w-screen">
          <Link
            className={buttonVariants({ variant: "default" })}
            to={"/setup"}
          >
            Setup
          </Link>
        </div>
      )}

      {chargeRecords && (
        <div className="flex justify-center">
          <div className="xl:w-3/5 lg:w-4/5 md:w-4/5 sm:w-4/5 w-4/5 py-4">
            <Carousel opts={{ loop: true, watchDrag: false }}>
              <CarouselContent className="h-full">
                {chargeRecords.length > 1 && (
                  <CarouselItem
                    key={`carousel-item-${chargeRecords[0].idChargeRecord}`}
                  >
                    <Card>
                      <CardHeader>
                        <h1 className="text-xl font-bold">
                          {chargeRecords[0].charger.name}
                        </h1>
                      </CardHeader>
                      <CardContent className="grid grid-cols-1 sm:grid-cols-1 md:grid-cols-1 lg:grid-cols-2 xl:grid-cols-2 gap-4">
                        {chargeRecords.map((record) => (
                          <ChargeRecordChart
                            key={record.idChargeRecord}
                            data={record}
                          />
                        ))}
                      </CardContent>
                    </Card>
                  </CarouselItem>
                )}
                {chargeRecords.map((record) => (
                  <CarouselItem key={`carousel-item-${record.idChargeRecord}`}>
                    <Card>
                      <CardContent>
                        <ChargeRecordChart
                          key={record.idChargeRecord}
                          data={record}
                        />
                      </CardContent>
                    </Card>
                  </CarouselItem>
                ))}
              </CarouselContent>
              <CarouselPrevious />
              <CarouselNext />
            </Carousel>
          </div>
        </div>
      )}
    </>
  );
}
