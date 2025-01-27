/*
 * Vaadin TestBench Addon
 *
 * Copyright (C) 2012-2025 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
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