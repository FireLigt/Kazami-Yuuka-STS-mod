package YuukaMod.powers;

import YuukaMod.Yuukamod;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.powers.StrengthPower;

public class WorthyOpponentPower extends BasePower {
    public static final String POWER_ID_BASE = Yuukamod.makeID(WorthyOpponentPower.class.getSimpleName());
    private static int globalInstanceCount = 0;

    public WorthyOpponentPower(AbstractCreature owner, int amount) {
        super(POWER_ID_BASE, PowerType.BUFF, false, owner, amount);
        this.ID = POWER_ID_BASE + "_" + globalInstanceCount++;
    }

    @Override
    public void stackPower(int stackAmount) {
    }

    @Override
    public void onAfterCardPlayed(AbstractCard card) {
        flash();
        addToBot(new ApplyPowerAction(
                this.owner,
                this.owner,
                new StrengthPower(this.owner, this.amount),
                this.amount
        ));
    }

    @Override
    public void updateDescription() {
        this.description = DESCRIPTIONS[0] + this.amount + DESCRIPTIONS[1];
    }
}
