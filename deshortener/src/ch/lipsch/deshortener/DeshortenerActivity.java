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

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import ch.lipsch.deshortener.persistence.DbAdapter;

/**
 * This activity is shown for certain shorten domains (defined in
 * AndroidManifest.xml). The activity will try to deshorten the uri given in the
 * starting intent.
 */
public final class DeshortenerActivity extends Activity {

	private TextView shortenedUrlTextView = null;
	private TextView deshortenedUrlTextView = null;
	private Button openUrlButton = null;
	private ProgressBar progressBar = null;
	private Button openErrorUrlButton = null;
	private CheckBox trustUrlChkBox = null;
	private CheckBox trustDomainChkBox = null;
	private DbAdapter dbAdapter = null;
	private Intent startIntent = null;

	@Override
	protected void onDestroy() {
		super.onDestroy();
		dbAdapter.close();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		boolean foundSelectedItem = false;

		foundSelectedItem = OptionsMenuHelper
				.doActionOnSelectedItem(item, this);

		if (!foundSelectedItem) {
			foundSelectedItem = super.onOptionsItemSelected(item);
		}

		return foundSelectedItem;
	}

	private void onCreateTrusted() {
		openUrlButton.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				openBrowser(Uri.parse(deshortenedUrlTextView.getText()
						.toString()));
			}
		});

		openErrorUrlButton.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				openBrowser(Uri
						.parse(shortenedUrlTextView.getText().toString()));
			}
		});

		trustDomainChkBox.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (trustDomainChkBox.isChecked()) {
					trustUrlChkBox.setEnabled(false);
					trustUrlChkBox.setChecked(false);
					dbAdapter.addDomain(startIntent.getData().getHost());
				} else {
					trustUrlChkBox.setEnabled(true);
					dbAdapter.removeDomain(startIntent.getData().getHost());
				}
			}
		});

		trustUrlChkBox.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				if (trustUrlChkBox.isChecked()) {
					dbAdapter.addUri(startIntent.getData());
				} else {
					dbAdapter.removeUri(startIntent.getData());
				}
			}
		});
	}

	private void onCreateUntrusted() {
		// Nothing to do yet.
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		dbAdapter = new DbAdapter(this);
		dbAdapter.open();
		startIntent = getIntent();

		setContentView(R.layout.deshortener);
		shortenedUrlTextView = (TextView) findViewById(R.id.shortenedUrl);
		deshortenedUrlTextView = (TextView) findViewById(R.id.deshortenedUrl);
		openUrlButton = (Button) findViewById(R.id.openUrlButton);
		openErrorUrlButton = (Button) findViewById(R.id.openUrlOnErrorButton);
		progressBar = (ProgressBar) findViewById(R.id.deshortenProgress);
		trustUrlChkBox = (CheckBox) findViewById(R.id.trustUrl);
		trustDomainChkBox = (CheckBox) findViewById(R.id.trustDomain);

		shortenedUrlTextView.setText(startIntent.getData().toString());

		UnshortenUrlTask unshortenTask = new UnshortenUrlTask();
		unshortenTask.execute(startIntent.getData());

		if (!isIntentTrusted(startIntent)) {
			onCreateTrusted();
		} else {
			onCreateUntrusted();
		}
	}

	@Override
	protected void onStart() {
		super.onStart();

		// bind to google safe browsing if necessary...
	}

	private boolean isIntentTrusted(Intent intent) {
		return dbAdapter.isDomainTrusted(intent.getData().getHost())
				|| dbAdapter.isUriTrusted(intent.getData());
	}

	private void switchWidgetsToStateDeshortened(boolean isTrusted) {
		if (isTrusted) {
			openUrlButton.setVisibility(View.INVISIBLE);
			trustDomainChkBox.setVisibility(View.INVISIBLE);
			trustUrlChkBox.setVisibility(View.INVISIBLE);
		} else {
			openUrlButton.setVisibility(View.VISIBLE);
			trustDomainChkBox.setVisibility(View.VISIBLE);
			trustUrlChkBox.setVisibility(View.VISIBLE);
		}
		openErrorUrlButton.setVisibility(View.INVISIBLE);
		progressBar.setVisibility(View.INVISIBLE);
	}

	private void switchWidgetsToStateError() {
		openUrlButton.setVisibility(View.INVISIBLE);
		openErrorUrlButton.setText(getString(R.string.openErrorUrl,
				shortenedUrlTextView.getText()));
		openErrorUrlButton.setVisibility(View.VISIBLE);
		progressBar.setVisibility(View.INVISIBLE);
		trustDomainChkBox.setVisibility(View.INVISIBLE);
		trustUrlChkBox.setVisibility(View.INVISIBLE);
	}

	private class UnshortenUrlTask extends AsyncTask<Uri, Void, Uri> {

		private boolean isUriTrusted = false;
		private Uri unshortenedUri = null;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			progressBar.setVisibility(View.VISIBLE);
		}

		@Override
		protected Uri doInBackground(Uri... params) {
			unshortenedUri = params[0];
			isUriTrusted = isIntentTrusted(startIntent);
			return Deshortener.deshorten(unshortenedUri);
		}

		@Override
		protected void onPostExecute(Uri result) {
			if (result != null) {
				deshortenedUrlTextView.setText(result.toString());
				if (isUriTrusted) {
					openBrowser(result);
					Toast toast = Toast.makeText(
							getBaseContext(),
							getString(R.string.toastMsgOpenTrusted,
									unshortenedUri), Toast.LENGTH_SHORT);
					toast.show();
				} else {
					trustDomainChkBox
							.setText(getString(
									R.string.trustDomain,
									Uri.parse(
											shortenedUrlTextView.getText()
													.toString()).getHost()));
					trustUrlChkBox.setText(getString(R.string.trustUrl, Uri
							.parse(shortenedUrlTextView.getText().toString())));
				}
				switchWidgetsToStateDeshortened(isUriTrusted);
			} else {
				deshortenedUrlTextView.setText(getText(R.string.error));
				switchWidgetsToStateError();
			}
		}
	}

	private void openBrowser(Uri uri) {
		Intent i = new Intent(Intent.ACTION_VIEW);
		i.setData(uri);
		startActivity(i);
	}
}