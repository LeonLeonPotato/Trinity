package me.leon.trinity.hud.components;

import me.leon.trinity.hacks.client.HUDeditor;
import me.leon.trinity.hud.AnchorPoint;
import me.leon.trinity.hud.Component;
import me.leon.trinity.setting.settings.Color;
import me.leon.trinity.utils.misc.FontUtil;
import me.leon.trinity.utils.rendering.Coloring;
import me.leon.trinity.utils.rendering.RenderUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class CoordComponent extends Component {
    public CoordComponent() {
        super("CoordComponent");
        this.visible = true;
        this.x = 1;
        this.y = res.getScaledHeight() - height();
        this.anchorPoint = AnchorPoint.BOTTOMLEFT;
    }

    private String coords;

    @Override
    public void render() {
        this.drawBackground();
        int x = (int) mc.player.posX;
        int y = (int) mc.player.posY;
        int z = (int) mc.player.posZ;


        String[] a = String.valueOf(roundToPlace((getNCoord(x)), 1)).split("\\.");
        String nx =  a[0] + "." + a[1].charAt(0);
        String[] b = String.valueOf(roundToPlace((getNCoord(z)), 1)).split("\\.");
        String nz =  b[0] + "." + b[1].charAt(0);
        if(!HUDeditor.netherCoords.getValue()) {
            this.coords = String.format("X: %d | Y: %d | Z: %d", x, y, z);
        } else {
            this.coords = String.format("X: %d | Y: %d | Z: %d   " + Coloring.getGREY() + "[" + Coloring.getRESET() + " X: %s | Z: %s" + Coloring.getGREY() + " ]" + Coloring.getRESET(), x, y, z, nx, nz);
        }
        FontUtil.drawString(coords, this.x + 1, this.y + 1, HUDeditor.textColor.getValue().getRGB());
    }

    @Override
    public float width() {
        if(coords == null) {
            return 0;
        }
        return FontUtil.getStringWidth(coords) + 1;
    }

    @Override
    public float height() {
        return FontUtil.getFontHeight();
    }

    private static double roundToPlace(double value, int places) {
        if (places < 0) {
            throw new IllegalArgumentException();
        }
        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    private float getNCoord(int x) {
        if(mc.player.dimension == 0) {
            return (float) (Math.floor((x / 8f) * 10) / 10);
        } else {
            return x * 8f;
        }
    }
}
