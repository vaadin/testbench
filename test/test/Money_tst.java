/*
 * Vaadin TestBench Addon
 *
 * Copyright (C) 2012-2024 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
import junit.framework.*;

public class Money_tst extends TestCase {
	
	public void testMoney_tst() throws Exception{
		Money cash = new Money();
		Assert.assertEquals(0, cash.getMoney());
		cash = new Money(50);
		Assert.assertEquals(50, cash.getMoney());
	}
}