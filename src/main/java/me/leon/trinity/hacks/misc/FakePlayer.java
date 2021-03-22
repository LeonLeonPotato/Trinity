package me.leon.trinity.hacks.misc;

import com.mojang.authlib.GameProfile;
import me.leon.trinity.hacks.Category;
import me.leon.trinity.hacks.Module;
import net.minecraft.client.entity.EntityOtherPlayerMP;

import java.util.UUID;

public class FakePlayer extends Module {
    public FakePlayer() {
        super("FakePlayer", "Spawns in a fakeplayer", Category.MISC);
    }

    private EntityOtherPlayerMP fake_player;

    @Override
    public void onEnable() {
        if(nullCheck()) return;
        fake_player = new EntityOtherPlayerMP(mc.world, new GameProfile(UUID.fromString("ef845538-72e9-49e5-9675-1d2995036cc3"), "Listed"));
        fake_player.copyLocationAndAnglesFrom(mc.player);
        fake_player.rotationYawHead = mc.player.rotationYawHead;
        mc.world.addEntityToWorld(-100, fake_player);
    }

    @Override
    public void onUpdate() {
        if(nullCheck()) this.setEnabled(false);
    }

    @Override
    public void onDisable() {
        try {
            if(nullCheck()) return;
            mc.world.removeEntity(fake_player);
        } catch (Exception ignored) {}
    }

}
