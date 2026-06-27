package YuukaMod.powers;

import YuukaMod.Yuukamod;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.powers.AbstractPower;

public class OwnerOfMugenkanPower extends BasePower {
    public static final String POWER_ID = Yuukamod.makeID(OwnerOfMugenkanPower.class.getSimpleName());

    private boolean upgraded;

    public OwnerOfMugenkanPower(AbstractCreature owner, boolean upgraded) {
        super(POWER_ID, PowerType.BUFF, false, owner, -1);
        this.upgraded = upgraded;
    }

    @Override
    public void atEndOfTurn(boolean isPlayer) {
        if (!isPlayer) return;

        int highestBuff = 0;
        for (AbstractPower p : owner.powers) {
            if (p.type == PowerType.BUFF && p.amount > highestBuff) {
                highestBuff = p.amount;
            }
        }

        if (highestBuff > 0) {
            flash();
            int block = upgraded ? highestBuff : highestBuff / 2;
            addToBot(new GainBlockAction(owner, owner, block));
        }
    }

    @Override
    public void updateDescription() {
        this.description = DESCRIPTIONS[upgraded ? 1 : 0];
    }
}
