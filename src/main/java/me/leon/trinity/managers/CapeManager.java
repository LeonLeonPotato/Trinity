package me.leon.trinity.managers;

import com.google.gson.JsonObject;
import me.leon.trinity.main.Trinity;
import me.leon.trinity.utils.client.CapeUtil;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;

import java.awt.image.ImageObserver;
import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;

public class CapeManager {
	public final static String LIGHT_CAPE = "capes/capewhite.png";
	public final static String DARK_CAPE = "capes/capeblack.png";
	public final static String RAINBOW_CAPE = "capes/caperainbow.png";
	private final String capeURL = "https://pastebin.com/raw/q6ed5hZt";
	private HashMap<UUID, ResourceLocation> capes = new HashMap<UUID, ResourceLocation>();

	public CapeManager() {
		try {
			capes = CapeUtil.getCapes(capeURL);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public boolean hasCape(UUID uuid) {
		return capes.containsKey(uuid);
	}

	public ResourceLocation getCapeForUUID(UUID uuid) {
		return capes.get(uuid);
	}

}
