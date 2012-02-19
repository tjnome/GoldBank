package GoldBank.tjnome.GoldBank.conf;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

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
	
	public boolean permission;
	public String permissionfailed;
	
	public boolean worldpermission;
	public String worldpermissionfailed;
	public List<String> permissionworlds = new ArrayList<String>();
	
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
	private String CommandIn2;
	private String CommandIn3;
	
	private String CommandOut;
	private String CommandOut1;
	private String CommandOut2;
	private String CommandOut3;
	private String CommandOut4;
	
	private String InvalidNumber;
	
	private String CommandPay;
	private String CommandPay1;
	private String CommandPay2;
	private String CommandPay3;
	private String CommandPay4;
	
	
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
		
		this.configDefaults.put("Bank.Permissions.Global.Enable", true);
		this.configDefaults.put("Bank.Permissions.Global.Failed", "You do not have permission to use this command");
		
		this.configDefaults.put("Bank.Permissions.World.Permission", false);
		this.configDefaults.put("Bank.Permissions.World.Enabled", getWorld());
		this.configDefaults.put("Bank.Permissions.World.Failed", "This command is not active for this world");
		
		this.configDefaults.put("Bank.Command.Info", "Show you have much #Value you have in your bank account");
		this.configDefaults.put("Bank.Command.Info-1-1", "You have: ");
		this.configDefaults.put("Bank.Command.Info-1-2", "#Value in your bank account");
		this.configDefaults.put("Bank.Command.Info-2-1", "You have no #Value on your bank account");
		
		this.configDefaults.put("Bank.Command.Top", "Top five on the server");
		
		this.configDefaults.put("Bank.Command.In", "You put #Value on your bank account");
		this.configDefaults.put("Bank.Command.In-1-1", "You can't put #Value into your bank account when you don't have it in your inventory");
		this.configDefaults.put("Bank.Command.In-2-1", "You stored #Amount #Value in your bank");
		this.configDefaults.put("Bank.Command.In-2-2", "You have now #Amount #Value in your bank");
			
		this.configDefaults.put("Bank.Command.Out", "Take out #Value from your bank account");
		this.configDefaults.put("Bank.Command.Out-1-1", "You took out #Amount #Value from your bank account");
		this.configDefaults.put("Bank.Command.Out-1-2", "Your inventory is not large enough. Returning #Amount #Value to your bank account");
		this.configDefaults.put("Bank.Command.Out-2-1", "You typed a higher value than you have in your bank account");
		this.configDefaults.put("Bank.Command.Out-2-2", "You have #Amount #Value in your bank account");
		
		this.configDefaults.put("Bank.Command.Number", "Invalid number");
		
		this.configDefaults.put("Bank.Command.Pay", "Pay a user with #Value from your bank account");
		this.configDefaults.put("Bank.Command.Pay-1-1", "You paid #Amount #Value to #Player");
		this.configDefaults.put("Bank.Command.Pay-1-2", "You got #Amount #Value from #Player");
		this.configDefaults.put("Bank.Command.Pay-2-1", "You can't pay yourself");
		this.configDefaults.put("Bank.Command.Pay-2-2", "The player you want to pay is not online or does not exist");
		
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
		
		this.permission = this.config.getBoolean("Bank.Permissions.Global.Enable");
		this.permissionfailed = this.config.getString("Bank.Permissions.Global.Failed");
		
		this.worldpermission = this.config.getBoolean("Bank.Permissions.World.Permission");
		this.worldpermissionfailed = this.config.getString("Bank.Permissions.World.Failed");
		this.permissionworlds = this.config.getStringList("Bank.Permissions.World.Enabled");
		
		this.CommandInfo = this.config.getString("Bank.Command.Info");
		this.CommandInfo1 = this.config.getString("Bank.Command.Info-1-1");
		this.CommandInfo2 = this.config.getString("Bank.Command.Info-1-2");
		this.CommandInfo3 = this.config.getString("Bank.Command.Info-2-1");
		
		this.CommandTop = this.config.getString("Bank.Command.Top");
		
		this.CommandIn = this.config.getString("Bank.Command.In");
		this.CommandIn1 = this.config.getString("Bank.Command.In-1-1");
		this.CommandIn2 = this.config.getString("Bank.Command.In-2-1");
		this.CommandIn3 = this.config.getString("Bank.Command.In-2-2");
		
		this.CommandOut = this.config.getString("Bank.Command.Out");
		this.CommandOut1 = this.config.getString("Bank.Command.Out-1-1");
		this.CommandOut2 = this.config.getString("Bank.Command.Out-1-2");
		this.CommandOut3 = this.config.getString("Bank.Command.Out-2-1");
		this.CommandOut4 = this.config.getString("Bank.Command.Out-2-2");
		
		this.InvalidNumber = this.config.getString("Bank.Command.Number");
		
		this.CommandPay = this.config.getString("Bank.Command.Pay");
		this.CommandPay1 = this.config.getString("Bank.Command.Pay-1-1");
		this.CommandPay2 = this.config.getString("Bank.Command.Pay-1-2");
		this.CommandPay3 = this.config.getString("Bank.Command.Pay-2-1");
		this.CommandPay4 = this.config.getString("Bank.Command.Pay-2-2");
		
		for (Player player : this.plugin.getServer().getOnlinePlayers()) {
			if (new File(this.plugin.getDataFolder() + "/bank/", player.getName()).exists()) {
				try {
					bankinfo.put(player.getName(), (BankData)(GoldBankConf.load(new File(this.plugin.getDataFolder() + "/bank/", player.getName()))));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public HashMap<String, BankData> getBank() {
		return this.bankinfo;
	}
	
	public int getMaterialId() {
		return this.materialid;
	}
	
	public String getMaterialName() {
		return this.materialname;
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
	
	public String CommandIn2() {
		return this.CommandIn2.replaceAll("#Value", this.materialname);
	}
	
	public String CommandIn3() {
		return this.CommandIn3.replaceAll("#Value", this.materialname);
	}
	
	public String CommandOut() {
		return this.CommandOut.replaceAll("#Value", this.materialname);
	}
	
	public String CommandOut1() {
		return this.CommandOut1.replaceAll("#Value", this.materialname);
	}
	
	public String CommandOut2() {
		return this.CommandOut2.replaceAll("#Value", this.materialname);
	}
	
	public String CommandOut3() {
		return this.CommandOut3.replaceAll("#Value", this.materialname);
	}
	
	public String CommandOut4() {
		return this.CommandOut4.replaceAll("#Value", this.materialname);
	}
	
	public String CommandPay() {
		return this.CommandPay.replaceAll("#Value", this.materialname);
	}
	
	public String CommandPay1() {
		return this.CommandPay1.replaceAll("#Value", this.materialname);
	}
	
	public String CommandPay2() {
		return this.CommandPay2.replaceAll("#Value", this.materialname);
	}
	
	public String CommandPay3() {
		return this.CommandPay3.replaceAll("#Value", this.materialname);
	}
	
	public String CommandPay4() {
		return this.CommandPay4.replaceAll("#Value", this.materialname);
	}
	
	public String InvalidNumber() {
		return this.InvalidNumber;
	}
	
	public void cleanup() throws Exception {
		if (!(bankinfo.isEmpty())) {
			
			try {
				for (String playername : bankinfo.keySet()) {
					GoldBankConf.save(bankinfo.get(playername), new File (this.plugin.getDataFolder() + "/bank/", playername));
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
	
	public final List<String> getWorld() {
		List<String> Worlds = new ArrayList<String>(); 
		for (World world : this.plugin.getServer().getWorlds()) {
			Worlds.add(world.getName());
		}
		return Worlds;
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
