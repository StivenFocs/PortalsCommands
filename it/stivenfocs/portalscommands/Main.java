package it.stivenfocs.portalcommands;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.Material;
import org.bukkit.Location;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.event.block.Action;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.*;

public class Main extends JavaPlugin implements Listener {
	
	public void onEnable() {
		reload();
		
		getCommand("portalscommands").setExecutor(this);
		Bukkit.getPluginManager().registerEvents(this, this);
	}
	
	public void reload() {
		reloadConfig();
		
		if (getConfig().get("portals") == null) {
			getConfig().createSection("portals");
		}
		
		saveConfig();
		reloadConfig();
	}
	
	public ItemStack getWand() {
		ItemStack wand = new ItemStack(Material.STICK);
		
		ItemMeta wandMeta = wand.getItemMeta();
		wandMeta.setDisplayName("§aPortalsCommands Wand");
		wand.setItemMeta(wandMeta);
		
		return wand;
	}
	
	public boolean isInArea(Location location, Location pos1, Location pos2) {
		if (location.getWorld().getName().equals(pos1.getWorld().getName())) {
			if (location.getBlockX() >= Math.min(pos1.getBlockX(), pos2.getBlockX()) && location.getBlockX() <= Math.max(pos1.getBlockX(), pos2.getBlockX())) {
				if (location.getBlockY() >= Math.min(pos1.getBlockY(), pos2.getBlockY()) && location.getBlockY() <= Math.max(pos1.getBlockY(), pos2.getBlockY())) {
					if (location.getBlockZ() >= Math.min(pos1.getBlockZ(), pos2.getBlockZ()) && location.getBlockZ() <= Math.max(pos1.getBlockZ(), pos2.getBlockZ())) {
						return true;
					}
				}
			}
		}
		
		return false;
	}
	
	public String locationToString(Location location, Boolean isBlock) {
		if (isBlock) return location.getWorld().getName() + "," + location.getBlockX() + "," + location.getBlockY() + "," + location.getBlockZ();
		return location.getWorld().getName() + "," + location.getX() + "," + location.getY() + "," + location.getZ() + "," + location.getYaw() + "," + location.getPitch();
	}
	
	public Location stringToLocation(String location_string) {
		String[] location_split = location_string.split(",");
		if (location_split.length > 4) return new Location(Bukkit.getWorld(location_split[0]), Double.parseDouble(location_split[1]), Double.parseDouble(location_split[2]), Double.parseDouble(location_split[3]), Float.parseFloat(location_split[4]), Float.parseFloat(location_split[5]));
		return new Location(Bukkit.getWorld(location_split[0]), Double.parseDouble(location_split[1]), Double.parseDouble(location_split[2]), Double.parseDouble(location_split[3]));
	}
	
