package me.leon.trinity.hacks.misc;

import me.leon.trinity.hacks.Category;
import me.leon.trinity.hacks.Module;
import net.minecraft.init.Items;

public class FastEXP extends Module {
    public FastEXP() {
        super("FastEXP", "Throw EXP bottles fast", Category.MISC);
    }

    public void onUpdate() {
        if (mc.player.getHeldItemMainhand().getItem() == Items.EXPERIENCE_BOTTLE || mc.player.getHeldItemOffhand().getItem() == Items.EXPERIENCE_BOTTLE) {
            mc.rightClickDelayTimer = 0;
        }
    }
}