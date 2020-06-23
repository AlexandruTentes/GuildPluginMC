package dev.sled.RPG.util;

import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;

import dev.sled.RPG.PlayerData;
import dev.sled.RPG.PlayerLoadData;
import net.md_5.bungee.api.ChatColor;
/**

* This class holds the common utility functions that might get used 
* multiple times in multiple locations

* @version 1.0
* @author Sled
* @since 2020-5-25

*/
public class Effects 
{
	/**
	 * Setters.
	 * 
	 * Adds firework effects to a player
	 * 
	 * @param player
	 */
	public void add_player_fireworks(Player player)
	{
		Firework fw = (Firework) player.getWorld().spawn(player.getLocation(), Firework.class);
        FireworkMeta fwm = fw.getFireworkMeta();
        
        Random rand = new Random();
        
        int rt = rand.nextInt(3);
        
        Type type = Type.BALL; // Default value  
        
        if (rt == 0) type = Type.BALL;
        if (rt == 1) type = Type.BURST;
        if (rt == 2) type = Type.STAR;
        
        Color c1 = Color.fromRGB(rand.nextInt(255), rand.nextInt(255), rand.nextInt(255));
        Color c2 = Color.fromRGB(rand.nextInt(255), rand.nextInt(255), rand.nextInt(255));
        
        fwm.addEffects(FireworkEffect.builder().withColor(c1).withFade(c2).with(type).trail(rand.nextBoolean()).build());
        
        fwm.setPower(rand.nextInt(2) + 1);
        fw.setFireworkMeta(fwm);
	}
	
	/**
	 * Setters.
	 * 
	 * Function which adds an xp bar to a player.
	 * 
	 * @param player
	 * @param double
	 * @param String
	 */
	public void add_player_xp_bar(Player player, String msg, String mob_name, double bar_duration)
	{
		PlayerLoadData pld = new PlayerLoadData();
		PlayerData pd = pld.get_player_data(player);
		Storage store = new Storage();
		double p = 0.0;
		
		if(pd.bar != null)
		{
			pd.bar.removePlayer(player);
			Bukkit.getScheduler().cancelTask(pd.task_experience_bar_id);
		}
		
		if(pd.get_player_level().get_level() != Storage.start_player_max_level)
		{
			pd.bar = Bukkit.createBossBar(msg, BarColor.GREEN, BarStyle.SEGMENTED_20);
			p = pd.get_player_level().get_current_experience() / pd.get_player_level().get_experience_required();
		}
		else if(store.get_money_gain_mob(mob_name) != 0.0)
		{
			pd.bar = Bukkit.createBossBar(ChatColor.GOLD + "$" + store.get_money_gain_mob(mob_name), BarColor.PURPLE, BarStyle.SOLID);
			p = 1.0;
		}
		else if(mob_name.equals(""))
		{
			pd.bar = Bukkit.createBossBar(ChatColor.GOLD + "MAX LEVEL", BarColor.PURPLE, BarStyle.SOLID);
			p = 1.0;
		}
		
		if(p > 1.0) p = 1.0; else if(p < 0.0) p = 0.0;
		
		pd.bar.setProgress(p);
		pd.bar.addPlayer(player);
		
		pd.task_experience_bar_id = Bukkit.getScheduler().scheduleSyncDelayedTask(Storage.plugin, new Runnable()
		{
			@Override
			public void run()
			{
				pd.bar.removePlayer(player);
			}
				
		}, (int) bar_duration * 20);
	}
}
