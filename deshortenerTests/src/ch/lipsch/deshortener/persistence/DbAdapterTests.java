package ch.lipsch.deshortener.persistence;

import android.net.Uri;
import android.test.AndroidTestCase;

public class DbAdapterTests extends AndroidTestCase {

	private DbAdapter dbAdapter;

	@Override
	protected void setUp() throws Exception {
		dbAdapter = new DbAdapter(getContext());
	}

	public void testOpen() {
		assertNotNull(openDbAdapter());
	}

	public void testClose() {
		closeDbAdapter();

		// Must fail due to closed underlying db accessor

		try {
			dbAdapter.isDomainTrusted("testDomain.uk");
			fail("expected exception not catched.");
		} catch (IllegalStateException e) {
			// expected
		}
	}

	public void testAddDomain() {
		openDbAdapter();

		String domain = "mydomain.ch";

		dbAdapter.addDomain(domain);
		assertTrue(dbAdapter.isDomainTrusted(domain));

		closeDbAdapter();
	}

	public void testRemoveDomain() {
		openDbAdapter();

		String domain = "mydomain2.ch";
		dbAdapter.addDomain(domain);
		dbAdapter.removeDomain(domain);
		assertFalse(dbAdapter.isDomainTrusted(domain));

		closeDbAdapter();
	}

	public void testRemoveAllDomains() {
		openDbAdapter();

		String domain1 = "mydomain3.ch";
		String domain2 = "mydomain4.ch";

		dbAdapter.addDomain(domain1);
		dbAdapter.addDomain(domain2);

		dbAdapter.removeAllDomains();
		assertFalse(dbAdapter.isDomainTrusted(domain1));
		assertFalse(dbAdapter.isDomainTrusted(domain2));

		closeDbAdapter();
	}

	public void testAddUri() {
		openDbAdapter();

		Uri uri = Uri.parse("http://mydomain.ch/path");
		dbAdapter.addUri(uri);
		assertTrue(dbAdapter.isUriTrusted(uri));

		closeDbAdapter();
	}

	public void testRemoveUri() {
		openDbAdapter();

		Uri uri = Uri.parse("http://mydomain.ch/path2");
		dbAdapter.addUri(uri);
		dbAdapter.removeUri(uri);

		assertFalse(dbAdapter.isUriTrusted(uri));

		closeDbAdapter();
	}

	public void testRemoveAllUris() {
		openDbAdapter();

		Uri uri = Uri.parse("http://mydomain.ch/path3");
		Uri uri2 = Uri.parse("http://mydomain.ch/path3");

		dbAdapter.addUri(uri);
		dbAdapter.addUri(uri2);

		dbAdapter.removeAllUris();

		assertFalse(dbAdapter.isUriTrusted(uri));
		assertFalse(dbAdapter.isUriTrusted(uri2));

		closeDbAdapter();
	}
	private void closeDbAdapter() {
		dbAdapter.close();
	}

	private DbAdapter openDbAdapter() {
		return dbAdapter.open();
	}

	@Override
	protected void tearDown() throws Exception {
		dbAdapter.close();
	}
}
