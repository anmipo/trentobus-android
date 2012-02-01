package com.anmipo.android.common;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.anmipo.android.trentobus.R;

/**
 * Verifies whether the user accepted the EULA, showing it if necessary, 
 * and caching the answer in preferences.
 *  
 * @author "Andrei Popleteev"
 */
public class EulaChecker {
	/**
	 * Receives the result of EULA acceptance check.
	 */
	public interface EulaListener {
		public void onEulaAccepted(boolean accepted);
	}
	
	private static final String PREF_EULA_ACCEPTED = "eula_v1_accepted";
	private Context context;
	private EulaListener listener;

	protected EulaChecker(Context context, EulaListener eulaListener) {
		super();
		this.context = context;
		this.listener = eulaListener;
	}

	public static void checkEulaAccepted(Context context, 
			EulaListener eulaListener) {
		EulaChecker checker = new EulaChecker(context, eulaListener);
		checker.checkEulaAccepted();
	}
	

	/**
	 * Checks if EULA has been accepted before.
	 * If yes - notifies the listener.
	 * If no - shows the EULA and notifies the listener about the result.
	 */
	protected void checkEulaAccepted() {
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(context);
		
		if (!prefs.getBoolean(PREF_EULA_ACCEPTED, false)) {
			show();
		} else {
			notifyEulaAccepted(true);
		}		
	}

	/**
	 * Displays the EULA text.
	 */
	protected void show() {
		OnClickListener clickListener = new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				boolean isAccepted = (which == Dialog.BUTTON_POSITIVE); 
				if (isAccepted) {
					PreferenceManager
						.getDefaultSharedPreferences(context)
						.edit()
						.putBoolean(PREF_EULA_ACCEPTED, true)
						.commit();
				}
				notifyEulaAccepted(isAccepted);
			}
		};
		
		Builder builder = new AlertDialog.Builder(context);
		builder
			.setTitle(R.string.eula_title)
			.setMessage(R.string.eula_text)
			.setPositiveButton(R.string.eula_accept, clickListener)
			.setNegativeButton(R.string.eula_reject, clickListener)
			.setCancelable(true)
			.setOnCancelListener(new OnCancelListener() {
				@Override
				public void onCancel(DialogInterface dialog) {
					notifyEulaAccepted(false);
				}
			})
			.show();		
	}
	
	/**
	 * Notifies the listener of whether the user accepted the EULA.
	 * @param accepted
	 */
	protected void notifyEulaAccepted(boolean accepted) {
		if (listener != null) {
			listener.onEulaAccepted(accepted);
		}
	}
}
