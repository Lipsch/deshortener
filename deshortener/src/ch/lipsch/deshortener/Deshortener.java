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

import java.io.IOException;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpParams;

import android.net.Uri;
import android.util.Log;

/**
 * The Deshortener is responsible to resolve shortened url to the normal urls.
 * 
 * @author Erwin Betschart
 */
public final class Deshortener {

	private static final String LOG_TAG = Deshortener.class.getName();

	/**
	 * Deshortens the provided uri.
	 * 
	 * @param uriToDeshorten
	 *            The uri to deshorten.
	 * @return Returns null except when the given uri returns an 30x. Then value
	 *         of the Location header is returned.
	 */
	public static Uri deshorten(Uri uriToDeshorten) {
		HttpGet get = new HttpGet(uriToDeshorten.toString());
		DefaultHttpClient client = new DefaultHttpClient();
		HttpParams clientParams = client.getParams();
		HttpClientParams.setRedirecting(clientParams, false);
		HttpResponse response = null;
		Uri deshortenedUri = null;
		try {
			response = client.execute(get);
		} catch (ClientProtocolException e) {
			Log.e(LOG_TAG, "Unable to communicate to shortened url: "
					+ uriToDeshorten.toString(), e);
			return null;
		} catch (IOException e) {
			Log.e(LOG_TAG, "Unable to communicate to shortened url: "
					+ uriToDeshorten.toString(), e);
			return null;
		}

		boolean isRedirection = ((response.getStatusLine().getStatusCode() == HttpStatus.SC_MOVED_PERMANENTLY)
				|| (response.getStatusLine().getStatusCode() == HttpStatus.SC_MOVED_TEMPORARILY) || (response
				.getStatusLine().getStatusCode() == HttpStatus.SC_SEE_OTHER));
		if (response != null && isRedirection) {
			Header header = response.getFirstHeader("Location");
			String redirectValue = header.getValue();
			deshortenedUri = Uri.parse(redirectValue);
		}

		return deshortenedUri;
	}

	public interface ErrorHandler {
		public void errorOccurred(String message, Exception exception);
	}
}
