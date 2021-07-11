package me.leon.trinity.managers;

import me.leon.trinity.utils.client.CapeUtil;
import net.minecraft.util.ResourceLocation;

import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;

public class CapeManager {
	public final static String LIGHT_CAPE = "trinity/capes/capewhite.png";
	public final static String DARK_CAPE = "trinity/capes/capedark.png";
	private final String capeURL = "https://pastebin.com/raw/q6ed5hZt";
	private HashMap<UUID, ResourceLocation> capes = new HashMap<UUID, ResourceLocation>();

	public CapeManager() {
		try {
			capes = CapeUtil.getCapes(capeURL);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public boolean hasCape(UUID uuid) {
		return capes.containsKey(uuid);
	}

	public ResourceLocation getCapeForUUID(UUID uuid) {
		if (!hasCape(uuid)) return null;

		return capes.get(uuid);
	}

}
