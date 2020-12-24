package com.askwinston.service.impl;

import org.junit.Test;

import static org.junit.Assert.*;

public class FaxServiceImplTest {

	@Test
	public void testParseSRFaxResponseSuccess() {
		String response = FaxServiceImpl.parseSRFaxResponse("{\"Status\":\"Success\",\"Result\":597211015}");
		assertEquals("597211015", response);
	}

	@Test
	public void testParseSRFaxResponseFailsOnNull() {
		try {
			FaxServiceImpl.parseSRFaxResponse(null);
			fail("Should have failed FaxServiceImpl.SRFaxException");
		} catch (FaxServiceImpl.SRFaxException e) {
			assertEquals("Response is null", e.getMessage());
		}
	}

	@Test
	public void testParseSRFaxResponseFailsWithStatusFailed() {
		try {
			FaxServiceImpl.parseSRFaxResponse("{\"Status\":\"Failed\",\"Result\":\"Invalid Access Code / Password\"}");
			fail("Should have failed FaxServiceImpl.SRFaxException");
		} catch (FaxServiceImpl.SRFaxException e) {
			assertEquals("SRFax responded with Status=[Failed], Result=[Invalid Access Code / Password]",
					e.getMessage());
		}
	}

	@Test
	public void testParseSRFaxResponseFailsWithMissingStatus() {
		try {
			FaxServiceImpl.parseSRFaxResponse("{\"Other\":\"Failed\",\"Etc\":\"Invalid Access Code / Password\"}");
			fail("Should have failed FaxServiceImpl.SRFaxException");
		} catch (FaxServiceImpl.SRFaxException e) {
			assertEquals("SRFax responded with Status=[null], Result=[null]", e.getMessage());
		}
	}

}