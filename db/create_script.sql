
-- ALTER TABLE Charge_Tracking DROP CONSTRAINT IF EXISTS fk_charge_tracking_charge_record;
-- ALTER TABLE Charge_Record DROP CONSTRAINT IF EXISTS fk_charge_charger;
-- ALTER TABLE Charge_Record DROP CONSTRAINT IF EXISTS fk_charge_battery;
-- ALTER TABLE Charger_Type DROP CONSTRAINT IF EXISTS fk_charger_type_charger;
-- ALTER TABLE Charger_Type DROP CONSTRAINT IF EXISTS fk_charger_type_type;
-- ALTER TABLE Battery DROP CONSTRAINT IF EXISTS fk_type;
DROP TABLE IF EXISTS Charge_Tracking;
DROP TABLE IF EXISTS Charge_Record;
DROP TABLE IF EXISTS Charger_Type;
DROP TABLE IF EXISTS Charger;
DROP TABLE IF EXISTS Battery;
DROP TABLE IF EXISTS Type;


--Create a table of battery type
create table Type(
    id_type INT GENERATED ALWAYS AS IDENTITY,
    shortcut VARCHAR(10) NOT NULL,
    name VARCHAR(30) NOT NULL,
    PRIMARY KEY(id_type)
);

--Create a table for battery
create table Battery(
    id_battery INT GENERATED ALWAYS AS IDENTITY,
    id_type INT,
    size VARCHAR(10) NOT NULL,
    factory_capacity INT NOT NULL,
    voltage INT NOT NULL,
    last_charged_capacity INT,
    last_time_charged_at TIMESTAMP,
    created_at TIMESTAMP,
    PRIMARY KEY (id_battery),
    CONSTRAINT fk_type FOREIGN KEY(id_type) REFERENCES Type(id_type)
);


--Create table for charger
create table Charger(
    id_charger INT GENERATED ALWAYS AS IDENTITY,
    name VARCHAR(50) NOT NULL,
    tty VARCHAR(10) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    PRIMARY KEY (id_charger)
);

--Create a connection table between the charger and type
create table Charger_Type(
    id_charger INT NOT NULL ,
    id_type INT NOT NULL ,
    PRIMARY KEY (id_charger,id_type),
    CONSTRAINT fk_charger_type_charger FOREIGN KEY(id_charger) REFERENCES Charger(id_charger),
    CONSTRAINT fk_charger_type_type FOREIGN KEY(id_type) REFERENCES Type(id_type)
);

--Create a table for the records for charging
create table Charge_Record(
    id_charge_record INT GENERATED ALWAYS AS IDENTITY,
    program INT NOT NULL,
    started_at TIMESTAMP NOT NULL,
    finished_at TIMESTAMP,
    charged_capacity INT,
    id_charger INT NOT NULL ,
    id_battery INT NOT NULL ,
    PRIMARY KEY (id_charge_record),
    CONSTRAINT fk_charge_charger FOREIGN KEY (id_charger) REFERENCES Charger(id_charger),
    CONSTRAINT fk_charge_battery FOREIGN KEY (id_battery) REFERENCES Battery(id_battery)
);

--Create table for tracking the charging
create table Charge_Tracking(
    timestamp TIMESTAMP,
    id_charge_record INT NOT NULL ,
    capacity INT NOT NULL,
    voltage INT NOT NULL,
    current INT NOT NULL,
    PRIMARY KEY (timestamp),
    CONSTRAINT fk_charge_tracking_charge_record FOREIGN KEY (id_charge_record) REFERENCES Charge_Record(id_charge_record)
);