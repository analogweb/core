package org.analogweb.util;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeNoException;
import static org.junit.Assume.assumeThat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

/**
 * @author snowgoose
 */
public class ResourceUtilsTest {

	@Rule
	public TemporaryFolder folder = new TemporaryFolder();

	@Test
	public void testFindURLViaClassPathResource() throws Exception {
		URL url = ResourceUtils.findResource(getClass().getCanonicalName()
				.replace(".", File.separator) + ".class");
		assertTrue(new File(url.toURI()).isFile());
	}

	@Test
	public void testFindURLViaFile() throws Exception {
		File tempFile = folder.newFile("hellow.log");
		OutputStream out = new FileOutputStream(tempFile);
		out.write("hello!".getBytes());
		out.flush();
		out.close();
		URL url = ResourceUtils.findResource(tempFile.getCanonicalPath());
		InputStream in = url.openStream();
		StringBuilder actual = new StringBuilder();
		int c;
		while ((c = in.read()) != -1) {
			actual.append((char) c);
		}
		assertThat(actual.toString(), is("hello!"));
	}

	@Test
	public void testFindURLViaFileIsNotExists() throws Exception {
		File tempFile = folder.newFile("hellow.log");
		OutputStream out = new FileOutputStream(tempFile);
		out.write("hello!".getBytes());
		out.flush();
		out.close();
		URL url = ResourceUtils.findResource(tempFile.getCanonicalPath()
				+ ".noexists");
		assertNull(url);
	}

	@Test
	public void testFindURLNoRootFileResource() throws Exception {
		File noRootTestFileResource = folder.newFile("noRoot.log");
		OutputStream out = new FileOutputStream(noRootTestFileResource);
		out.write("hello!".getBytes());
		out.flush();
		out.close();
		URL url = ResourceUtils.findResource(noRootTestFileResource.getPath());
		InputStream in = url.openStream();
		StringBuilder actual = new StringBuilder();
		int c;
		while ((c = in.read()) != -1) {
			actual.append((char) c);
		}
		assertThat(actual.toString(), is("hello!"));
	}

	@Test
	public void testFindURLViaHttp() throws Exception {
		try {
			HttpURLConnection c = (HttpURLConnection) new URL(
					"http://example.com/").openConnection();
			c.setConnectTimeout(3000);
			assumeThat(c.getResponseCode(), is(200));
		} catch (Exception e) {
			assumeNoException(e);
		}
		URL url = ResourceUtils.findResource("http://example.com/");
		assertNotNull(url);
		InputStream in = url.openStream();
		StringBuilder actual = new StringBuilder();
		int c;
		while ((c = in.read()) != -1) {
			actual.append((char) c);
		}
		assertNotNull(actual.toString());
	}

	@Test
	public void testFindURLInvalidScheme() throws Exception {
		URL url = ResourceUtils
				.findResource("invalid://example.com/invalid://root");
		assertNull(url);
	}

	@Test
	public void testFindURLArgIsNull() throws Exception {
		URL actual = ResourceUtils.findResource(null);
		assertNull(actual);
	}

	@Test
	public void findResources() throws Exception {
		List<URL> found = ResourceUtils.findResources("org/analogweb/core",
				Thread.currentThread().getContextClassLoader());
		assertThat(found.size(), is(2));
	}
}
