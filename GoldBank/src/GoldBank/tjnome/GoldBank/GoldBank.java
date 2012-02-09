package GoldBank.tjnome.GoldBank;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeSet;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import GoldBank.tjnome.GoldBank.Listner.GoldBankPlayerListner;
import GoldBank.tjnome.GoldBank.conf.BankData;
import GoldBank.tjnome.GoldBank.conf.GoldBankConf;

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

public class GoldBank extends JavaPlugin {
	
	public PluginManager pm;
	protected final GoldBankConf configuration;
	
	public GoldBank() {
		configuration = new GoldBankConf(this);
	}

	@Override
	public void onDisable() {
		try {
			this.configuration.cleanup();
		} catch (Exception e) {
			e.printStackTrace();
		}
		PluginDescriptionFile pdfFile = this.getDescription();
		System.out.println(pdfFile.getName() + " disabled");
		
	}

	@Override
	public void onEnable() {
		try {
			this.configuration.load();
		} catch (Exception e) {
			e.printStackTrace();
		}
		getDataFolder().mkdirs();
		registerEvents();
		PluginDescriptionFile pdfFile = this.getDescription();
		System.out.println(pdfFile.getName() + " version " + pdfFile.getVersion() + " is enabled!");
		
	}
	
	public void registerEvents() {
		this.pm = getServer().getPluginManager();
		getServer().getPluginManager().registerEvents(new GoldBankPlayerListner(this), this);
	}
	
