package ru.kt15.finomen.neerc.core;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

public class SettingsManager {
	private final static String STORAGE = "settings.yaml";
	private static SettingsManager instance = new SettingsManager();
	private Map<String, Object> settings = new HashMap<String, Object>();
	
	private SettingsManager() {
		Yaml yaml = new Yaml();
		Log.writeInfo("Loading configuration");
		try {
			settings = (Map<String, Object>) yaml
					.load(new FileReader(new File(STORAGE)));
		} catch (FileNotFoundException e) {
			Log.writeError(e.getLocalizedMessage());
		}
	}
	
	private void save() {
		try {
			OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(STORAGE), "UTF-8");
			DumperOptions options = new DumperOptions();
			options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
			Yaml yaml = new Yaml(options);
			yaml.dump(settings, writer);
			writer.close();
		} catch (IOException e) {
			Log.writeError(e.getLocalizedMessage());
		}
	}
	
	public static SettingsManager instance() {
		return instance;
	}
	
	public <T> T get(String name, T defaultValue) {
		String[] path = name.split("\\.");
		Map<String, Object> currentNode = settings;
		boolean changed = false;
		
		for (int currentPos = 0; currentPos < path.length - 1; ++currentPos) {
			if (currentNode.containsKey(path[currentPos])
					&& (currentNode.get(path[currentPos]) instanceof Map<?, ?>)) {
				currentNode = (Map<String, Object>)currentNode.get(path[currentPos]);
			} else {
				changed = true;
				Map<String, Object> nm = new HashMap<String, Object>();
				currentNode.put(path[currentPos], nm);
				currentNode = nm;
			}
		}
		
		T value = (T) currentNode.get(path[path.length - 1]);
		
		if (value == null) {
			value = defaultValue;
			currentNode.put(path[path.length - 1], value);
		}
		
		if (changed) {
			save();
		}
		
		return value;
	}
	
	public Map<String, Object> getTree() {
		return Collections.unmodifiableMap(settings);
	}

	public <T> void set(String name, T value) {
		Log.writeDebug("Set " + name + " = " + value.toString());
		String[] path = name.split("\\.");
		Map<String, Object> currentNode = settings;
		
		for (int currentPos = 0; currentPos < path.length - 1; ++currentPos) {
			if (currentNode.containsKey(path[currentPos])
					&& (currentNode.get(path[currentPos]) instanceof Map<?, ?>)) {
				currentNode = (Map<String, Object>)currentNode.get(path[currentPos]);
			} else {
				Map<String, Object> nm = new HashMap<String, Object>();
				currentNode.put(path[currentPos], nm);
				currentNode = nm;
			}
		}
		
		currentNode.put(path[path.length - 1], value);
		
		save();
	}
	
	
}
