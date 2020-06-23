package dev.sled.RPG;

import java.util.UUID;

import org.bukkit.entity.Player;

import dev.sled.RPG.util.Storage;
import net.md_5.bungee.api.ChatColor;

/**
* The class handles the leveling system of each player, the level being set based
* on the groups inside the GroupManager
*
* @version 1.0
* @author Sled
* @since 2019-3-17
*/
public class Level
{
	private double current_experience = 0;
	private double experience_required = 0;
	private int level = 0;
	private double damage = 0.0;
	private int life = 0;
	
	/**
	 * This is the class constructor which will
	 * load player's leveling data.
	 * 
	 * Use the getter and setter functions provided 
	 */
	public Level(double current_experience, 
				 double experience_required,
				 int level)
	{
		this.current_experience = current_experience;
		this.experience_required = experience_required;
		this.level = level;
	}
	
	public Level()
	{}
	
	/**
	 * Setters.
	 * 
	 * Function which sets the player's current experience
	 */
	public void set_current_experience(double gain)
	{
		current_experience = gain;
	}
	
	/**
	 * Setters.
	 * 
	 * Function which sets the player's required experience
	 * to level up
	 */
	public void set_experience_required(double d)
	{
		experience_required = d;
	}
	
	/**
	 * Setters.
	 * 
	 * Function which sets the player's level
	 */
	public void set_level(int value)
	{
		level = value;
	}
	
	/**
	 * Setters.
	 * 
	 * Function which sets the player's damage
	 * 
	 * @param double
	 */
	public void set_damage(double value)
	{
		damage = value;
	}
	
	/**
	 * Setters.
	 * 
	 * Function which sets the player's life
	 * 
	 * @param int
	 */
	public void set_life(int value)
	{
		life = value;
	}
	
	/**
	 * Getters.
	 * 
	 * Function which returns the player's additional damage
	 * 
	 * @return double
	 */
	public double get_damage()
	{
		return damage;
	}
	
	/**
	 * Getters.
	 * 
	 * Function which returns the player's life boost level
	 * 
	 * @return int
	 */
	public int get_life()
	{
		return life;
	}
	
	/**
	 * Getters.
	 * 
	 * Function which returns the player's current experience
	 * 
	 * @return double
	 */
	public double get_current_experience()
	{
		return current_experience;
	}
	
	/**
	 * Getters.
	 * 
	 * Function which returns the player's requeired expereince
	 * to level up
	 * 
	 * @return double
	 */
	public double get_experience_required()
	{
		return experience_required;
	}
	
	/**
	 * Getters.
	 * 
	 * Function which returns the player's current level
	 * 
	 * @return int
	 */
	public int get_level()
	{
		return level;
	}
	
	/**
	 * Setters.
	 * 
	 * Function which changes the player level
	 * 
	 * @param Player, int, Player
	 */
	public void change_level(String name, UUID id, int level, Player sender)
	{
		Storage store = new Storage();
		PlayerLoadData pld = new PlayerLoadData();
		
		PlayerData other_pd;
		
		if(pld.is_online(name))
			other_pd = pld.get_player_data(name);
		
		else
			other_pd = store.load_data(name, id);
		
		other_pd.get_player_level().set_level(level);
		
		double xp_req = Math.round(Storage.start_player_experience_required * Math.pow(
						Storage.start_player_experience_required_multiplier + 1, level - 1));
		
		if(other_pd.get_player_level().get_level() < Storage.start_player_max_level)
			other_pd.get_player_level().set_experience_required(xp_req < Math.pow(2, 30) ? xp_req : Math.pow(2, 30));
		
		else
		{
			other_pd.get_player_level().set_experience_required(0);
			other_pd.get_player_level().set_current_experience(0);
		}
		
		other_pd.get_player_level().set_life(other_pd.get_player_level().get_level() / 10);
		
		if(other_pd.get_player_level().get_current_experience() >= other_pd.get_player_level().get_experience_required())
		{
			other_pd.get_player_level().set_current_experience(Math.round(other_pd.get_player_level().get_experience_required() * 0.75));
		}
		
		other_pd.get_player_level().set_life((int) (level / 10) + 
				Storage.start_player_milestone_additive_life_boost);
		other_pd.get_player_level().set_damage((int) (level / 10) + 
				Storage.start_player_milestone_additive_damage_boost);
		other_pd.set_experience_gain_multiplier(1.0 + ((int) (level / 10)) * 
				Storage.start_player_milestone_multiplicative_experience +
				Storage.start_player_milestone_additive_experience);
		
		if(pld.is_online(name))
		{
			Double mul = ((int) (other_pd.get_player_level().get_level() / 10)) * 
					Storage.start_player_milestone_multiplicative_experience +
					Storage.start_player_milestone_additive_experience;
			
			pld.set_player_data(name, other_pd);
			pld.get_player(name).sendMessage(ChatColor.BLUE + "Milestone level reached! Gaining: " + (1 + 
					Storage.start_player_milestone_additive_damage_boost) +
					" life boost, " + (1 + Storage.start_player_milestone_additive_damage_boost) + 
					" damage boost" + (mul > 0.0 ? ", " + Math.round((1.0 * 
							Storage.start_player_milestone_multiplicative_experience +
							Storage.start_player_experience_gain_multiplier) * 100) + "% xp gain multiplier" : ""));
			
			store.remove_effects(pld.get_player(name));
			store.add_effects(pld.get_player(name));
		}
		
		else
			store.save_data(id, other_pd);
	}
	
	/**
	 * Getters.
	 * 
	 * Function which helps print the data structure.
	 */
	public String toString()
	{
		return " [LEVEL]: " + Integer.toString(level) + ", Current XP: " + 
				Double.toString(Math.round(current_experience)) + ", Next lvl exp required: " + 
				Double.toString(Math.round(experience_required));
	}
}
