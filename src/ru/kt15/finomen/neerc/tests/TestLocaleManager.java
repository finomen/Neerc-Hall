package ru.kt15.finomen.neerc.tests;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

import ru.kt15.finomen.neerc.LocaleManager;
import ru.kt15.finomen.neerc.LocaleManager.Locale;

public class TestLocaleManager {

	@Test
	public void testLocaleManager() {
		LocaleManager lm = new LocaleManager();
	}

	@Test
	public void testGetLocales() {
		LocaleManager lm = new LocaleManager();
		int i = 0;
		String lnames[] = {"English", "Russian"};
		for (LocaleManager.Locale loc : lm.getLocales()) {
			assertEquals(lnames[i], loc.getName());
			++i;
		}
		
		assertEquals(lnames.length, i);
	}

	@Test
	public void testSetLocaleAndLocalize() {
		LocaleManager lm = new LocaleManager();
		for (Locale locale : lm.getLocales()) {
			lm.setLocale(locale);
			assertEquals(lm.localize("CurrentLocaleName"), locale.getName());
		}
	}

}
