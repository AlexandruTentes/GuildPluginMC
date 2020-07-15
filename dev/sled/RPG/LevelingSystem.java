package dev.sled.RPG;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import dev.sled.RPG.util.Effects;
import dev.sled.RPG.util.PluginLogger;
import dev.sled.RPG.util.Storage;
import dev.sled.RPG.util.UtilityFunctions;
import dev.sled.RPG.util.VaultFunctions;
import net.md_5.bungee.api.ChatColor;
import net.milkbowl.vault.permission.Permission;

/**

* This class is the one which handles the leveling system logic

* @version 1.0
* @author Sled
* @since 2020-3-21

*/
public class LevelingSystem
{	
	private Effects effect = new Effects();
	
	private PluginLogger log = new PluginLogger();
	
	/**
	 * Setters.
	 * 
	 * Function which handles the milestone player messaging and buff setting.
	 * 
	 * @param Player
	 * @param PlayerData
	 * @param boolean
	 * 
	 * @return PlayerData
	 */
	public void milestone(Player player, PlayerData pd, boolean reload)
	{
		try 
		{
			Double mul;
			int milestone_stage = 10;
			
			mul = ((int) (pd.get_player_level().get_level() / milestone_stage)) * 
					Storage.start_player_milestone_multiplicative_experience+
					Storage.start_player_milestone_additive_experience;
			
			if(!reload)
				player.sendMessage(ChatColor.BLUE + "Milestone level reached! Gaining: " + (1 + 
						Storage.start_player_milestone_additive_life_boost +
						" life boost, " + (1 + Storage.start_player_milestone_additive_damage_boost) + 
						" damage boost"+ (mul > 0.0 ? ", " + Math.round((1.0 *
								Storage.start_player_milestone_multiplicative_experience +
								Storage.start_player_milestone_additive_experience) * 100) + "% xp gain multiplier" : "")));
			
			milestone_buffs(pd);
		}
		catch(Exception exception)
		{
			log.print_log("milestone(Player, PlayerData, boolean) exception inside LevelingSystem.java class", exception);
		}
	}
	
	/**
	 * Setters.
	 * 
	 * Function which sets the player buffs at reaching a milestone
	 * 
	 * @param PlayerData
	 */
	private void milestone_buffs(PlayerData pd)
	{
		Level lvl = pd.get_player_level();
		int milestone_stage = 10;
		
		pd.get_player_level().set_life((int) (lvl.get_level() / milestone_stage) + 
				Storage.start_player_milestone_additive_life_boost);
		pd.get_player_level().set_damage((int) (lvl.get_level() / milestone_stage) + 
				Storage.start_player_milestone_additive_damage_boost);
		pd.set_experience_gain_multiplier(1.0 + (int) (lvl.get_level() / milestone_stage) * 
				Storage.start_player_milestone_multiplicative_experience +
				Storage.start_player_milestone_additive_experience);
	}
	
	/**
	 * Setters.
	 * 
	 * Function which adds different effects on player level up
	 * 
	 * @param Player
	 */
	private void level_up_effects(Player player)
	{	
		effect.add_player_fireworks(player);
	}
	
	/**
	 * Setters.
	 * 
	 * Function which changes the player group. If
	 * the groups are 'Level1' to 'Level1000' for instance
	 * this function will detect that and change their world group 
	 * based on their GuildPlugin level.
	 * 
	 * Works only if the primary group (index 0) is the first to be shown.
	 * Either way it's best to give all players only 1 group at all times.
	 * 
	 * @param Player
	 */
	private void change_player_group(Player player)
	{
		VaultFunctions vf = new VaultFunctions();
		Permission perm = vf.get_permission();
		
		try
		{
			String group = perm.getPlayerGroups(player)[0];
			String new_group = "Level" + Integer.toString(Integer.parseInt(group.split("Level")[1]) + 1);
			
			perm.playerRemoveGroup(player, group);
			perm.playerAddGroup(player, new_group);
		}
		catch(Exception err)
		{} // Just pass if their group is not 'Level1' for instance
	}
	
