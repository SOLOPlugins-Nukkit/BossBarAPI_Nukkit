package xenialdan.BossBarAPI.network;

import cn.nukkit.network.protocol.DataPacket;
import cn.nukkit.entity.Attribute;

public class UpdateAttributesPacket extends DataPacket{

	public static final byte NETWORK_ID = 0x1f;

	public long eid;
	
	/** @var BossBarValues[] */
	public BossBarValues[] entries = new BossBarValues[]{};

	@Override
	public byte pid(){
		return NETWORK_ID;
	}

	@Override
	public void decode(){

	}

	@Override
	public void encode(){
		this.reset();

		this.putVarLong(this.eid);
		this.putUnsignedVarInt(this.entries.length);
		for(BossBarValues entry : this.entries){
			this.putLFloat(entry.getMinValue());
			this.putLFloat(entry.getMaxValue());
			this.putLFloat(entry.getValue());
			this.putLFloat(entry.getDefaultValue());
			this.putString(entry.getName());
		}
	}
}