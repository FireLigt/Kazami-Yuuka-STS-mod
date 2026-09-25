package YuukaMod.relics;

import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.relics.AbstractRelic;

import static YuukaMod.Yuukamod.makeID;
import YuukaMod.relics.YuukaFumoRelic;
import YuukaMod.util.AutoTriggerLimit;

public class DaisyRelic extends BaseRelic {
    public static final String ID = makeID("DaisyRelic");
    private static final int CARDS_PER_TRIGGER = 5;
    private static final int BLOCK_AMOUNT = 10;
    private static final int FUMO_BLOCK_AMOUNT = 15;
    private int cardsPlayedThisTurn;

    public DaisyRelic() {
        super(ID, "DaisyRelic", RelicTier.COMMON, LandingSound.FLAT);
    }

    @Override
    public void atBattleStart() {
        cardsPlayedThisTurn = 0;
        setCounter(0);
    }

    @Override
    public void atTurnStart() {
        cardsPlayedThisTurn = 0;
        setCounter(0);
    }

    @Override
    public void onPlayCard(AbstractCard c, AbstractMonster m) {
        if (AutoTriggerLimit.isAutoTriggered(c)) return;
        cardsPlayedThisTurn++;
        setCounter(cardsPlayedThisTurn);
        int blockAmount = AbstractDungeon.player.hasRelic(YuukaFumoRelic.ID) ? FUMO_BLOCK_AMOUNT : BLOCK_AMOUNT;
        if (cardsPlayedThisTurn % CARDS_PER_TRIGGER == 0) {
            flash();
            AbstractDungeon.actionManager.addToBottom(new GainBlockAction(AbstractDungeon.player, AbstractDungeon.player, blockAmount));
        }
    }

    @Override
    public String getUpdatedDescription() {
        return DESCRIPTIONS[0];
    }

    @Override
    public AbstractRelic makeCopy() {
        return new DaisyRelic();
    }
}
