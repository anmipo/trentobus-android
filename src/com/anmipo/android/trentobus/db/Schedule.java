package com.anmipo.android.trentobus.db;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.Date;

import android.content.Context;

/**
 * Complete schedule of one bus, for one direction.
 * 
 * @author Andrei Popleteev
 */
public class Schedule {
	private ScheduleInfo scheduleInfo;
	protected String[][] times;
	protected String[] stopNames;
	// protected String[] frequenzaLine;
	// protected String[] lineaLine;
	protected ScheduleLegend[] legends;
	protected int rowCount;
	protected int colCount;
	protected Date validFrom;
	protected Date validTo;

	public Schedule(ScheduleInfo schInfo) {
		this.scheduleInfo = schInfo;
		validFrom = new Date(0);
		validTo = new Date(0);
		resize(0, 0);
	}

	/**
	 * Reallocates the timetable holding arrays for new dimensions. Cols do not
	 * include stop names, and thus can be 0;
	 * 
	 * @param rows
	 * @param cols
	 */
	protected void resize(int rows, int cols) {
		this.rowCount = rows;
		this.colCount = cols;
		times = new String[rowCount][colCount];
		legends = new ScheduleLegend[colCount];
		stopNames = new String[rowCount];
	}

	public ScheduleInfo getScheduleInfo() {
		return scheduleInfo;
	}

	/**
	 * Loads the data from the binary resource created by ScheduleConverter.
	 * 
	 * @param dataIn
	 * @throws IOException
	 */
	public void loadFromStream(DataInputStream dataIn) throws IOException {
		// Skip the fields already loaded by the parent form from the index
		// file.
		dataIn.readUTF(); // String busNumber - ignored
		dataIn.readUTF(); // String isHolidayChar - ignored
		dataIn.readUTF(); // String direction - ignored

		// schedule validity dates
		validFrom = new Date(dataIn.readLong());
		validTo = new Date(dataIn.readLong());

		/*
		 * schedule legend items - they map legend symbols with descriptions.
		 * We read them, but do not use - the symbols mapping is pre-defined 
		 * in ScheduleLegend.
		 */
		byte legendItemCount = dataIn.readByte();
		for (int i = 0; i < legendItemCount; i++) {
			dataIn.readUTF(); // symbol
			dataIn.readUTF(); // description
		}

		// timetable dimensions
		int cols = dataIn.readUnsignedShort();
		int rows = dataIn.readUnsignedShort();

		// cols value includes stop names column, therefore -1
		resize(rows, cols - 1); // allocate memory for the timetable content

		// In the stream, timetable includes both times and stop names.
		// To separate the, we handle the first column separately.
		dataIn.readUTF(); // skip first (empty) column
		String[] frequenzaLine = readScheduleLine(dataIn, colCount);
		dataIn.readUTF(); // skip first (empty) column
		String[] lineaLine = readScheduleLine(dataIn, colCount);
		for (int row = 0; row < rowCount; row++) {
			stopNames[row] = dataIn.readUTF();
			times[row] = readScheduleLine(dataIn, colCount);
		}
		for (int col = 0; col < colCount; col++) {
			legends[col] = ScheduleLegend
					.getInstance(frequenzaLine[col], lineaLine[col]);
		}
	}

	/**
	 * Loads one timetable row with "count" items. Returns an array
	 * [busStopName, time, time, time...].
	 * 
	 * @param in
	 * @param count
	 * @return
	 * @throws IOException
	 */
	protected String[] readScheduleLine(DataInputStream in, int count)
			throws IOException {
		String[] result = new String[count];
		for (int i = 0; i < count; i++)
			result[i] = in.readUTF();
		return result;
	}

	/**
	 * A data stream managing boilerplate for loadFromStream().
	 * 
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
	 * Returns the matrix of departure times. <b>CONTRACT</b>: this method
	 * returns the original schedule array; this significantly improves the
	 * performance, but the caller must take care to keep the received array
	 * unmodified.
	 * 
	 * @return
	 */
	public String[][] getTimes() {
		return times;
	}

	/**
	 * Returns an array of bus stop names. <b>CONTRACT</b>: this method returns
	 * the original array; this significantly improves the performance, but the
	 * caller must take care to keep the received array unmodified.
	 * 
	 * @return
	 */
	public String[] getStopNames() {
		return stopNames;
	}

	/**
	 * Returns an array of legend symbols for each route (timetable column).
	 * <b>CONTRACT</b>: this method returns the original array; this
	 * significantly improves the performance, but the caller must take care to
	 * keep the received array unmodified.
	 * 
	 * @return
	 */
	public ScheduleLegend[] getLegends() {
		return legends;
	}

	/**
	 * Returns the number of column that corresponds to the 
	 * nearest forthcoming departure from the first bus stop.
	 * 
	 * @param calendar
	 *            Calendar specifying the reference time. The resulting column 
	 *            should contain either later time, or be the first one 
	 *            in the schedule (meaning next day departure).
	 * @return
	 */
	public int getForthcomingDepartureColumn(Calendar calendar) {
		int result = 0;
		Time refTime = new Time(
				calendar.get(Calendar.HOUR_OF_DAY),
				calendar.get(Calendar.MINUTE));
		for (int col = 0; col < colCount; col++) {
			Time colTime = getFirstTime(col);
			if (colTime.compareTo(refTime) >= 0) {
				result = col;
				break;
			}
		}
		return result;
	}
	
	/**
	 * Returns the first valid time in the given column.
	 * If no valid time found, returns null.
	 * @param times
	 * @return
	 */
	private Time getFirstTime(int col) {
		Time result = null;
		for (int row = 0; row < rowCount; row++) {
			result = parseTimeString(times[row][col]);
			if (result != null) break;
		}
		return result;
	}

	/**
	 * Parses the given time string (H:MM). 
	 * If string is empty or ill-formatted, return null.
	 * @param timeStr
	 * @return
	 */
	public Time parseTimeString(String timeStr) {
		Time result = null;
		int dividerPos;
		if (timeStr != null && timeStr.length() >= 3 && 
				(dividerPos = timeStr.indexOf(':')) > 0) {
			try {
				result = new Time(
						Integer.valueOf(timeStr.substring(0, dividerPos)),
						Integer.valueOf(timeStr.substring(dividerPos + 1)));
			} catch(NumberFormatException nfe) {
				// do nothing: in case of problems we return null
			}
		}
		return result;
	}

	/**
	 * Time of the schedule (hours and minutes) 
	 */
	private class Time implements Comparable<Time> {
		final int hour;
		final int minute;
		public Time(int hour, int minute) {
			this.hour = hour;
			this.minute = minute;
		}
		@Override
		public int compareTo(Time another) {
			int result = hour - another.hour;
			if (result == 0) {
				result = minute - another.minute;
			}
			return result;
		}
	}
}
