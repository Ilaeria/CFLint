package com.cflint.config;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;

import javax.xml.bind.JAXBException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

public class ConfigFileLoader {

	/**
	 * Load the configuration from a file, ignore a null configuration filename. bubble up any exceptions.
	 * @param configfile
	 * @return
	 * @throws JsonParseException
	 * @throws JsonMappingException
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws JAXBException
	 */
	public static CFLintConfig loadConfig(final String configfile) throws JsonParseException, JsonMappingException, FileNotFoundException, IOException, JAXBException {
		if (configfile != null) {
			CFLintPluginInfo pluginInfo = null;
			if (configfile.toLowerCase().endsWith(".xml")) {
				final Object configObj = ConfigUtils.unmarshal(new FileInputStream(configfile));
				if (configObj instanceof CFLintPluginInfo)
					pluginInfo = (CFLintPluginInfo) configObj;
				else if (configObj instanceof CFLintConfig) {
					return (CFLintConfig) configObj;
				}
			} else {
				pluginInfo = ConfigUtils.unmarshalJson(new FileInputStream(configfile), CFLintPluginInfo.class);
			}
			CFLintConfig returnVal = new CFLintConfig();
			returnVal.setRules(pluginInfo.getRules());
			return returnVal;
		}
		return null;
	}

	/**
	 * Load the configuration.  If there is an exception print it on the provided printstream, but return a null.
	 * @param configfile
	 * @param printStreamErr
	 * @return
	 */
	public static CFLintConfig loadConfigQuietly(final String configfile, final PrintStream printStreamErr) {
		if (configfile != null) {
			try {
				return loadConfig(configfile);
			} catch (final Exception e) {
				if (printStreamErr != null) {
					printStreamErr.println("Unable to load config file. " + e.getMessage());
				}
			}
		}
		return null;
	}
}
