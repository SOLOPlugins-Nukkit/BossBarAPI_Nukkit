package xenialdan.BossBarAPI;

import cn.nukkit.Player;
import cn.nukkit.network.protocol.RemoveEntityPacket;
import cn.nukkit.Server;
import cn.nukkit.network.protocol.SetEntityDataPacket;
import cn.nukkit.network.protocol.UpdateAttributesPacket;
import cn.nukkit.network.protocol.AddEntityPacket;
import cn.nukkit.network.protocol.BossEventPacket;
import cn.nukkit.entity.Attribute;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.data.EntityMetadata;
import cn.nukkit.network.protocol.MoveEntityPacket;

public abstract class PacketAPI{

	public static void broadcastBossBar(int eid, String title){
		for(Player o : Server.getInstance().getOnlinePlayers().values()){
			sendBossBar(o, eid, title);
		}
	}

	public static void broadcastPercentage(int eid, double percentage){
		for(Player o : Server.getInstance().getOnlinePlayers().values()){
			sendPercentage(o, eid, percentage);
		}
	}

	public static void broadcastTitle(int eid, String title){
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
		packet.yaw = packet.pitch = 0;
		
		long flags = 0;
		flags |= 1 << Entity.DATA_FLAG_SILENT;
		flags |= 1 << Entity.DATA_FLAG_INVISIBLE;
		flags |= 1 << Entity.DATA_FLAG_NO_AI;
		
		EntityMetadata dataProperties = new EntityMetadata()
				.putLong(Entity.DATA_FLAGS, flags)
				.putShort(Entity.DATA_AIR, 400)
				.putShort(Entity.DATA_MAX_AIR, 400)
				.putLong(Entity.DATA_LEAD_HOLDER_EID, -1)
				.putFloat(Entity.DATA_SCALE, 1f)
				.putString(Entity.DATA_NAMETAG, title)
				.putInt(Entity.DATA_SCALE, 0);
		
		packet.metadata = dataProperties;
		packet.x = (float) player.x;
		packet.y = (float) (player.y - 28);
		packet.z = (float) player.z;
		player.dataPacket(packet);
		
		BossEventPacket bpk = new BossEventPacket(); // This updates the bar
		bpk.eid = eid;
		bpk.type = 0;
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
		if(percentage > 100){
			percentage = 100;
		}
		if(percentage < 0){
			percentage = 0;
		}
		UpdateAttributesPacket upk = new UpdateAttributesPacket(); // Change health of fake wither . bar progress
		upk.entityId = eid;
		Attribute attr = Attribute.getAttribute(Attribute.MAX_HEALTH);
		attr.setMaxValue(100);
		attr.setValue((float) percentage);
		upk.entries = new Attribute[]{ attr };
		player.dataPacket(upk);
		
		BossEventPacket bpk = new BossEventPacket(); // This updates the bar
		bpk.eid = eid;
		bpk.type = 1; // UPDATE
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
		
		long flags = 0;
		flags |= 1 << Entity.DATA_FLAG_SILENT;
		flags |= 1 << Entity.DATA_FLAG_INVISIBLE;
		flags |= 1 << Entity.DATA_FLAG_NO_AI;
		
		EntityMetadata dataProperties = new EntityMetadata()
				.putLong(Entity.DATA_FLAGS, flags)
				.putShort(Entity.DATA_AIR, 400)
				.putShort(Entity.DATA_MAX_AIR, 400)
				.putLong(Entity.DATA_LEAD_HOLDER_EID, -1)
				.putFloat(Entity.DATA_SCALE, 1f)
				.putString(Entity.DATA_NAMETAG, title)
				.putInt(Entity.DATA_SCALE, 0);
		
		npk.metadata = dataProperties;
		npk.eid = eid;
		player.dataPacket(npk);
		
		BossEventPacket bpk = new BossEventPacket(); // This updates the bar
		bpk.eid = eid;
		bpk.type = 1; // UPDATE
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
	 * @param Position pos
	 * @param unknown eid 
	 * @return MoveEntityPacket pk
	 */
	public static void updateMove(Player player, int eid){
		MoveEntityPacket pk = new MoveEntityPacket();
		pk.x = (float) player.x;
		pk.y = (float) (player.y - 28);
		pk.z = (float) player.z;
		pk.eid = eid;
		pk.yaw = pk.pitch = pk.headYaw = 0;
		player.dataPacket(pk);
	}
}