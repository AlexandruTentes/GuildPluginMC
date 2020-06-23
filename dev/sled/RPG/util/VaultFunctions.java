package dev.sled.RPG.util;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.plugin.RegisteredServiceProvider;

import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import net.milkbowl.vault.permission.Permission;

/**
* The class handles the Vault functions for setters and getters 
* of vault permissions, chat and economy.
*
* @version 1.0
* @author Sled
* @since 2019-11-23
*/
public class VaultFunctions
{
	//Global variables
	private static Permission permission = null; //Initialize permissions
	private static Economy economy = null; //Initialize economy
	private static Chat chat = null; //Initialize chat
	private static ArrayList<String> ERROR = new ArrayList<String>(); //Initialize error messages
	private Player player;
    
	/**
	 * This is the class constructor which will 
	 * load vault's permissions, economy and chat.
	 * 
	 * It will also set any error messages.
	 * 
	 * Use the getter functions provided 
	 * (get_permission(), get_economy(), etc)
	 */
    public VaultFunctions()
    {    	   	
    	if(!setup_permissions())
    		ERROR.add("Cannot load permissions!");
    	
    	if(!setup_chat())
    		ERROR.add("Cannot load chat");
    	
    	if(!setup_economy())
    		ERROR.add("Cannot load economy");
    }	

    /**
     * Functions which loads the Vault's permissions 
     * Returns true if successful, false otherwise.
     * 
     * @return boolean
     */
    private boolean setup_permissions()
    {
    	if (Bukkit.getServer().getPluginManager().getPlugin("Vault") == null)
            return false;
    	
        RegisteredServiceProvider<Permission> permissionProvider = Bukkit.getServer().getServicesManager().getRegistration(net.milkbowl.vault.permission.Permission.class);
        
        if (permissionProvider != null)
            permission = permissionProvider.getProvider();
        
        return (permission != null);
    }

    /**
     * Functions which loads the Vault's chat 
     * Returns true if successful, false otherwise.
     * 
     * @return boolean
     */
    public boolean setup_chat()
    {
    	if (Bukkit.getServer().getPluginManager().getPlugin("Vault") == null)
            return false;
    	
        RegisteredServiceProvider<Chat> chatProvider = Bukkit.getServer().getServicesManager().getRegistration(net.milkbowl.vault.chat.Chat.class);
        
        if (chatProvider != null)
            chat = chatProvider.getProvider();

        return (chat != null);
    }
    
    /**
     * Functions which loads the Vault's economy 
     * Returns true if successful, false otherwise.
     * 
     * @return boolean
     */
    public boolean setup_economy()
    {
    	if (Bukkit.getServer().getPluginManager().getPlugin("Vault") == null)
            return false;
    	
        RegisteredServiceProvider<Economy> economyProvider = Bukkit.getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
        
        if (economyProvider != null)
            economy = economyProvider.getProvider();

        return (economy != null);
    }
    
    /**
     * Setters.
     * 
     * Function which sets the player. Eventually used to get this player's vault data
     * 
     * @param player
     */
    public void set_player(Player player)
    {
    	this.player = player;
    }
    
    /**
     * Getters.
     * 
     * Function which returns the Vault's permissions loaded from the server.
     * 
     * @return Permission
     */
    public Permission get_permission()
    {
    	return permission;
    }
    
    /**
     * Getters.
     * 
     * Function which returns the Vault's economy loaded from the server.
     * 
     * @return Economy
     */
    public Economy get_economy()
    {
    	return economy;
    }
    
    /**
     * Getters.
     * 
     * Function which returns the Vault's chat loaded from the server.
     * 
     * @return Chat
     */
    public Chat get_chat()
    {
    	return chat;
    }
    
    /**
     * Getters.
     * 
     * Function which returns the error messages as an array.
     * 
     * @return ArrayList<String>
     */
    public ArrayList<String> get_vault_load_errors()
    {
    	return ERROR;
    }
    
    /**
     * Getters.
     * 
     * Function which returns a string array of a player's groups
     * 
     * @param player
     * @return String []
     */
    public String [] get_player_groups()
    {
    	return permission.getPlayerGroups(player);
    }
    
    /**
     * Getters.
     * 
     * Function which helps print the data structure.
     */
    public String toString()
    {
    	String output = "|GROUPS| -> ";
    	String [] player_groups = get_player_groups();
    	
    	for(int i = 0; i < player_groups.length; i++)
    		output += player_groups[i] + ", ";
    	
    	output += ( player_groups.length == 0 ? "NONE;" : ";");
    	
    	return output;
    }
    
    /**
	 * Event
	 * 
	 * Function which is called by a player command message event
	 * Used to print and test this class
	 * 
	 * @param event
	 */
	@SuppressWarnings("deprecation")
	public void on_player_message(PlayerCommandPreprocessEvent event)
	{
		Player sender = event.getPlayer();
		String [] command = event.getMessage().split(" ");
		
		if(!(sender instanceof Player))
			return;
		
		Player player = (Player) sender;
		
		if(command[0].equals("/gp_test_economy"))
		{
			if(!sender.hasPermission("gp.op.permission"))
				return;
			
			if(economy == null)
			{
				sender.sendMessage("No economy found!");
				return;
			}

	        sender.sendMessage(String.format("You have %s", economy.format(economy.getBalance(player.getName()))));
	        EconomyResponse res = economy.depositPlayer(player, 1.05);
	        
	        if(res.transactionSuccess())
	        {
	            sender.sendMessage(String.format("You were given %s and now have %s", economy.format(res.amount), economy.format(res.balance)));
	            sender.sendMessage("Removing the sum!");
	            
	            res = economy.withdrawPlayer(player, res.amount);
	            
	            sender.sendMessage(String.format("Money withdrawn! Current balance: %s", economy.format(res.balance)));
	        }
	        
	        else
	            sender.sendMessage(String.format("An error occured: %s", res.errorMessage));
	    }
		else if(command[0].equals("/gp_test_permission"))
		{
			if(!sender.hasPermission("gp.op.permission"))
				return;
			
			if(permission == null)
			{
				sender.sendMessage("No permission found!");
				return;
			}
			
			if(command.length != 2)
			{
				sender.sendMessage("Exactly 1 argument is needed!");
				return;
			}

	        if(permission.playerHas(player, command[1]))
	            sender.sendMessage("You have the permission!");
	        
	        else
	            sender.sendMessage("No permisison found!");
	    }
	}
}