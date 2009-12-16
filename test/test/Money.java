public class Money {
	private int money;
	
	Money(){
		this.money = 0;
	}
	
	Money(int amount){
		this.money = amount;
	}
	
	public int getMoney(){
		return money;
	}
	
	public void addMoney(int addThis){
	    this.money += addThis;
	}
}