package com.anmipo.android.trentobus.db;

import com.anmipo.android.trentobus.R;

public class ScheduleLegendItem {
	/** Instance for unrecognized legend entries */
	public static final ScheduleLegendItem UNKNOWN = new ScheduleLegendItem(
			"", R.string.freq_unknown, R.drawable.freq_unknown);
	
	final public String key;
	final public int textId;
	final public int iconId;
	public ScheduleLegendItem(String key, int textId, int iconId) {
		this.key = key;
		this.textId = textId;
		this.iconId = iconId;
	}
	
	public String toString() {
		return "key: '" + key + "', textId: " + textId + ", iconId: " + iconId;
	}
}
