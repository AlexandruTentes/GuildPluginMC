package dev.sled.RPG;

import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

import dev.sled.RPG.util.PluginLogger;
import dev.sled.RPG.util.Storage;
import dev.sled.RPG.util.UtilityThreads;
import dev.sled.RPG.util.VaultFunctions;

/**

* This class links all the subclasses needed for the whole plugin 
* creating the final product.

* @version 1.0
* @author Sled
* @since 2019-11-23

*/
@SuppressWarnings("unused")
public class Main extends JavaPlugin
{
	//Global variables
	
	private Storage store = null; // Initialize the storage
	private BukkitScheduler scheduler = null; //Initialize a scheduler
	private PluginManager pm = null; //Initialize a plugin manager
	private PlayerLoadData pld = null; //Initialize the player load data logic
	private VaultFunctions vault = null; //Initialize the vault object
	private Combat c = null; // Initialize the combat logic 
	private UtilityThreads threads = null; // Initialize the utility threads
	private PluginLogger log = null; // Initialize the logger
   
	/**
	 * The main class's constructor with no args (this gets auto loaded at server startup), 
	 * which instantiates any needed variables among the global ones.
	 */
	public Main()
	{
		scheduler = Bukkit.getScheduler(); //instantiate bukkit scheduler
		pm = Bukkit.getPluginManager(); //Get the plugin
	}
	
	/**
	 *  Setters.
	 *  
	 *  Function used to load all the initial required data
	 */
	private void initial_load()
	{				
		//Initializations
		store = new Storage(this);
		pld = new PlayerLoadData();
		c = new Combat();
		vault = new VaultFunctions();
		
		// Loaders
		store.load_config();
		
		// Threads initialization
		threads = new UtilityThreads();
		
		//Register events
		getServer().getPluginManager().registerEvents(new Events(), this); //register the events with this plugin
		
		// Running threads
		threads.start_thread(UtilityThreads.auto_save_thread, threads.auto_save_runnable);
		threads.start_thread(UtilityThreads.garbage_collector_thread, threads.garbage_collector_runnable);
		
		// Loading the config.yml data
		store.load_configuration();
		
		// Message printing
		Bukkit.getLogger().warning("Guild plugin has been loaded!");
		
		// Error handling
		vault.get_vault_load_errors().forEach((element) -> log.log("initial_load() error inside Main.java", new Exception(element)));
	}
	
	/**
	 *  Plugin load function
	 */
	@Override
	public void onLoad()
	{}
	
	/**
	 *  Happens after plugin load
	 */
	@Override
	public void onEnable()
	{
		initial_load();
	}
	
	/**
	 *  Happens at server shutdown
	 */
	@Override
	public void onDisable()
	{
		if(Storage.start_plugin_leveling_system_status)
			store.save_all_data();
		
		threads.stop_thread(UtilityThreads.auto_save_thread);
		threads.stop_thread(UtilityThreads.garbage_collector_thread);
		threads.stop_players_threads();
	}
}