	public GoldBankConf getGoldBankConf() {
		return configuration;
	}
	
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (isPlayer(sender)) {
			Player player = (Player) sender;
			if (command.getName().equalsIgnoreCase("bank")) {
				if (args.length == 0) {
					player.sendMessage("------------" + ChatColor.GREEN + " " + this.configuration.getBankName() + " " + ChatColor.WHITE + "-------------");
					player.sendMessage(ChatColor.BLUE + "/bank info" + " " + ChatColor.WHITE + this.configuration.CommandInfo());
					player.sendMessage("/bank inn" + " Tar inn gull i banken");
					player.sendMessage("/bank ut [antall]" + " Tar ut gull fra banken");
					player.sendMessage("/bank betal [bruker] [antall] [grunn] " + " Betal en bruker");
					return true;
				} else if (args.length == 1) {
					if (args[0].equalsIgnoreCase("info")) {
						if (this.configuration.getBank().containsKey(player.getName())) {
							player.sendMessage("Du har: " + this.configuration.getBank().get(player.getName()).getBankAmount() + " gull i banken");
							return true;
						} else {
							player.sendMessage("Du har ingen gull i banken");
							return true;
						}
					} else if (args[0].equalsIgnoreCase("top")) {
						/*HashMap<String, Integer> unsortetBank = new HashMap<String, Integer>();
						for (Entry<String, BankData> entry : this.configuration.getBank().entrySet()) {
							unsortetBank.put(entry.getKey(), entry.getValue().getBankAmount());
						}*/
						HashMap<String, Integer> sortedBank = new HashMap<String, Integer>();
						sortedBank = sortHashMap(this.configuration.banktop);
						int count = 0;
						player.sendMessage(ChatColor.BLUE + "Top 5");
						for (String name : sortedBank.keySet()){
							if (count < 5) {
								player.sendMessage(name + " " + sortedBank.get(name));
								count++;
							}
						}
					} else if (args[0].equalsIgnoreCase("inn")) {
						int amount = 0;
						for (ItemStack stack : player.getInventory().getContents()) {
							if (stack != null) {
								if (stack.getTypeId() == this.configuration.getMaterialId()) {
									amount += stack.getAmount();
								}
							}
						}
						if (amount == 0) {
							player.sendMessage("Du kan ikke sette inn gull, der du ikke har gull i inventory");
							
						} else {
							if (this.configuration.getBank().containsKey(player.getName())) {
								int value = this.configuration.getBank().get(player.getName()).getBankAmount();
								this.configuration.getBank().get(player.getName()).setBankAmount(value + amount);
								this.configuration.banktop.put(player.getName(), value + amount);
							} else {
								this.configuration.getBank().put(player.getName(), new BankData());
								this.configuration.getBank().get(player.getName()).setBankAmount(amount);
								this.configuration.banktop.put(player.getName(), amount);
							}
							player.getInventory().remove(this.configuration.getMaterialId());
							player.sendMessage("Du satt inn: " + amount + " gull");
							player.sendMessage("Du har nå på konto: " + this.configuration.getBank().get(player.getName()).getBankAmount() + " gull");
							return true;
						} 
					}
				} else if (args.length == 2) {
					if (args[0].equalsIgnoreCase("ut")) {
						int amount = Short.parseShort(args[1]);
						if (amount >= 1) {
							if (this.configuration.getBank().containsKey(player.getName())) {
								if (this.configuration.getBank().get(player.getName()).getBankAmount() != 0) {
									if (this.configuration.getBank().get(player.getName()).getBankAmount() >= amount) {
										int value = (this.configuration.getBank().get(player.getName()).getBankAmount() - amount);
										this.configuration.getBank().get(player.getName()).setBankAmount(value);
										this.configuration.banktop.put(player.getName(), value);
										ItemStack[] gold = new ItemStack[1];
										gold[0] = new ItemStack(this.configuration.getMaterialId(), amount);
										HashMap<Integer, ItemStack> igjen = player.getInventory().addItem(gold);
										int gulligjen = 0;
										for (Entry<Integer, ItemStack> e : igjen.entrySet()) {
											gulligjen += e.getValue().getAmount();
										}
										player.sendMessage("Du tok ut " + amount + " gull");
										if (gulligjen != 0) {
											int gullibank = this.configuration.getBank().get(player.getName()).getBankAmount();
											this.configuration.getBank().get(player.getName()).setBankAmount(gullibank + gulligjen);
											this.configuration.banktop.put(player.getName(), gullibank + gulligjen);
											player.sendMessage("Du hadde ikke plass i inventorien, og vi retunerete " + gulligjen + " gull");
											return true;
										}
										return true;
									} else {
										player.sendMessage("Du har skrevet et høgre beløp en det du har i banken.");
										player.sendMessage("Du har i banken: " + this.configuration.getBank().get(player.getName()).getBankAmount() + " gull");
										return true;
									}
								} else {
									player.sendMessage("Du har ingenting å ta ut.");
									return true;
								}
							} else {
								player.sendMessage("Du har ingenting å ta ut.");
								return true;
							}
						} else {
							player.sendMessage("Du skrev et ugyldig tall");
							return false;
						}
					}
				} else if (args.length >= 4) {
					if (args[0].equalsIgnoreCase("betal")) {
						Player victim = getServer().getPlayer(args[1]);
						if (victim != null) {
							if (victim != player) {
								int amount = Integer.parseInt(args[2]);
								if (amount >= 1) {
									if (this.configuration.getBank().containsKey(player.getName())) {
										if (this.configuration.getBank().get(player.getName()).getBankAmount() >= amount) {
											if (this.configuration.getBank().containsKey(victim.getName())) {
												int playermoney = this.configuration.getBank().get(player.getName()).getBankAmount();
												this.configuration.getBank().get(player.getName()).setBankAmount(playermoney - amount);
												this.configuration.banktop.put(player.getName(), playermoney - amount);
												int victimmoney = this.configuration.getBank().get(player.getName()).getBankAmount();
												this.configuration.getBank().get(victim.getName()).setBankAmount(victimmoney + amount);
												this.configuration.banktop.put(victim.getName(), victimmoney + amount);
											} else {
												int playermoney = this.configuration.getBank().get(player.getName()).getBankAmount();
												this.configuration.getBank().get(player.getName()).setBankAmount(playermoney - amount);
												this.configuration.banktop.put(player.getName(), playermoney - amount);
												this.configuration.getBank().put(player.getName(), new BankData());
												this.configuration.getBank().get(victim.getName()).setBankAmount(amount);
												this.configuration.banktop.put(player.getName(), amount);
											}
											StringBuilder build = new StringBuilder();
											for (int i = 3; i < args.length; i++) {
												build.append(args[i] + " ");
											}
											player.sendMessage("Du betalte " + amount + " gull til " + victim.getName() + " " + build.toString());
											victim.sendMessage("Du fekk av " + player.getName() + " " + amount + " gull på grunn av: " + build.toString());
											return true;
										} else {
											player.sendMessage("Du har ikke nok gull på konto.");
											player.sendMessage("Du har på konto: " + this.configuration.getBank().get(player.getName()).getBankAmount() + " gull");
											return true;
										}
									} else {
										player.sendMessage("Du har ingenting å betale med.");
										return true;
									}
								} else {
									player.sendMessage("Du skrev et ugyldig tall");
									return false;
								}
							} else {
								player.sendMessage("Du kan ikke betale til deg selv");
								return false;
							}
						} else {
							player.sendMessage("Spiller du valgte er ikke online eller finnes ikke");
							return false;
						}
					}
				}
				
			}
			
		}
		
		return false;
		
	}

	private static boolean isPlayer(CommandSender sender) {
		if (sender instanceof Player) {
			return true;
		}
		return false;
	}
	
	private HashMap<String, Integer> sortHashMap(HashMap<String, Integer> input){
	    Map<String, Integer> tempMap = new HashMap<String, Integer>();
	    for (String wsState : input.keySet()){
	        tempMap.put(wsState,input.get(wsState));
	    }
	    List<String> mapKeys = new ArrayList<String>(tempMap.keySet());
	    List<Integer> mapValues = new ArrayList<Integer>(tempMap.values());
	    HashMap<String, Integer> sortedMap = new LinkedHashMap<String, Integer>();
	    TreeSet<Integer> sortedSet = new TreeSet<Integer>(mapValues);
	    Object[] sortedArray = sortedSet.toArray();
	    int size = sortedArray.length;
	    for (int i=(size-1); i >= 0; i--) {
	        sortedMap.put(mapKeys.get(mapValues.indexOf(sortedArray[i])), 
	                      (Integer)sortedArray[i]);
	    }
	    return sortedMap;
	}
}
