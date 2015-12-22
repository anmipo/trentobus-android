package com.anmipo.android.trentobus.db;

import java.io.DataInputStream;
import java.io.IOException;

public class BusStop {
    String name;
    Direction direction;
    public BusStop(String name, Direction direction) {
        this.name = name;
        this.direction = direction;
    }
    public static BusStop readFromDataStream(DataInputStream out) throws IOException {
        String name = out.readUTF();
        Direction direction = Direction.fromString(out.readUTF());
        return new BusStop(name, direction);
    }
    @Override
    public boolean equals(Object obj) {
        boolean result = false;
        if (obj instanceof BusStop) {
            BusStop bs = (BusStop) obj;
            result = this.direction.equals(bs.direction) && 
                    this.name.equals(bs.name);
        }
        return result;
    }
}
