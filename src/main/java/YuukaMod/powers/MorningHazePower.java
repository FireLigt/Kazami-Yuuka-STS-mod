package YuukaMod.powers;

import YuukaMod.Yuukamod;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.ReducePowerAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.StrengthPower;

public class MorningHazePower extends BasePower {
    public static final String POWER_ID_BASE = Yuukamod.makeID(MorningHazePower.class.getSimpleName());
    private static final com.megacrit.cardcrawl.localization.PowerStrings powerStrings =
            com.megacrit.cardcrawl.core.CardCrawlGame.languagePack.getPowerStrings(POWER_ID_BASE);
    private static int globalInstanceCount = 0;
    private boolean upgraded;
    private int hpLostTracker = 0;

    public MorningHazePower(AbstractCreature owner, boolean upgraded) {
        super(POWER_ID_BASE, PowerType.BUFF, false, owner, -1);
        this.ID = POWER_ID_BASE + "_" + globalInstanceCount++;
        this.upgraded = upgraded;
    }

    @Override
    public void stackPower(int stackAmount) {
    }

    @Override
    public void onInitialApplication() {
        if (upgraded) {
            int lostHP = this.owner.maxHealth - this.owner.currentHealth;
            int strengthGain = lostHP / 2;
            if (strengthGain > 0) {
                flash();
                addToBot(new ApplyPowerAction(this.owner, this.owner, new StrengthPower(this.owner, strengthGain), strengthGain));
            }
        }
    }

    @Override
    public void wasHPLost(DamageInfo info, int damageAmount) {
        if (damageAmount > 0
                && info.owner != null
                && info.owner != this.owner
                && info.type == DamageInfo.DamageType.NORMAL) {
            hpLostTracker += damageAmount;
            int strengthGain = hpLostTracker / 2;
            if (strengthGain > 0) {
                hpLostTracker -= strengthGain * 2;
                flash();
                addToBot(new ApplyPowerAction(this.owner, this.owner, new StrengthPower(this.owner, strengthGain), strengthGain));
            }
        }
    }

    @Override
    public int onHeal(int healAmount) {
        if (upgraded && this.owner.currentHealth <= 0 && healAmount > 0) {
            AbstractPower strength = this.owner.getPower(StrengthPower.POWER_ID);
            if (strength != null) {
                addToTop(new ReducePowerAction(this.owner, this.owner, StrengthPower.POWER_ID, strength.amount));
            }
            int newHP = Math.min(this.owner.currentHealth + healAmount, this.owner.maxHealth);
            int newStrength = this.owner.maxHealth - newHP;
            if (newStrength > 0) {
                addToBot(new ApplyPowerAction(this.owner, this.owner, new StrengthPower(this.owner, newStrength), newStrength));
            }
            return healAmount;
        }

        if (healAmount > 0) {
            flash();
            AbstractPower strength = this.owner.getPower(StrengthPower.POWER_ID);
            int reduceAmount = strength != null ? Math.min(healAmount, strength.amount) : 0;
            if (reduceAmount > 0) {
                addToTop(new ReducePowerAction(
                        this.owner,
                        this.owner,
                        StrengthPower.POWER_ID,
                        reduceAmount
                ));
            }
        }
        return healAmount;
    }

    @Override
    public void updateDescription() {
        if (upgraded) {
            this.description = DESCRIPTIONS[1];
        } else {
            this.description = DESCRIPTIONS[0];
        }
    }
}
