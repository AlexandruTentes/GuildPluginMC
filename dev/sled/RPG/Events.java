package dev.sled.RPG;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import dev.sled.RPG.util.Storage;
import dev.sled.RPG.util.VaultFunctions;

/**
* The class handles the events.
*
* @version 1.0
* @author Sled
* @since 2019-11-23
*/
public class Events implements Listener
{	
	/**
	 * Set player's data on the server according to previous logins
	 */
	@EventHandler
	public void on_join(PlayerJoinEvent event)
	{
		PlayerLoadData pld = new PlayerLoadData();
		
		pld.on_join(event);
	}
	
	/**
	 * Save player's data to the server
	 */
	@EventHandler
	public void on_disconnect(PlayerQuitEvent event)
	{
		PlayerLoadData pld = new PlayerLoadData();
		
		pld.on_disconnect(event);
	}
	
	/*
	 * When entity is damaged event. This will run the leveling system logic.
	 */
	 @EventHandler
	 public void on_damage(EntityDamageByEntityEvent e)
	 {		 
		 LevelingSystem ls = new LevelingSystem();
		 
		 if(Storage.start_plugin_leveling_system_status)
			 ls.on_damage(e);
		 
		 else
			 return;
	 }
	 
	 /*
	 * Same as on_damage(e) but for sweep attacks
	 */
	 @EventHandler
	 public void on_sweep_damage(EntityDamageEvent e)
	 {
		 LevelingSystem ls = new LevelingSystem();
		 
		 if(Storage.start_plugin_leveling_system_status)
			 ls.on_sweep_damage(e);
		 
		 else
			 return;
	 }
	 
	 @EventHandler
	 public void on_respawn(PlayerRespawnEvent e)
	 {
		 (new PlayerLoadData()).on_respawn(e);
	 }
	 
	 @EventHandler
	 public void on_drink(PlayerInteractEvent e)
	 {		 
		(new Storage()).add_effects(e.getPlayer());
	 }
	 
	 /**
	  * Event which check if the held item is a name tag
	  * If so, check the name. If it starts with 'Level'
	  * then change to 'level'
	  * 
	  * @param e
	  */
	 @EventHandler
	 public void on_item_rename(InventoryClickEvent e)
	 {
		 if(e.isCancelled())
			 return;
		 
		 Inventory inv = e.getInventory();
		 InventoryView view = null;
		 int slot = 0;
		 
		 if(inv instanceof AnvilInventory)
		 {
			 view = e.getView();
			 slot = e.getRawSlot();
		 }
		 else return;
		 
		 if(slot != view.convertSlot(slot))
			 return;
		 
		 if(slot == 2)
		 {
			 ItemStack item = e.getCurrentItem();
			 
			 String type = item.getType().toString();
			 
			 if(type.equals("NAME_TAG") || type.split("NAME_TAG").length > 1)
			 {
				 String [] name = item.getItemMeta().getDisplayName().split(" ");
				 String new_name = "";
				 
				if(name.length >= 1)
					 if(name[0].equals("Level"))
						 new_name += "level ";
					 else
						 return;
				 
				 for(int i = 1; i < name.length; i++)
					 new_name += name[i] + (i + 1 == name.length ? "" : " ");
				 
				 ItemMeta im = item.getItemMeta();
				 im.setDisplayName(new_name);
				 
				 item.setItemMeta(im);
			 }
		 }
	 }
	
	/**
	 * Print and test this player's permissions
	 */
	@EventHandler
	public void on_player_message(PlayerCommandPreprocessEvent event)
	{
		PlayerLoadData pld = new PlayerLoadData();
		VaultFunctions vault = new VaultFunctions();
		LevelingSystem ls = new LevelingSystem();
		Storage store = new Storage();
		
		vault.on_player_message(event);
		
		// Testing the static player data held in memory at runtime
		pld.on_player_message(event);
		store.on_player_message(event);
		ls.on_player_message(event);
	}
}
