package YuukaMod.powers;

import YuukaMod.Yuukamod;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;

public class SunflowerPower extends BasePower {
    public static final String POWER_ID_BASE = Yuukamod.makeID(SunflowerPower.class.getSimpleName());
    private static int globalInstanceCount = 0;

    public SunflowerPower(AbstractCreature owner, int amount) {
        super(POWER_ID_BASE, PowerType.BUFF, false, owner, amount);
        this.ID = POWER_ID_BASE + "_" + globalInstanceCount++;
    }

    @Override
    public void stackPower(int stackAmount) {
    }

    @Override
    public void atStartOfTurnPostDraw() {
        flash();
        if (owner instanceof AbstractPlayer) {
            ((AbstractPlayer) owner).gainEnergy(1);
        }
        amount--;
        if (amount <= 0) {
            addToTop(new RemoveSpecificPowerAction(owner, owner, this.ID));
        }
        updateDescription();
    }

    @Override
    public void updateDescription() {
        this.description = DESCRIPTIONS[0] + amount + DESCRIPTIONS[1];
    }
}
