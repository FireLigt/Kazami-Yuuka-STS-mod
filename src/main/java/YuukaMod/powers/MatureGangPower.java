package YuukaMod.powers;

import YuukaMod.Yuukamod;
import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.core.AbstractCreature;

public class MatureGangPower extends BasePower {
    public static final String POWER_ID = Yuukamod.makeID(MatureGangPower.class.getSimpleName());

    public MatureGangPower(AbstractCreature owner, int amount) {
        super(POWER_ID, PowerType.BUFF, false, owner, amount);
    }

    @Override
        public void stackPower(int stackAmount) {
            // Prevent stacking: ignore attempts to stack this power
            Yuukamod.logger.info("MatureGangPower: stack attempt ignored");
        }

        @Override
        public void atStartOfTurnPostDraw() {
            flash();
            // Draw extra cards after the normal draw
            addToBot(new DrawCardAction(owner, amount));
        }

    @Override
    public void updateDescription() {
        this.description = DESCRIPTIONS[0] + amount + DESCRIPTIONS[1];
    }
}
