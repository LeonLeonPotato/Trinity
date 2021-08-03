package me.leon.trinity.hacks.client;

import me.leon.trinity.hacks.Category;
import me.leon.trinity.hacks.Module;
import me.leon.trinity.setting.rewrite.BooleanSetting;
import me.leon.trinity.setting.rewrite.TextBoxSetting;

public class TestModule extends Module {
    private static final BooleanSetting one = new BooleanSetting("One", true);
    private static final BooleanSetting two = new BooleanSetting("Two", one, true);
    private static final BooleanSetting three = new BooleanSetting("Three", two, true);

    private static final TextBoxSetting text = new TextBoxSetting("TextBox", "test");

    public TestModule() {
        super("TestModule", "Testing purposes", Category.CLIENT);
    }
}
