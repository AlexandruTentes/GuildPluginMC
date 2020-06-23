package dev.sled.RPG;

import org.bukkit.boss.BossBar;

/**
* The class handles the player data functions for setters and getters 
* of vault permissions, chat and economy and basic player data (name, guild etc)
*
* @version 1.0
* @author Sled
* @since 2019-11-23
*/
public class PlayerData
{
	//Global variables
	private String player_name = null; //player name
	private Guild player_guild = null; //player guild
	private Party player_party = null; //player party
	private Quest player_quest = null; //player quest
	private Level player_level = null; //player level
	
	private double kill_streak_experience_gain_multiplier = 1.0;
	private double experience_gain_multiplier = 1.0;
	private double experience_multiplier = 1.0;
	
	private double current_health = 0;
	private double damage_dealt = 0.0;
	private double mobs_killed = 0;
	private double players_killed = 0;
	private double total_xp = 0;
	private double highest_mob_kill_streak = 0.0;
	private double highest_player_kill_streak = 0.0;
	
	public int milestone_count = 0;
	public int kill_streak_milestone = 0;
	public double player_kill_streak = 0.0;
	public double mob_kill_streak = 0.0;
	public BossBar bar = null;
	public int task_experience_bar_id = -1;
	public int task_mob_kill_streak_id = -1;
	public int task_player_kill_streak_id = -1;
	
	private double database_version = 1.0;
	
	/**
	 * This is the class constructor which will
	 * load player's data.
	 * 
	 * Use the getter functions provided 
	 * (get_player_name(), get_player_guild(), etc)
	 */
	public PlayerData(String player_name,
					  Guild player_guild,
					  Party player_party,
					  Quest player_quest,
					  Level player_level,
					  double experience_gain_multiplier,
					  double database_version)
	{
		this.player_name = player_name;
		this.player_guild = player_guild;
		this.player_party = player_party;
		this.player_quest = player_quest;
		this.player_level = player_level;
		this.experience_gain_multiplier = experience_gain_multiplier;
		this.database_version = database_version;
	}
	
	/**
	 * Getters.
	 * 
	 * Function which returns the player's name.
	 * 
	 * @return String
	 */
	public String get_player_name()
	{
		return player_name;
	}
	
	/**
	 * Getters.
	 * 
	 * Function which returns the player's guild.
	 * 
	 * @return Guild
	 */
	public Guild get_player_guild()
	{
		return player_guild;
	}
	
	/**
	 * Getters.
	 * 
	 * Function which returns the player's party.
	 * 
	 * @return Party
	 */
	public Party get_player_party()
	{
		return player_party;
	}
	
	/**
	 * Getters.
	 * 
	 * Function which returns the player's quest.
	 * 
	 * @return Quest
	 */
	public Quest get_player_quest()
	{
		return player_quest;
	}
	
	/**
	 * Getters.
	 * 
	 * Function which returns the player's level object.
	 * 
	 * @returnLevel
	 */
	public Level get_player_level()
	{
		return player_level;
	}
	
	/**
	 * Getters.
	 * 
	 * Function which returns the player's current damage dealt
	 * 
	 * @return double
	 */
	public double get_damage_dealt()
	{
		return damage_dealt;
	}
	
	/**
	 * Getters.
	 * 
	 * Function which returns the player's current mobs killed count
	 * 
	 * @return double
	 */
	public double get_mobs_killed()
	{
		return mobs_killed;
	}
	
	/**
	 * Getters.
	 * 
	 * Function which returns the player's current players killed count
	 * 
	 * @return double
	 */
	public double get_players_killed()
	{
		return players_killed;
	}
	
	/**
	 * Getters.
	 * 
	 * Function which returns the player's current player's total xp earned
	 * 
	 * @return double
	 */
	public double get_total_xp()
	{
		return total_xp;
	}
	
	/**
	 * Getters.
	 * 
	 * Function which returns the player's current player's database version
	 * 
	 * @return double
	 */
	public double get_database_version()
	{
		return database_version;
	}
	
