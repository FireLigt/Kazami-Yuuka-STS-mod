package YuukaMod.powers;

import YuukaMod.Yuukamod;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ReducePowerAction;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.powers.AbstractPower;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class NoMannersPower extends BasePower {
    public static final String POWER_ID = Yuukamod.makeID(NoMannersPower.class.getSimpleName());
    private static final com.megacrit.cardcrawl.localization.PowerStrings powerStrings =
            com.megacrit.cardcrawl.core.CardCrawlGame.languagePack.getPowerStrings(POWER_ID);

    private ArrayList<String> existingBuffIDs = new ArrayList<>();
    private Map<String, Integer> existingBuffAmounts = new HashMap<>();

    public NoMannersPower(AbstractCreature owner) {
        super(POWER_ID, PowerType.DEBUFF, false, owner, 1);
    }

    @Override
    public void onInitialApplication() {
        for (AbstractPower p : owner.powers) {
            if (p.type == PowerType.BUFF) {
                existingBuffIDs.add(p.ID);
                existingBuffAmounts.put(p.ID, p.amount);
            }
        }
    }

    @Override
    public void onApplyPower(AbstractPower power, AbstractCreature target, AbstractCreature source) {
        if (amount > 0 && target == owner && power.type == PowerType.BUFF) {
            flash();
            addToBot(new AbstractGameAction() {
                @Override
                public void update() {
                    sweepBuffs();
                    this.isDone = true;
                }
            });
        }
    }

    @Override
    public void atStartOfTurn() {
        sweepBuffs();
    }

    @Override
    public void stackPower(int stackAmount) {
        this.amount = 1;
    }

    @Override
    public void updateDescription() {
        this.description = DESCRIPTIONS[0];
    }

    private void sweepBuffs() {
        for (AbstractPower p : new ArrayList<>(owner.powers)) {
            if (p.type != PowerType.BUFF) {
                continue;
            }
            if (!existingBuffIDs.contains(p.ID)) {
                addToTop(new RemoveSpecificPowerAction(owner, owner, p.ID));
                continue;
            }
            int baseline = existingBuffAmounts.getOrDefault(p.ID, p.amount);
            if (baseline >= 0 && p.amount > baseline) {
                addToTop(new ReducePowerAction(owner, owner, p.ID, p.amount - baseline));
            }
        }
    }
}
