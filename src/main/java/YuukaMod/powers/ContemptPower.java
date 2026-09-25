package YuukaMod.powers;

import YuukaMod.Yuukamod;
import YuukaMod.util.ContemptUtil;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

public class ContemptPower extends BasePower {
    public static final String POWER_ID = Yuukamod.makeID(ContemptPower.class.getSimpleName());
    private static final com.megacrit.cardcrawl.localization.PowerStrings powerStrings =
            com.megacrit.cardcrawl.core.CardCrawlGame.languagePack.getPowerStrings(POWER_ID);

    public ContemptPower(AbstractCreature owner) {
        super(POWER_ID, PowerType.BUFF, false, owner, 1);
    }

    @Override
    public void updateDescription() {
        this.description = DESCRIPTIONS[0];
    }

    @Override
    public void onInitialApplication() {
        for (AbstractCard c : new java.util.ArrayList<>(AbstractDungeon.player.hand.group)) {
            ContemptUtil.reduceCostForTurn(c);
        }
        AbstractDungeon.player.hand.refreshHandLayout();
        AbstractDungeon.player.hand.applyPowers();
    }

    @Override
    public void onCardDraw(AbstractCard card) {
        ContemptUtil.reduceCostForTurn(card);
    }

    @Override
    public void atEndOfTurn(boolean isPlayer) {
        AbstractDungeon.actionManager.addToBottom(new RemoveSpecificPowerAction(this.owner, this.owner, POWER_ID));
    }
}
