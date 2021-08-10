package me.leon.trinity.utils.client;

import me.leon.trinity.managers.CapeManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.inventory.ClickType;
import net.minecraft.network.play.client.CPacketClickWindow;
import net.minecraft.util.ResourceLocation;

import javax.imageio.ImageIO;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.UUID;

public class CapeUtil {

	public static HashMap<UUID, ResourceLocation> getCapes(String pastebinURL) throws Exception {

		HashMap<UUID, ResourceLocation> outMap = new HashMap<>();

		URL url = new URL(pastebinURL);
		URLConnection connection = url.openConnection();
		connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/28.0.1500.29 Safari/537.36");

		BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
		String line;
		while ((line = reader.readLine()) != null) {
			String[] splitLine = line.split(" ");

			if (splitLine.length != 2)
				throw new IllegalStateException("Incorrect cape line found");

			switch (splitLine[1]) {
				case "LIGHT":
					outMap.put(UUID.fromString(splitLine[0]), new ResourceLocation("trinity", CapeManager.LIGHT_CAPE));
					break;
				case "DARK":
					outMap.put(UUID.fromString(splitLine[0]), new ResourceLocation("trinity", CapeManager.DARK_CAPE));
					break;
				case "RAINBOW":
					outMap.put(UUID.fromString(splitLine[0]), new ResourceLocation("trinity", CapeManager.RAINBOW_CAPE));
					break;
				default:
					URL capeURL = new URL(splitLine[1]);
					outMap.put(UUID.fromString(splitLine[0]), downloadFromURL(capeURL));
					break;
			}
		}

		return outMap;
	}

	public static ResourceLocation downloadFromURL(URL url) throws Exception {
		ResourceLocation r = Minecraft.getMinecraft().getTextureManager().getDynamicTextureLocation("capes", new DynamicTexture(ImageIO.read(url)));
		r.getClass().getDeclaredField("namespace").set(r, "trinity");
		return r;
	}

}
