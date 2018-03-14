package com.vaadin.testbench.parallel;

import org.junit.ClassRule;
import org.junit.rules.ExternalResource;

public class SauceSuite {

	private static final SauceConnect sauceConnect = new SauceConnect();
	
	@ClassRule
	public static ExternalResource resource = new ExternalResource() {
		@Override
		protected void before() throws Throwable {
			sauceConnect.openConnection();
		}

		@Override
		protected void after() {
			sauceConnect.closeConnection();
		}
	};

}
