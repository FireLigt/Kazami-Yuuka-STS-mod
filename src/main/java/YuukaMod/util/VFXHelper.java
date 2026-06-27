package YuukaMod.util;

import YuukaMod.vfx.combat.ScaledLaserEffect;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

import java.util.ArrayList;

public class VFXHelper {
    private static final String THIN = "combat/laserThin";
    private static final String THICK = "combat/laserThick";

    private static float[] getGroupCenter(ArrayList<AbstractMonster> group) {
        float cx = 0f, cy = 0f;
        for (AbstractMonster mo : group) {
            cx += mo.hb.cX;
            cy += mo.hb.cY;
        }
        int count = group.size();
        if (count > 0) {
            cx /= count;
            cy /= count;
        }
        return new float[]{cx, cy};
    }

    private static void fireLaser(AbstractPlayer p, float[] center, String tex, float sc) {
        if (center == null) return;
        float dur = 0.2f + sc * 0.3f;
        float dirX = center[0] - p.hb.cX;
        float dirY = center[1] - p.hb.cY;
        float len = (float) Math.sqrt(dirX * dirX + dirY * dirY);
        if (len > 0f) {
            dirX /= len;
            dirY /= len;
        }
        float roomWidth = Settings.WIDTH * Settings.scale;
        AbstractDungeon.actionManager.addToBottom(
                new VFXAction(new ScaledLaserEffect(
                        p.hb.cX, p.hb.cY,
                        center[0] + dirX * roomWidth,
                        center[1] + dirY * roomWidth,
                        tex, sc), dur));
    }

    private static void fireLasersToAll(AbstractPlayer p, String tex, float sc) {
        ArrayList<AbstractMonster> monsters = AbstractDungeon.getMonsters().monsters;
        ArrayList<AbstractMonster> left = new ArrayList<>();
        ArrayList<AbstractMonster> right = new ArrayList<>();
        for (AbstractMonster mo : monsters) {
            if (!mo.isDeadOrEscaped()) {
                if (mo.hb.cX < p.hb.cX) {
                    left.add(mo);
                } else {
                    right.add(mo);
                }
            }
        }
        if (!left.isEmpty()) {
            fireLaser(p, getGroupCenter(left), tex, sc);
        }
        if (!right.isEmpty()) {
            fireLaser(p, getGroupCenter(right), tex, sc);
        }
    }

    public static void tinyLaserToAll(AbstractPlayer p) {
        fireLasersToAll(p, THIN, 0.75f);
    }

    public static void thinLaserToAll(AbstractPlayer p) {
        fireLasersToAll(p, THIN, 1.50f);
    }

    public static void mediumLaserToAll(AbstractPlayer p) {
        fireLasersToAll(p, THICK, 2.50f);
    }

    public static void thickBeamToAll(AbstractPlayer p) {
        fireLasersToAll(p, THICK, 3.00f);
    }

    public static void giantBeamToAll(AbstractPlayer p) {
        fireLasersToAll(p, THICK, 3.50f);
    }

    public static void ultraBeamToAll(AbstractPlayer p) {
        fireLasersToAll(p, THICK, 4.00f);
    }

    public static void megaBeamToAll(AbstractPlayer p) {
        fireLasersToAll(p, THICK, 8.00f);
    }
}
