/*
 * Copyright (C) 2011 Erwin Betschart
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

import java.net.MalformedURLException;
import java.net.URL;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import ch.lipsch.deshortener.persistence.DbAdapter;

public class MainActivity extends Activity {

	private static final int INFO_DIALOG = 0;

	private Button clearTrustedButton = null;
	private DbAdapter dbAdapter = null;
	private Button deshortenButton = null;
	private EditText shortendedUrlEditText = null;

	@Override
	protected void onDestroy() {
		dbAdapter.close();
		super.onDestroy();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.homepageMenu:
			openHomepage();
			return true;
		case R.id.infoMenu:
			showDialog(INFO_DIALOG);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		Dialog dialog = null;

		if (id == INFO_DIALOG) {
			dialog = createInfoDialog();
		}

		return dialog;
	}

	private Dialog createInfoDialog() {
		AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);

		dialogBuilder.setTitle(getString(R.string.infoDialogTitle));
		dialogBuilder.setNegativeButton(
				getString(R.string.infoDialogCloseButton), null);

		View view = LayoutInflater.from(this)
				.inflate(R.layout.infodialog, null);

		dialogBuilder.setView(view);

		return dialogBuilder.create();
	}

	private void openHomepage() {
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setData(Uri.parse("http://www.google.ch"));
		startActivity(intent);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		clearTrustedButton = (Button) findViewById(R.id.clrTrusted);
		deshortenButton = (Button) findViewById(R.id.deshortenButton);
		shortendedUrlEditText = (EditText) findViewById(R.id.shortenedUrl);

		dbAdapter = new DbAdapter(this);
		dbAdapter.open();

		shortendedUrlEditText.addTextChangedListener(new TextWatcher() {

			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// Nothing to do
			}

			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// Nothing to do

			}

			public void afterTextChanged(Editable s) {
				try {
					URL url = new URL(shortendedUrlEditText.getText()
							.toString());
					boolean hasHost = url.getHost() != null
							&& (!url.getHost().equals(""));
					if (hasHost) {
						deshortenButton.setEnabled(true);
					} else {
						deshortenButton.setEnabled(false);
					}
				} catch (MalformedURLException e) {
					deshortenButton.setEnabled(false);
				}
			}
		});

		clearTrustedButton.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				dbAdapter.removeAllDomains();
				dbAdapter.removeAllUris();
				Toast toast = Toast.makeText(MainActivity.this,
						MainActivity.this.getText(R.string.trustedCleared),
						Toast.LENGTH_SHORT);
				toast.show();
			}
		});

		deshortenButton.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(MainActivity.this, DeshortenerActivity.class);
				intent.setData(Uri.parse(shortendedUrlEditText.getText()
						.toString()));
				startActivity(intent);
			}
		});
	}
}
