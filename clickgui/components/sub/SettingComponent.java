package me.leon.trinity.clickgui.components.sub;

import me.leon.trinity.clickgui.ClickGui;
import me.leon.trinity.clickgui.Component;
import me.leon.trinity.clickgui.components.ButtonComponent;
import me.leon.trinity.clickgui.components.FrameComponent;
import me.leon.trinity.hacks.client.ClickGUI;
import me.leon.trinity.setting.rewrite.BooleanSetting;
import me.leon.trinity.setting.rewrite.ModeSetting;
import me.leon.trinity.setting.rewrite.Setting;
import me.leon.trinity.setting.rewrite.SliderSetting;
import me.leon.trinity.utils.rendering.RenderUtils;
import net.minecraft.util.Session;

import java.util.ArrayList;

public abstract class SettingComponent<T extends Setting> extends Component {
    protected final T set;
    protected final Component parent;
    protected final ButtonComponent superParent;
    protected final FrameComponent frame;
    protected boolean open, hasSubSettings;
    protected int offset;
    protected int xOffset;
    protected ArrayList<Component> subComponents;

    public SettingComponent(T setting, Component parent, ButtonComponent superParent, int offset, int xOffset) {
        this.set = setting;
        this.parent = parent;
        this.offset = offset;
        this.superParent = superParent;
        this.open = false;
        this.hasSubSettings = !setting.getSubSettings().isEmpty();
        this.frame = superParent.parent;
        this.xOffset = xOffset;
        int y = offset + 14;
        for(Setting s : setting.getSubSettings()) {
            if(s instanceof BooleanSetting) {
                subComponents.add(new BooleanComponent((BooleanSetting) s, superParent, y));
            }
            y += 14;
        }
    }

    @Override
    public void render() {
        RenderUtils.drawRect(frame.x + ClickGui.width, frame.y + offset, frame.x, frame.y + offset + 14, ClickGUI.backgroundColor.getValue());
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int button) {

    }

    @Override
    public int getXOffset() {
        return parent.getXOffset() + 3;
    }

    public final boolean onButton(int x, int y) {
        return y > frame.y + offset
                && y < frame.y + offset
                && x > frame.x
                && x < frame.x + ClickGui.width;
    }

    protected final ButtonComponent getButtonParent() {

    }

    public T getSetting() {
        return set;
    }

    public Component getParent() {
        return parent;
    }

    public boolean isOpen() {
        return open;
    }

    public SettingComponent<T> setOpen(boolean open) {
        this.open = open;
        return this;
    }

    public int getOffset() {
        return offset;
    }

    public SettingComponent<T> setOffset(int offset) {
        this.offset = offset;
        return this;
    }
}
