package GoldBank.tjnome.GoldBank.conf;

import java.io.Serializable;

public class BankData implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4021121914134320498L;
	private int bankamount = 0;
	
	public int getBankAmount() {
		return bankamount;
	}
	
	public void setBankAmount(int value) {
		bankamount = value;
	}

}
