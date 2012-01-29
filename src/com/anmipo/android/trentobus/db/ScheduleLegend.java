package com.anmipo.android.trentobus.db;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.anmipo.android.trentobus.R;

public class ScheduleLegend {
	// all available legend items
	private static HashMap<Character, ScheduleLegendItem> sAllItemsMap;
	private static ScheduleLegendItem[] sAllItems;
	
	static {
		sAllItems = new ScheduleLegendItem[5];
		sAllItems[0] = new ScheduleLegendItem('Ü',
				R.string.freq_feriale_lunedi_a_venerdi,
				R.drawable.freq_square_empty);
		sAllItems[1] = new ScheduleLegendItem('Ý',
				R.string.freq_scolastica_lunedi_a_sabato, 
				R.drawable.freq_star);
		sAllItems[2] = new ScheduleLegendItem('Þ',
				R.string.freq_scolastica_lunedi_a_venerdi,
				R.drawable.freq_square_star);
		sAllItems[3] = new ScheduleLegendItem('á', 
				R.string.freq_feriale_solo_sabato, 
				R.drawable.freq_square_filled);
		sAllItems[4] = new ScheduleLegendItem('o', 
				R.string.freq_autonoleggiatore_privato, 
				R.drawable.linea_zero);
		
		sAllItemsMap = new HashMap<Character, ScheduleLegendItem>();
		for (ScheduleLegendItem item: sAllItems) {
			sAllItemsMap.put(item.getKey(), item);
		}
	}
	
	// items of this specific legend
	private ScheduleLegendItem[] items;
		
	protected ScheduleLegend(int size) {
		items = new ScheduleLegendItem[size];
	}

	public static ScheduleLegend getInstance(String frequenza, String linea) {
		int length = frequenza.length() + linea.length();
		ScheduleLegend legend = new ScheduleLegend(length);
		
		int index = 0;
		for (int i = 0; i < frequenza.length(); i++) {
			legend.setEntry(index, frequenza.charAt(i));
			index++;
		}
		for (int i = 0; i < linea.length(); i++) {
			legend.setEntry(index, linea.charAt(i));
			index++;
		}
		return legend;
	}

	private void setEntry(int index, char key) {
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

	public static class Adapter extends ArrayAdapter<ScheduleLegendItem> {
		int iconPadding;
		public Adapter(Context context) {
			super(context, R.layout.item_legend_item, R.id.text, sAllItems);
			DisplayMetrics dm = context.getResources().getDisplayMetrics();
			iconPadding = (int) TypedValue.applyDimension(
					TypedValue.COMPLEX_UNIT_DIP, 10, dm);
		}

		@Override
		public int getCount() {
			return sAllItems.length;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// User super class to create the View
			View v = super.getView(position, convertView, parent);
			v.setClickable(false);

			TextView textView = (TextView) v.findViewById(R.id.text);
			// ImageView imageView = (ImageView) v.findViewById(R.id.icon);
			ScheduleLegendItem legendItem = sAllItems[position];
			textView.setText(legendItem.getTextId());
			// imageView.setImageResource(legendItem.getIconId());
			textView.setCompoundDrawablesWithIntrinsicBounds(
					legendItem.getIconId(), 0, 0, 0);
			textView.setCompoundDrawablePadding(iconPadding);

			return v;
		}
	};
}
