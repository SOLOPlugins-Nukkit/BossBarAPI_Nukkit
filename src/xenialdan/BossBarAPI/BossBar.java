package xenialdan.BossBarAPI;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.entity.Entity;

import java.util.Collection;
import java.util.HashSet;


public class BossBar{

	public int eid;
	public String owner;
	public String title;
	public int maxHealth;
	public int currentHealth;
	public boolean visible = true;

	public Long lastUpdated = System.currentTimeMillis();
	public int totalUpdate = 0;

	public int startTime = -1;
	public int endTime = -1;
	public boolean showRemainTime = true;

	public BossBar(String owner){
		this(owner, "Undefined");
	}

	public BossBar(String owner, String title){
		this(owner, title, 600);
	}

	public BossBar(String owner, String title, int currentHealth){
		this(owner, title, currentHealth, 600);
	}

	public BossBar(String owner, String title, int currentHealth, int maxHealth){
		if(maxHealth < 0){
			maxHealth = 0;
		}
		if(currentHealth > maxHealth){
			currentHealth = maxHealth;
		}
		this.owner = owner;
		this.eid = (int) Entity.entityCount++;
		this.title = title;
		this.currentHealth = currentHealth;
		this.maxHealth = maxHealth;
	}

	public void setVisible(boolean bool){
		if(bool){
			this.sendToAll();
		}else for(Player o : Server.getInstance().getOnlinePlayers().values()){
			PacketAPI.removeBossBar(o, this.eid);
		}
		this.visible = bool;
	}

	public void setHealth(int health){
		if(health > this.maxHealth){
			health = this.maxHealth;
		}
		if(health < 0){
			health = 0;
		}
		this.currentHealth = health;
		PacketAPI.broadcastPercentage(this.eid, this.currentHealth / (double) this.maxHealth * 100);
	}

	public int getHealth(){
		return this.currentHealth;
	}

	public void setMaxHealth(int maxHealth){
		if(maxHealth < 0){
			maxHealth = 0;
		}
		this.maxHealth = maxHealth;
		PacketAPI.broadcastPercentage(this.eid, this.currentHealth / (double) this.maxHealth * 100);
	}

	public int getMaxHealth(){
		return this.maxHealth;
	}

	public String getOwner(){
		return this.owner;
	}

	public void setTitle(String title){
		this.title = title;
		PacketAPI.broadcastTitle(this.eid, this.title);
	}

	public String getTitle(){
		return this.title;
	}

	public void setTimer(int second){
		this.setTimer(second, true);
	}

	public void setTimer(int second, boolean showRemainTime){
		int currentTime = (int) (System.currentTimeMillis() / 1000l);
		this.startTime = currentTime;
		this.endTime = currentTime + second;
		this.showRemainTime = showRemainTime;
		this.onUpdate();
	}

	public int getRemainingTime(){
		int ret = this.endTime - this.startTime;
		if(ret < 0){
			return 0;
		}
		return ret; 
	}












	//update interval is 10 tick
	public void onUpdate(){
		if(this.sendTimerDataToAll()){
			return;
		}
		//	if(System.currentTimeMillis() - this.lastUpdated > 2000){
		//		this.sendToAll();
		//		this.lastUpdated = System.currentTimeMillis();
		//	}
	}

	public void sendToAll(){
		if(! this.visible){
			return;
		}
		if(this.sendTimerDataToAll()){
			return;
		}
		for(Player p : Server.getInstance().getOnlinePlayers().values()){
			PacketAPI.sendBossBar(p, this.eid, this.title);
			PacketAPI.sendPercentage(p, this.eid, this.currentHealth / (double) this.maxHealth * 100);
		}
	}

	public void sendTo(Player player){
		if(! this.visible){
			return;
		}
		if(this.sendTimerDataTo(player)){
			return;
		}
		PacketAPI.sendBossBar(player, this.eid, this.title);
		PacketAPI.sendPercentage(player, this.eid, this.currentHealth / (double) this.maxHealth * 100);
		return;
	}

	protected boolean sendTimerDataToAll(){
		HashSet<Player> players = new HashSet<Player>();
		for(Player o : Server.getInstance().getOnlinePlayers().values()){
			players.add(o);
		}
		return this.sendTimerDataTo(players);
	}

	protected boolean sendTimerDataTo(Player player){
		return this.sendTimerDataTo(new HashSet<Player>(){{ add(player); }});
	}

	protected boolean sendTimerDataTo(Collection<Player> players){
		if(players.size() == 0){
			return false;
		}
		if(! this.visible){
			return false;
		}
		if(this.startTime > 0){
			double total = this.endTime - this.startTime;
			double current = this.endTime - (int) (System.currentTimeMillis() / 1000l);
			double percent = 0;
			if(total > 0){
				percent = current / total * 100;
			}

			String remain = "";
			if(this.showRemainTime){
				if(! this.title.equals("")){
					remain += " §f(";
				}
				if(current >= 0){
					if(current < 10){
						remain += "§c";
					}else if(current < 30){
						remain += "§6";
					}
					int hour = (int) current / 3600;
					current -= hour*3600;
					int minute = (int) current / 60;
					current -= minute*60;
					int second = (int) current;
					if(hour > 0){
						remain += Integer.toString(hour) + ":";
					}
					if(hour > 0 && minute < 10){
						remain += "0";
					}
					remain += Integer.toString(minute) + ":";
					if(second < 10){
						remain += "0";
					}
					remain += Integer.toString(second);
				}else{
					current = Math.abs(current);
					if(current > 5){
						this.startTime = -1;
						this.endTime = -1;
						return false;
					}else if(current % 2 == 1){
						remain += "§0";
					}else{
						remain += "§c";
					}
					remain += "0:00";
				}
				if(! this.title.equals("")){
					remain += "§f)";
				}
			}
			PacketAPI.broadcastBossBar(this.eid, this.title + remain);
			PacketAPI.broadcastPercentage(this.eid, percent);
			return true;
		}
		return false;
	}
}