	/**
	 * Getters.
	 * 
	 * Function which levels a player up and returns the remaining current xp.
	 * 
	 * @param double
	 * @param Player
	 * @return double
	 */
	public double level_up(double gain, Player player)
	{
		try
		{
			PlayerLoadData pld = new PlayerLoadData();
			Storage store = new Storage();
			PlayerData pd = pld.get_player_data(player);
			Level lvl = pd.get_player_level();
			double xp_req = 0.0;
			boolean level_up_flag = false;
			
			if(lvl.get_experience_required() == 0)
				return 0.0;

			if(gain >= lvl.get_experience_required())
			{
				level_up_flag = true;
				level_up_effects(player);
			}
				
			while(gain >= lvl.get_experience_required())
			{	
				gain = gain - lvl.get_experience_required();
				lvl.set_level(lvl.get_level() + 1);
				
				xp_req = Math.round(lvl.get_experience_required() *
						(1 + Storage.start_player_experience_required_multiplier));
				
				if(lvl.get_level() < Storage.start_player_max_level)
					lvl.set_experience_required(xp_req < Storage.define_MAX_XP_REQ ? 
							xp_req : Storage.define_MAX_XP_REQ);
				else
					lvl.set_experience_required(0);
				
				if(lvl.get_level() % 10 == 0)
					milestone(player, pd, false);
				
				//store.remove_effects(player);
				store.add_effects(player);
				
				change_player_group(player);
			}
			
			if(level_up_flag)
				player.sendMessage(ChatColor.BLUE + "You have reached level " + lvl.get_level());
		}
		catch(Exception exception)
		{
			log.print_log("level_up(double, Player) exception inside LevelingSystem.java class", exception);
		}
		
		return gain;
	}
	
	/**
	 * Setters.
	 * 
	 * Function which handles the player mob kill streak.
	 * 
	 * @param Player
	 */
	private void check_player_killstreak(Player player)
	{
		PlayerLoadData pld = new PlayerLoadData();
		PlayerData pd = pld.get_player_data(player);
		
		if(pd.task_mob_kill_streak_id != -1)
		{
			Bukkit.getScheduler().cancelTask(pd.task_mob_kill_streak_id);
			pd.task_mob_kill_streak_id = -1;
		}
		
		if(pd.kill_streak_milestone == 0)
			pd.kill_streak_milestone = Storage.start_player_killstreak_start;
		
		if(pd.mob_kill_streak >= pd.kill_streak_milestone)
			if(pd.milestone_count + 1 <= Storage.start_player_killstreak_max_count)
			{				
				pd.milestone_count++;
				
				pd.set_kill_streak_experience_gain_multiplier(pd.get_kill_streak_experience_gain_multiplier() +
						Storage.start_player_killstreak_additive_experience);
				
				pd.kill_streak_milestone *= (1.0 + Storage.start_player_killstreak_multiplicative_kill_count);
				pd.kill_streak_milestone += Storage.start_player_killstreak_start;
			}
		
		pd.task_mob_kill_streak_id = Bukkit.getScheduler().scheduleSyncDelayedTask(Storage.plugin, new Runnable()
		{
			@Override
			public void run()
			{
				pd.task_mob_kill_streak_id = -1;
				pd.set_highest_mob_kill_streak(pd.mob_kill_streak);
				pd.set_kill_streak_experience_gain_multiplier(1.0);
				pd.mob_kill_streak = 0.0;
				pd.kill_streak_milestone = 0;
				pd.milestone_count = 0;
			}
				
		}, 100); // 5 sec
	}
	
