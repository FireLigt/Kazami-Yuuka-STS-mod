package YuukaMod.relics;

import YuukaMod.util.AutoTriggerLimit;
import com.megacrit.cardcrawl.actions.common.GainEnergyAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.relics.AbstractRelic;

import static YuukaMod.Yuukamod.makeID;

public class BloomingSunflowersRelic extends BaseRelic {
    public static final String ID = makeID("BloomingSunflowersRelic");
    private static final int ENERGY_THRESHOLD = 3;

    private int cumulativeSpent;
    private int pendingBonus;

    public BloomingSunflowersRelic() {
        super(ID, "BloomingSunflowersRelic", RelicTier.BOSS, LandingSound.FLAT);
    }

    @Override
    public void onEquip() {
        for (AbstractRelic r : AbstractDungeon.player.relics) {
            if (r.relicId.equals(SunflowerRelic.ID)) {
                AbstractDungeon.player.loseRelic(r.relicId);
                break;
            }
        }
    }

    @Override
    public void atBattleStart() {
        cumulativeSpent = 0;
        pendingBonus = 0;
        setCounter(0);
    }

    @Override
    public void atTurnStart() {
        if (pendingBonus > 0) {
            flash();
            AbstractDungeon.actionManager.addToBottom(new GainEnergyAction(pendingBonus));
            pendingBonus = 0;
        }
    }

    @Override
    public void onPlayCard(AbstractCard c, AbstractMonster m) {
        if (c.costForTurn > 0 && !AutoTriggerLimit.isAutoTriggered(c)) {
            cumulativeSpent += c.costForTurn;
            int triggered = cumulativeSpent / ENERGY_THRESHOLD;
            if (triggered > 0) {
                pendingBonus += triggered;
                cumulativeSpent -= triggered * ENERGY_THRESHOLD;
            }
            setCounter(cumulativeSpent);
        }
    }

    @Override
    public String getUpdatedDescription() {
        return DESCRIPTIONS[0];
    }

    @Override
    public AbstractRelic makeCopy() {
        return new BloomingSunflowersRelic();
    }
}
