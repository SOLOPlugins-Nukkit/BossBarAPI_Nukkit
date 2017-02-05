/*
 * BossBarAPI
 * A plugin by XenialDan aka thebigsmileXD
 * http://github.com/thebigsmileXD/BossBarAPI
 * Sending the Bossbar independ from the Server software
 *
 * Command and some API added by solo5star
 * porting to nukkit by solo5star
 */
package xenialdan.BossBarAPI;

import cn.nukkit.Player;
import cn.nukkit.event.Listener;
import cn.nukkit.event.EventHandler;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.event.player.PlayerPreLoginEvent;
import cn.nukkit.event.player.PlayerTeleportEvent;
import cn.nukkit.event.player.PlayerJoinEvent;
import cn.nukkit.event.player.PlayerQuitEvent;
import cn.nukkit.event.player.PlayerMoveEvent;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.math.Vector3;
import cn.nukkit.network.protocol.BossEventPacket;
import cn.nukkit.network.protocol.UpdateAttributesPacket;
import cn.nukkit.utils.Config;

import xenialdan.BossBarAPI.task.BossBarTask;

import java.util.LinkedHashMap;
import java.util.HashMap;
import java.io.File;

public class Main extends PluginBase implements Listener{

	private static Main instance = null;

	public LinkedHashMap<String, Object> hide;

	public HashMap<Player, Vector3> lastMove = new HashMap<Player, Vector3>();

	@Override
	public void onEnable(){
		this.getDataFolder().mkdirs();
		Config config = new Config(new File(this.getDataFolder(), "hide.yml"), Config.YAML);
		this.hide = (LinkedHashMap<String, Object>) config.getAll();

		Config bossBarConfig = new Config(new File(this.getDataFolder(), "bossBar.yml"), Config.YAML);
		LinkedHashMap<String, Object> bossBarData = (LinkedHashMap<String, Object>) bossBarConfig.getAll();
		for(Object obj : bossBarData.values()){
			LinkedHashMap<String, Object> dat = (LinkedHashMap<String, Object>) obj;

			BossBar bossBar = new BossBar((String) dat.get("owner"));
			bossBar.title = (String) dat.get("title");
			bossBar.maxHealth = (int) dat.get("maxHealth");
			bossBar.currentHealth = (int) dat.get("currentHealth");
			bossBar.visible = (boolean) dat.get("visible");
			bossBar.startTime = (int) dat.get("startTime");
			bossBar.endTime = (int) dat.get("endTime");
			bossBar.showRemainTime = (boolean) dat.get("showRemainTime");
			BossBarAPI.registerBossBar(bossBar);
		}

		this.getServer().getPluginManager().registerEvents(this, this);
		this.getServer().getNetwork().registerPacket(BossEventPacket.NETWORK_ID, BossEventPacket.class);
		this.getServer().getNetwork().registerPacket(UpdateAttributesPacket.NETWORK_ID, UpdateAttributesPacket.class);
		//this.getServer().getNetwork().registerPacket(SetEntityDataPacket.NETWORK_ID, SetEntityDataPacket.class);

		this.getServer().getScheduler().scheduleRepeatingTask(new BossBarTask(this), 10);
	}

	public static Main getInstance(){
		return Main.instance;
	}

	@Override
	public void onLoad(){
		Main.instance = this;
	}

	@Override
	public void onDisable(){
		this.save();
	}

	public void save(){
		Config config = new Config(new File(this.getDataFolder(), "hide.yml"), Config.YAML);
		config.setAll(this.hide);
		config.save();

		Config bossBarConfig = new Config(new File(this.getDataFolder(), "bossBar.yml"), Config.YAML);
		LinkedHashMap<String, Object> data = new LinkedHashMap<String, Object>();
		for(BossBar bossBar : BossBarAPI.getAllBossBar().values()){
			LinkedHashMap<String, Object> dat = new LinkedHashMap<String, Object>(){{
				put("owner", bossBar.owner);
				put("title", bossBar.title);
				put("maxHealth", bossBar.maxHealth);
				put("currentHealth", bossBar.currentHealth);
				put("visible", bossBar.visible);
				put("startTime", bossBar.startTime);
				put("endTime", bossBar.endTime);
				put("showRemainTime", bossBar.showRemainTime);
			}};
			data.put(bossBar.owner, dat);
		}
		bossBarConfig.setAll(data);
		bossBarConfig.save();
	}

	public void message(CommandSender sender, String msg){
		sender.sendMessage("§b§o[ 알림 ] §7" + msg);
	}

	@EventHandler
	public void onPreLogin(PlayerPreLoginEvent event){
		Player p = event.getPlayer();
		this.lastMove.put(p, new Vector3(0, 0));
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent event){
		BossBarAPI.updateBossBarToPlayer(event.getPlayer());
	}

	@EventHandler
	public void onQuit(PlayerQuitEvent event){
		this.lastMove.remove(event.getPlayer());
	}

	@EventHandler
	public void onMove(PlayerMoveEvent event){
		Player p = event.getPlayer();
		if(this.lastMove.get(p).distance(p) > 20){
			BossBarAPI.updateBossBarToPlayer(p);
			this.lastMove.put(p, new Vector3(p.x, p.y, p.z));
		}
	}

	@EventHandler
	public void onTeleport(PlayerTeleportEvent event){
		BossBarAPI.updateBossBarToPlayer(event.getPlayer());
	}

