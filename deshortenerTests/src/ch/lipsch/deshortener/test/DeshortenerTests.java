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

public class DeshortenerTests extends AndroidTestCase {

	public void testBitLy() {
		assertEquals(
				"http://maps.google.com/maps?f=d&saddr=New+York+Penn+Station&daddr=9th+Ave+%26+14th+St,+New+York,+NY&hl=en&geocode=&mra=ls&dirflg=r&date=11%2F12%2F08&time=4:13pm&ttype=dep&noexp=0&noal=0&sort=&sll=40.746175,-73.998395&sspn=0.014468,0.036392&ie=UTF8&z=14",
				Deshortener.deshorten(Uri.parse("http://bit.ly/CUjV"))
						.toString());
	}

	public void testCanUrlCom() {
		assertEquals("http://canurl.com",
				Deshortener.deshorten(Uri.parse("http://canurl.com/s0hui"))
						.toString());
	}

	public void testGooGl() {
		assertEquals("http://www.google.com/",
				Deshortener.deshorten(Uri.parse("http://goo.gl/fbsS"))
						.toString());
	}

	public void testIdGd() {
		assertEquals("http://www.google.com",
				Deshortener.deshorten(Uri.parse("http://is.gd/gbKNRq"))
						.toString());
	}

	public void testPlurlCom() {
		assertEquals("http://www.google.com",
				Deshortener.deshorten(Uri.parse("http://plurl.us/2t"))
						.toString());
	}

	public void testSnipurlCom() {
		assertEquals("http://www.google.com",
				Deshortener.deshorten(Uri.parse("http://snipurl.com/uxg1p"))
						.toString());
	}

	public void testSnurlCom() {
		assertEquals("http://www.google.com",
				Deshortener.deshorten(Uri.parse("http://snurl.com/uxg1p"))
						.toString());
	}

	public void testTCo() {
		assertEquals("http://goo.gl/Sj4XW",
				Deshortener.deshorten(Uri.parse("http://t.co/Viub5y8"))
						.toString());
	}

	public void testTinyUrlCom() {
		assertEquals("http://www.google.com",
				Deshortener.deshorten(Uri.parse("http://tinyurl.com/1c2"))
						.toString());
	}

}
