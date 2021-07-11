package me.leon.trinity.managers;

import me.leon.trinity.hud.Component;
import me.leon.trinity.hud.components.*;
import me.leon.trinity.setting.rewrite.Setting;
import scala.annotation.meta.field;

import java.lang.reflect.Field;
import java.util.ArrayList;

public class HudManager {
	public ArrayList<Component> comps;

	public HudManager() {
		this.comps = new ArrayList<>();

		addComp(new RagDollComponent());
		addComp(new FPSComponent());
		addComp(new TPSComponent());
		addComp(new ModuleListComponent());
		addComp(new CoordComponent());
		addComp(new NotificationComponent());
	}

	public Component getComponentByName(String name) {
		for (Component comp : comps) {
			if (comp.name.equalsIgnoreCase(name)) {
				return comp;
			}
		}
		return null;
	}

	private void addComp(Component comp) {
		try {
			for (Field field : comp.getClass().getDeclaredFields()) {
				if (Setting.class.isAssignableFrom(field.getType())) {
					if (!field.isAccessible()) {
						field.setAccessible(true);
					}
					final Setting val = (Setting) field.get(comp);
					if(val.getParent() == null) val.setParent(comp);
					val.setSuperParent(comp);

					comp.addSetting(val);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		comps.add(comp);
	}
}
