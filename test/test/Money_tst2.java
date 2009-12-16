import junit.framework.*;

public class Money_tst2 extends TestCase {
	
	public void testMoney_tst2() throws Exception{
		Money cash = new Money(10);
		Assert.assertEquals(10, cash.getMoney());
		cash = new Money(20);
		Assert.assertEquals(20, cash.getMoney());
		cash.addMoney(10);
		Assert.assertEquals(30, cash.getMoney());
	}
}