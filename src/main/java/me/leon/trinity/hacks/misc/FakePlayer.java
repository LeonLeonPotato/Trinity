package me.leon.trinity.hacks.misc;

import com.mojang.authlib.GameProfile;
import io.netty.channel.ChannelHandlerContext;
import me.leon.trinity.events.EventStage;
import me.leon.trinity.events.main.EventPacketRecieve;
import me.leon.trinity.events.main.EventTotemPop;
import me.leon.trinity.hacks.Category;
import me.leon.trinity.hacks.Module;
import me.leon.trinity.main.Trinity;
import me.leon.trinity.managers.TotempopManager;
import me.leon.trinity.setting.rewrite.BooleanSetting;
import me.leon.trinity.utils.entity.EntityUtils;
import me.leon.trinity.utils.math.MathUtils;
import me.leon.trinity.utils.world.DamageUtils;
import me.leon.trinity.utils.world.WorldUtils;
import me.zero.alpine.fork.listener.EventHandler;
import me.zero.alpine.fork.listener.Listener;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.init.MobEffects;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.SPacketDestroyEntities;
import net.minecraft.network.play.server.SPacketEntityStatus;
import net.minecraft.potion.PotionEffect;

import java.util.UUID;

public class FakePlayer extends Module {
	public static BooleanSetting inv = new BooleanSetting("CopyInv", true);
	public static BooleanSetting pop = new BooleanSetting("Pop", true);

	private EntityOtherPlayerMP fake_player;

	public FakePlayer() {
		super("FakePlayer", "Spawns in a fakeplayer", Category.MISC);
	}

	@Override
	public void onEnable() {
		if (nullCheck()) return;
		fake_player = new EntityOtherPlayerMP(mc.world, new GameProfile(UUID.fromString("ef845538-72e9-49e5-9675-1d2995036cc3"), "Listed"));
		fake_player.copyLocationAndAnglesFrom(mc.player);
		fake_player.rotationYawHead = mc.player.rotationYawHead;
		if (inv.getValue()) {
			fake_player.inventory.copyInventory(mc.player.inventory);
		}
		mc.world.addEntityToWorld(-100, fake_player);
	}

	@Override
	public void onUpdate() {
		if (nullCheck()) this.setEnabled(false);
	}

	@Override
	public void onDisable() {
		try {
			if (nullCheck()) return;
			mc.world.removeEntity(fake_player);
		} catch (Exception ignored) {
		}
	}

	@EventHandler
	private final Listener<EventPacketRecieve> packetRecieveListener = new Listener<>(event -> {
		if(pop.getValue()) {
			if(event.getPacket() instanceof SPacketDestroyEntities) {
				final SPacketDestroyEntities packet = (SPacketDestroyEntities) event.getPacket();

				for(int id : packet.getEntityIDs()) {
					final Entity entity = mc.world.getEntityByID(id);

					if(entity instanceof EntityEnderCrystal) {
						if(entity.getDistanceSq(fake_player) < 144) {
							final float rawDamage = DamageUtils.calculateDamage(entity.getPositionVector(), fake_player);
							final float absorption = fake_player.getAbsorptionAmount() - rawDamage;
							final boolean hasHealthDmg = absorption < 0;
							final float health = fake_player.getHealth() + absorption;

							if(hasHealthDmg && health > 0) {
								fake_player.setHealth(health);
								fake_player.setAbsorptionAmount(0);
							} else if(health > 0) {
								fake_player.setAbsorptionAmount(absorption);
							} else {
								fake_player.setHealth(2);
								fake_player.setAbsorptionAmount(8);
								fake_player.addPotionEffect(new PotionEffect(MobEffects.ABSORPTION, 5));
								fake_player.addPotionEffect(new PotionEffect(MobEffects.REGENERATION, 1));

								try {
									mc.player.connection.handleEntityStatus(new SPacketEntityStatus(fake_player, (byte) 35));
								} catch (Exception e) {
								}

								if(TotempopManager.totemMap.containsKey(fake_player)) {
									int times = TotempopManager.totemMap.get(fake_player) + 1;
									Trinity.dispatcher.post(new EventTotemPop(EventStage.PRE, fake_player, times));
									TotempopManager.totemMap.remove(fake_player);
									TotempopManager.totemMap.put(fake_player, times);
								} else {
									Trinity.dispatcher.post(new EventTotemPop(EventStage.PRE, fake_player, 1));
									TotempopManager.totemMap.put(fake_player, 1);
								}
							}

							fake_player.hurtTime = 5;
						}
					}
				}
			}
		}
	});
}
