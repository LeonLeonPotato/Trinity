package me.leon.trinity.setting.settings.sub;

import me.leon.trinity.events.EventStage;
import me.leon.trinity.events.settings.EventBooleanToggle;
import me.leon.trinity.main.Trinity;
import me.leon.trinity.setting.Setting;
import me.leon.trinity.setting.settings.SubSetting;

public class SubBoolean extends SubSetting {
    private boolean enabled;

    public SubBoolean(String name, Setting parent, boolean enabled) {
        super(name, parent);
        this.enabled = enabled;
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
