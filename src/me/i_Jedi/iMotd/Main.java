package me.i_Jedi.iMotd;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerListPingEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Main extends JavaPlugin implements Listener {

    //Variables
    private String topLine, bottomLine;
    private List<String> randomList = new ArrayList<>();
    private Boolean pluginEnabled = false;
    private Boolean randomEnabled = false;

    //Enabled
    @Override
    public void onEnable(){
        //Default config
        saveDefaultConfig();
        getStartInfo();

        if(pluginEnabled){
            //Register events
            getServer().getPluginManager().registerEvents(this, this);
            getLogger().info("iMotd has been enabled!");
        }else{
            getLogger().info("iMotd was NOT enabled!");
        }
    }

    //Disabled
    @Override
    public void onDisable(){
        //Check if enabled in config
        if(pluginEnabled){
            getLogger().info("iMotd has been disabled!");
        }
    }

    //Ping event
    @EventHandler
    public void pingEvent(ServerListPingEvent event){
        String motd;
        //Check for random bottom line
        if(!randomEnabled){
            motd = ChatColor.translateAlternateColorCodes('$', topLine + "\n" + bottomLine);
        }else{
            //Get random bottom line
            Random random = new Random();
            int x = random.nextInt(randomList.size());
            motd = ChatColor.translateAlternateColorCodes('$', topLine + "\n" + randomList.get(x));
        }
        event.setMotd(motd);
    }

    //Command
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        //Store command and check it
        String cmd = command.getName().toUpperCase();
        if(cmd.equals("IMOTD")){
            //Check for perms
            if(sender instanceof Player){
                Player player = (Player) sender;
                if(!player.hasPermission("imotd.reload")){
                    player.sendMessage(ChatColor.RED + "You do not have permission to use this command.");
                    return true;
                }
            }

            //Check args
            if(args.length == 1){
                //Store and check args
                String arg = args[0].toUpperCase();
                if(arg.equals("RELOAD")){
                    //Reload and tell sender
                    this.reloadConfig();
                    getStartInfo();
                    sender.sendMessage(ChatColor.GOLD + "" + ChatColor.BOLD + "[iMotd] " + ChatColor.RED + "The config has been reloaded.");
                }
            }
        }
        return true;
    }

    //Get startup info
    public void getStartInfo(){
        //Check if the plugin is enabled
        if(getConfig().getBoolean("enabled")){
            pluginEnabled = true;

            //Get top line
            topLine = getConfig().getString("topLine.message");

            //Figure out if bottom line is random or not
            if(getConfig().getBoolean("bottomLine.enableRandom")){
                //Get list
                randomList = getConfig().getStringList("bottomLine.randomMessages");
                randomEnabled = true;
                return;
            }

            //Get bottom line
            bottomLine = getConfig().getString("bottomLine.message");
            randomEnabled = false;

        }else{
            pluginEnabled = false;
            randomEnabled = false;
        }
    }
}
