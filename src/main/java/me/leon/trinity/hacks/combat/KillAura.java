package me.leon.trinity.hacks.combat;

import me.leon.trinity.hacks.Category;
import me.leon.trinity.hacks.Module;
import me.leon.trinity.managers.RotationManager;
import me.leon.trinity.setting.settings.SettingParent;
import me.leon.trinity.setting.settings.sub.SubMode;
import me.leon.trinity.setting.settings.sub.SubSlider;
import me.leon.trinity.utils.world.Rotation.Rotation;

public class KillAura extends Module {
    public static SettingParent rotate = new SettingParent("Rotate", true, true);
    public static SubMode rotation = new SubMode("Mode", rotate, "Packet", "Packet", "Client", "Random");
    public static SubSlider offset = new SubSlider("Mode", rotate, 0, 1, 2, false);

    public static SettingParent delays = new SettingParent("Delays", true, false);
    public static SubMode delayMode = new SubMode("Mode", delays, "Ready", "Custom", "Ready");
    public static SubSlider delay = new SubSlider("Mode", delays, 10, 700, 2000, false);

    public static SettingParent targeting = new SettingParent("Rotate", true, false);


    public KillAura() {
        super("KillAura", "Attacks Entities nearby", Category.COMBAT);
    }

    public void onUpdate() {
        Rotation rotation = new Rotation(0, 0);
        RotationManager.rotationQueue.add(rotation);
    }


}
