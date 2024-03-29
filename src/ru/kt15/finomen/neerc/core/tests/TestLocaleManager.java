package ru.kt15.finomen.neerc.core.tests;

import static org.junit.Assert.*;
import java.io.UnsupportedEncodingException;

import org.junit.Test;

import ru.kt15.finomen.neerc.core.LocaleManager;
import ru.kt15.finomen.neerc.core.LocaleManager.Locale;

public class TestLocaleManager {

	@Test
	public void testLocaleManager() throws UnsupportedEncodingException {
		LocaleManager lm = new LocaleManager();
	}

	@Test
	public void testGetLocales() throws UnsupportedEncodingException {
		LocaleManager lm = new LocaleManager();
		assertTrue(lm.getLocales() != null);
	}

	@Test
	public void testSetLocaleAndLocalize() throws UnsupportedEncodingException {
		LocaleManager lm = new LocaleManager();
		for (Locale locale : lm.getLocales()) {
			lm.setLocale(locale);
			assertEquals(lm.localize("CurrentLocaleName"), locale.getName());
		}
	}

}
