/*
 * Copyright (C) 2012 Erwin Betschart
 *
 * This file is part of Deshortener.
 *
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program; If not, see <http://www.gnu.org/licenses/>.
 */
package ch.lipsch.deshortener;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;

public final class OptionsMenuHelper {

	/** Id used as identifier when showing an info dialog. */
	public static final int INFO_DIALOG_ID = 0;

	/**
	 * Call this in {@link Activity#onOptionsItemSelected(MenuItem)} in order
	 * handle the selected menu.
	 *
	 * @param menuItem
	 *            The menu item.
	 * @param activity
	 *            The activity on which
	 *            {@link Activity#onOptionsItemSelected(MenuItem)} was called.
	 * @return {@code true} if the menu item could be processed.
	 */
	public static boolean doActionOnSelectedItem(MenuItem menuItem,
			Activity activity) {
		switch (menuItem.getItemId()) {
		case R.id.homepageMenu:
			openHomepage(activity);
			return true;
		case R.id.infoMenu:
			activity.showDialog(INFO_DIALOG_ID);
			return true;
		case R.id.preferencesMenu:
			openPreferencesActivity(activity);
			return true;
		default:
			return false;
		}
	}

	/**
	 * Creates the info dialog.
	 *
	 * @param context
	 *            The context on which the dialog will be shown.
	 * @return The dialog.
	 */
	public static Dialog createInfoDialog(Context context) {
		AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);

		dialogBuilder.setTitle(context.getString(R.string.infoDialogTitle));
		dialogBuilder.setNegativeButton(
				context.getString(R.string.infoDialogCloseButton), null);

		View view = LayoutInflater.from(context).inflate(R.layout.infodialog,
				null);

		dialogBuilder.setView(view);

		return dialogBuilder.create();
	}

	private static void openPreferencesActivity(Context context) {
		Intent intent = new Intent(context, Preferences.class);
		context.startActivity(intent);
	}

	private static void openHomepage(Context context) {
		Intent intent = new Intent(Intent.ACTION_VIEW);
		Uri homepageUri = Uri.parse(context.getString(R.string.homepageUrl));
		intent.setData(homepageUri);
		context.startActivity(intent);
	}
}
