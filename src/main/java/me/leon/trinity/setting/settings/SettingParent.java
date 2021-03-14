package me.leon.trinity.setting.settings;

import me.leon.trinity.events.EventStage;
import me.leon.trinity.events.settings.EventBooleanToggle;
import me.leon.trinity.main.Trinity;
import me.leon.trinity.setting.Setting;

public class SettingParent extends Setting {
    private boolean enabled;
    private final boolean canEnable;

    public SettingParent(String name, boolean enabled) {
        super(name);
        this.enabled = enabled;
        this.canEnable = true;
    }

    public SettingParent(String name) {
        super(name);
        this.enabled = true;
        this.canEnable = false;
    }

    public boolean canEnable() {
        return canEnable;
    }

    public boolean getValue() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void toggle() {
        this.enabled = !enabled;
        EventBooleanToggle event = new EventBooleanToggle(EventStage.PRE, this, enabled);
        Trinity.settingsDispatcher.post(event);

        if(event.isCanceled()) {
            this.enabled = !enabled;
        }
    }
}
