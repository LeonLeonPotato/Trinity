package me.leon.trinity.hacks.client;

import me.leon.trinity.hacks.Category;
import me.leon.trinity.hacks.Module;
import me.leon.trinity.setting.rewrite.BooleanSetting;
import me.leon.trinity.setting.rewrite.ModeSetting;
import me.leon.trinity.setting.rewrite.TextBoxSetting;

public class TestModule extends Module {
    private static final BooleanSetting one = new BooleanSetting("One", true);
    private static final BooleanSetting two = new BooleanSetting("Two", one, true);
    private static final BooleanSetting three = new BooleanSetting("Three", two, true);

    private static final TextBoxSetting text = new TextBoxSetting("TextBox", "test");

    private static final BooleanSetting var1 = new BooleanSetting("Var1", true);
    private static final BooleanSetting predicate1 = new BooleanSetting("Test2", true, s -> var1.getValue());

    private static final ModeSetting var2 = new ModeSetting("Var2", "popbob", "fitmc", "popbob", "cringe");
    private static final BooleanSetting predicate2 = new BooleanSetting("Test2", true, s -> var2.is("cringe"));

    public TestModule() {
        super("TestModule", "Testing purposes", Category.CLIENT);
    }
}