	/**
	 * Getters.
	 * 
	 * Function which returns the player's current experience gain multiplier
	 * 
	 * @return double
	 */
	public double get_experience_multiplier()
	{
		return experience_multiplier;
	}
	
	/**
	 * Getters.
	 * 
	 * Function which returns the player's total experience multiplier
	 * 
	 * @return double
	 */
	public double get_experience_gain_multiplier()
	{
		return experience_gain_multiplier;
	}
	
	/**
	 * Getters.
	 * 
	 * Function which returns the player's current life ammount
	 * 
	 * @return double
	 */
	public double get_current_health()
	{
		return current_health;
	}
	
	/**
	 * Getters.
	 * 
	 * Function which gets the player's highest player kill streak
	 * 
	 * @return Double
	 */
	public double get_highest_player_kill_streak()
	{
		return highest_player_kill_streak;
	}
	
	/**
	 * Getters.
	 * 
	 * Function which gets the player's kill streak experience gain multiplier
	 * 
	 * @return Double
	 */
	public double get_kill_streak_experience_gain_multiplier()
	{
		return kill_streak_experience_gain_multiplier;
	}
	
	/**
	 * Getters.
	 * 
	 * Function which gets the player's highest mob kill streak
	 * 
	 * @return Double
	 */
	public double get_highest_mob_kill_streak()
	{
		return highest_mob_kill_streak;
	}
	
	/**
	 * Setters.
	 * 
	 * Function which sets the player's current life amount
	 * 
	 * @param Double
	 */
	public void set_current_health(double value)
	{
		current_health = value;
	}
	
	/**
	 * Setters.
	 * 
	 * Function which sets the player's kill streak experience gain multiplier
	 * 
	 * @param Double
	 */
	public void set_kill_streak_experience_gain_multiplier(double value)
	{
		kill_streak_experience_gain_multiplier = value;
	}
	
	/**
	 * Setters.
	 * 
	 * Function which sets the player's highest player kill streak
	 * 
	 * @param Double
	 */
	public void set_highest_player_kill_streak(double value)
	{
		if(value >= highest_player_kill_streak)
			highest_player_kill_streak = value;
	}
	
	/**
	 * Setters.
	 * 
	 * Function which sets the player's highest mob kill streak
	 * 
	 * @param Double
	 */
	public void set_highest_mob_kill_streak(double value)
	{
		if(value >= highest_mob_kill_streak)
			highest_mob_kill_streak = value;
	}
	
	/**
	 * Setters.
	 * 
	 * Function which sets the player's experience gain multiplier
	 * 
	 * @param double
	 */
	public void set_experience_gain_multiplier(double value)
	{
		experience_gain_multiplier = value;
	}
	
	/**
	 * Setters.
	 * 
	 * Function which sets the player's total experience multiplier
	 * 
	 * @param double
	 */
	public void set_experience_multiplier(double value)
	{
		experience_multiplier = value;
	}
	
	/**
	 * Setters.
	 * 
	 * Function which sets the player's damage dealt
	 * 
	 * @param double
	 */
	public void set_damage_dealt(double value)
	{
		damage_dealt = value;
	}
	
	/**
	 * Setters.
	 * 
	 * Function which sets the player's current mobs killed count
	 * 
	 * @param double
	 */
	public void set_mobs_killed(double value)
	{
		mobs_killed = value;
	}
	
	/**
	 * Setters.
	 * 
	 * Function which sets the player's current players killed count
	 * 
	 * @param int
	 */
	public void set_players_killed(double d)
	{
		players_killed = d;
	}
	
	/**
	 * Setters.
	 * 
	 * Function which sets the player's current player's total xp gained
	 * 
	 * @param double
	 */
	public void set_total_xp(double l)
	{
		total_xp = l;
	}
	
	/**
	 * Setters.
	 * 
	 * Function which sets the player's current database version
	 * 
	 * @param double
	 */
	public void set_database_version(double value)
	{
		database_version = value;
	}
}
