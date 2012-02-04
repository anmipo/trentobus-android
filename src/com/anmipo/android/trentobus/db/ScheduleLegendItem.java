package com.anmipo.android.trentobus.db;

import com.anmipo.android.trentobus.R;

public class ScheduleLegendItem {
	/** Instance for unrecognized legend entries */
	public static final ScheduleLegendItem UNKNOWN = new ScheduleLegendItem(
			"", R.string.freq_unknown, R.drawable.freq_unknown);
	
	private static final int NO_DESCRIPTION = 0;
	
	final public String key;
	final public int textId;
	final public int iconId;
	/**
	 * Creates a schedule legend item which has no description.
	 * @param key
	 * @param iconId
	 */
	public ScheduleLegendItem(String key, int iconId) {
		this(key, NO_DESCRIPTION, iconId);
	}
	/**
	 * Creates a schedule legend item with a description.
	 * @param key
	 * @param textId
	 * @param iconId
	 */
	public ScheduleLegendItem(String key, int textId, int iconId) {
		this.key = key;
		this.textId = textId;
		this.iconId = iconId;
	}
	
	public String toString() {
		return "key: '" + key + "', textId: " + textId + ", iconId: " + iconId;
	}

	public boolean hasText() {
		return (textId != NO_DESCRIPTION);
	}
}
