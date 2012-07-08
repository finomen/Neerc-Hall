package ru.kt15.finomen.neerc;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

public class LocaleManager {
private Locale currentLocale;
private ArrayList<Locale> locales;
private ArrayList<Localized> localizedObjects;

public static class Locale {
	private String name;
	private Map<String, String> dictionary;
	private File file;
	
	private void dump() throws IOException {
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("name", name);
		data.put("dictionary", dictionary);
		FileWriter writer = new FileWriter(file);
		DumperOptions options = new DumperOptions();
		options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
		Yaml yaml = new Yaml(options);
		yaml.dump(data, writer);
		writer.close();
	}
	
	public Locale(File file) throws FileNotFoundException {
		this.file = file;
		Yaml yaml = new Yaml();
		Map<String, Object> data = (Map<String, Object>) yaml.load(new FileReader(file));
		name = (String) data.get("name");
		dictionary = (Map<String, String>) data.get("dictionary");
	}
	
	public Locale(File file, String name) throws IOException {
		this.file = file;
		this.name = name;
		dictionary = new HashMap<String, String>();
		dump();
	}
	
	public String getName() {
		return name;
	}
	
	public String localize(String str) {
		String result = dictionary.get(str);
		if (result != null) {
			return result;
		} else {
			dictionary.put(str, null);
			
			try {
				dump();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			 
			return str;
		}
	}
}

public LocaleManager() {
	File localeDir = new File("locale");
	locales = new ArrayList<Locale>();
	for (File localeFile : localeDir.listFiles(new FilenameFilter() {
		@Override
		public boolean accept(File arg0, String arg1) {
			return arg1.endsWith(".loc");
		}
	})) {
		try {
			Locale l = new Locale(localeFile);
			locales.add(l);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	if (!locales.isEmpty()) {
		currentLocale = locales.get(0);
	}
	
	localizedObjects = new ArrayList<Localized>();
}

public void addLocalizedObject(Localized localized) {
	localizedObjects.add(localized);
	localized.setLocaleStrings();
}

public Iterable<Locale> getLocales() {
	return locales;
}

public void setLocale(Locale locale) {
	currentLocale = locale;
	for (Localized localized : localizedObjects) {
		//TODO: remove garbage
		if (!localized.isDisposed()) {
			localized.setLocaleStrings();
		}
	}
}

public String localize(String str) {
	if (currentLocale != null) {
		return currentLocale.localize(str);
	} else {
		return str;
	}
}

public Locale getCurrentLocale() {
	return currentLocale;
}

}
