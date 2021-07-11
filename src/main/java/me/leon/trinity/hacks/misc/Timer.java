package me.leon.trinity.hacks.misc;

import me.leon.trinity.hacks.Category;
import me.leon.trinity.hacks.Module;
import me.leon.trinity.setting.rewrite.SliderSetting;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class Timer extends Module {
	private static final SliderSetting speed = new SliderSetting("Speed", 0, 3, 20, false);

	public Timer() {
		super("Timer", "Increases your client tick speed", Category.MISC);
	}

	@Override
	public void onDisable() {
		mc.timer.tickLength = 50.0f;
	}

	@Override
	public String getHudInfo() {
		return BigDecimal.valueOf(speed.getValue()).setScale(2, RoundingMode.HALF_UP).toString();
	}

	@Override
	public void onUpdate() {
		mc.timer.tickLength = 50.0F / ((float) speed.getValue() == 0.0F ? 0.1F : (float) speed.getValue());
	}
}