	@Override
	public boolean onCommand(CommandSender sd, Command command, String label, String[] args){
		if(command.getName().equals("보스바")){
			if(args.length == 0){
				args = new String[]{"x"};
			}
			if(!(sd instanceof Player)){
				sd.sendMessage("인게임에서만 가능합니다.");
				return true;
			}
			Player sender = (Player) sd;
			String name = sender.getName().toLowerCase();
			StringBuilder sb;

			switch(args[0]){
				case "켜기":
					if(! this.hide.containsKey(name)){
						this.message(sender, "이미 보스바가 켜져있습니다.");
						return true;
					}
					this.hide.remove(name);
					BossBarAPI.updateBossBarToPlayer(sender);
					this.message(sender, "보스바를 켰습니다.");
					return true;

				case "끄기":
					if(this.hide.containsKey(name)){
						this.message(sender, "이미 보스바가 꺼져있습니다.");
						return true;
					}
					this.hide.put(name, true);
					BossBarAPI.removeBossBarToPlayer(sender);
					this.message(sender, "보스바를 껐습니다.");
					return true;

				case "생성":
					if(sender.isOp()){
						if(args.length < 2){
							this.message(sender, "사용법 : /보스바 생성 [타이틀]");
							return true;
						}
						sb = new StringBuilder();
						for(int i = 1; i < args.length; ++i){
							sb.append(args[i]);
							if(i != args.length - 1){
								sb.append(" ");
							}
						}
						String owner;
						for(int id = 1; true; ++id){
							if(BossBarAPI.getBossBar(Integer.toString(id)) == null){
								owner = Integer.toString(id);
								break;
							}
						}
						BossBarAPI.registerBossBar(new BossBar(owner, sb.toString()));
						this.message(sender, "성공적으로 보스바를 생성하였습니다.");
						return true;
					}

				case "목록":
					if(sender.isOp()){
						this.message(sender, "====== 등록된 보스바 목록 ======");
						for(BossBar bossBar : BossBarAPI.getAllBossBar().values()){
							this.message(sender, "id : " + bossBar.getOwner() + ", 타이틀 : " + bossBar.getTitle());
						}
						return true;
					}

				case "삭제":
					if(sender.isOp()){
						if(args.length < 2){
							this.message(sender, "사용법 : /보스바 삭제 [id]");
							return true;
						}
						if(BossBarAPI.unregisterBossBar(args[1])){
							this.message(sender, "성공적으로 보스바를 삭제하였습니다.");
							return true;
						}
						this.message(sender, "해당 id의 보스바가 없습니다.");
						return true;
					}

				case "타이틀":
					if(sender.isOp()){
						if(args.length < 3){
							this.message(sender, "사용법 : /보스바 타이틀 [id] [타이틀...]");
							return true;
						}
						BossBar bossBar = BossBarAPI.getBossBar(args[1]);
						if(bossBar == null){
							this.message(sender, "해당 id의 보스바가 없습니다.");
							return true;
						}
						sb = new StringBuilder();
						for(int i = 2; i < args.length; ++i){
							sb.append(args[i]);
							if(i != args.length - 1){
								sb.append(" ");
							}
						}
						String title = sb.toString();
						bossBar.setTitle(title);
						this.message(sender, "성공적으로 타이틀을 변경하였습니다 : " + title);
						return true;
					}


				case "체력설정":
					if(sender.isOp()){
						if(args.length < 3){
							this.message(sender, "사용법 : /보스바 체력설정 [id] [퍼센트(1~100)]");
							return true;
						}
						int percent;
						try{
							percent = Integer.parseInt(args[2]);
						}catch(Exception e){
							this.message(sender, "사용법 : /보스바 체력설정 [id] [퍼센트(1~100)]");
							return true;
						}
						BossBar bossBar = BossBarAPI.getBossBar(args[1]);
						if(bossBar == null){
							this.message(sender, "해당 id의 보스바가 없습니다.");
							return true;
						}
						bossBar.setHealth(bossBar.getMaxHealth() * percent / 100);
						this.message(sender, "성공적으로 체력을 변경하였습니다.");
						return true;
					}


				case "타이머":
					if(sender.isOp()){
						if(args.length < 3){
							this.message(sender, "사용법 : /보스바 타이머 [id] [시간(단위:초)]");
							return true;
						}
						int sec;
						try{
							sec = Integer.parseInt(args[2]);
						}catch(Exception e){
							this.message(sender, "사용법 : /보스바 타이머 [id] [시간(단위:초)]");
							return true;
						}
						BossBar bossBar = BossBarAPI.getBossBar(args[1]);
						if(bossBar == null){
							this.message(sender, "해당 id의 보스바가 없습니다.");
							return true;
						}
						bossBar.setTimer(sec);
						this.message(sender, "성공적으로 타이머를 설정하였습니다.");
						return true;
					}

				default:
					this.message(sender, "/보스바 [켜기/끄기]");
					if(sender.isOp()){
						this.message(sender, "/보스바 생성 [타이틀] - 해당 타이틀로 보스바를 생성합니다.");
						this.message(sender, "/보스바 목록 - 등록된 보스바 목록을 봅니다.");
						this.message(sender, "/보스바 삭제 [id] - 해당 보스바를 삭제합니다.");
						this.message(sender, "/보스바 타이틀 [id] [타이틀] - 보스바 타이틀을 설정합니다.");
						this.message(sender, "/보스바 체력설정 [id] [퍼센트(0~100)] - 보스바의 체력을 설정합니다.");
						this.message(sender, "/보스바 타이머 [id] [시간(단위:초)] - 타이머를 설정합니다.");
					}
					return true;
			}
		}
		return true;
	}
}