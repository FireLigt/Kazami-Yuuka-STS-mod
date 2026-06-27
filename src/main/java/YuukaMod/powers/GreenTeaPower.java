package YuukaMod.powers;

import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.PowerStrings;

import static YuukaMod.Yuukamod.makeID;

public class GreenTeaPower extends BasePower {
    public static final String POWER_ID = makeID("GreenTeaPower");
    private static final PowerStrings strings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);

    public GreenTeaPower(AbstractCreature owner, int amount) {
        super(POWER_ID, PowerType.BUFF, false, owner, amount);
        updateDescription();
    }

    @Override
    public void onUseCard(AbstractCard card, com.megacrit.cardcrawl.actions.utility.UseCardAction action) {
        if (!card.exhaust && !card.exhaustOnUseOnce) return;
        if (amount <= 0) return;

        card.exhaust = false;
        card.exhaustOnUseOnce = false;
        amount--;

        if (amount <= 0) {
            flash();
            addToTop(new RemoveSpecificPowerAction(owner, owner, this));
        } else {
            flash();
            updateDescription();
        }
    }

    @Override
    public void updateDescription() {
        description = strings.DESCRIPTIONS[0] + amount + strings.DESCRIPTIONS[1];
    }
}
