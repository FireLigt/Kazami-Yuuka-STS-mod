package YuukaMod.cards.basic;

import YuukaMod.cards.BaseCard;
import YuukaMod.character.KazamiYuuka;
import YuukaMod.util.AutoTriggerLimit;
import YuukaMod.util.CardStats;
import YuukaMod.util.MagicCannonComboTracker;
import YuukaMod.util.VFXHelper;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAllEnemiesAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class Small_magic_cannon extends BaseCard {
    public static final String ID = makeID(Small_magic_cannon.class.getSimpleName());
    private static final CardStats info = new CardStats(
            KazamiYuuka.Meta.YUUKA_COLOR,
            CardType.ATTACK,
            CardRarity.BASIC,
            CardTarget.ALL_ENEMY,
            1
    );
    private static final int DAMAGE = 5;
    private static final int UPG_DAMAGE = 3;

    public Small_magic_cannon() {
        super(ID, info);

        setDamage(DAMAGE, UPG_DAMAGE);
        this.isMultiDamage = true;

        tags.add(CustomTags.MAGICCANNON);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        VFXHelper.tinyLaserToAll(p);
        AbstractDungeon.actionManager.addToBottom(new DamageAllEnemiesAction(p, multiDamage, this.damageTypeForTurn, AbstractGameAction.AttackEffect.NONE));
        MagicCannonComboTracker.record(this);
        AutoTriggerLimit.onMagicCannonPlayed(this);
    }

    @Override
    public void triggerOnCardPlayed(AbstractCard cardPlayed) {
        if (cardPlayed != this) {
            MagicCannonComboTracker.onAnyCardPlayed(cardPlayed);
        }
    }

    @Override
    public AbstractCard makeCopy() {
        return new Small_magic_cannon();
    }
}
