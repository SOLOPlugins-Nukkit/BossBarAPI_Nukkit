package xenialdan.BossBarAPI;

import cn.nukkit.Player;

import java.util.HashMap;

public class BossBarAPI {

	public static HashMap<String, BossBar> bossBarList = new HashMap<String, BossBar>();

	public static BossBar registerBossBar(BossBar bossBar){
		bossBarList.put(bossBar.getOwner(), bossBar);
		bossBar.sendToAll();
		return bossBar;
	}

	public static boolean unregisterBossBar(String owner){
		if(bossBarList.containsKey(owner)){
			bossBarList.remove(owner);
			return true;
		}
		return false;
	}

	public static BossBar getBossBar(String owner){
		if(bossBarList.containsKey(owner)){
			return bossBarList.get(owner);
		}
		return null;
	}

	public static HashMap<String, BossBar> getAllBossBar(){
		return bossBarList;
	}

	public static void clearBossBar(){
		for(BossBar bs : bossBarList.values()){
			bs.setVisible(false);
		}
		bossBarList = new HashMap<String, BossBar>();
	}

	public static void updateBossBar(){
		if(bossBarList.size() == 0){
			return;
		}
		for(BossBar bossBar : bossBarList.values()){
			bossBar.onUpdate();
		}
	}

	public static void updateBossBarToPlayer(Player player){
		for(BossBar bossBar : bossBarList.values()){
			bossBar.sendTo(player);
		}
	}

	public static void removeBossBarToPlayer(Player player){
		for(BossBar bossBar : bossBarList.values()){
			PacketAPI.removeBossBar(player, bossBar.eid);
		}
	}
}