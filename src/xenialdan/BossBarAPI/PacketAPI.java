package xenialdan.BossBarAPI;

import cn.nukkit.Player;
import cn.nukkit.network.protocol.DataPacket;
import cn.nukkit.network.protocol.RemoveEntityPacket;
import cn.nukkit.Server;
import cn.nukkit.network.protocol.SetEntityDataPacket;
import cn.nukkit.network.protocol.AddEntityPacket;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.data.EntityMetadata;
import cn.nukkit.network.protocol.MoveEntityPacket;
import cn.nukkit.level.Location;

import xenialdan.BossBarAPI.network.BossBarValues;
import xenialdan.BossBarAPI.network.BossEventPacket;
import xenialdan.BossBarAPI.network.EventPacket;
import xenialdan.BossBarAPI.network.UpdateAttributesPacket;

import java.util.Collection;
import java.util.HashSet;

public abstract class PacketAPI{

	public static void broadcastBossBar(int eid, String title){
		HashSet<Player> players = new HashSet<Player>();
		for(Player o : Server.getInstance().getOnlinePlayers().values()){
			sendBossBar(o, eid, title);
		}
	}

	public static void broadcastPercentage(int eid, double percentage){
		HashSet<Player> players = new HashSet<Player>();
		for(Player o : Server.getInstance().getOnlinePlayers().values()){
			sendPercentage(o, eid, percentage);
		}
	}

	public static void broadcastTitle(int eid, String title){
		HashSet<Player> players = new HashSet<Player>();
		for(Player o : Server.getInstance().getOnlinePlayers().values()){
			sendTitle(o, eid, title);
		}
	}

	/**
	 * Sends the text to one player
	 *
	 * @param Player players
	 * To who to send
	 * @param int eid
	 * The EID of an existing fake wither
	 * @param String title
	 * The title of the boss bar
	 * @param null|int ticks
	 * How long it displays
	 */
	public static void sendBossBar(Player player, int eid, String title){
		if(title.equals("")){
			return;
		}
		if(Main.getInstance().hide.containsKey(player.getName().toLowerCase())){
			return;
		}
		AddEntityPacket packet = new AddEntityPacket();
		packet.entityUniqueId = eid;
		packet.entityRuntimeId = eid;
		packet.type = 52;
		packet.yaw = 0;
		packet.pitch = 0;
		EntityMetadata dataProperties= new EntityMetadata();
		dataProperties.putLong(Entity.DATA_LEAD_HOLDER_EID, -1);
		dataProperties.putLong(Entity.DATA_FLAGS, 0 ^ 1 << Entity.DATA_FLAG_SILENT ^ 1 << Entity.DATA_FLAG_INVISIBLE ^ 1 << Entity.DATA_FLAG_NO_AI);
		dataProperties.putFloat(Entity.DATA_SCALE, 0);
		dataProperties.putString(Entity.DATA_NAMETAG, title);
		dataProperties.putFloat(Entity.DATA_BOUNDING_BOX_WIDTH, 0);
		dataProperties.putFloat(Entity.DATA_BOUNDING_BOX_HEIGHT, 0);
		packet.metadata = dataProperties;
		packet.x = (float) player.x;
		packet.y = (float) (player.y - 28);
		packet.z = (float) player.z;
		player.dataPacket(packet);
		
		BossEventPacket bpk = new BossEventPacket(); // This updates the bar
		bpk.eid = eid;
		bpk.state = 0;
		player.dataPacket(bpk);
	}

	/**
	 * Sets how many % the bar is full by EID
	 *
	 * @param double percentage
	 * 0-100
	 * @param int eid 
	 */
	public static void sendPercentage(Player player, int eid, double percentage){
		UpdateAttributesPacket upk = new UpdateAttributesPacket(); // Change health of fake wither . bar progress
		if(percentage > 100){
			percentage = 100;
		}
		if(percentage < 0){
			percentage = 0;
		}
		BossBarValues newV = new BossBarValues(0, 600, (float) (percentage * 6), "minecraft:health"); // Ensures that the number is between 0 and 100;
		BossBarValues[] newEntries;
		if(upk.entries.length > 0){
			int c = 0;
			newEntries = new BossBarValues[upk.entries.length];
			for(BossBarValues v : upk.entries){
				newEntries[c++] = v;
			}
			newEntries[c++] = newV;
		}else{
			newEntries = new BossBarValues[1];
			newEntries[0] = newV;
		}
		upk.entries = newEntries;
		upk.eid = eid;
		player.dataPacket(upk);
		
		BossEventPacket bpk = new BossEventPacket(); // This updates the bar
		bpk.eid = eid;
		bpk.state = 0;
		player.dataPacket(bpk);
	}

	/**
	 * Sets the BossBar title by EID
	 *
	 * @param String title 
	 * @param int eid 
	 */
	public static void sendTitle(Player player, int eid, String title){
		SetEntityDataPacket npk = new SetEntityDataPacket(); // change name of fake wither . bar text
		EntityMetadata dataProperties= new EntityMetadata();
		dataProperties.putString(Entity.DATA_NAMETAG, title);
		npk.metadata = dataProperties;
		npk.eid = eid;
		player.dataPacket(npk);
		
		BossEventPacket bpk = new BossEventPacket(); // This updates the bar
		bpk.eid = eid;
		bpk.state = 0;
		player.dataPacket(bpk);
	}

	/**
	 * Remove BossBar from players by EID
	 *
	 * @param Player[] players 
	 * @param int eid 
	 * @return boolean removed
	 */
	public static boolean removeBossBar(Player player, int eid){
		RemoveEntityPacket pk = new RemoveEntityPacket();
		pk.eid = eid;
		player.dataPacket(pk);
		return true;
	}

	/**
	 * Handle player movement
	 *
	 * @param Location pos
	 * @param unknown eid 
	 * @return MoveEntityPacket pk
	 */
	public static DataPacket playerMove(Location pos, int eid){
		MoveEntityPacket pk = new MoveEntityPacket();
		pk.x = (float) pos.x;
		pk.y = (float) (pos.y - 28);
		pk.z = (float) pos.z;
		pk.eid = eid;
		pk.yaw = pk.pitch = pk.headYaw = 0;
		return pk.clone();
	}
}