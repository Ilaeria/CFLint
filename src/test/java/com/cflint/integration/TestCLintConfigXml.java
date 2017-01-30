package com.cflint.integration;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.cflint.BugInfo;
import com.cflint.CFLint;
import com.cflint.config.CFLintConfig;

import cfml.parsing.reporting.ParseException;

public class TestCLintConfigXml {

	private CFLint cflint;
	@Before
	public void setUp() throws Exception{
		final com.cflint.config.CFLintConfiguration conf = CFLintConfig.createDefaultLimited("CFInsertChecker","CFUpdateChecker","CFModuleChecker");
		cflint = new CFLint(conf);
	}
	@Test
	/**
	 * Confirm rule is overridden.
	 */
	public void test_CFINSERT() throws ParseException, IOException {
		final String cfcSrc = "<cfcomponent hint=\"hint\">\r\n" 
				+ "<cfinsert dataSource = \"data source name\" "  
				+ "tableName = \"table name\" "
				+ "formFields = \"formfield1, formfield2, ...\" " + "password = \"password\" "
				+ "tableOwner = \"owner\" " + "tableQualifier = \"table qualifier\" " + "username = \"user name\">\r\n"
				+ "</cfcomponent>";
		cflint.process(cfcSrc, "Test.cfc");
		List<BugInfo> result = cflint.getBugs().getFlatBugList();
		
		assertEquals(1, result.size());
		assertEquals("AVOID_USING_CFINSERT_TAG", result.get(0).getMessageCode());
		assertEquals("Avoid using <cfinsert> tags. Use cfquery and cfstoredproc instead.", result.get(0).getMessage());
		assertEquals("WARNING", result.get(0).getSeverity());
	}
	@Test
	/**
	 * Confirm rule is still inherited from definition xml
	 */
	public void test_CFUPDATE() throws ParseException, IOException {
		final String cfcSrc = "<cfcomponent hint=\"hint\">\r\n" 
				+ "<cfupdate dataSource = \"data source name\" "  
				+ "tableName = \"table name\" "
				+ "formFields = \"formfield1, formfield2, ...\" " + "password = \"password\" "
				+ "tableOwner = \"owner\" " + "tableQualifier = \"table qualifier\" " + "username = \"user name\">\r\n"
				+ "</cfcomponent>";
		cflint.process(cfcSrc, "Test.cfc");
		List<BugInfo> result = cflint.getBugs().getFlatBugList();
		assertEquals(1, result.size());
		assertEquals("AVOID_USING_CFUPDATE_TAG", result.get(0).getMessageCode());
		assertEquals("Avoid using <cfupdate> tags. Use cfquery and cfstoredproc instead.", result.get(0).getMessage());
	}
	@Test
	public void test_CFMODULE() throws ParseException, IOException {
		final String cfcSrc = "<cfcomponent hint=\"hint\">\r\n" 
				+ "<cfmodule dataSource = \"data source name\" "  
				+ "tableName = \"table name\" "
				+ "formFields = \"formfield1, formfield2, ...\" " + "password = \"password\" "
				+ "tableOwner = \"owner\" " + "tableQualifier = \"table qualifier\" " + "username = \"user name\">\r\n"
				+ "</cfcomponent>";
		cflint.process(cfcSrc, "Test.cfc");
		List<BugInfo> result = cflint.getBugs().getFlatBugList();
		assertEquals(1, result.size());
		assertEquals("AVOID_USING_CFMODULE_TAG", result.get(0).getMessageCode());
		assertEquals("Avoid using <cfmodule> tags.", result.get(0).getMessage());
	}
}
