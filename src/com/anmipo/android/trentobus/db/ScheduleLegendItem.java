package com.anmipo.android.trentobus.db;

import com.anmipo.android.trentobus.R;

public class ScheduleLegendItem {
	/** Instance for unrecognized legend entries */
	public static final ScheduleLegendItem UNKNOWN = new ScheduleLegendItem(
			' ', R.string.freq_unknown, R.drawable.freq_unknown);
	
	private char key;
	private int textId;
	private int iconId;
	public ScheduleLegendItem(char key, int textId, int iconId) {
		this.key = key;
		this.textId = textId;
		this.iconId = iconId;
	}
	public char getKey() {
		return key;
	}
	public int getTextId() {
		return textId;
	}
	public int getIconId() {
		return iconId;
	}
	
}
