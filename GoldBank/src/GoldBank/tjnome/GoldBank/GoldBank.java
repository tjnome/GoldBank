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
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import ru.tehkode.permissions.PermissionManager;
import ru.tehkode.permissions.bukkit.PermissionsEx;
import GoldBank.tjnome.GoldBank.Listner.GoldBankPlayerListner;
import GoldBank.tjnome.GoldBank.conf.BankData;
import GoldBank.tjnome.GoldBank.conf.GoldBankConf;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;

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
		System.out.println("Plugin by tjnome!");
		
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
		System.out.println("Plugin by tjnome!");
		
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
			if (CheckWorldPermission(player)) {
				if (command.getName().equalsIgnoreCase("bank")) {
					if (args.length == 0) {
						if (checkpermissions(player, "bank")) {
							player.sendMessage("------------" + ChatColor.GREEN + " " + this.configuration.getBankName() + " " + ChatColor.WHITE + "-------------");
							player.sendMessage(ChatColor.BLUE + "/bank info" + " " + ChatColor.WHITE + this.configuration.CommandInfo());
							player.sendMessage(ChatColor.BLUE + "/bank top" + " " + ChatColor.WHITE + this.configuration.CommandTop());
							player.sendMessage(ChatColor.BLUE + "/bank in" + " " + ChatColor.WHITE + this.configuration.CommandIn());
							player.sendMessage(ChatColor.BLUE + "/bank out [amount]" + " " + ChatColor.WHITE + this.configuration.CommandOut());
							player.sendMessage(ChatColor.BLUE + "/bank pay [name] [amount] [reason] " + ChatColor.WHITE + this.configuration.CommandPay());
							return true;
						} else {
							player.sendMessage(ChatColor.RED + this.configuration.permissionfailed);
							return true;
						}
					} else if (args.length == 1) {
						if (args[0].equalsIgnoreCase("info")) {
							if (checkpermissions(player, "info")) {
								if (this.configuration.getBank().containsKey(player.getName())) {
									player.sendMessage(ChatColor.BLUE + this.configuration.CommandInfo1() + ChatColor.GOLD + this.configuration.getBank().get(player.getName()).getBankAmount() + " " + ChatColor.BLUE + this.configuration.CommandInfo2());
									return true;
								} else {
									player.sendMessage(ChatColor.BLUE + this.configuration.CommandInfo3());
									return true;
								}
							} else {
								player.sendMessage(ChatColor.RED + this.configuration.permissionfailed);
								return true;
							}
						} else if (args[0].equalsIgnoreCase("top")) {
							if (checkpermissions(player, "top")) {
								HashMap<String, Integer> sortedBank = new HashMap<String, Integer>();
								sortedBank = sortHashMap(this.configuration.banktop);
								int count = 0;
								int nr = 1;
								player.sendMessage(ChatColor.BLUE + "Top 5");
								for (String name : sortedBank.keySet()){
									if (count < 5) {
										player.sendMessage("Nr: " + nr + " " + ChatColor.BLUE + name + " " + ChatColor.WHITE + sortedBank.get(name) + " " + this.configuration.getMaterialName());
										count++;
										nr++;
									}
								}
							return true;
							} else {
								player.sendMessage(ChatColor.RED + this.configuration.permissionfailed);
								return true;
							}
						} else if (args[0].equalsIgnoreCase("in")) {
							if (checkpermissions(player, "in")) {
								int amount = 0;
								for (ItemStack stack : player.getInventory().getContents()) {
									if (stack != null) {
										if (stack.getTypeId() == this.configuration.getMaterialId()) {
											amount += stack.getAmount();
										}
									}
								}
								if (amount == 0) {
									player.sendMessage(ChatColor.RED + this.configuration.CommandIn1());
								
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
									player.sendMessage(ChatColor.BLUE + this.configuration.CommandIn2().replaceAll("#Amount", ChatColor.GOLD + Integer.toString(amount) + ChatColor.BLUE));
									player.sendMessage(ChatColor.BLUE + this.configuration.CommandIn3().replaceAll("#Amount", ChatColor.GOLD + Integer.toString(amount) + ChatColor.BLUE));
									return true;
								}
							} else {
								player.sendMessage(ChatColor.RED + this.configuration.permissionfailed);
								return true;
							} 
						}
					} else if (args.length == 2) {
						if (args[0].equalsIgnoreCase("out")) {
							if (checkpermissions(player, "out")) {
								if (checkIfStringIsInteger(args[1])) {
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
													player.sendMessage(ChatColor.BLUE + this.configuration.CommandOut1().replaceAll("#Amount", ChatColor.GOLD + Integer.toString(amount) + ChatColor.BLUE));
													if (gulligjen != 0) {
														int gullibank = this.configuration.getBank().get(player.getName()).getBankAmount();
														this.configuration.getBank().get(player.getName()).setBankAmount(gullibank + gulligjen);
														this.configuration.banktop.put(player.getName(), gullibank + gulligjen);
														player.sendMessage(ChatColor.BLUE + this.configuration.CommandOut2().replaceAll("#Amount", ChatColor.GOLD + Integer.toString(amount) + ChatColor.BLUE));
														return true;
													}
													return true;
												} else {
													player.sendMessage(ChatColor.RED + this.configuration.CommandOut3());
													player.sendMessage(ChatColor.RED + this.configuration.CommandOut4().replaceAll("#Amount", ChatColor.GOLD + Integer.toString(amount) + ChatColor.BLUE));
													return true;
												}
											} else {
												player.sendMessage(ChatColor.RED + this.configuration.CommandInfo3());
												return true;
											}
										} else {
											player.sendMessage(ChatColor.RED + this.configuration.CommandInfo3());
											return true;
										}
									} else {
										player.sendMessage(ChatColor.RED + this.configuration.InvalidNumber());
										return false;
									}
								} else {
									player.sendMessage(ChatColor.RED + this.configuration.permissionfailed);
									return true;
								}
							} else {
								player.sendMessage(ChatColor.RED + this.configuration.InvalidNumber());
								return false;
							}
						}
					} else if (args.length >= 4) {
						if (args[0].equalsIgnoreCase("pay")) {
							if (checkpermissions(player, "pay")) {
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
														this.configuration.getBank().put(victim.getName(), new BankData());
														this.configuration.getBank().get(victim.getName()).setBankAmount(amount);
														this.configuration.banktop.put(player.getName(), amount);
													}
													StringBuilder build = new StringBuilder();
													for (int i = 3; i < args.length; i++) {
														build.append(args[i] + " ");
													}
													player.sendMessage(ChatColor.BLUE + this.configuration.CommandPay1().replaceAll("#Amount", ChatColor.GOLD + Integer.toString(amount) + ChatColor.BLUE).replaceAll("#Player", ChatColor.GOLD + victim.getName()) + " " + ChatColor.WHITE + build.toString());
													victim.sendMessage(ChatColor.BLUE + this.configuration.CommandPay2().replaceAll("#Amount", ChatColor.GOLD + Integer.toString(amount) + ChatColor.BLUE).replaceAll("#Player", ChatColor.GOLD + victim.getName()) + " " + ChatColor.WHITE + build.toString());
													return true;
												} else {
													player.sendMessage(ChatColor.RED + this.configuration.CommandOut3());
													player.sendMessage(ChatColor.RED + this.configuration.CommandOut4().replaceAll("#Amount", ChatColor.GOLD + Integer.toString(amount) + ChatColor.BLUE));
													return true;
												}
											} else {
												player.sendMessage(ChatColor.RED + this.configuration.CommandInfo3());
												return true;
											}
										} else {
											player.sendMessage(ChatColor.RED + this.configuration.InvalidNumber());
											return false;
										}
									} else {
										player.sendMessage(ChatColor.RED + this.configuration.CommandPay3());
										return false;
									}
								} else {
									player.sendMessage(ChatColor.RED + this.configuration.CommandPay4());
									return false;
								}
							} else {
								player.sendMessage(ChatColor.RED + this.configuration.permissionfailed);
								return true;
							}
						}
					}
					
				}
				
			} else {
				player.sendMessage(ChatColor.RED + this.configuration.worldpermissionfailed);
				return true;
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
	
	public boolean checkIfStringIsInteger(String check) {
		try {
			Integer.parseInt(check);
			return true;
			
		} catch (NumberFormatException NFE) {
			return false;
		}
	}
	
	//TRYING TO FIND OUT HOW I SHOULD USE SPOUT IN THE PLUGIN!
	
	public boolean checkpermissions(Player player, String action) {
		if (this.configuration.permission) {
			if (!getServer().getPluginManager().isPluginEnabled("PermissionsEX")) {
				if (player.hasPermission("GoldBank." + action)) {
					return true;
				}
			} else {
				if (getServer().getPluginManager().isPluginEnabled("PermissionsEX")) {
					PermissionManager permission = PermissionsEx.getPermissionManager();
					if (permission.has(player, "GoldBank." + action)) {
						return true;
					}
				}
			}
		} else {
			return true;
		}
		return false;
	}
	
	public boolean CheckWorldPermission(Player player) {
		if (this.configuration.worldpermission) {
			if (this.configuration.permission) {
				if (checkpermissions(player, "anyworld")) {
					return true;
				} else {
					if (this.configuration.permissionworlds.contains(player.getWorld().getName())) {
						return true;
					} else {
						return false;
					}
				}
			} else {
				if (this.configuration.permissionworlds.contains(player.getWorld().getName())) {
					return true;
				} else {
					return false;
				}
			}
		} else {
			return true;
		}
	}
	
	/*public boolean RegionRules(Player player) {
		WorldGuardPlugin worldguard = getWorldGuard();
		LocalPlayer localPlayer = worldguard.wrapPlayer(player);
		RegionManager regionManager = worldguard.getRegionManager(player.getWorld());
		Location loc = player.getLocation();
		ApplicableRegionSet set = regionManager.getApplicableRegions(loc);
		return set.allows(DefaultFlag. ,localPlayer);
		// NOW HOW TO FIND OUT HOW TO MAKE CUSTOMFLAGS FOR REGION, So USERS CAN HAVE REGION WHERE MY GOLDBANK PLUGIN IS NOT WORKING!
	}*/
	
	// NO simple solution, wil try to make some configuration to it little later. First a Per World system.
	
	public WorldGuardPlugin getWorldGuard() {
		if (getServer().getPluginManager().isPluginEnabled("WorldGuard")) {
			Plugin plugin = getServer().getPluginManager().getPlugin("WorldGuard");
			return (WorldGuardPlugin) plugin;
		}
		return null;
	}
}
