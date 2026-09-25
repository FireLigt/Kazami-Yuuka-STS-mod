package YuukaMod.powers;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ScreenShake;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.vfx.combat.ScreenOnFireEffect;
import YuukaMod.util.Sounds;
import YuukaMod.util.TextureLoader;

import static YuukaMod.Yuukamod.imagePath;
import static YuukaMod.Yuukamod.makeID;

public class BombPower extends BasePower {
    public static final String POWER_ID = makeID("BombPower");
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);

    public BombPower(AbstractCreature owner) {
        super(POWER_ID, PowerType.BUFF, false, owner, 0);
        if (img == null && region48 == null && region128 == null) {
            img = TextureLoader.getTexture(imagePath("missing.png"));
        }
    }

    @Override
    public void stackPower(int stackAmount) {
    }

    @Override
    public void atEndOfTurn(boolean isPlayer) {
        if (!isPlayer) {
            return;
        }
        int buffCount = 0;
        for (AbstractPower power : AbstractDungeon.player.powers) {
            if (power.type == PowerType.BUFF) {
                buffCount++;
            }
        }
        if (buffCount >= 10) {
            flash();
            if (Sounds.se_enep01 != null) {
                CardCrawlGame.sound.play(Sounds.se_enep01);
            }
            addToBot(new AbstractGameAction() {
                {
                    this.duration = this.startDuration = 0.6F;
                }

                @Override
                public void update() {
                    if (this.duration == this.startDuration) {
                        CardCrawlGame.screenShake.shake(ScreenShake.ShakeIntensity.HIGH, ScreenShake.ShakeDur.XLONG, false);
                        AbstractDungeon.effectList.add(new ScreenOnFireEffect());
                        if (Sounds.se_enep01 != null) {
                            CardCrawlGame.sound.play(Sounds.se_enep01);
                        }
                    }
                    this.tickDuration();
                    if (this.isDone) {
                        for (AbstractMonster m : AbstractDungeon.getMonsters().monsters) {
                            if (!m.isDead && !m.isDying) {
                                m.die();
                            }
                        }
                    }
                }
            });
        }
    }

    @Override
    public void updateDescription() {
        description = powerStrings.DESCRIPTIONS[0];
    }
}
