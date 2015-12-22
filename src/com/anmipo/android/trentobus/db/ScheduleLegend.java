package com.anmipo.android.trentobus.db;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.anmipo.android.trentobus.R;

public class ScheduleLegend {
	// all available legend items
	private static HashMap<String, ScheduleLegendItem> sAllItemsMap;
	private static ScheduleLegendItem[] sItemsWithDescription;
	
	static {
		int index = 0;
		sItemsWithDescription = new ScheduleLegendItem[11];
		/* 
		 * "Frequenza" items, those that are to be shown 
		 * in the legend description. 
		 */
		sItemsWithDescription[index++] = new ScheduleLegendItem("\u00DC",
				R.string.freq_feriale_lunedi_a_venerdi,
				R.drawable.freq_square_empty);
		sItemsWithDescription[index++] = new ScheduleLegendItem("\u00DD",
				R.string.freq_scolastica_lunedi_a_sabato, 
				R.drawable.freq_star);
		sItemsWithDescription[index++] = new ScheduleLegendItem("\u00DE",
				R.string.freq_scolastica_lunedi_a_venerdi,
				R.drawable.freq_square_star);
		sItemsWithDescription[index++] = new ScheduleLegendItem("\u00E1", 
				R.string.freq_feriale_solo_sabato, 
				R.drawable.freq_square_filled);
		sItemsWithDescription[index++] = new ScheduleLegendItem("\u00E0",
				R.string.freq_lun_ven_sospesa_0308_2808, 
				R.drawable.freq_square_half_filled);
		sItemsWithDescription[index++] = new ScheduleLegendItem("X",
				R.string.freq_romagnosi_canova_linea_7,
				R.drawable.freq_circle_x);
		sItemsWithDescription[index++] = new ScheduleLegendItem("W", // an arbitrary one-character replacement for "A" which is taken for bus 
				R.string.freq_funivia_sospesa_festivi, 
				R.drawable.freq_circle_a);
		
		// Some special "Linea" items that have a description. 
		sItemsWithDescription[index++] = new ScheduleLegendItem("o", 
				R.string.freq_autonoleggiatore_privato, 
				R.drawable.linea_zero);
		sItemsWithDescription[index++] = new ScheduleLegendItem("E", 
				R.string.freq_mezzo_extraurbano, 
				R.drawable.linea_extra);
		sItemsWithDescription[index++] = new ScheduleLegendItem("fvb", 
				R.string.freq_funivia_bus, 
				R.drawable.linea_funivia_bus);
		sItemsWithDescription[index++] = new ScheduleLegendItem("fvbc", 
				R.string.freq_funivia_bici, 
				R.drawable.linea_bike);
		
		sAllItemsMap = new HashMap<String, ScheduleLegendItem>();
		for (ScheduleLegendItem item: sItemsWithDescription) {
			sAllItemsMap.put(item.key, item);
		}
		
		/*
		 * "Linea" items, those that are drawn, 
		 * but are not listed in legend description.
		 */
		sAllItemsMap.put("1", new ScheduleLegendItem("1", R.drawable.linea_1));
		sAllItemsMap.put("1/", new ScheduleLegendItem("1/", R.drawable.linea_1b));
		sAllItemsMap.put("2", new ScheduleLegendItem("2", R.drawable.linea_2));
		sAllItemsMap.put("2/", new ScheduleLegendItem("2/", R.drawable.linea_2b));
		sAllItemsMap.put("3", new ScheduleLegendItem("3", R.drawable.linea_3));
		sAllItemsMap.put("3/", new ScheduleLegendItem("3/", R.drawable.linea_3b));
		sAllItemsMap.put("4", new ScheduleLegendItem("4", R.drawable.linea_4));
		sAllItemsMap.put("4/", new ScheduleLegendItem("4/", R.drawable.linea_4b));
		sAllItemsMap.put("5", new ScheduleLegendItem("5", R.drawable.linea_5));
		sAllItemsMap.put("5/", new ScheduleLegendItem("5/", R.drawable.linea_5b));
		sAllItemsMap.put("6", new ScheduleLegendItem("6", R.drawable.linea_6));
		sAllItemsMap.put("6/", new ScheduleLegendItem("6/", R.drawable.linea_6b));
		sAllItemsMap.put("7", new ScheduleLegendItem("7", R.drawable.linea_7));
		sAllItemsMap.put("7/", new ScheduleLegendItem("7/", R.drawable.linea_7b));
		sAllItemsMap.put("8", new ScheduleLegendItem("8", R.drawable.linea_8));
		sAllItemsMap.put("8/", new ScheduleLegendItem("8/", R.drawable.linea_8b));
		sAllItemsMap.put("9", new ScheduleLegendItem("9", R.drawable.linea_9));
		sAllItemsMap.put("9/", new ScheduleLegendItem("9/", R.drawable.linea_9b));
		sAllItemsMap.put("10", new ScheduleLegendItem("10", R.drawable.linea_10));
		sAllItemsMap.put("10/", new ScheduleLegendItem("10/", R.drawable.linea_10b));
		sAllItemsMap.put("11", new ScheduleLegendItem("11", R.drawable.linea_11));
		sAllItemsMap.put("12", new ScheduleLegendItem("12", R.drawable.linea_12));
		sAllItemsMap.put("13", new ScheduleLegendItem("13", R.drawable.linea_13));
		sAllItemsMap.put("14", new ScheduleLegendItem("14", R.drawable.linea_14));
		sAllItemsMap.put("15", new ScheduleLegendItem("15", R.drawable.linea_15));
		sAllItemsMap.put("16", new ScheduleLegendItem("16", R.drawable.linea_16));
		sAllItemsMap.put("17", new ScheduleLegendItem("17", R.drawable.linea_17));
		sAllItemsMap.put("17/", new ScheduleLegendItem("17/", R.drawable.linea_17b));
		sAllItemsMap.put("A", new ScheduleLegendItem("A", R.drawable.linea_a));
		sAllItemsMap.put("A/", new ScheduleLegendItem("A/", R.drawable.linea_ab));
		sAllItemsMap.put("B", new ScheduleLegendItem("B", R.drawable.linea_b));
		sAllItemsMap.put("C", new ScheduleLegendItem("C", R.drawable.linea_c));
		sAllItemsMap.put("D", new ScheduleLegendItem("D", R.drawable.linea_d));
		sAllItemsMap.put("NP", new ScheduleLegendItem("NP", R.drawable.linea_np));
	}
	
