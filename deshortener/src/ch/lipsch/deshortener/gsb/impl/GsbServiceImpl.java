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
package ch.lipsch.deshortener.gsb.impl;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import ch.lipsch.deshortener.gsb.GoogleSafeBrowsingService;

public final class GsbServiceImpl extends Service implements
		GoogleSafeBrowsingService {

	private final IBinder gsbBinder = new LocalBinder();

	private class LocalBinder extends Binder {
	}

	@Override
	public IBinder onBind(Intent intent) {
		return gsbBinder;
	}

	public void checkUrl() {
		// TODO impl
	}
}
