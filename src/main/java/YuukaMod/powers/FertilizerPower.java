package YuukaMod.powers;

import YuukaMod.Yuukamod;
import YuukaMod.cards.BaseCard;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;

public class FertilizerPower extends BasePower {
    public static final String POWER_ID_BASE = Yuukamod.makeID(FertilizerPower.class.getSimpleName());
    private static final com.megacrit.cardcrawl.localization.PowerStrings powerStrings =
            com.megacrit.cardcrawl.core.CardCrawlGame.languagePack.getPowerStrings(POWER_ID_BASE);
    private static int globalInstanceCount = 0;

    public FertilizerPower(AbstractCreature owner) {
        super(POWER_ID_BASE, PowerType.BUFF, false, owner, 1);
        this.ID = POWER_ID_BASE + "_" + globalInstanceCount++;
    }

    @Override
    public void stackPower(int stackAmount) {
    }

    @Override
    public void onUseCard(AbstractCard card, UseCardAction action) {
        if (!card.hasTag(BaseCard.CustomTags.FLOWER) || !card.exhaust)
            return;

        flash();
        action.exhaustCard = false;
    }

    @Override
    public void updateDescription() {
        this.description = DESCRIPTIONS[0];
    }
}
