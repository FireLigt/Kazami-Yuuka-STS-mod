package YuukaMod.relics;

import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.AbstractRelic;

import static YuukaMod.Yuukamod.makeID;
import YuukaMod.util.AutoTriggerLimit;

public class RoseRelic extends BaseRelic {
    public static final String ID = makeID("RoseRelic");
    private static final int EXHAUST_PER_TRIGGER = 2;
    private int exhaustsThisTurn;
    private int bonusDraw;
    private int drawnThisCombat;

    public RoseRelic() {
        super(ID, "RoseRelic", RelicTier.UNCOMMON, LandingSound.FLAT);
    }

    @Override
    public void atBattleStart() {
        exhaustsThisTurn = 0;
        bonusDraw = 0;
        drawnThisCombat = 0;
        setCounter(0);
    }

    @Override
    public void atTurnStart() {
        if (!AbstractDungeon.player.hasRelic(YuukaFumoRelic.ID)) {
            exhaustsThisTurn = 0;
            setCounter(0);
        }
    }

    @Override
    public void atTurnStartPostDraw() {
        if (bonusDraw > 0) {
            flash();
            AbstractDungeon.actionManager.addToBottom(new DrawCardAction(AbstractDungeon.player, bonusDraw));
            drawnThisCombat += bonusDraw;
            bonusDraw = 0;
        }
    }

    @Override
    public void onPlayerEndTurn() {
        if (AbstractDungeon.player.hasRelic(YuukaFumoRelic.ID)) {
            bonusDraw = Math.max(0, exhaustsThisTurn / EXHAUST_PER_TRIGGER - drawnThisCombat);
        } else {
            bonusDraw = exhaustsThisTurn / EXHAUST_PER_TRIGGER;
        }
    }

    @Override
    public void onExhaust(AbstractCard c) {
        if (AutoTriggerLimit.isAutoTriggered(c)) return;
        exhaustsThisTurn++;
        setCounter(exhaustsThisTurn);
    }

    @Override
    public String getUpdatedDescription() {
        return DESCRIPTIONS[0];
    }

    @Override
    public AbstractRelic makeCopy() {
        return new RoseRelic();
    }
}
