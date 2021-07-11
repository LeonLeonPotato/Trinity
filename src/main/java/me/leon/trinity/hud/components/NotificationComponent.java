package me.leon.trinity.hud.components;

import me.leon.trinity.hud.Component;
import me.leon.trinity.managers.NotificationManager;
import me.leon.trinity.notification.NotifType;
import me.leon.trinity.notification.Notification;
import me.leon.trinity.setting.rewrite.ColorSetting;
import me.leon.trinity.setting.rewrite.SliderSetting;
import me.leon.trinity.utils.misc.BezierCurve;
import me.leon.trinity.utils.misc.FontUtil;
import me.leon.trinity.utils.rendering.RenderUtils;
import me.leon.trinity.utils.rendering.skeet.Quad;
import me.leon.trinity.utils.rendering.skeet.SkeetUtils;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class NotificationComponent extends Component {
    public static ColorSetting infoColor = new ColorSetting("InfoColor", 32, 140, 61,255, false);
    public static ColorSetting warningColor = new ColorSetting("WarningColor", 255, 0, 0,255, false);
    public static ColorSetting textColor = new ColorSetting("TextColor", 255, 255, 255,255, false);
    public static SliderSetting width = new SliderSetting("Width", 50, 300, 600, true);
    public static SliderSetting height = new SliderSetting("Height", 50, 150, 400, true);
    public static SliderSetting fadeInTime = new SliderSetting("FadeTime", 100, 400, 2000, true);
    public static SliderSetting time = new SliderSetting("LiveTime", 500, 3500, 7000, true);
    public static SliderSetting dieTime = new SliderSetting("DieTime", 500, 1000, 2000, true);

    private final BezierCurve curve = new BezierCurve(0, 175, 150, 200);

    public NotificationComponent() {
        super("Notifications");
        this.x = new ScaledResolution(mc).getScaledWidth() - 300;
        this.y = new ScaledResolution(mc).getScaledHeight() - 300;
    }

    @Override
    public void render() {
        if(false) {
            final Quad quad = drawNotificationBase(0, 255);
            drawInfoIcon((int) quad.getX() + 6, (int) quad.getY() + 6, 255);
            RenderUtils.scissor(quad);
            FontUtil.drawString("Example Notification", quad.getX() + 40, quad.getY() + 6, infoColor.getValue().getRGB());
            FontUtil.drawString("This is an Example!", quad.getX() + 40, quad.getY() + 10 + FontUtil.getFontHeight(), textColor.getValue().getRGB());
            RenderUtils.restoreScissor();
        } else {
            if(!NotificationManager.queue.isEmpty()) {
                int off = 0;

                NotificationManager.queue.forEach(e -> { if(e.isOverTime()) e.setMode(1); });
                NotificationManager.queue.forEach(e -> { if(e.isOverTime1()) e.setMode(2); });
                NotificationManager.queue.removeIf(Notification::isOverTime2);

                for(Notification not : NotificationManager.queue) {
                    String des = "";
                    String des2 = "";
                    String[] split = not.getDescription().split(" ");
                    for(int i = 0; i < split.length; i++) {
                        String next = (i == split.length - 1 ? null : split[i + 1]);
                        if(next != null) {
                            split[i] = split[i].concat(" ");
                        }
                    }

                    boolean flag = false;
                    for (String s : split) {
                        if(!flag) {
                            if(FontUtil.getStringWidth(des.concat(s)) < 134) {
                                des = des.concat(s);
                            } else {
                                des2 = des2.concat(s);
                                flag = true;
                            }
                        } else {
                            des2 = des2.concat(s);
                        }
                    }

                    if(not.getMode() == 2) {
                        final double deathPercent = 1 - (not.getDeathTime() / dieTime.getValue());
                        final Quad quad = drawNotificationBase(off, (int) (deathPercent * 255));

                        if(not.getType() == NotifType.INFO)
                            drawInfoIcon(quad.getX() + 6, quad.getY() + 6, deathPercent);
                        else
                            drawWarningIcon(quad.getX() + 6, quad.getY() + 6, deathPercent);

                        RenderUtils.scissor(quad);
                        FontUtil.drawString(not.getName(), quad.getX() + 40, quad.getY() + 6, RenderUtils.lower(not.getType() == NotifType.INFO ? infoColor.getValue() : warningColor.getValue(), (int) ((not.getDeathTime() / dieTime.getValue()) * 255)).getRGB());
                        FontUtil.drawString(des, quad.getX() + 40, quad.getY() + 10 + FontUtil.getFontHeight(), RenderUtils.lower(textColor.getValue(), (int) ((not.getDeathTime() / dieTime.getValue()) * 255)).getRGB());
                        FontUtil.drawString(des2, quad.getX() + 40, quad.getY() + 10 + (FontUtil.getFontHeight() * 2), RenderUtils.lower(textColor.getValue(), (int) ((not.getDeathTime() / dieTime.getValue()) * 255)).getRGB());
                    } else if(not.getMode() == 0) {
                        final double fadeTime = -(not.getFadeTime() / fadeInTime.getValue());
                        final Quad quad = drawNotificationBase((float) curve.get(fadeTime), off, 255);

                        if(not.getType() == NotifType.INFO)
                            drawInfoIcon(quad.getX() + 6, quad.getY() + 6, 255);
                        else
                            drawWarningIcon(quad.getX() + 6, quad.getY() + 6, 255);

                        RenderUtils.scissor(quad);
                        FontUtil.drawString(not.getName(), quad.getX() + 40, quad.getY() + 6, (not.getType() == NotifType.INFO ? infoColor.getValue() : warningColor.getValue()).getRGB());
                        FontUtil.drawString(des, quad.getX() + 40, quad.getY() + 10 + FontUtil.getFontHeight(), textColor.getValue().getRGB());
                        FontUtil.drawString(des2, quad.getX() + 40, quad.getY() + 10 + (FontUtil.getFontHeight() * 2), textColor.getValue().getRGB());
                    } else {
                        final Quad quad = drawNotificationBase(off, 255);

                        if(not.getType() == NotifType.INFO)
                            drawInfoIcon(quad.getX() + 6, quad.getY() + 6, 255);
                        else
                            drawWarningIcon(quad.getX() + 6, quad.getY() + 6, 255);

                        RenderUtils.scissor(quad);
                        FontUtil.drawString(not.getName(), quad.getX() + 40, quad.getY() + 6, (not.getType() == NotifType.INFO ? infoColor.getValue() : warningColor.getValue()).getRGB());
                        FontUtil.drawString(des, quad.getX() + 40, quad.getY() + 10 + FontUtil.getFontHeight(), textColor.getValue().getRGB());
                        FontUtil.drawString(des2, quad.getX() + 40, quad.getY() + 10 + (FontUtil.getFontHeight() * 2), textColor.getValue().getRGB());
                    }
                    RenderUtils.restoreScissor();

                    off -= 64;
                }
            }
        }
    }

    private Quad drawNotificationBase(int off, int alpha) {
        return SkeetUtils.renderSkeetBox(new Quad(x, y + off, x + 180, y + 50 + off), alpha);
    }

    private Quad drawNotificationBase(float xOff, float off, int alpha) {
        return SkeetUtils.renderSkeetBox(new Quad(x + xOff, y + off, x + 180 + xOff, y + 50 + off), alpha);
    }

    private void drawInfoIcon(double x, double y, double grey) {
        try {
            mc.getTextureManager().bindTexture(new ResourceLocation("trinity", "info.png"));
            GlStateManager.enableTexture2D();
            GL11.glColor4d(grey, grey, grey, grey);
            drawTexturedRect(x, y);
        } catch (Exception ignored) { }
    }

    private void drawWarningIcon(double x, double y, double grey) {
        try {
            mc.getTextureManager().bindTexture(new ResourceLocation("trinity", "warning.png"));
            GlStateManager.enableTexture2D();
            GL11.glColor4d(grey, grey, grey, grey);
            drawTexturedRect(x, y);
        } catch (Exception ignored) { }
    }

    private void drawTexturedRect(double x, double y) {
        float f = 1.0F / (float) 8;
        float f1 = 1.0F / (float) 8;
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
        bufferbuilder.pos(x, y + 30, 0.0D).tex((float) 16 * f, ((float) 16 + (float) 8) * f1).endVertex();
        bufferbuilder.pos(x + 30, y + 30, 0.0D).tex(((float) 16 + (float) 8) * f, ((float) 16 + (float) 8) * f1).endVertex();
        bufferbuilder.pos(x + 30, y, 0.0D).tex(((float) 16 + (float) 8) * f, (float) 16 * f1).endVertex();
        bufferbuilder.pos(x, y, 0.0D).tex((float) 16 * f, (float) 16 * f1).endVertex();
        tessellator.draw();
    }

    @Override
    public float width() {
        return 180;
    }

    @Override
    public float height() {
        return 50;
    }
}
