package YuukaMod.relics;

import YuukaMod.util.AutoTriggerLimit;
import com.megacrit.cardcrawl.actions.common.GainEnergyAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.relics.AbstractRelic;

import static YuukaMod.Yuukamod.makeID;

public class SunflowerRelic extends BaseRelic {
    public static final String ID = makeID("SunflowerRelic");
    private static final int ENERGY_THRESHOLD = 3;

    private int energySpentThisTurn;
    private int bonusEnergy;

    public SunflowerRelic() {
        super(ID, "SunflowerRelic", RelicTier.STARTER, LandingSound.FLAT);
    }

    @Override
    public void atBattleStart() {
        energySpentThisTurn = 0;
        bonusEnergy = 0;
        setCounter(0);
    }

    @Override
    public void atTurnStart() {
        if (bonusEnergy > 0) {
            flash();
            AbstractDungeon.actionManager.addToBottom(new GainEnergyAction(bonusEnergy));
        }
        energySpentThisTurn = 0;
        bonusEnergy = 0;
        setCounter(0);
    }

    @Override
    public void onPlayCard(AbstractCard c, AbstractMonster m) {
        if (c.costForTurn > 0 && !AutoTriggerLimit.isAutoTriggered(c)) {
            energySpentThisTurn += c.costForTurn;
            setCounter(energySpentThisTurn);
        }
    }

    @Override
    public void onPlayerEndTurn() {
        bonusEnergy = energySpentThisTurn / ENERGY_THRESHOLD;
    }

    @Override
    public String getUpdatedDescription() {
        return DESCRIPTIONS[0];
    }

    @Override
    public AbstractRelic makeCopy() {
        return new SunflowerRelic();
    }
}