	/**
	 * Setters.
	 * 
	 * Function which handles the rewards for a mob kill. It
	 * decided if the player levels up, keeps track of killstreak, etc.
	 * 
	 * @param EntityDamageByEntityEvent
	 * @param LivingEntity
	 * @param Player
	 */
	@SuppressWarnings("deprecation")
	private void reward(EntityDamageByEntityEvent e, LivingEntity entity, Player player)
	{		
		try
		{			
			if(player.getGameMode() != GameMode.SURVIVAL && player.getGameMode() != GameMode.ADVENTURE)
				return;
			
			UtilityFunctions.Tuple tuple = new UtilityFunctions().get_mob_name_and_level(entity);
			String mob_name = (String) tuple.get_by_type(String.class);
			int mob_level = (int) tuple.get_by_type(Integer.class);
			PlayerLoadData pld = new PlayerLoadData();
			PlayerData pd = pld.get_player_data(player);
			Storage store = new Storage();
			Combat combat = new Combat();
			Level lvl = pd.get_player_level();
			double hp = entity.getMaxHealth();
			VaultFunctions vf = new VaultFunctions();
			
			combat.set_attacker(entity.getUniqueId(), player);
			combat.set_attacker_damage_dealt(entity.getUniqueId(), player, 
				combat.get_attacker_damage_dealt(entity.getUniqueId(), player) + e.getFinalDamage());
			
			if(entity instanceof Monster && entity instanceof LivingEntity)
				pd.set_damage_dealt(pd.get_damage_dealt() + e.getFinalDamage());

			if(entity.getHealth() - e.getFinalDamage() <= 0.0)
			{							
				vf.get_economy().depositPlayer(player.getName(), store.get_money_gain_mob(mob_name));
			
				double prob = (combat.get_attacker_damage_dealt(entity.getUniqueId(), player) >= hp ? hp :
					combat.get_attacker_damage_dealt(entity.getUniqueId(), player)) / hp;
				double mob_lvl_exp_multi = ((double)(((double) Storage.start_mob_experience_multiplier * mob_level) / 100.0) + 1.00);
				double xp = store.get_experience_gain_mob(mob_name) * mob_lvl_exp_multi * 
					prob * Storage.start_player_experience_gain_multiplier;
				
				xp *= (pd.get_experience_gain_multiplier() + pd.get_kill_streak_experience_gain_multiplier() - 1.0);
				xp *= pd.get_experience_multiplier();
				double gain = lvl.get_current_experience() + xp;
				
				if(entity instanceof Monster)
				{
					pd.set_mobs_killed(pd.get_mobs_killed() + 1);
					pd.mob_kill_streak++;
					pd.set_highest_mob_kill_streak(pd.mob_kill_streak);
					pd.set_total_xp(pd.get_total_xp() + xp);
				}		
				
				if(lvl.get_experience_required() == 0)
					return;
				
				gain = level_up(gain, player);
				lvl.set_current_experience(gain);
				
				if(xp != 0 || store.get_money_gain_mob(mob_name) != 0.0)	
					effect.add_player_xp_bar(player, ChatColor.BLUE + "Gained " + (xp != 0 ? Long.toString(Math.round(xp)) + " XP" : "") + 
							(xp != 0 && store.get_money_gain_mob(mob_name) != 0.0 ? " and " : "") +
							(store.get_money_gain_mob(mob_name) != 0.0 ? "$" + store.get_money_gain_mob(mob_name) : "") + 
							" Kill Streak: " + Math.round(pd.mob_kill_streak), mob_name, 5.0);
				
				check_player_killstreak(player);
	
				combat.remove_attacked_entity(entity.getUniqueId());
			}
		}
		catch(Exception exception)
		{
			log.print_log("reward(EntityDamageByEntityEvent, LivingEntity, Player) exception inside LevelingSystem.java class", exception);
		}
	}
	
	/**
	 * Getters.
	 * 
	 * Function which returns the player that caused damage to another entity.
	 * 
	 * @param EntityDamageByEntityEvent
	 * @return Player
	 */
	private Player get_player_from_entity(EntityDamageByEntityEvent e)
	{
		Player player;
		
		if(e.getDamager() instanceof Player)
			 player = (Player) e.getDamager();
		 else if(e.getDamager() instanceof Arrow)
		 {
			 Arrow a = (Arrow) e.getDamager();
			 
			 if(a.getShooter() instanceof Player)
				 player = (Player) a.getShooter();
			 else 
				 return null;
		 }
		 else if(e.getDamager() instanceof ThrownPotion)
		 {
			 ThrownPotion a = (ThrownPotion) e.getDamager();
			 
			 if(a.getShooter() instanceof Player)
				 player = (Player) a.getShooter();
			 else 
				 return null;
		 }
		 else if(e.getDamager() instanceof Trident)
		 {
			 Trident a = (Trident) e.getDamager();
			 
			 if(a.getShooter() instanceof Player)
				 player = (Player) a.getShooter();
			 else 
				 return null;
		 }
		 else
			 return null;
		
		return player;
	}
	
	/**
	 * Setters.
	 * 
	 * Function which sets the damage a mob takes. It is based
	 * on the player bonus damage based on their level and the
	 * mob armor.
	 * 
	 * @param Player
	 * @param Entity
	 * @param EntityDamageByEntityEvent
	 */
	private void set_mob_damage_taken(Player player, Entity entity, EntityDamageByEntityEvent e)
	{
		PlayerLoadData pld = new PlayerLoadData();
		Double mob_armor = 1.0;
		Double dmg;
		
		dmg = 1.0 + (pld.get_player_data(player).get_player_level().get_damage() * 
				 Storage.start_player_milestone_multiplicative_damage_boost) / e.getDamage();
		 
		mob_armor = 1.0 - ((double)(1.0 / (Storage.start_mob_max_level -
			new UtilityFunctions().get_mob_level(entity))));
		 
		 if(mob_armor < 0.2) mob_armor = 0.2;
		 
		 e.setDamage(e.getDamage() * dmg * mob_armor + 
				 Storage.start_player_milestone_additive_damage_boost);
	}
	
