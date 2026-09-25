package YuukaMod.powers;

import YuukaMod.Yuukamod;
import YuukaMod.cards.BaseCard;
import YuukaMod.util.GeneralUtils;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;

public class MeetRetsuPower extends BasePower {
    public static final String POWER_ID = Yuukamod.makeID(MeetRetsuPower.class.getSimpleName());
    private static final com.megacrit.cardcrawl.localization.PowerStrings powerStrings =
            com.megacrit.cardcrawl.core.CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    private boolean upgraded;
    private int threshold;

    public MeetRetsuPower(AbstractCreature owner, boolean upgraded) {
        super(POWER_ID, PowerType.BUFF, false, owner, 0);
        this.upgraded = upgraded;
        this.threshold = upgraded ? 2 : 3;
        updateDescription();
    }

    @Override
    public void stackPower(int stackAmount) {
    }

    @Override
    public void onInitialApplication() {
        amount = threshold;
        updateDescription();
    }

    @Override
    public void onAfterCardPlayed(AbstractCard card) {
        if (!card.hasTag(BaseCard.CustomTags.PHYSICAL_ARTS)) return;

        amount--;
        if (amount <= 0) {
            flash();
            amount = threshold;

            AbstractCard copy = card.makeStatEquivalentCopy();
            copy.cost = 0;
            copy.costForTurn = 0;
            copy.isCostModified = true;
            GeneralUtils.makeTemporary(copy);

            if (owner instanceof AbstractPlayer) {
                AbstractPlayer p = (AbstractPlayer) owner;
                p.hand.addToHand(copy);
                p.hand.refreshHandLayout();
            }
        }
        updateDescription();
    }

    @Override
    public void updateDescription() {
        this.description = DESCRIPTIONS[upgraded ? 1 : 0];
    }
}
