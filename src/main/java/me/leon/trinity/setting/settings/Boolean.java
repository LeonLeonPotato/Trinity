package me.leon.trinity.setting.settings;

import me.leon.trinity.events.EventStage;
import me.leon.trinity.events.settings.EventBooleanToggle;
import me.leon.trinity.main.Trinity;
import me.leon.trinity.setting.Setting;

public class Boolean extends Setting {
    private boolean enabled;

    public Boolean(String name, boolean enabled) {
        super(name);
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

        if(event.isCancelled()) {
            this.enabled = !enabled;
        }
    }
}
