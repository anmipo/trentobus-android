package com.anmipo.android.trentobus.db;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.Hashtable;

import android.content.Context;

/**
 * Complete schedule of one bus, for one direction.
 * @author Andrei Popleteev
 */
public class Schedule {
    private ScheduleInfo scheduleInfo;
    protected String[][] times;
    protected String[] stopNames;
    protected String[] frequenzaLine;
    protected String[] lineaLine;
    protected int rowCount; 
    protected int colCount;
    protected Hashtable<String, String> legend;
    protected Date validFrom;
    protected Date validTo;
    
    public Schedule(ScheduleInfo schInfo) {
        this.scheduleInfo = schInfo;
        legend = new Hashtable<String, String>();
        validFrom = new Date(0);
        validTo = new Date(0);
        resize(0, 0);
    }
    
    /**
     * Reallocates the timetable holding arrays for new dimensions.
     * Cols do not include stop names, and thus can be 0;
     * @param rows
     * @param cols
     */
    protected void resize(int rows, int cols) {
        this.rowCount = rows;
        this.colCount = cols;
        times = new String[rowCount][colCount];
        frequenzaLine = new String[colCount];
        lineaLine = new String[colCount];
        stopNames = new String[rowCount];
    }
    
    public ScheduleInfo getScheduleInfo() {
        return scheduleInfo;
    }
    
    /**
     * Loads the data from the binary resource created by ScheduleConverter.
     * @param dataIn
     * @throws IOException
     */
    public void loadFromStream(DataInputStream dataIn) throws IOException {
        legend.clear();
        
        //Skip the fields already loaded by the parent form from the index file.
        dataIn.readUTF(); //String busNumber - ignored
        dataIn.readUTF(); //String isHolidayChar - ignored
        dataIn.readUTF(); //String direction - ignored
        
        // schedule validity dates
        validFrom = new Date(dataIn.readLong());
        validTo = new Date(dataIn.readLong());
        
        // schedule legend items
        byte legendItemCount = dataIn.readByte();
        legend.clear();
        for (int i=0; i<legendItemCount; i++) {
            String symbol = dataIn.readUTF();
            String descr = dataIn.readUTF();
            legend.put(symbol, descr);
        }

        // timetable dimensions
        int cols = dataIn.readUnsignedShort();
        int rows = dataIn.readUnsignedShort();
        
        // cols value includes stop names column, therefore -1
        resize(rows, cols - 1); // allocate memory for the timetable content
        
        //In the stream, timetable includes both times and stop names.
        //To separate the, we handle the first column separately.
        dataIn.readUTF(); //skip first (empty) column
        frequenzaLine = readScheduleLine(dataIn, colCount);
        dataIn.readUTF(); //skip first (empty) column
        lineaLine = readScheduleLine(dataIn, colCount);
        for (int row=0; row<rowCount; row++) {
            stopNames[row] = dataIn.readUTF();
            times[row] = readScheduleLine(dataIn, colCount);
        }
    }

    /**
     * Loads one timetable row with "count" items.
     * Returns an array [busStopName, time, time, time...]. 
     * @param in
     * @param count
     * @return
     * @throws IOException
     */
    protected String[] readScheduleLine(DataInputStream in, int count) throws IOException {
        String[] result = new String[count];
        for (int i=0; i<count; i++)
            result[i] = in.readUTF();
        return result;
    }
    
    /**
     * A data stream managing boilerplate for loadFromStream().
     * @param context
     * @param fileName
     * @throws IOException 
     */
    public void loadFromAsset(Context context, String fileName) 
            throws IOException {
        InputStream rawIn = null;
        try {
            rawIn = context.getAssets().open(fileName);
            DataInputStream dataIn = new DataInputStream(rawIn);
            try {
                loadFromStream(dataIn);
            } finally {
                dataIn.close();
            }
        } finally {
            if (rawIn != null) {
                rawIn.close();
            }
        }        
    }
    
    public int getRowCount() {
        return rowCount;
    }
    public int getColCount() {
        return colCount;
    }

    public String getTime(int row, int col) {
        return times[row][col];
    }

    public CharSequence getStopName(int row) {
        return stopNames[row];
    }
    
    /**
     * Returns the matrix of departure times.
     * <b>CONTRACT</b>: this method returns the original schedule array;
     * this significantly improves the performance, but the caller 
     * must take care to keep the received array unmodified.
     * @return
     */
    public String[][] getTimes() {
    	return times;
    }
    
    /**
     * Returns an array of bus stop names.
     * <b>CONTRACT</b>: this method returns the original array;
     * this significantly improves the performance, but the caller 
     * must take care to keep the received array unmodified.
     * @return
     */
    public String[] getStopNames() {
    	return stopNames;
    }

    /**
     * Returns an array of legend symbols for each route (timetable column).
     * <b>CONTRACT</b>: this method returns the original array;
     * this significantly improves the performance, but the caller 
     * must take care to keep the received array unmodified.
     * @return
     */
	public String[] getLegends() {
		// TODO: return proper legends, this is a stub
		return frequenzaLine;
	}
}
