package com.anmipo.android.trentobus.db;

import java.util.HashMap;

import com.anmipo.android.trentobus.R;

public class ScheduleLegend {
	private static HashMap<Character, Integer> sAllDrawables;
	private static HashMap<Character, Integer> sAllDescriptions;
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
	
	public static ScheduleLegend getInstance(String frequenza, String linea) {
		int size = frequenza.length() + linea.length();
		ScheduleLegend legend = new ScheduleLegend();
		legend.drawables = new int[size];
		legend.descriptions = new int[size];
		
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
		drawables[index] =
				sAllDrawables.containsKey(key) ?
				sAllDrawables.get(key) : R.drawable.freq_unknown;
		descriptions[index] = 
				sAllDescriptions.containsKey(key) ?
				sAllDescriptions.get(key) : R.string.freq_unknown;
	}
}
