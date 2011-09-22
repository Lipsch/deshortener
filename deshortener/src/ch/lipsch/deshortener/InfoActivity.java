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
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import ch.lipsch.deshortener.persistence.DbAdapter;

public class InfoActivity extends Activity {

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
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.info);

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
				Toast toast = Toast.makeText(InfoActivity.this,
						InfoActivity.this.getText(R.string.trustedCleared),
						Toast.LENGTH_SHORT);
				toast.show();
			}
		});

		deshortenButton.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(InfoActivity.this, DeshortenerActivity.class);
				intent.setData(Uri.parse(shortendedUrlEditText.getText()
						.toString()));
				startActivity(intent);
			}
		});
	}
}
