package xenialdan.BossBarAPI.task;

import cn.nukkit.scheduler.PluginTask;
import xenialdan.BossBarAPI.Main;
import xenialdan.BossBarAPI.BossBarAPI;

public class BossBarTask extends PluginTask<Main>{

	public BossBarTask(Main owner){
		super(owner);
	}

	@Override
	public void onRun(int currentTick){
		BossBarAPI.updateBossBar();
	}
}