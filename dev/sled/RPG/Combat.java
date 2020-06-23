package dev.sled.RPG;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.entity.Player;

import dev.sled.RPG.util.UtilityThreads;

public class Combat
{
	private static Map<UUID, Map<Player, Double>> entity_attackers = null;
	private static Map<UUID, Double> total_entity_damage_taken = null;
	
	public Combat()
	{
		if(entity_attackers == null)
			entity_attackers = new HashMap<UUID, Map<Player, Double>>();
		
		if(total_entity_damage_taken == null)
			total_entity_damage_taken = new HashMap<UUID, Double>();
	}
	
	public void set_attacker(UUID id, Player player)
	{
		Map<Player, Double> check = null;
		
		check = entity_attackers.putIfAbsent(id, new HashMap<Player, Double>());
		total_entity_damage_taken.putIfAbsent(id, 0.0);
		
		entity_attackers.get(id).putIfAbsent(player, 0.0);
		
		if(check == null)
		{
			UtilityThreads combat_clear_thread = new UtilityThreads(id);
			combat_clear_thread.start_thread(combat_clear_thread.combat_data_thread, 
					combat_clear_thread.combat_data_runnable);
		}
	}
	
	public void set_attacker_damage_dealt(UUID id, Player player, double value)
	{
		entity_attackers.get(id).put(player, value);
		total_entity_damage_taken.put(id, total_entity_damage_taken.get(id) + value);
	}
	
	public Map<Player, Double> get_all_attackers(UUID id)
	{
		return entity_attackers.get(id);
	}
	
	public double get_attacker_damage_dealt(UUID id, Player player)
	{
		return entity_attackers.get(id).get(player);
	}
	
	public double get_total_entity_damage_taken(UUID id)
	{
		if(!total_entity_damage_taken.containsKey(id))
			return 0.0;
		
		return total_entity_damage_taken.get(id);
	}
	
	public void remove_attacked_entity(UUID id)
	{
		if(entity_attackers.containsKey(id))
			entity_attackers.remove(id);
		
		if(total_entity_damage_taken.containsKey(id))
			total_entity_damage_taken.remove(id);
	}
}
