package com.anmipo.android.trentobus.db;

import java.util.HashMap;

import android.content.res.Resources;
import android.graphics.drawable.Drawable;

import com.anmipo.android.trentobus.R;

public class ScheduleLegend {
	private static HashMap<Character, Integer> sAllDrawables;
	private static HashMap<Character, Integer> sAllDescriptions;
	private int size;
	static {
		sAllDrawables = new HashMap<Character, Integer>();
		sAllDescriptions = new HashMap<Character, Integer>();
		
		sAllDescriptions.put('o', R.string.freq_autonoleggiatore_privato);
		sAllDescriptions.put('á', R.string.freq_feriale_solo_sabato);
		sAllDescriptions.put('Ü', R.string.freq_feriale_lunedi_a_venerdi);
		sAllDescriptions.put('Ý', R.string.freq_scolastica_lunedi_a_sabato);
		sAllDescriptions.put('Þ', R.string.freq_scolastica_lunedi_a_venerdi);
		sAllDrawables.put('o', R.drawable.linea_zero);
		sAllDrawables.put('á', R.drawable.freq_square_filled);
		sAllDrawables.put('Ü', R.drawable.freq_square_empty);
		sAllDrawables.put('Ý', R.drawable.freq_star);
		sAllDrawables.put('Þ', R.drawable.freq_square_star);
	}
	
	private int[] drawables;
	private int[] descriptions;
	
	public ScheduleLegend(int size) {
		super();
		this.size = size;
	}

	public static ScheduleLegend getInstance(String frequenza, String linea) {
		int length = frequenza.length() + linea.length();
		ScheduleLegend legend = new ScheduleLegend(length);
		legend.drawables = new int[length];
		legend.descriptions = new int[length];
		
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

	private void setEntry(int index, char key) {
		drawables[index] =
				sAllDrawables.containsKey(key) ?
				sAllDrawables.get(key) : R.drawable.freq_unknown;
		descriptions[index] = 
				sAllDescriptions.containsKey(key) ?
				sAllDescriptions.get(key) : R.string.freq_unknown;
	}
	public int getLength() {
		return size;
	}
	public int getDrawableId(int index) {
		return drawables[index];
	}
	public int getDescriptionId(int index) {
		return descriptions[index];
	}
}
