package YuukaMod.relics;

import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.AbstractRelic;

import static YuukaMod.Yuukamod.makeID;

public class RoseRelic extends BaseRelic {
    public static final String ID = makeID("RoseRelic");
    private static final int EXHAUST_PER_TRIGGER = 2;
    private int exhaustsThisTurn;
    private int bonusDraw;

    public RoseRelic() {
        super(ID, "RoseRelic", RelicTier.UNCOMMON, LandingSound.FLAT);
    }

    @Override
    public void atBattleStart() {
        exhaustsThisTurn = 0;
        bonusDraw = 0;
        setCounter(0);
    }

    @Override
    public void atTurnStart() {
        exhaustsThisTurn = 0;
        setCounter(0);
    }

    @Override
    public void atTurnStartPostDraw() {
        if (bonusDraw > 0) {
            flash();
            AbstractDungeon.actionManager.addToBottom(new DrawCardAction(AbstractDungeon.player, bonusDraw));
            bonusDraw = 0;
        }
    }

    @Override
    public void onPlayerEndTurn() {
        int threshold = AbstractDungeon.player.hasRelic(YuukaFumoRelic.ID) ? 1 : EXHAUST_PER_TRIGGER;
        bonusDraw = exhaustsThisTurn / threshold;
    }

    @Override
    public void onExhaust(AbstractCard c) {
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
