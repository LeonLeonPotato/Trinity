package me.leon.trinity.clickgui.components.sub;

import me.leon.trinity.clickgui.ClickGui;
import me.leon.trinity.clickgui.Component;
import me.leon.trinity.clickgui.components.Button;
import me.leon.trinity.main.Trinity;
import me.leon.trinity.setting.Setting;
import me.leon.trinity.setting.settings.SettingParent;
import me.leon.trinity.setting.settings.sub.SubMode;
import me.leon.trinity.setting.settings.sub.SubSlider;
import me.leon.trinity.utils.misc.FontUtil;
import me.leon.trinity.utils.rendering.Rainbow;
import me.leon.trinity.utils.rendering.RenderUtils;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.awt.*;
import java.util.ArrayList;

public class SubSetting extends Component {
    public boolean open;
    public Button parent;
    public SettingParent set;
    public int offset;
    public ArrayList<Component> comps;
    public int setY;

    public SubSetting(SettingParent set, Button parent, int offset) {
        this.set = set;
        this.parent = parent;
        this.offset = offset;
        this.comps = new ArrayList<>();
        this.setY = 14;
        for(Setting set0 : Trinity.settingManager.sets) {
            if(set0 instanceof me.leon.trinity.setting.settings.SubSetting) {
                if(set0 instanceof SubSlider) {
                    this.comps.add(new me.leon.trinity.clickgui.components.sub.sub.SubSlider());
                    this.setY += 14;
                }
                if(set0 instanceof SubMode) {
                    this.comps.add(new me.leon.trinity.clickgui.components.sub.sub.SubMode((SubMode) set0, this, setY));
                    this.setY += 14;
                }
            }
        }
    }

    @Override
    public void render() {

    }

    @Override
    public void updateComponent(int mouseX, int mouseY) {

    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int button) {

    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int mouseButton) {

    }

    @Override
    public int getParentHeight() {
        return 0;
    }

    @Override
    public void keyTyped(char typedChar, int key) {

    }

    @Override
    public void setOff(int newOff) {

    }

    @Override
    public int getHeight() {
        return 0;
    }

    public void refresh() {
        if(this.open) {
            int a = 0;
            for(Component c : this.comps) {
                c.setOff(a);
                a += c.getHeight();
            }
        }
    }
}
