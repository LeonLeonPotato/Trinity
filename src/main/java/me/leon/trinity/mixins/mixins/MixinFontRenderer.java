package me.leon.trinity.mixins.mixins;

import me.leon.trinity.hacks.client.Font;
import me.leon.trinity.main.Trinity;
import me.leon.trinity.managers.ModuleManager;
import me.leon.trinity.mixins.IMixin;
import me.leon.trinity.utils.misc.FontUtil;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.awt.*;

@Mixin(FontRenderer.class)
public abstract class MixinFontRenderer implements IMixin {
    @Inject(method = "getStringWidth(Ljava/lang/String;)I", at = @At("HEAD"), cancellable = true)
    public void getStringWidth(String text, CallbackInfoReturnable<Integer> cir) {
        if(Trinity.finishLoading) {
            if(Font.enabled() && Font.vanilla.getValue()) {
                cir.setReturnValue((int) FontUtil.getStringWidth(text));
                cir.cancel();
            }
        }
    }

    @Inject(method = "drawString(Ljava/lang/String;FFIZ)I", at = @At("HEAD"), cancellable = true)
    public void renderStringHook(String text, float x, float y, int color, boolean dropShadow, CallbackInfoReturnable<Integer> info) {
        if (Trinity.finishLoading) {
            if(Font.enabled() && Font.vanilla.getValue()) {
                float result = FontUtil.drawString(text, x, y, color, dropShadow);
                info.setReturnValue((int) result);
                GlStateManager.enableAlpha(); // fix!
                info.cancel();
            }
        }
    }


    @Redirect(method = "drawStringWithShadow", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/FontRenderer;drawString(Ljava/lang/String;FFIZ)I"))
    public int drawCustomFontStringWithShadow(FontRenderer fontRenderer, String text, float x, float y, int color, boolean dropShadow) {
        if(Font.enabled() && Font.vanilla.getValue()) {
            return (int) FontUtil.drawString(text, x, y, new Color(color));
        } else {
            return fontRenderer.drawString(text, x, y, color, dropShadow);
        }
    }
}
