package dev.sled.RPG.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
/**

* This class holds the common utility functions that might get used 
* multiple times in multiple locations

* @version 1.0
* @author Sled
* @since 2020-5-12

*/
public class UtilityFunctions
{	
	/**
	 * 
	 * Class which stores a tuple of objects to be later obtained
	 * via get_by_type() method. A cast or parse (depending on the wanted type)
	 * will be required when accessing get_by_type().
	 * 
	 * @version 1.0
	 * @author sled
	 * @since 2020-5-25
	 */
	public class Tuple
	{   
		/*
		 * Global variable map which stores the tuples variable by type
		 */
		private Map<Class<?>, List<Object>> tuple = null;
		private Class<?> prev_clazz = null;
		
		/**
		 * Constructor which sets the tuple variables and
		 * stores them by their type in a list map
		 * 
		 * @param Object...
		 */
		public Tuple(Object... objects)
		{
			List<Object> my_list;
			
			for(Object obj : objects)
			{
				if(prev_clazz != obj.getClass())
				    my_list = new ArrayList<Object>();
				else
					my_list = tuple.get(obj.getClass());
				
				my_list.add(obj);
				tuple.put(obj.getClass(), my_list);
				prev_clazz = obj.getClass();
			}
		}
		
		/**
		 * Getters. 
		 *
		 * Function which returns an object based on the wanted class type. Else null
		 * This object is either a single object (aka one variable)
		 * or a list of them (aka multiple variables).
		 * It all depends on the variables given in the consturctor.
		 * 
		 * @param Class<?>
		 * @return Object
		 */
		public Object get_by_type(Class<?> clazz)
		{
			List<Object> list = tuple.get(clazz);
			
			if(list.size() == 0)
				return null;
			
			if(list.size() == 1)
				return list.get(0);
			
			return list;
		}
	}
	
	/**
	 * 
	 * Getters.
	 * 
	 * Function which return a tuples object of a mob's name and level
	 * 
	 * @param entity
	 * @return Tuple (String, Int)
	 */
	public Tuple get_mob_name_and_level(Entity entity)
	{
		String [] mob_name_parts;
		
		if(entity.getCustomName() != null)
			mob_name_parts = entity.getCustomName().split(" ");
		else
			mob_name_parts = entity.getName().split(" ");
		
		String mob_name = "";
		int mob_level = 1;
		int k = 0;
		
		if(mob_name_parts.length >= 3)
			if(mob_name_parts[k].equals("Level"))
				try
				{
					k++;
					mob_level = Integer.parseInt(mob_name_parts[k]);
					k++;
				}
				catch(NumberFormatException ex)
				{}
		
		for(int i = k; i < mob_name_parts.length; i++)
			mob_name += mob_name_parts[i] + (i + 1 == mob_name_parts.length ? "" : " ");
		
		if(mob_level > 0)
			mob_level--;
		
		return new Tuple(mob_name, mob_level);
	}
	
	/**
	 * Getters.
	 * 
	 * Function which returns an entity's level
	 * 
	 * @param Entity
	 * @return Int
	 */
	public int get_mob_level(Entity entity)
	{
		String [] mob_name_parts;
		int mob_level = 1;
		
		if(entity.getCustomName() == null)
			return 1;
		
		mob_name_parts = entity.getCustomName().split(" ");
		
		if(mob_name_parts.length >= 3)
			if(mob_name_parts[0].equals("Level"))
				try
				{
					mob_level = Integer.parseInt(mob_name_parts[1]);
				}
				catch(NumberFormatException ex)
				{}
		
		return mob_level;
	}
	
	/**
	 * Getters.
	 * 
	 * Function which returns the distance in a cylinder form between 2 locations.
	 * 
	 * @param Location, Location
	 * @return double
	 */
	public double get_distance_cylinder(Location l1, Location l2)
	{
		double dist = 0.0;
		
		double dist_x = Math.pow(Math.abs(l2.getX() - l1.getX()), 2);
		double dist_y = Math.pow(Math.abs(l2.getY() - l1.getY()), 2);
		
		dist = Math.sqrt(dist_x + dist_y);
		
		return dist;
	}
	
	/**
	 * Getters.
	 * 
	 * Function which returns the entity a player is looking at.
	 * 
	 * @param Player
	 * @return Entity
	 */
	public Entity get_entity_lookat(Player p, int range)
	{					
		Entity entity = null;
		Collection<Entity> entity_arr;
		World world = p.getWorld();
		double distance_between_locations = 3;
		int circle_radius_at_each_location = 2;
		
		double lon = Math.toRadians(p.getLocation().getYaw());
		double lat = Math.toRadians(-p.getLocation().getPitch());
		
		double p_x = p.getLocation().getX();
		double p_y = p.getLocation().getY();
		double p_z = p.getLocation().getZ();
		
		double new_p_x = p_x;
		double new_p_y = p_y;
		double new_p_z = p_z;
		
		Location l;
		
		while(range > 0)
		{	
			new_p_x -= distance_between_locations * Math.cos(lat) * Math.sin(lon);
			new_p_y += distance_between_locations * Math.sin(lat);
			new_p_z += distance_between_locations * Math.cos(lon) * Math.cos(lat);
			
			l = new Location(world, new_p_x, new_p_y, new_p_z);
			
			entity_arr = world.getNearbyEntities(l, circle_radius_at_each_location,
					circle_radius_at_each_location, circle_radius_at_each_location);
			
			if(entity_arr.size() != 0)
			{
				if(entity_arr.iterator().hasNext())
				{
					entity = entity_arr.iterator().next();
					
					if(entity instanceof Player)
						if(entity.getUniqueId() == p.getUniqueId())
							entity = null;
					
					break;
				}
			}
			
			range--;
		}
		
		return entity;
	}
}
