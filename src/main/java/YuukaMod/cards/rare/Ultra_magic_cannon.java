package YuukaMod.cards.rare;

import YuukaMod.cards.BaseCard;
import YuukaMod.character.KazamiYuuka;
import YuukaMod.util.AutoTriggerLimit;
import YuukaMod.util.CardStats;
import YuukaMod.util.MagicCannonComboTracker;
import YuukaMod.util.VFXHelper;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAllEnemiesAction;
import com.megacrit.cardcrawl.actions.utility.WaitAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class Ultra_magic_cannon extends BaseCard {
    public static final String ID = makeID(Ultra_magic_cannon.class.getSimpleName());
    private static final CardStats info = new CardStats(
            KazamiYuuka.Meta.YUUKA_COLOR,
            CardType.ATTACK,
            CardRarity.RARE,
            CardTarget.ALL_ENEMY,
            6
    );
    private static final int DAMAGE = 100;
    private static final int UPG_DAMAGE = 50;

    public Ultra_magic_cannon() {
        super(ID, info);

        setDamage(DAMAGE, UPG_DAMAGE);
        this.isMultiDamage = true;

        tags.add(CustomTags.MAGICCANNON);
    }

    @Override
    public void triggerWhenDrawn()
    {
        if (this.cost > 0) {
            this.updateCost(-1);
        }

        if (this.canUse(AbstractDungeon.player, null)) {
            beginGlowing();
        } else {
            stopGlowing();
        }
    }

    @Override
    public void triggerOnCardPlayed(AbstractCard cardPlayed) {
        if (cardPlayed != this) {
            MagicCannonComboTracker.onAnyCardPlayed(cardPlayed);
        }
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        VFXHelper.ultraBeamToAll(p);
        AbstractDungeon.actionManager.addToBottom(new DamageAllEnemiesAction(p, multiDamage, this.damageTypeForTurn, AbstractGameAction.AttackEffect.NONE));
        MagicCannonComboTracker.record(this);
        AutoTriggerLimit.onMagicCannonPlayed(this);
        this.updateCost(6 - this.cost);
        addToBot(new WaitAction(0.1F));
    }

    @Override
    public void onMoveToDiscard(){

        this.costForTurn = this.cost;
    }

    @Override
    public AbstractCard makeCopy() {
        return new Ultra_magic_cannon();
    }
}
