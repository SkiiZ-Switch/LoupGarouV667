package fr.leomelki.loupgarou.listeners;

import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerResourcePackStatusEvent;
import org.bukkit.event.player.PlayerResourcePackStatusEvent.Status;
import org.bukkit.potion.PotionEffectType;

import fr.leomelki.loupgarou.MainLg;
import fr.leomelki.loupgarou.classes.LGPlayer;
import fr.leomelki.loupgarou.events.LGPlayerKilledEvent.Reason;

public class JoinListener implements Listener {

	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		Player p = e.getPlayer();
		System.out.println("player");
		System.out.println(p.getName());
		//TODO Remake this part
		// WrapperPlayServerScoreboardTeam myTeam = new WrapperPlayServerScoreboardTeam();
		// myTeam.setName(p.getName());
		// myTeam.setPrefix(WrappedChatComponent.fromText(""));
		// myTeam.setPlayers(Arrays.asList(p.getName()));
		// myTeam.setMode(0);
		boolean noSpec = p.getGameMode() != GameMode.SPECTATOR;
		for (Player player : Bukkit.getOnlinePlayers())
			if (player != p) {
				if (player.getGameMode() != GameMode.SPECTATOR)
					player.hidePlayer(MainLg.getInstance(), p);
				// WrapperPlayServerScoreboardTeam team = new WrapperPlayServerScoreboardTeam();
				// team.setName(player.getName());
				// team.setPrefix(WrappedChatComponent.fromText(""));
				// team.setPlayers(Arrays.asList(player.getName()));
				// team.setMode(0);

				// team.sendPacket(p);
				// myTeam.sendPacket(player);
			}
		p.setFoodLevel(6);
		if (e.getJoinMessage() == null || !e.getJoinMessage().equals("joinall"))
			p.getPlayer().setResourcePack("https://download938.mediafire.com/k1mtp1fou3ugfYAuikSNfYurYLEL95wFoEdlGgP5Y3q4ub_j40zHEs6MgS_N0ZIhrftf4iYvxU_ewdxbnn6LripbG5HyvGa14ffSSB0DIoaU3rvU3BunbFtXa-2PaVyIz5JSW8tH_aMUpfseJNiX8OrezHwZoJZOxXVyejt-aZH5ng/x6rz9zlbblby0ce/loup_garou.zip");
		else {
			LGPlayer lgp = LGPlayer.thePlayer(e.getPlayer());
			lgp.showView();
			lgp.join(MainLg.getInstance().getCurrentGame());
		}
		if (noSpec)
			p.setGameMode(GameMode.ADVENTURE);
		e.setJoinMessage("");
		p.removePotionEffect(PotionEffectType.JUMP_BOOST);
		p.removePotionEffect(PotionEffectType.INVISIBILITY);
		p.setWalkSpeed(0.2f);
	}

	@EventHandler
	public void onResoucePack(PlayerResourcePackStatusEvent e) {
		System.out.println("We are here");
		if (e.getStatus() == Status.SUCCESSFULLY_LOADED) {
			Player p = e.getPlayer();
			LGPlayer lgp = LGPlayer.thePlayer(p);
			lgp.showView();
			lgp.join(MainLg.getInstance().getCurrentGame());
			if (p.hasPermission("loupgarou.admin")) {
				p.getInventory().setItem(1, new fr.leomelki.loupgarou.utils.ItemBuilder(Material.ENDER_EYE)
						.setName("Choisir les rôles").build());
				p.getInventory().setItem(3, new fr.leomelki.loupgarou.utils.ItemBuilder(Material.EMERALD)
						.setName("Lancer la partie").build());
			}
		} else if (e.getStatus() == Status.DECLINED || e.getStatus() == Status.FAILED_DOWNLOAD)
			e.getPlayer().kickPlayer(
					MainLg.getPrefix() + "§cIl vous faut le resourcepack pour jouer ! (" + e.getStatus() + ")");
	}

	@EventHandler
	public void onLeave(PlayerQuitEvent e) {
		Player p = e.getPlayer();
		LGPlayer lgp = LGPlayer.thePlayer(p);
		if (lgp.getGame() != null) {
			lgp.leaveChat();
			if (lgp.getRole() != null && !lgp.isDead())
				lgp.getGame().kill(lgp, Reason.DISCONNECTED, true);
			lgp.getGame().getInGame().remove(lgp);
			lgp.getGame().checkLeave();
		}
		LGPlayer.removePlayer(p);
		lgp.remove();
	}

}
