package dev.sled.RPG.util;

import org.bukkit.Bukkit;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.Logger;

/**

* This class is the logger handler (keeps track of all errors)

* @version 1.0
* @author Sled
* @since 2020-4-5

*/
public class PluginLogger
{
	private Logger log = null;
	private Storage store = null;
	private DateTimeFormatter date_format = null;
	
	/**
	 * The class's constructor which initilizes the class's
	 * global variables data only once
	 */
	public PluginLogger()
	{
		if(log == null)
			log = Bukkit.getLogger();
		
		if(store == null)
			store = new Storage();
		
		if(date_format == null)
			date_format = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
	}
	
	/**
	 * Setters.
	 * 
	 * Function which writes the log data in a file
	 * with detailed error track
	 * 
	 * This one is the recommended one!
	 * 
	 * @param String, Exception 
	**/
	public void print_log(String msg, Exception exception)
	{
		if(exception == null)
			return;
		
		String output = "";
		
		output += "CAUSE: " + exception.getCause();
		
		if(exception.getCause() != null)
			output += "DETAILS: " + exception.getCause().getStackTrace()[0];
		else
			return;
		
		if(exception.getCause().getStackTrace() != null)
			output += "\nLINES: " + exception.getCause().getStackTrace()[0].getLineNumber();
		
		log(msg + "\n" + output, exception);
	}
	
	/**
	 * Setters.
	 * 
	 * Function which writes the log data in a file
	 * 
	 * @param String, Exception 
	**/
	public void log(String message, Exception e)
	{
		LocalDateTime time = LocalDateTime.now();
		
		log.warning(message);
		
		String contents = "";
		
		contents += date_format.format(time) + " ---- ";
		contents += message + " ---- ";
		contents += e;
		
		store.set_config("Logs");
		store.set_data("Log_" + System.currentTimeMillis(), contents);
	}
}
