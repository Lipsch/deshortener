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
import java.util.HashSet;
import java.util.Set;

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

	/**
	 * Contains all the possible result states of a deshortening action.
	 */
	public enum ResultType {
		SUCCESS, NETWORK_ERROR, SHOWS_PREVIEW, CANNOT_DESHORTEN
	};

	/** Contains all hosts which show a preview. */
	private static final Set<String> PREVIEW_HOSTS = new HashSet<String>();

	private static final String LOG_TAG = Deshortener.class.getName();

	static {
		fillPreviewHosts();
	}

	/**
	 * Deshortens the provided uri.
	 * 
	 * @param uriToDeshorten
	 *            The uri to deshorten.
	 * @return Returns the result of the deshorten process. The result is only
	 *         successful when the given uri returns an 30x. Then value of the
	 *         Location header is returned within the result.
	 * @throws IllegalArgumentException
	 *             If the provided uri is invalid.
	 */
	public static Result deshorten(Uri uriToDeshorten) {
		// Checks if the url shortener shows a preview
		if (checkForPreview(uriToDeshorten)) {
			return new Result(ResultType.SHOWS_PREVIEW);
		}

		// Open the network connetion
		HttpGet get = new HttpGet(uriToDeshorten.toString());
		DefaultHttpClient client = new DefaultHttpClient();
		HttpParams clientParams = client.getParams();
		HttpClientParams.setRedirecting(clientParams, false);
		HttpResponse response = null;
		try {
			response = client.execute(get);
		} catch (ClientProtocolException e) {
			Log.e(LOG_TAG, "Unable to communicate to shortened url: "
					+ uriToDeshorten.toString(), e);
			return new Result(ResultType.NETWORK_ERROR);
		} catch (IOException e) {
			Log.e(LOG_TAG, "Unable to communicate to shortened url: "
					+ uriToDeshorten.toString(), e);
			return new Result(ResultType.NETWORK_ERROR);
		}

		// Check for redirection
		boolean isRedirection = ((response.getStatusLine().getStatusCode() == HttpStatus.SC_MOVED_PERMANENTLY)
				|| (response.getStatusLine().getStatusCode() == HttpStatus.SC_MOVED_TEMPORARILY) || (response
				.getStatusLine().getStatusCode() == HttpStatus.SC_SEE_OTHER));
		if (response != null && isRedirection) {
			Header header = response.getFirstHeader("Location");
			String redirectValue = header.getValue();
			Uri deshortenedUri = Uri.parse(redirectValue);
			return new Result(deshortenedUri);
		} else {
			return new Result(ResultType.CANNOT_DESHORTEN);
		}
	}

	private static void fillPreviewHosts() {
		PREVIEW_HOSTS.add("cli.gs");
	}

	private static boolean checkForPreview(Uri uri) {
		return PREVIEW_HOSTS.contains(uri.getHost());
	}

	/**
	 * The result of a deshortening action.
	 */
	public final static class Result {

		private final ResultType resultType;
		private final Uri uri;

		/**
		 * Creates a successful result.
		 *
		 * @param deshortenedUri
		 *            The successful deshortened uri.
		 * @throws NullPointerException
		 *             Is thrown in case deshortenedUri is <code>null.</code>
		 */
		public Result(Uri deshortenedUri) {
			if (deshortenedUri == null) {
				throw new NullPointerException(
						"deshortenedUri must not be null");
			}

			uri = deshortenedUri;
			resultType = ResultType.SUCCESS;
		}

		/**
		 * Creates an unsuccessful result.
		 *
		 * @param unsuccessfulReason
		 *            The failure reason.
		 * @throws IllegalArgumentException
		 *             In case the provided reason was successful.
		 */
		public Result(ResultType unsuccessfulReason) {
			uri = null;
			resultType = unsuccessfulReason;

			if (wasSuccessful()) {
				throw new IllegalArgumentException(
						"unsuccessfulReason must not be " + ResultType.SUCCESS);
			}
		}

		/**
		 * Delivers the type of this result object. Provides information about
		 * the type of error that occurred during the deshortening action.
		 *
		 * @return The type of result.
		 */
		public ResultType getResultType() {
			return resultType;
		}

		/**
		 * Determines if the deshortening action was successfull or not.
		 *
		 * @return <code>true</code> if the deshortening action was successful
		 *         else <code>false</code>.
		 */
		public boolean wasSuccessful() {
			return ResultType.SUCCESS.equals(resultType);
		}

		/**
		 * Delivers the deshortened uri.
		 *
		 * @return The deshortened uri.
		 * @throws IllegalStateException
		 *             Must not be called in case of an unsuccessful result that
		 *             is {@linkplain Result#wasSuccessful()} returns
		 *             <code>false</code>.
		 */
		public Uri getDeshortenedUri() {
			if (!wasSuccessful()) {
				throw new IllegalStateException(
						"Cannot deliver deshortened uri. Result must be of type "
								+ ResultType.SUCCESS);
			}
			return uri;
		}
	}
}
