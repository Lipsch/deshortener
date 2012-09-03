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
package ch.lipsch.deshortener.test;

import android.net.Uri;
import android.test.AndroidTestCase;
import ch.lipsch.deshortener.Deshortener;
import ch.lipsch.deshortener.Deshortener.Result;
import ch.lipsch.deshortener.Deshortener.ResultType;

public class DeshortenerTests extends AndroidTestCase {

	public void testBitLy() {

		Result result = Deshortener.deshorten(Uri.parse("http://bit.ly/CUjV"));
		assertTrue(result.wasSuccessful());
		assertEquals(
				"http://maps.google.com/maps?f=d&saddr=New+York+Penn+Station&daddr=9th+Ave+%26+14th+St,+New+York,+NY&hl=en&geocode=&mra=ls&dirflg=r&date=11%2F12%2F08&time=4:13pm&ttype=dep&noexp=0&noal=0&sort=&sll=40.746175,-73.998395&sspn=0.014468,0.036392&ie=UTF8&z=14",
				result.getDeshortenedUri().toString());
	}

	public void testCanUrlCom() {
		Result result = Deshortener.deshorten(Uri
				.parse("http://canurl.com/s0hui"));
		assertTrue(result.wasSuccessful());
		assertEquals("http://canurl.com", result.getDeshortenedUri().toString());
	}

	public void testFbMe() {
		Result result = Deshortener.deshorten(Uri
				.parse("http://fb.me/1nodCMsN3"));
		assertTrue(result.wasSuccessful());
		assertEquals(
				"http://www.publikative.org/2011/12/06/npd-anhanger-verlieren-die-nerven/",
				result.getDeshortenedUri().toString());
	}

	public void testGooGl() {
		Result result = Deshortener.deshorten(Uri.parse("http://goo.gl/fbsS"));
		assertTrue(result.wasSuccessful());
		assertEquals("http://www.google.com/", result.getDeshortenedUri()
				.toString());
	}

	public void testIdGd() {
		Result result = Deshortener.deshorten(Uri.parse("http://is.gd/gbKNRq"));
		assertTrue(result.wasSuccessful());
		assertEquals("http://www.google.com", result.getDeshortenedUri()
				.toString());
	}

	public void testOwLy() {
		Result result = Deshortener.deshorten(Uri.parse("http://ow.ly/8e5i5"));
		assertTrue(result.wasSuccessful());
		assertEquals(
				"http://blog.flattr.net/2011/12/the-top-foss-projects-in-2011/",
				result.getDeshortenedUri().toString());
	}

	public void testPlurlCom() {
		Result result = Deshortener.deshorten(Uri.parse("http://plurl.us/2t"));
		assertTrue(result.wasSuccessful());
		assertEquals("http://www.google.com", result.getDeshortenedUri()
				.toString());
	}

	public void testSnipurlCom() {
		Result result = Deshortener.deshorten(Uri
				.parse("http://snipurl.com/uxg1p"));
		assertTrue(result.wasSuccessful());
		assertEquals("http://www.google.com", result.getDeshortenedUri()
				.toString());
	}

	public void testSnurlCom() {
		Result result = Deshortener.deshorten(Uri
				.parse("http://snurl.com/uxg1p"));
		assertTrue(result.wasSuccessful());
		assertEquals("http://www.google.com", result.getDeshortenedUri()
				.toString());
	}

	public void testTCo() {
		Result result = Deshortener.deshorten(Uri.parse("http://t.co/Viub5y8"));
		assertTrue(result.wasSuccessful());
		assertEquals("http://goo.gl/Sj4XW", result.getDeshortenedUri()
				.toString());
	}

	public void testTinyUrlCom() {
		Result result = Deshortener.deshorten(Uri
				.parse("http://tinyurl.com/1c2"));
		assertTrue(result.wasSuccessful());
		assertEquals("http://www.google.com", result.getDeshortenedUri()
				.toString());
	}

	public void testUr1Ca() {
		Result result = Deshortener.deshorten(Uri.parse("http://ur1.ca/7xguz"));
		assertTrue(result.wasSuccessful());
		assertEquals("http://hackspace-jena.de/wiki/", result
				.getDeshortenedUri().toString());
	}

	public void testCliGs() {
		Result result = Deshortener.deshorten(Uri
				.parse("http://cli.gs/6fwxm69"));
		assertEquals(ResultType.SHOWS_PREVIEW, result.getResultType());
	}

}
