package com.anmipo.android.trentobus.db;

import java.io.DataInputStream;
import java.io.IOException;

/**
 * Direction associated with a schedule (forward, return, unspecified).
 * Allows distinguishing between two bus stops with the same name, but at 
 * alternate sides of the road.
 * @author "Andrei Popleteev"
 */
public class Direction {
    public final static Direction UNDEFINED = new Direction(" ");
    public final static Direction FORWARD = new Direction("a");
    public final static Direction RETURN = new Direction("r");
    
    public final String symbol;
    private Direction(String symbol) {
        this.symbol = symbol;
    }

    /**
     * Returns a Direction instance associated with the given symbol,
     * or throws an IllegalArgumentException if there is no such direction.
     * @param symbol
     * @return
     */
    public static Direction fromString(String symbol) {
        if (symbol.equals(FORWARD.symbol)) {
            return FORWARD;
        } else if (symbol.equals(RETURN.symbol)) {
            return RETURN;
        } else if (symbol.equals(UNDEFINED.symbol)) {
            return UNDEFINED;
        } else {
            throw new IllegalArgumentException("unknown direction: " + symbol);
        }
    }
    
    public static Direction readFromStream(DataInputStream dataIn)
            throws IOException, IllegalArgumentException {
        return fromString(dataIn.readUTF());
    }
}