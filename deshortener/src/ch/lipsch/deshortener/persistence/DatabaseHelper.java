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

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * The class is responsible for the database lifecycle of the Deshortener
 * application. Manages the creation and upgrade of the database.
 * 
 * @author Erwin Betschart
 * 
 */
public final class DatabaseHelper extends SQLiteOpenHelper {
	protected static final String DATABASE_NAME = "deshortener";
	private static final int DATABASE_VERSION = 1;
	private static final String CREATE_TABLE_TRUSTED_DOMAIN = "CREATE TABLE trustedDomain (domain TEXT)";
	private static final String CREATE_TABLE_TRUSTED_URI = "CREATE TABLE trustedUri (uri TEST)";

	public DatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CREATE_TABLE_TRUSTED_DOMAIN);
		db.execSQL(CREATE_TABLE_TRUSTED_URI);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// Nothing to do yet.
	}
}
