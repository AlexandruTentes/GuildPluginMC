package dev.sled.RPG;

import java.util.Random;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.attribute.Attributable;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;

import dev.sled.RPG.util.Storage;
import dev.sled.RPG.util.UtilityFunctions;
/**

* This class holds the mob level functions
* which set the mob level, damage, life, speed, etc

* @version 1.0
* @author Sled
* @since 2020-5-12

*/
public class MobLevel
{
	// Global variables
	//private PlayerLoadData pld = new PlayerLoadData();
	private Random rand = new Random();
	private UtilityFunctions uf = new UtilityFunctions();
	
	/**
	 * Setters.
	 * 
	 * Function which sets the mob's level +- scale up to max_level
	 * 
	 * @param Entity, Player, Int, Int
	 */
	public void set_mob_level(Entity entity, Player player, int scale, int max_level)
	{
		if(player.getGameMode() != GameMode.SURVIVAL && player.getGameMode() != GameMode.ADVENTURE)
			return;
		
		Storage store = new Storage();
		String mob_name = "";
		Location world_spawn = Storage.plugin.getServer().getWorld("world").getSpawnLocation();
		int nr = 0;
		//int level = pld.get_player_data(player).get_player_level().get_level();
		long dist = 0;
		
		if(entity instanceof LivingEntity)
			if((LivingEntity) entity instanceof Monster)
			{
				mob_name = entity.getName();
				
				if(store.get_money_gain_mob(mob_name) != 0.0 || store.get_experience_gain_mob(mob_name) != 0)
				{
					String [] arr = mob_name.split(" ");
					
					if(!arr[0].equals("Level"))
					{
						nr = rand.nextInt(scale);
						//nr = rand.nextInt(scale * 2 + 1) - scale;
						//nr += level;
						
						if(((LivingEntity) entity).getRemoveWhenFarAway())
						{
							dist =Math.round(uf.get_distance_cylinder(world_spawn, entity.getLocation()));
							
							if(dist >= 0 && dist <= 500)
								nr += 0;
							
							if(dist > 500)
								nr += 5 * ((dist - 500) / 250);
							
							if(nr < 1) nr = 1;
							if(nr > max_level) nr = max_level;
							
							entity.setCustomName("Level " + nr + " " + mob_name);
							((LivingEntity) entity).setRemoveWhenFarAway(true);
							set_mob_buffs((Monster) entity, nr);
						}
					}
					else if(arr.length > 2)
						try
						{
							set_mob_buffs((Monster) entity, Integer.parseInt(arr[1]));
						}
						catch(NumberFormatException ex)
						{}
				}
			}
	}
	
	/**
	 * 
	 * Setters.
	 * 
	 * Function which sets the mob buffs at specific level
	 * 
	 * @param Monster, Int
	 */
	public void set_mob_buffs(Monster entity, int level)
	{
		if(entity.getHealth() <= 0.0)
			return;
		
		AttributeInstance instance_life = ((Attributable) entity).getAttribute(Attribute.GENERIC_MAX_HEALTH);
		AttributeInstance instance_damage = ((Attributable) entity).getAttribute(Attribute.GENERIC_ATTACK_DAMAGE);
		AttributeInstance instance_movement_speed = ((Attributable) entity).getAttribute(Attribute.GENERIC_MOVEMENT_SPEED);
		AttributeInstance instance_attack_speed = ((Attributable) entity).getAttribute(Attribute.GENERIC_ATTACK_SPEED);
		
		int stages = 6;
		double scale = 5.0;
		
		double damage_scale = 0.5;
		double movement_speed_scale = 0.0025;
		double attack_speed_scale = 0.01;
		double explosion_radius_scale = 0.25;
		
		int mob_level_milestone = 3;
		
		double damage_multi = 3.0;
		double life_multi = 1.0;
		double movement_speed_multi = 1.0;
		double attack_speed_multi = 1.0;
		int expl_radius_multi = 1;
		
		double life = 0.0;
		double damage;
		double movement_speed;
		double attack_speed;
		
		int max_mob_level = Storage.start_mob_max_level;
		int current_mob_stage = (int) ((int)(((double) level / max_mob_level) * 100) / (int)(((double)1.0 / stages) * 100));
		
		double stage_scale = scale * current_mob_stage;
		
		if(instance_damage != null)
		{
			damage = instance_damage.getBaseValue() + damage_scale * (level / mob_level_milestone);
			instance_damage.setBaseValue(damage * damage_multi);
		}
		
		if(instance_movement_speed != null)
		{
			movement_speed = instance_movement_speed.getBaseValue() + movement_speed_scale * (level / mob_level_milestone);
			instance_movement_speed.setBaseValue(movement_speed * movement_speed_multi);
		}
		
		if(instance_attack_speed != null)
		{
			attack_speed = instance_attack_speed.getBaseValue() + attack_speed_scale * (level / mob_level_milestone);
			instance_attack_speed.setBaseValue(attack_speed * attack_speed_multi);
		}
		
		if(instance_life != null)
		{
			life = instance_life.getBaseValue() + ((double)current_mob_stage / stages) * 100 + 
					(double) ((current_mob_stage) / (((double) current_mob_stage / stages) != 0 ? ((double) max_mob_level * 
					((double) current_mob_stage / stages)) : 1) * stage_scale) * level;
			
			instance_life.setBaseValue(life * life_multi);
			entity.setHealth(instance_life.getBaseValue());
		}
		
		if(entity instanceof Creeper)
		{
			Creeper c = (Creeper) entity;
			
			int radius = (int) (c.getExplosionRadius() + explosion_radius_scale * (level / mob_level_milestone));
			
			c.setExplosionRadius(radius * expl_radius_multi);
		}
	}
}
