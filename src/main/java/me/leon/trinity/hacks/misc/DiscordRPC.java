package me.leon.trinity.hacks.misc;

import me.leon.trinity.hacks.Category;
import me.leon.trinity.hacks.Module;
import me.leon.trinity.main.RPCHandler;
import me.leon.trinity.utils.misc.MessageBus;

public class DiscordRPC extends Module {
	public DiscordRPC() {
		super("DiscordRPC", "Displays a custom Discord Rich Presence", Category.MISC);
	}

	@Override
	public void onEnable() {
		if (!nullCheck())
			MessageBus.sendClientMessage("Discord Rich Presence started!", true);
		RPCHandler.start();
	}

	@Override
	public void onDisable() {
		if (!nullCheck())
			MessageBus.sendClientMessage("Discord Rich Presence shutdown!", true);
		RPCHandler.shutdown();
	}
}
