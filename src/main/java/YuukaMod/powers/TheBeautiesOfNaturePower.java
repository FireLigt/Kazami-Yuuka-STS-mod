package YuukaMod.powers;

import YuukaMod.Yuukamod;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;

public class TheBeautiesOfNaturePower extends BasePower {
    public static final String POWER_ID = Yuukamod.makeID(TheBeautiesOfNaturePower.class.getSimpleName());
    private static final com.megacrit.cardcrawl.localization.PowerStrings powerStrings =
            com.megacrit.cardcrawl.core.CardCrawlGame.languagePack.getPowerStrings(POWER_ID);

    private AbstractCreature lastAttacker;
    private boolean pendingAttack = false;

    public TheBeautiesOfNaturePower(AbstractCreature owner, int amount) {
        super(POWER_ID, PowerType.BUFF, false, owner, amount);
    }

    @Override
    public int onAttacked(DamageInfo info, int damageAmount) {
        this.lastAttacker = info.owner;
        this.pendingAttack = damageAmount > 0;
        return damageAmount;
    }

    @Override
    public int onLoseHp(int damage) {
        if (this.pendingAttack && this.amount > 0 && damage > 0) {
            this.amount--;
            flash();
            if (this.lastAttacker != null) {
                addToTop(new DamageAction(
                        this.lastAttacker,
                        new DamageInfo(this.owner, damage / 2, DamageInfo.DamageType.THORNS),
                        AbstractGameAction.AttackEffect.BLUNT_LIGHT
                ));
            }
            this.pendingAttack = false;
            return 0;
        }
        this.pendingAttack = false;
        return damage;
    }

    @Override
    public void updateDescription() {
        this.description = DESCRIPTIONS[0];
    }
}
