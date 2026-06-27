package YuukaMod.cards.special;

import YuukaMod.cards.BaseCard;
import YuukaMod.character.KazamiYuuka;
import YuukaMod.util.AutoTriggerLimit;
import YuukaMod.util.CardStats;
import YuukaMod.util.VFXHelper;
import YuukaMod.vfx.combat.ScaledLaserEffect;
import YuukaMod.vfx.combat.ScreenDarkenEffect;
import YuukaMod.vfx.combat.ScreenFlashEffect;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.DamageAllEnemiesAction;
import com.megacrit.cardcrawl.actions.utility.WaitAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.ScreenShake;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class Mega_magic_cannon extends BaseCard {
    public static final String ID = makeID(Mega_magic_cannon.class.getSimpleName());
    private static final CardStats info = new CardStats(
            KazamiYuuka.Meta.YUUKA_COLOR,
            CardType.ATTACK,
            CardRarity.SPECIAL,
            CardTarget.ALL_ENEMY,
            0
    );

    public Mega_magic_cannon() {
        super(ID, info);
        setDamage(99999);
        this.isMultiDamage = true;
        tags.add(CustomTags.MAGICCANNON);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot(new VFXAction(new ScreenDarkenEffect()));
        addToBot(new WaitAction(0.4F));
        VFXHelper.megaBeamToAll(p);
        addToBot(new VFXAction(new ScreenFlashEffect()));
        addToBot(new AbstractGameAction() {
            @Override
            public void update() {
                CardCrawlGame.screenShake.shake(ScreenShake.ShakeIntensity.HIGH, ScreenShake.ShakeDur.LONG, false);
                this.isDone = true;
            }
        });
        addToBot(new VFXAction(new ScaledLaserEffect(
                p.hb.cX, p.hb.cY,
                p.hb.cX + 100.0F * Settings.scale,
                p.hb.cY + 100.0F * Settings.scale,
                "combat/laserThick", 8.0f), 0.5F));
        addToBot(new DamageAllEnemiesAction(p, multiDamage, this.damageTypeForTurn, AbstractGameAction.AttackEffect.NONE));
        addToBot(new WaitAction(0.5F));
        addToBot(new AbstractGameAction() {
            @Override
            public void update() {
                AutoTriggerLimit.onMagicCannonPlayed(Mega_magic_cannon.this);
                this.isDone = true;
            }
        });
    }

    @Override
    public AbstractCard makeCopy() {
        return new Mega_magic_cannon();
    }
}
