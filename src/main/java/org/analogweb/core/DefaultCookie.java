package org.analogweb.core;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.analogweb.Cookies;

public class DefaultCookie implements Cookies.Cookie {

	private final String name;
	private final String value;
	private final String domain;
	private final String path;
	private final Date expires;
	private final int maxAge;
	private final boolean secure;
	private final boolean httpOnly;

	public DefaultCookie(String name, String value) {
		this(name, value, null, null, null, -1, false, false);
	}

	public DefaultCookie(String name, String value, String domain, String path,
			Date expires, int maxAge, boolean secure, boolean httpOnly) {
		super();
		this.name = name;
		this.value = value;
		this.domain = domain;
		this.path = path;
		this.expires = expires;
		this.maxAge = maxAge;
		this.secure = secure;
		this.httpOnly = httpOnly;
	}

	@Override
	public String getComment() {
		// TODO remove.
		// return this.comment;
		return null;
	}

	public Date getExpires() {
		return this.expires;
	}

	@Override
	public String getDomain() {
		return this.domain;
	}

	@Override
	public int getMaxAge() {
		return this.maxAge;
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public String getPath() {
		return this.path;
	}

	@Override
	public String getValue() {
		return this.value;
	}

	@Override
	public int getVersion() {
		// TODO remove
		return -1;
	}

	@Override
	public boolean isSecure() {
		return this.secure;
	}

	public boolean isHttpOnly() {
		return this.httpOnly;
	}

	@Override
	public String toString() {
		StringBuilder b = new StringBuilder();
		b.append(getName()).append('=');
		appendQuotedIfWhitespace(b, getValue());
		if (getPath() != null) {
			b.append(";Path=");
			appendQuotedIfWhitespace(b, getPath());
		}
		if (getDomain() != null) {
			b.append(";Domain=");
			appendQuotedIfWhitespace(b, getDomain());
		}
		if (getExpires() != null) {
			b.append(";Expires=");
			// RFC 2616
			SimpleDateFormat formatter = new SimpleDateFormat(
					"EEE, dd MMM yyyy HH:mm:ss zzz");
			appendQuotedIfWhitespace(b, formatter.format(getExpires()));
		}
		if (getMaxAge() > 0) {
			b.append(";Max-Age=");
			appendQuotedIfWhitespace(b, String.valueOf(getMaxAge()));
		}
		if (isSecure()) {
			b.append(";Secure");
		}
		if (isHttpOnly()) {
			b.append(";HttpOnly");
		}
		return b.toString();
	}

	public static void appendQuotedIfWhitespace(StringBuilder b, String value) {
		if (value == null)
			return;
		boolean quote = containsWhiteSpace(value);
		if (quote)
			b.append('"');
		appendEscapingQuotes(b, value);
		if (quote)
			b.append('"');
	}

	public static boolean containsWhiteSpace(String s) {
		for (char c : s.toCharArray()) {
			if (isWhiteSpace(c)) {
				return true;
			}
		}
		return false;
	}

	public static boolean isWhiteSpace(char c) {
		return (c < 128 && (c == '\t' || c == '\r' || c == '\n' || c == ' '));
	}

	public static void appendEscapingQuotes(StringBuilder b, String value) {
		for (int i = 0; i < value.length(); i++) {
			char c = value.charAt(i);
			if (c == '"')
				b.append('\\');
			b.append(c);
		}
	}
}
