package com.anmipo.android.trentobus.db;

/**
 * Time of the schedule (hours and minutes) 
 */
class Time implements Comparable<Time> {
	// symbol separating hours and minutes in text representation
	public static final char DIVIDER = ':';
	private static final String PIPE = "|";

	// empty cell in paper timetable
	public static final Time EMPTY = new Time(-1, -1, "");
	// shown as pipe in paper timetable
	public static final Time PASS = 
			new Time(-2, -2, PIPE);

	final int hour;
	final int minute;
	private String text;
	
	public Time(int hour, int minute) {
		this.hour = hour;
		this.minute = minute;
	}
	/**
	 * Constructor for special cases with non-standard text.
	 * @param hour
	 * @param minute
	 * @param text
	 */
	private Time(int hour, int minute, String text) {
		this.hour = hour;
		this.minute = minute;
		this.text = text;
	}
	
	@Override
	public int compareTo(Time another) {
		int result = hour - another.hour;
		if (result == 0) {
			result = minute - another.minute;
		}
		return result;
	}
	public String toString() {
		if (text == null) {
			text = String.format("%d" + DIVIDER + "%02d", hour, minute);
		}
		return text;
	}
	/**
	 * Parses the given time string (H:MM).
	 * For empty and null strings returns {@link #EMPTY}.
	 * For pipe character ('|') returns {@link #PASS}.
	 * If the string is ill-formatted, throws an 
	 * {@link IllegalArgumentException}.
	 * 
	 * @param timeStr
	 * @return
	 * @throws IllegalArgumentException
	 */
	public static Time parse(String timeStr) 
			throws IllegalArgumentException {
		Time result = null;
		int dividerPos;
		if (timeStr == null || timeStr.length() == 0) {
			result = EMPTY;
		} else if (timeStr.equals(PIPE)){
			result = PASS;
		} else if ((dividerPos = timeStr.indexOf(DIVIDER)) > 0) {
			try {
				result = new Time(
						Integer.valueOf(timeStr.substring(0, dividerPos)),
						Integer.valueOf(timeStr.substring(dividerPos + 1)));
			} catch (NumberFormatException nfe) {
				throw new IllegalArgumentException(
						"Wrong time format: " + timeStr, nfe);
			}
		} else {
			throw new IllegalArgumentException("Wrong time format: " + timeStr);
		}
		return result;
	}
	/**
	 * Returns true if this instance represents a valid time 
	 * (and not an empty or "pipe" cell).
	 * 
	 * @return
	 */
	public boolean isValidTime() {
		return (this != EMPTY) && (this != PASS);
	}
}