	/**
	 * Setters.
	 * 
	 * Function which handles a pvp situation. It sets the leaderboard 
	 * scores of total dmg dealt, player kill streak, etc.
	 * 
	 * @param Player
	 * @param Player
	 * @param EntityDamageByEntityEvent
	 */
	private void check_player_pvp(Player attacked, Player player, EntityDamageByEntityEvent e)
	{
		PlayerLoadData pld = new PlayerLoadData();
		
		if(attacked != player && !(attacked instanceof OfflinePlayer))
		 {
			 PlayerData pd = pld.get_player_data(player);
			 
			 pd.set_damage_dealt(pd.get_damage_dealt() + e.getFinalDamage());
			 
			 if(attacked.getHealth() - e.getFinalDamage() <= 0.0)
			 {
				 pd.set_players_killed(pd.get_players_killed() + 1);
				 pd.player_kill_streak++;
				 pd.set_highest_player_kill_streak(pd.player_kill_streak);
				 
				 if(pd.task_player_kill_streak_id != -1)
					{
					 	Bukkit.getScheduler().cancelTask(pd.task_player_kill_streak_id);
						pd.task_player_kill_streak_id = -1;
					}
					
					pd.task_player_kill_streak_id = Bukkit.getScheduler().scheduleSyncDelayedTask(Storage.plugin, new Runnable()
					{
						@Override
						public void run()
						{
							pd.task_player_kill_streak_id = -1;
							pd.set_highest_player_kill_streak(pd.player_kill_streak);
							pd.player_kill_streak = 0.0;
						}
							
					}, 100); // 5 sec
			 }
		 }
	}
	
	/*
	 * When entity is damaged event. This will run the leveling system logic.
	 */
	 @EventHandler
	  public void on_damage(EntityDamageByEntityEvent e)
	 {
		 try
		 {			 
			 Player player = null;
			 Entity entity = null;
			 Monster monster = null;
			 Animals animal = null;
			 Villager villager = null;
			 Player attacked = null;
			 
			 player = get_player_from_entity(e);
			 
			 if(player == null)
				 return;
			 
			 entity = e.getEntity();
			 set_mob_damage_taken(player, entity, e);
	
			 if(entity instanceof LivingEntity)
			 {			 
				 if(entity instanceof Monster)
				 {
					 monster = (Monster) entity;
					 reward(e, monster, player);
				 }
				 else if(entity instanceof Animals)
				 {
					 animal = (Animals) entity;
					 reward(e, animal, player);
				 }
				 else if(entity instanceof Villager)
				 {
					 villager = (Villager) entity;
					 reward(e, villager, player);
				 }
				 else if(entity instanceof Player)
				 {
					 attacked = (Player) entity;
					 check_player_pvp(attacked, player, e);
				 }
				 else
					 return;
			 }
			 else 
				 return;				 
		 }
		catch(Exception exception)
		{
			log.print_log("on_damage(EntityDamageByEntityEvent) exception inside LevelingSystem.java class", exception);
		}
	 }
	 
	 /*
	 * Same as on_damage(e) but for sweep attacks
	 */
	 @EventHandler
	  public void on_sweep_damage(EntityDamageEvent e)
	 {
		try
		{
			if(e.getCause() == EntityDamageEvent.DamageCause.ENTITY_SWEEP_ATTACK)
				on_damage((EntityDamageByEntityEvent) e);
		}
		catch(Exception exception)
		{
			log.print_log("on_sweep_damage(EntityDamageEvent) exception inside LevelingSystem.java class", exception);
		}
	 }
	 
	 /**
	 * Event
	 * 
	 * Function which is called by a player command message event
	 * Used to print and test this class
	 * 
	 * @param event
	 */
	public void on_player_message(PlayerCommandPreprocessEvent event)
	{
		try
		{
			Player sender = event.getPlayer();
			String [] command = event.getMessage().split(" ");
			MessageCommands msgc = new MessageCommands();
			
			if(!(sender instanceof Player))
				return;
			
			Player player = (Player) sender;
			
			switch(command[0])
			{
				case "/career": msgc.msg_career(command, player, sender); break;
				case "/gp_enable_leveling_system": msgc.msg_enable_leveling_system(sender); break;
				case "/gp_disable_leveling_system": msgc.msg_disable_leveling_system(player, sender); break;
				case "/manuadd": msgc.msg_manuadd_command_handler(command, sender); break;
				case "/gp_change_level": msgc.msg_change_level(command, sender); break;
				case "/gp_change_current_xp": msgc.msg_change_current_xp(command, player, sender); break;
				case "/heal": msgc.msg_heal_command_handler(command, player); break;
				case "/butcher": msgc.msg_butcher_command_handler(player); break;
				default: return;
			}

		}
		catch(Exception exception)
		{
			log.print_log("on_player_message(PlayerCommandPreprocessEvent) exception inside LevelingSystem.java class. Command: " + event.getMessage(), exception);
		}
	}
}
