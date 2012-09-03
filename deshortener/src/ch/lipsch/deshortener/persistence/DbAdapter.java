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
package ch.lipsch.deshortener.persistence;

import java.text.MessageFormat;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

/**
 * This class provides an easy access to the deshortener database. This class is
 * thread-safe.
 *
 * TODO: Synchronization could be done tighter.
 *
 * @author Erwin Betschart
 */
public final class DbAdapter {
	private static final String LOG_TAG = DbAdapter.class.getName();
	private static final String DOMAIN_TABLE = "trustedDomain";
	private static final String URI_TABLE = "trustedUri";
	private static final String DOMAIN_KEY = "domain";
	private static final String URI_KEY = "uri";

	private DatabaseHelper dbHelper = null;
	private Context context = null;
	private SQLiteDatabase database = null;

	/**
	 * Is set to <code>true</code> when {@link #open()} is called and set to
	 * <code>false</code> when {@link #close()} is called. Access must be
	 * synchronized with {@link DbAdapter} instance.
	 */
	private boolean isOpened = false;

	public DbAdapter(Context context) {
		if (context == null) {
			throw new NullPointerException("context must not be null");
		}
		this.context = context;
	}

	public synchronized DbAdapter open() {
		if (isOpened) {
			return this;
		}

		dbHelper = new DatabaseHelper(context);
		database = dbHelper.getWritableDatabase();
		isOpened = true;

		return this;
	}

	public synchronized void close() {
		if (!isOpened) {
			return;
		}

		dbHelper.close();
		isOpened = false;
	}

	/**
	 * Adds a new domain to the trusted domains.
	 *
	 * @param domain
	 *            The domain to trust. Provide just the hostname. E.g. goo.gl
	 * @return The row id of the newly created database entry or -1 in case of
	 *         an error.
	 * @throws NullPointerException
	 *             in case domain is null.
	 * @throws IllegalStateException
	 *             In case {@link #open()} was never called.
	 */
	public synchronized long addDomain(String domain)
			throws NullPointerException, IllegalStateException {
		if (domain == null) {
			throw new NullPointerException("domain must not be null");
		}
		if (!isOpened) {
			throw new IllegalStateException("DbAdapter must be opened");
		}

		ContentValues initialValues = createSingleContentValues(DOMAIN_KEY,
				domain);

		return database.insert(DOMAIN_TABLE, null, initialValues);
	}

	/**
	 * Removes the provided domain from the trusted domains.
	 *
	 * @param url
	 *            The url to remove.
	 * @return True if the url was removed else false.
	 * @throws NullPointerException
	 *             In case the parameter url was null.
	 * @throws IllegalStateException
	 *             In case {@link #open()} was never called.
	 */
	public synchronized boolean removeDomain(String domain)
			throws NullPointerException, IllegalStateException {
		if (domain == null) {
			throw new NullPointerException("domain must not be null");
		}
		if (!isOpened) {
			throw new IllegalStateException("DbAdapter must be opened");
		}
		return (database.delete(DOMAIN_TABLE, DOMAIN_KEY + "= ?",
				new String[] { domain })) > 0;
	}

	/**
	 * Removes all trusted domains.
	 *
	 * @throws IllegalStateException
	 *             In case {@link #open()} was never called.
	 */
	public synchronized void removeAllDomains() throws IllegalStateException {
		if (!isOpened) {
			throw new IllegalStateException("DbAdapter must be opened");
		}

		database.delete(DOMAIN_TABLE, null, new String[0]);
	}

	/**
	 * Adds a new uri to the trusted uris.
	 *
	 * @param uri
	 *            The uri to trust.
	 * @return The row id of the newly created database entry or -1 in case of
	 *         an error.
	 * @throws NullPointerException
	 *             in case uri is null.
	 * @throws IllegalStateException
	 *             In case {@link #open()} was never called.
	 */
	public synchronized long addUri(Uri uri) {
		if (uri == null) {
			throw new NullPointerException("uri must not be null");
		}
		if (!isOpened) {
			throw new IllegalStateException("DbAdapter must be opened");
		}
		ContentValues initialValues = createSingleContentValues(URI_KEY,
				uri.toString());

		return database.insert(URI_TABLE, null, initialValues);
	}

	/**
	 * Removes the provided uri from the trusted uris.
	 *
	 * @param uri
	 *            The uri to remove.
	 * @return True if the uri was removed else false.
	 * @throws NullPointerException
	 *             In case the parameter uri was null.
	 * @throws IllegalStateException
	 *             In case {@link #open()} was never called.
	 */
	public synchronized boolean removeUri(Uri uri) {
		if (uri == null) {
			throw new NullPointerException("uri must not be null");
		}
		if (!isOpened) {
			throw new IllegalStateException("DbAdapter must be opened");
		}
		return (database.delete(URI_TABLE, URI_KEY + "= ?",
				new String[] { uri.toString() })) > 0;
	}

	/**
	 * Removes all trusted uris.
	 *
	 * @throws IllegalStateException
	 *             In case {@link #open()} was never called.
	 */
	public synchronized void removeAllUris() throws IllegalStateException {
		if (!isOpened) {
			throw new IllegalStateException("DbAdapter must be opened");
		}

		database.delete(URI_TABLE, null, new String[0]);
	}

	/**
	 * Checks if the provided domain is trusted.
	 *
	 * @param domain
	 *            The domain (host) to check.
	 * @return true if the domain is trusted.
	 * @throws NullPointerException
	 *             In case domain is null.
	 * @throws IllegalStateException
	 *             In case {@link #open()} was never called.
	 */
	public synchronized boolean isDomainTrusted(String domain) {
		if (domain == null) {
			throw new NullPointerException("domain must not be null");
		}
		if (!isOpened) {
			throw new IllegalStateException("DbAdapter must be opened");
		}

		Cursor cursor = null;
		try {
			cursor = database.query(DOMAIN_TABLE, new String[] { DOMAIN_KEY },
					DOMAIN_KEY + "='" + domain + "'", null, null, null, null);

			boolean isTrusted = cursor.getCount() > 0;

			Log.d(LOG_TAG, MessageFormat.format(
					"Trust check for domain {0}: {1}", domain, isTrusted));

			return isTrusted;
		} finally {
			cursor.close();
		}
	}

	/**
	 * Checks if the provided uri is trusted.
	 *
	 * @param uri
	 *            The uri to check.
	 * @return true if the uri is trusted.
	 * @throws NullPointerException
	 *             In case uri is null.
	 * @throws IllegalStateException
	 *             In case {@link #open()} was never called.
	 */
	public synchronized boolean isUriTrusted(Uri uri) {
		if (uri == null) {
			throw new NullPointerException("uri must not be null");
		}
		if (!isOpened) {
			throw new IllegalStateException("DbAdapter must be opened");
		}

		Cursor cursor = null;
		try {
			cursor = database.query(URI_TABLE, new String[] { URI_KEY },
					URI_KEY + "='" + uri.toString() + "'", null, null, null,
					null);

			boolean isTrusted = cursor.getCount() > 0;
			Log.d(LOG_TAG, MessageFormat.format("Trust check for uri {0}: {1}",
					uri, isTrusted));

			return isTrusted;
		} finally {
			cursor.close();
		}
	}

	private ContentValues createSingleContentValues(String row, String data) {
		ContentValues values = new ContentValues();
		values.put(row, data);
		return values;
	}
}
