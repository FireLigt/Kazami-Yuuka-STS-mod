package YuukaMod.cards.uncommon;

import YuukaMod.cards.BaseCard;
import YuukaMod.character.KazamiYuuka;
import YuukaMod.util.AutoTriggerLimit;
import YuukaMod.util.CardStats;
import YuukaMod.util.MagicCannonComboTracker;
import YuukaMod.util.VFXHelper;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.DamageAllEnemiesAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.WeakPower;

public class Big_magic_cannon extends BaseCard {
    public static final String ID = makeID(Big_magic_cannon.class.getSimpleName());
    private static final CardStats info = new CardStats(
            KazamiYuuka.Meta.YUUKA_COLOR,
            CardType.ATTACK,
            CardRarity.UNCOMMON,
            CardTarget.ALL_ENEMY,
            3
    );
    private static final int DAMAGE = 28;
    private static final int WEAK = 0;
    private static final int UPG_WEAK = 2;

    public Big_magic_cannon() {
        super(ID, info);

        setDamage(DAMAGE);
        setMagic(WEAK,UPG_WEAK);
        this.isMultiDamage = true;

        tags.add(CustomTags.MAGICCANNON);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        VFXHelper.mediumLaserToAll(p);
        AbstractDungeon.actionManager.addToBottom(new DamageAllEnemiesAction(p, multiDamage, this.damageTypeForTurn, AbstractGameAction.AttackEffect.NONE));
        MagicCannonComboTracker.record(this);
        AutoTriggerLimit.onMagicCannonPlayed(this);

        for (AbstractMonster mo : AbstractDungeon.getCurrRoom().monsters.monsters) {

            if (!mo.isDeadOrEscaped()) {

                addToBot(new ApplyPowerAction(
                        mo,
                        p,
                        new WeakPower(
                                mo,
                                this.magicNumber,
                                false
                        ),
                        this.magicNumber
                ));
            }
        }
    }


    @Override
    public void triggerOnCardPlayed(AbstractCard cardPlayed) {
        if (cardPlayed != this) {
            MagicCannonComboTracker.onAnyCardPlayed(cardPlayed);
        }
    }

    @Override
    public AbstractCard makeCopy() {
        return new Big_magic_cannon();
    }
}
