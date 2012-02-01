package GoldBank.tjnome.GoldBank.Listner;

import java.io.File;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import GoldBank.tjnome.GoldBank.GoldBank;
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

public class GoldBankPlayerListner implements Listener {
	
	private final GoldBank plugin;
	
	public GoldBankPlayerListner(GoldBank plugin) {
		this.plugin = plugin;
	}
	
	@EventHandler(priority=EventPriority.NORMAL)
	public void onPlayerJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		GoldBankConf cfg = this.plugin.getGoldBankConf();
		if (new File(this.plugin.getDataFolder() + "/bank/", player.getName()).exists()) {
			try {
				cfg.getBank().put(player.getName(), (BankData)(GoldBankConf.load(new File(this.plugin.getDataFolder() + "/bank/", player.getName()))));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
	}
	
	@EventHandler(priority=EventPriority.NORMAL)
	public void onPlayerQuit(PlayerQuitEvent event) {
		GoldBankConf cfg = this.plugin.getGoldBankConf();
		Player player = event.getPlayer();
		if (!(cfg.getBank().isEmpty())) {
			try {
				GoldBankConf.save(cfg.getBank().get(player.getName()), new File (this.plugin.getDataFolder() + "/bank/", player.getName()));
			} catch (Exception e) {
				e.printStackTrace();
			}
			cfg.getBank().remove(player.getName());
		}
	}

}
