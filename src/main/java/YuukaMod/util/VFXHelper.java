package YuukaMod.util;

import YuukaMod.vfx.combat.ScaledLaserEffect;
import com.badlogic.gdx.math.Vector2;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.MinionPower;

import java.util.ArrayList;

public class VFXHelper {
    private static final String THIN = "combat/laserThin";
    private static final String THICK = "combat/laserThick";

    private static float[] getGroupCenter(ArrayList<Vector2> group) {
        float cx = 0f, cy = 0f;
        for (Vector2 pt : group) {
            cx += pt.x;
            cy += pt.y;
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

    private static ArrayList<Vector2> collectLaserTargets(AbstractPlayer p) {
        ArrayList<Vector2> targets = new ArrayList<>();
        if (p instanceof PhantomPlayer) {
            AbstractPlayer real = ((PhantomPlayer) p).getRealPlayer();
            if (real != null && real.hb != null && !real.isDeadOrEscaped()) {
                targets.add(new Vector2(real.hb.cX, real.hb.cY));
            }
            for (AbstractMonster mo : AbstractDungeon.getMonsters().monsters) {
                if (!mo.isDeadOrEscaped() && mo.hasPower(MinionPower.POWER_ID)) {
                    targets.add(new Vector2(mo.hb.cX, mo.hb.cY));
                }
            }
        } else {
            for (AbstractMonster mo : AbstractDungeon.getMonsters().monsters) {
                if (!mo.isDeadOrEscaped()) {
                    targets.add(new Vector2(mo.hb.cX, mo.hb.cY));
                }
            }
        }
        return targets;
    }

    private static void fireLasersToAll(AbstractPlayer p, String tex, float sc) {
        ArrayList<Vector2> left = new ArrayList<>();
        ArrayList<Vector2> right = new ArrayList<>();
        for (Vector2 target : collectLaserTargets(p)) {
            if (target.x < p.hb.cX) {
                left.add(target);
            } else {
                right.add(target);
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