	public static boolean isDigit(String digit) {
		try {
			Integer.parseInt(digit);
			return true;
		} catch (Exception ignored) {}
		return false;
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (sender.hasPermission("portalscommands.admin")) {
			if (args.length == 0) {
				sender.sendMessage("§ePlugin sviluppato da StivenFocs");
				sender.sendMessage("§7/portalscommands reload");
				sender.sendMessage("§7/portalscommands wand");
				sender.sendMessage("§7/portalscommands setportal <nome>");
				sender.sendMessage("§7/portalscommands commands <nome> <add/list/remove> [comando/posizione comando]");
				sender.sendMessage("§7/portalscommands listportals");
				sender.sendMessage("§7/portalscommands deleteportal <nome>");
			} else if (args[0].equalsIgnoreCase("reload")) {
				reload();
				sender.sendMessage("§aConfigurazione ricaricata");
			} else if (args[0].equalsIgnoreCase("wand")) {
				if (sender instanceof Player) {
					Player p = (Player) sender;
					p.getInventory().addItem(getWand());
					sender.sendMessage("§aHai ricevuto la wand di PortalsCommands");
				} else sender.sendMessage("§aLa console non può eseguire questo comando!");
			} else if (args[0].equalsIgnoreCase("setportal")) {
				if (args.length > 1) {
					String portalName = args[1];
					if (sender instanceof Player) {
						Player p = (Player) sender;
						
						if (pos1.containsKey(p.getUniqueId()) && pos2.containsKey(p.getUniqueId()) && pos1.get(p.getUniqueId()).getWorld().equals(pos2.get(p.getUniqueId()).getWorld())) {
							getConfig().set("portals." + portalName + ".pos1", locationToString(pos1.get(p.getUniqueId()), true));
							getConfig().set("portals." + portalName + ".pos2", locationToString(pos2.get(p.getUniqueId()), true));
							if (getConfig().get("portals." + portalName + ".commands") == null) getConfig().set("portals." + portalName + ".commands", new ArrayList<String>());
							saveConfig();
							sender.sendMessage("§aPosizioni impostate al portale §f" + portalName + "\n§7Apri il file di configurazione per configurare i comandi.");
						} else {
							sender.sendMessage("§cLe due posizioni non sono valide, usa la wand per definirle.");
						}
					} else {
						sender.sendMessage("§aLa console non può eseguire questo comando!");
					}
				} else {
					sender.sendMessage("§cDevi inserire il nome del portale a cui impostare le posizioni");
				}
			} else if (args[0].equalsIgnoreCase("commands")) {
				if (args.length > 1) {
					String portalName = args[1];
					
					if (getConfig().get("portals." + portalName + ".commands") == null) {
						getConfig().set("portals." + portalName + ".commands", new ArrayList<String>());
						reloadConfig();
					}
					List<String> commands = getConfig().getStringList("portals." + portalName + ".commands");
					
					if (args.length > 2) {
						if (args[2].equalsIgnoreCase("add")) {
							if (args.length > 3) {
								String commandToAdd = "";
								int x = 0;
								for (String arg : args) {
									if (x <= 2) {
										x++;
										continue;
									}
									
									commandToAdd = commandToAdd == "" ? arg : commandToAdd + " " + arg;
								}
								
								commands.add(commandToAdd);
								getConfig().set("portals." + portalName + ".commands", commands);
								saveConfig();
								sender.sendMessage("§aComando aggiunto alla lista dei comandi del portale §f" + portalName);
							} else sender.sendMessage("§cDevi inserire il comando da aggiungere alla lista");
						} else if (args[2].equalsIgnoreCase("list")) {
							if (commands.size() > 0) {
								sender.sendMessage("§eLista dei comandi del portale §f" + portalName);
								for (int x = 0; x < commands.size(); x++) {
									sender.sendMessage("§6[" + x + "] " + commands.get(x));
								}
							} else sender.sendMessage("§eQuesto portale non ha alcun comando configurato.");
						} else if (args[2].equalsIgnoreCase("remove")) {
							if (args.length > 3) {
								if (isDigit(args[3])) {
									Integer index = Integer.parseInt(args[3]);
									if ((commands.size() - 1) >= index) {
										String removedCommand = commands.get(index);
										commands.remove(commands.get(index));
										getConfig().set("portals." + portalName + ".commands", commands);
										saveConfig();
										sender.sendMessage("§eComando rimosso:\n§7" + removedCommand);
									} else sender.sendMessage("§cNon c'è nessun comando in quella posizione");
								} else sender.sendMessage("§cIl numero inserito non è valido");
							} else sender.sendMessage("§cInserisci la posizione del comando da rimuovere.\n§cSfrutta la lista per sapere il numero della poszione del comando che vuoi rimuovere.");
						} else sender.sendMessage("§4Azione non riconosciuta!\n§cInserisci una di queste tre scelte: add, list, remove");
					} else sender.sendMessage("§cInserisci una di queste tre scelte: add, list, remove");
				} else sender.sendMessage("§cDevi inserire il nome di un portale");
			} else if (args[0].equalsIgnoreCase("listportals")) {
				List<String> portalsNames = new ArrayList<>(getConfig().getConfigurationSection("portals").getKeys(false));
				if (portalsNames.size() > 0) {
					for (String portalName : portalsNames) {
						sender.sendMessage("§a" + portalName);
					}
				} else sender.sendMessage("§cNessun portale configurato");
			} else if (args[0].equalsIgnoreCase("deleteportal")) {
				if (args.length > 1) {
					String portalName = args[1];
					if (getConfig().get("portals." + portalName) != null) {
						getConfig().set("portals." + portalName, null);
						saveConfig();
						sender.sendMessage("§ePortale cancellato: §f" + portalName);
					} else sender.sendMessage("§cNon esiste alcun portale con quel nome");
				} else {
					sender.sendMessage("§cInserisci il nome del portale da rimuovere");
				}
			} else {
				sender.sendMessage("§ePlugin sviluppato da StivenFocs");
				sender.sendMessage("§7/portalscommands reload");
				sender.sendMessage("§7/portalscommands wand");
				sender.sendMessage("§7/portalscommands setportal <nome>");
				sender.sendMessage("§7/portalscommands commands <nome> <add/list/remove> [comando/posizione comando]");
				sender.sendMessage("§7/portalscommands listportals");
				sender.sendMessage("§7/portalscommands deleteportal <nome>");
			}
		} else {
			sender.sendMessage("§cNon hai il permesso di usare questo comando.");
		}
		
		return true;
	}
	
	HashMap<UUID, List<String>> playersPortalsIn = new HashMap();
	
	@EventHandler
	public void onPlayerMove(org.bukkit.event.player.PlayerMoveEvent event) {
		Player p = event.getPlayer();
		
		if (!playersPortalsIn.containsKey(p.getUniqueId())) {
			playersPortalsIn.put(p.getUniqueId(), new ArrayList<String>());
		}
		
		for (String portalName : getConfig().getConfigurationSection("portals").getKeys(false)) {
			try {
				Location pos1 = stringToLocation(getConfig().getString("portals." + portalName + ".pos1"));
				Location pos2 = stringToLocation(getConfig().getString("portals." + portalName + ".pos2"));
				
				if (isInArea(p.getLocation(), pos1, pos2)) {
					if (!playersPortalsIn.get(p.getUniqueId()).contains(portalName)) {
						playersPortalsIn.get(p.getUniqueId()).add(portalName);
						
						for (String command : getConfig().getStringList("portals." + portalName + ".commands")) {
							if (command.startsWith("console:")) Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command.replace("%player%", p.getName()).replace("console:", ""));
							else Bukkit.dispatchCommand(p, command.replace("%player%", p.getName()));
						}
					}
				} else {
					playersPortalsIn.get(p.getUniqueId()).remove(portalName);
				}
				
			} catch (Exception ignored) {}
		}
	}
	
	HashMap<UUID, Location> pos1 = new HashMap();
	HashMap<UUID, Location> pos2 = new HashMap();
	
	@EventHandler
	public void onPlayerInteract(org.bukkit.event.player.PlayerInteractEvent event) {
		Player p = event.getPlayer();
		ItemStack item = event.getItem();
		
		if (item != null && item.isSimilar(getWand()) && p.hasPermission("portalscommands.admin")) {
			event.setCancelled(true);
			
			if (event.getAction().equals(Action.LEFT_CLICK_BLOCK)) {
				pos1.put(p.getUniqueId(), event.getClickedBlock().getLocation());
				p.sendMessage("§aPos1 impostata");
			} else if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
				pos2.put(p.getUniqueId(), event.getClickedBlock().getLocation());
				p.sendMessage("§aPos2 impostata");
			}
		}
	}
	
}