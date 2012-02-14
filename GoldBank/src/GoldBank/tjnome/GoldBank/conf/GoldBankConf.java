package GoldBank.tjnome.GoldBank.conf;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;

import org.bukkit.configuration.file.YamlConfiguration;

import GoldBank.tjnome.GoldBank.GoldBank;

/**
*
* GoldBank
* Copyright (C) 2011 tjnome
*
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
* GNU General Public License for more details.
*
* You should have received a copy of the GNU General Public License
* along with this program. If not, see <http://www.gnu.org/licenses/>.
* 
*  @author tjnome
*/

public class GoldBankConf {
	private GoldBank plugin;
	private YamlConfiguration config;
	private File configFile;
	private File binFile;
	private HashMap<String, BankData> bankinfo = new HashMap<String, BankData>();
	private HashMap<String, Object> configDefaults = new HashMap<String, Object>();
	public HashMap<String, Integer> banktop = new HashMap<String, Integer>();
	
	private int materialid;
	private String materialname;
	
	private String bankname;
	private String CommandInfo;
	private String CommandInfo1;
	private String CommandInfo2;
	private String CommandInfo3;
	private String CommandTop;
	private String CommandIn;
	private String CommandIn1;
	private String CommandOut;
	private String CommandPay;
	
	
	public GoldBankConf(GoldBank plugin) {
		this.plugin = plugin;
	}
	
	@SuppressWarnings("unchecked")
	public void load() throws Exception {
		this.binFile = new File(this.plugin.getDataFolder(), "bank.bin");
		if (this.binFile.exists()) {
			this.banktop = (HashMap<String, Integer>)load(this.binFile);
		}
		this.config = new YamlConfiguration();
		this.configFile = new File(this.plugin.getDataFolder(), "config.yml");
		
		this.configDefaults.put("Bank.Name", "GoldBank ASA");
		this.configDefaults.put("Bank.Material.Id", 266);
		this.configDefaults.put("Bank.Material.Name", "Gold");
		
		this.configDefaults.put("Bank.Command.Info", "Show you have much #Value you have in your bank account");
		this.configDefaults.put("Bank.Command.Info-1-1", "You have: ");
		this.configDefaults.put("Bank.Command.Info-1-2", "#Value in your bank account");
		this.configDefaults.put("Bank.Command.Info-2-1", "You have no #Value on your bank account");
		
		this.configDefaults.put("Bank.Command.Top", "Top five on the server");
		
		this.configDefaults.put("Bank.Command.In", "Take in #Value on your bank account");
		this.configDefaults.put("Bank.Command.In-1-1", "You can't put #Value into your bank account when you don't have it in your inventory");
		
		
		this.configDefaults.put("Bank.Command.Out", "Take out #Value from your bank account");
		this.configDefaults.put("Bank.Command.Pay", "Pay a user with #Value from your bank account");
		
		if (!this.configFile.exists()) {
			for (String key : this.configDefaults.keySet()) {
				this.config.set(key, this.configDefaults.get(key));
			}
			this.config.save(this.configFile);
			
		} else {
			this.config.load(configFile);
		}
		
		this.materialid = this.config.getInt("Bank.Material.Id");
		this.bankname = this.config.getString("Bank.Name");
		this.materialname = this.config.getString("Bank.Material.Name");
		
		this.CommandInfo = this.config.getString("Bank.Command.Info");
		this.CommandInfo1 = this.config.getString("Bank.Command.Info-1-1");
		this.CommandInfo2 = this.config.getString("Bank.Command.Info-1-2");
		this.CommandInfo3 = this.config.getString("Bank.Command.Info-2-1");
		
		this.CommandTop = this.config.getString("Bank.Command.Top");
		
		this.CommandIn = this.config.getString("Bank.Command.In");
		this.CommandIn1 = this.config.getString("Bank.Command.In-1-1");
		
		this.CommandOut = this.config.getString("Bank.Command.Out");
		this.CommandPay = this.config.getString("Bank.Command.Pay");
	}
	
	public HashMap<String, BankData> getBank() {
		return this.bankinfo;
	}
	
	public int getMaterialId() {
		return this.materialid;
	}
	
	public String getBankName() {
		return this.bankname;
	}
	
	public String CommandInfo() {
		return this.CommandInfo.replaceAll("#Value", this.materialname);
	}
	
	public String CommandInfo1() {
		return this.CommandInfo1.replaceAll("#Value", this.materialname);
	}
	
	public String CommandInfo2() {
		return this.CommandInfo2.replaceAll("#Value", this.materialname);
	}
	
	public String CommandInfo3() {
		return this.CommandInfo3.replaceAll("#Value", this.materialname);
	}
	
	public String CommandTop() {
		return this.CommandTop.replaceAll("#Value", this.materialname);
	}
	
	public String CommandIn() {
		return this.CommandIn.replaceAll("#Value", this.materialname);
	}
	
	public String CommandIn1() {
		return this.CommandIn1.replaceAll("#Value", this.materialname);
	}
	
	public String CommandOut() {
		return this.CommandOut.replaceAll("#Value", this.materialname);
	}
	
	public String CommandPay() {
		return this.CommandPay.replaceAll("#Value", this.materialname);
	}
	
	public void cleanup() throws Exception {
		if (!(bankinfo.isEmpty())) {
			try {
				for (String player : bankinfo.keySet()) {
					GoldBankConf.save(bankinfo.get(player), new File (this.plugin.getDataFolder() + "/bank/", player));
				}
				bankinfo.clear();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if (!(banktop.isEmpty())) {
			save(banktop, this.binFile);
		}
	}
	
	public static void save(Object obj, File binFile) throws Exception {
		ObjectOutputStream oops = new ObjectOutputStream(new FileOutputStream(binFile));
		oops.writeObject(obj);
		oops.flush();
		oops.close();
	}
	
	public static Object load(File binFile) throws Exception {
		ObjectInputStream ois = new ObjectInputStream(new FileInputStream(binFile));
		Object result = ois.readObject();
		ois.close();
		return result;
	}
}
