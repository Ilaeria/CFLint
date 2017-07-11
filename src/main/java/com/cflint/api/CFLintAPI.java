package com.cflint.api;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.List;

import javax.xml.bind.JAXBException;
import javax.xml.transform.TransformerException;

import org.apache.commons.io.IOUtils;

import com.cflint.BugInfo;
import com.cflint.BugList;
import com.cflint.CFLint;
import com.cflint.Version;
import com.cflint.config.CFLintConfiguration;
import com.cflint.config.CFLintPluginInfo;
import com.cflint.config.CFLintPluginInfo.RuleGroup;
import com.cflint.config.ConfigBuilder;
import com.cflint.config.ConfigUtils;
import com.cflint.tools.CFLintFilter;
import com.cflint.xml.MarshallerException;

public class CFLintAPI {

	PrintStream printStreamOut = System.out;
	PrintStream printStreamErr = System.err;
	final CFLintPluginInfo pluginInfo = ConfigUtils.loadDefaultPluginInfo();

	public static final String CFLINT = "cflint";
	public static final String RULES = "rules";
	public static final String INCLUDE_RULE = "includeRule";
	public static final String EXCLUDE_RULE = "excludeRule";
	public static final String FOLDER = "folder";
	public static final String FILTER_FILE = "filterFile";
	public static final String VERBOSE = "verbose";
	public static final String SHOWPROGRESS = "showprogress";
	public static final String QUIET = "quiet";
	public static final String DISPLAY_THIS_HELP = "display this help";
	public static final String XMLFILE = "xmlfile";
	public static final String XMLSTYLE = "xmlstyle";
	public static final String HTMLFILE = "htmlfile";
	public static final String HTMLSTYLE = "htmlstyle";
	public static final String JSONFILE = "jsonfile";
	public static final String TEXTFILE = "textfile";
	public static final String EXTENSIONS = "extensions";
	public static final String CONFIGFILE = "configfile";
	public static final String STRICT_INCLUDE = "strictinclude";
	String filterFile = null;
	boolean verbose = false;
	boolean logError = false;
	boolean quiet = false;
	boolean xmlOutput = false;
	boolean jsonOutput = false;
	boolean htmlOutput = true;
	boolean textOutput = false;

	private List<String> extensions;
	private boolean strictInclude;

	CFLintConfiguration configuration;
	CFLint cflint;

	public CFLintResult scan(List<String> fileOrFolder)
			throws IOException, TransformerException, JAXBException, MarshallerException {
		cflint = createCFLintInstance();

		for (final String scanfolder : fileOrFolder) {
			cflint.scan(scanfolder);
		}
		for (BugInfo bug : cflint.getBugs()) {
			cflint.getStats().getCounts().add(bug.getMessageCode(), bug.getSeverity());
		}
		return new CFLintResult(cflint);
	}

	public CFLintResult scan(String source) throws IOException, TransformerException, JAXBException, MarshallerException {
		return scan(source,"source.cfc");
	}
	
	public CFLintResult scan(String source, String filename) throws IOException, TransformerException, JAXBException, MarshallerException {
		cflint = createCFLintInstance();
		cflint.process(source, filename);
		for (BugInfo bug : cflint.getBugs()) {
			cflint.getStats().getCounts().add(bug.getMessageCode(), bug.getSeverity());
		}
		return new CFLintResult(cflint);
	}

	public BugList getResults() {
		return cflint.getBugs();
	}

	private CFLint createCFLintInstance() throws IOException, FileNotFoundException {
		final CFLint cflint = new CFLint(getOrLoadConfig());
		cflint.setVerbose(verbose);
		cflint.setLogError(logError);
		cflint.setQuiet(quiet);
		cflint.setStrictIncludes(strictInclude);
		cflint.setAllowedExtensions(extensions);
		cflint.getBugs().setFilter(createFilter());
		return cflint;
	}

	private CFLintConfiguration getOrLoadConfig() {
		if (configuration == null) {
			configuration = new ConfigBuilder().build();
		}
		return configuration;
	}

	protected CFLintFilter createFilter() throws IOException, FileNotFoundException {
		CFLintFilter filter = CFLintFilter.createFilter(verbose);
		if (filterFile != null) {
			final File ffile = new File(filterFile);
			if (ffile.exists()) {
				final FileInputStream fis = new FileInputStream(ffile);
				final byte b[] = new byte[fis.available()];
				IOUtils.read(fis, b);
				fis.close();
				filter = CFLintFilter.createFilter(new String(b), verbose);
			}
		}
		return filter;
	}

	/**
	 * Return the current version of CFLint
	 * 
	 * @return
	 */
	public String getVersion() {
		return Version.getVersion();
	}

	/**
	 * Return the version of CFParser used by the current CFLint
	 * 
	 * @return
	 */
	public String getCFParserVersion() {
		return cfml.parsing.Version.getVersion();
	}

	/**
	 * List the rule groups
	 * 
	 * @param pluginInfo
	 * @return
	 */
	public List<RuleGroup> getRuleGroups() {
		return pluginInfo.getRuleGroups();
	}

	/**
	 * Limit file extensions to this list
	 * 
	 * @param extensions
	 */
	public void setExtensions(final List<String> extensions) {
		this.extensions = extensions;
	}

	/**
	 * Verbose output
	 * 
	 * @param verbose
	 */
	public void setVerbose(boolean verbose) {
		this.verbose = verbose;
	}

	/**
	 * Log errors to output
	 * 
	 * @param logerror
	 */
	public void setLogError(boolean logerror) {
		this.logError = logerror;
	}

	/**
	 * Run quietly
	 * 
	 * @param quiet
	 */
	public void setQuiet(boolean quiet) {
		this.quiet = quiet;
	}

	/**
	 * Follow include paths and report an error if the included file cannot be
	 * found
	 * 
	 * @param strictInclude
	 */
	public void setStrictInclude(boolean strictInclude) {
		this.strictInclude = strictInclude;
	}

	/**
	 * Get the configuration object
	 * 
	 * @return
	 */
	public CFLintConfiguration getConfiguration() {
		return configuration;
	}

	/**
	 * Set the configuration object.  This object will be created automatically if it is not provided.
	 * 
	 * @param configuration
	 */
	public void setConfiguration(CFLintConfiguration configuration) {
		this.configuration = configuration;
	}

	/**
	 * Set filter file
	 * 
	 * @param filterFile
	 */
	public void setFilterFile(String filterFile) {
		this.filterFile = filterFile;
	}

	/**
	 * Provide the stream to use for standard output, by default System.out is
	 * used.
	 * 
	 * @param printStreamOut
	 */
	public void setPrintStreamOut(PrintStream printStreamOut) {
		this.printStreamOut = printStreamOut;
	}

	/**
	 * Provide the stream to use for error output, by default System.err is
	 * used.
	 * 
	 * @param printStreamErr
	 */
	public void setPrintStreamErr(PrintStream printStreamErr) {
		this.printStreamErr = printStreamErr;
	}

}