	// items of this specific legend
	private ScheduleLegendItem[] items;
		
	protected ScheduleLegend(int size) {
		items = new ScheduleLegendItem[size];
	}

	public static ScheduleLegend getInstance(String frequenza, String linea) {
		int length = frequenza.length() + 
				((linea.length() > 0) ? 1 : 0);
		ScheduleLegend legend = new ScheduleLegend(length);
		
		// in "Frequenza" there can be several entries,
		// each represented by one character
		int index = 0;
		for (int i = 0; i < frequenza.length(); i++) {
			legend.setEntry(index, String.valueOf(frequenza.charAt(i)));
			index++;
		}
		// in "Linea" there can be at most one entry, 
		// but a multicharacter one (like bus number 17 or NP)
		if (linea.length() > 0) {
			legend.setEntry(index, linea);
		}
		return legend;
	}

	private void setEntry(int index, String key) {
		if (sAllItemsMap.containsKey(key)) {
			items[index] = sAllItemsMap.get(key);
		} else {
			items[index] = ScheduleLegendItem.UNKNOWN;
		}
	}
	public int getLength() {
		return items.length;
	}
	public ScheduleLegendItem getItem(int index) {
		return items[index];
	}
	

	/**
	 * Returns dimensions of the legend icons
	 * @return
	 */
	public static int getIconSize(Resources res) {
		int result;
		Drawable d = res.getDrawable(R.drawable.freq_unknown);
		result = d.getIntrinsicHeight();
		d = null;
		return result;
	}

	/**
	 * Returns an adapter with the items of this legend which have description. 
	 * @param context
	 * @return
	 */
	public Adapter getDescriptionsAdapter(Context context) {
		ArrayList<ScheduleLegendItem> itemsWithDescription = 
				new ArrayList<ScheduleLegendItem>(items.length);
		for (ScheduleLegendItem item: items) {
			if (item.hasText()) {
				itemsWithDescription.add(item);
			}
		}
		return new Adapter(context, itemsWithDescription);
	};
	
	public static class Adapter extends ArrayAdapter<ScheduleLegendItem> {
		int iconPadding;
		
		public Adapter(Context context) {
			super(context, R.layout.item_legend_item, R.id.text, 
					sItemsWithDescription);
			setupLayout();
		}
		public Adapter(Context context, List<ScheduleLegendItem> items) {
			super(context, R.layout.item_legend_item, R.id.text, items);
			setupLayout();
		}

		private void setupLayout() {
			DisplayMetrics dm = getContext().getResources().getDisplayMetrics();
			iconPadding = (int) TypedValue.applyDimension(
					TypedValue.COMPLEX_UNIT_DIP, 10, dm);
		}
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// Use super class to create the View
			View v = super.getView(position, convertView, parent);
			v.setClickable(false);

			TextView textView = (TextView) v.findViewById(R.id.text);
			ScheduleLegendItem legendItem = getItem(position);
			textView.setText(legendItem.textId);
			textView.setCompoundDrawablesWithIntrinsicBounds(
					legendItem.iconId, 0, 0, 0);
			textView.setCompoundDrawablePadding(iconPadding);

			return v;
		}
	}
}
