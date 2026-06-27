package YuukaMod.powers;

import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import YuukaMod.Yuukamod;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;

public class EnergyDebtPower extends BasePower {
    public static final String POWER_ID = Yuukamod.makeID(EnergyDebtPower.class.getSimpleName());
    private boolean debtApplied = false;

    public EnergyDebtPower(AbstractCreature owner, int amount) {
        super(POWER_ID, PowerType.DEBUFF, false, owner, amount);
    }

    @Override
    public void updateDescription() {
        if (DESCRIPTIONS != null && DESCRIPTIONS.length >= 2) {
            this.description = DESCRIPTIONS[0] + this.amount + DESCRIPTIONS[1];
        } else {
            this.description = "Lose " + this.amount + " energy next turn.";
        }
    }

    @Override
    public void atStartOfTurnPostDraw() {
        if (!debtApplied && AbstractDungeon.player != null) {
            flash();
            debtApplied = true;
            int loss = Math.min(amount, Math.max(0, AbstractDungeon.player.energy.energy));
            if (loss > 0) {
                AbstractDungeon.player.energy.use(loss);
            }
            addToTop(new RemoveSpecificPowerAction(AbstractDungeon.player, AbstractDungeon.player, POWER_ID));
        }
    }
}
