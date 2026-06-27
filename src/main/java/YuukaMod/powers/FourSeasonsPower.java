package YuukaMod.powers;

import YuukaMod.Yuukamod;
import com.megacrit.cardcrawl.actions.common.GainEnergyAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;

public class FourSeasonsPower extends BasePower {
    public static final String POWER_ID = Yuukamod.makeID(FourSeasonsPower.class.getSimpleName());

    private int triggersRemaining;

    public FourSeasonsPower(AbstractCreature owner, int amount) {
        super(POWER_ID, PowerType.BUFF, false, owner, amount);
        this.triggersRemaining = amount;
    }

    @Override
    public void onUseCard(AbstractCard card, UseCardAction action) {
        if (triggersRemaining > 0 && card.costForTurn >= 2) {
            triggersRemaining--;
            flash();
            addToBot(new GainEnergyAction(1));
        }
    }

    @Override
    public void atStartOfTurn() {
        triggersRemaining = amount;
    }

    @Override
    public void updateDescription() {
        this.description = DESCRIPTIONS[amount > 1 ? 1 : 0];
    }
}
