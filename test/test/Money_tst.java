import junit.framework.*;

public class Money_tst extends TestCase {
	
	public void testMoney_tst() throws Exception{
		Money cash = new Money();
		Assert.assertEquals(0, cash.getMoney());
		cash = new Money(50);
		Assert.assertEquals(50, cash.getMoney());
	}
}