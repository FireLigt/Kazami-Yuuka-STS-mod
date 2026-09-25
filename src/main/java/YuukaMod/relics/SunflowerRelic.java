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

    private int lastTurnEnergySpent;
    private int thisTurnEnergySpent;
    private boolean firstTurn;

    public SunflowerRelic() {
        super(ID, "SunflowerRelic", RelicTier.STARTER, LandingSound.FLAT);
    }

    @Override
    public void atBattleStart() {
        lastTurnEnergySpent = 0;
        thisTurnEnergySpent = 0;
        firstTurn = true;
        setCounter(-1);
    }

    @Override
    public void atTurnStart() {
        boolean fumoBoost = AbstractDungeon.player != null && AbstractDungeon.player.hasRelic(YuukaFumoRelic.ID);
        if (!firstTurn && (fumoBoost ? thisTurnEnergySpent >= lastTurnEnergySpent : thisTurnEnergySpent > lastTurnEnergySpent)) {
            flash();
            AbstractDungeon.actionManager.addToBottom(new GainEnergyAction(1));
        }
        lastTurnEnergySpent = thisTurnEnergySpent;
        thisTurnEnergySpent = 0;
        firstTurn = false;
    }

    @Override
    public void onPlayCard(AbstractCard c, AbstractMonster m) {
        if (c.costForTurn > 0 && !AutoTriggerLimit.isAutoTriggered(c)) {
            thisTurnEnergySpent += c.costForTurn;
            setCounter(thisTurnEnergySpent);
        }
    }

    @Override
    public void onPlayerEndTurn() {
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
