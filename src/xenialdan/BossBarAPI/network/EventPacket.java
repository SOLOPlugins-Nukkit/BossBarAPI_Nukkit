package xenialdan.BossBarAPI.network;

import cn.nukkit.network.protocol.DataPacket;

public class EventPacket extends DataPacket{

	public static final byte NETWORK_ID = 0x3f;

	public long eid;
	public int varint1;
	public byte state;

	public int extra1;
	public int extra2;
	public int extra3;
	public int extra4;
	public int extra5;
	public int extra6;
	public int extra7;
	public int extra8;

	@Override
	public byte pid(){
		return NETWORK_ID;
	}

	@Override
	public void decode(){

	}

	@Override
	public void encode(){
		this.putVarLong(this.eid);
		this.putVarInt(this.varint1);
		this.putByte(this.state);
		switch(this.state){
			case 0:
			case 2:
				break;
			case 3:
			case 6:
				this.putVarInt(this.extra1);
				break;
			case 1:
				this.putVarInt(this.extra2);
				break;
			case 4:
				this.putLong(this.extra3);
				this.putLong(this.extra4);
				break;
			case 5:
				this.putUnsignedVarInt(this.extra5);
				this.putVarInt(this.extra6);
				break;
			case 7:
				this.putLong(this.extra7);
				this.putVarInt(this.extra8);
				break;
			default:
				break;
		}
		// 8 cases. 0,2 nothing. 3,6 . putVarInt. 1 . putVarInt. 4 . writeVarInt64(long long) writeVarInt64(long long). 5 . writeUnsignedVarInt(uint) writeVarInt(int). 7 . writeVarInt64(long long) writeVarInt(int).
	}
}