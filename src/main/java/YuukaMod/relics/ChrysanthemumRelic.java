package YuukaMod.relics;

import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.RegenPower;
import com.megacrit.cardcrawl.relics.AbstractRelic;

import static YuukaMod.Yuukamod.makeID;

public class ChrysanthemumRelic extends BaseRelic {
    public static final String ID = makeID("ChrysanthemumRelic");
    private static final int BLOCK_PER_TRIGGER = 10;
    private static final int REGEN_AMOUNT = 2;
    private float lastBlock;
    private int blockGainedThisTurn;

    public ChrysanthemumRelic() {
        super(ID, "ChrysanthemumRelic", RelicTier.UNCOMMON, LandingSound.FLAT);
    }

    @Override
    public void atBattleStart() {
        lastBlock = 0;
        blockGainedThisTurn = 0;
        setCounter(0);
    }

    @Override
    public void atTurnStart() {
        lastBlock = 0;
        blockGainedThisTurn = 0;
        setCounter(0);
    }

    @Override
    public void update() {
        super.update();
        if (AbstractDungeon.player != null) {
            float current = AbstractDungeon.player.currentBlock;
            if (current > lastBlock) {
                blockGainedThisTurn += (int) (current - lastBlock);
                setCounter(blockGainedThisTurn);
            }
            lastBlock = current;
        }
    }

    @Override
    public void onPlayerEndTurn() {
        int stacks = blockGainedThisTurn / BLOCK_PER_TRIGGER;
        if (stacks > 0) {
            flash();
            int regenAmount = AbstractDungeon.player.hasRelic(YuukaFumoRelic.ID) ? REGEN_AMOUNT + 1 : REGEN_AMOUNT;
        AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(AbstractDungeon.player, AbstractDungeon.player,
                    new RegenPower(AbstractDungeon.player, stacks * regenAmount), stacks * regenAmount));
        }
    }

    @Override
    public String getUpdatedDescription() {
        return DESCRIPTIONS[0];
    }

    @Override
    public AbstractRelic makeCopy() {
        return new ChrysanthemumRelic();
    }
}
