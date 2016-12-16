package xenialdan.BossBarAPI.network;

import cn.nukkit.network.protocol.DataPacket;

public class BossEventPacket extends DataPacket{

	public static final byte NETWORK_ID = 0x4a;

	public long eid;
	public int state;


	@Override
	public byte pid(){
		return NETWORK_ID;
	}

	@Override
	public void decode(){
		//this.eid = this.getUUID();
		//this.state = this.getUnsignedVarInt();

		// this.ka2 = this.getString();
		// this.ka3 = this.getFloat();
		// this.ka4 = this.getShort();
		// this.ka5 = this.getUnsignedVarInt();
		// print ka2 . '|' . ka3 . '|' . ka4 . '|' . ka5 . '.n';
		// print '|' . this.eid . '|' . this.state . '.n';
	}

	@Override
	public void encode(){
		this.reset();
		this.putVarLong(this.eid);
		this.putUnsignedVarInt(this.state);
	}
}