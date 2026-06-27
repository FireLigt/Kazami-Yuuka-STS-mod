package YuukaMod.powers;

import YuukaMod.cards.BaseCard;
import YuukaMod.cards.uncommon.Suika;
import YuukaMod.Yuukamod;
import YuukaMod.util.GeneralUtils;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.CardLibrary;

import java.util.ArrayList;

public class CrazyYuukaPower extends BasePower {
    public static final String POWER_ID = Yuukamod.makeID(CrazyYuukaPower.class.getSimpleName());

    private boolean upgraded;

    public CrazyYuukaPower(AbstractCreature owner, int amount, boolean upgraded) {
        super(POWER_ID, PowerType.BUFF, false, owner, amount);
        this.upgraded = upgraded;
    }

    @Override
    public void onAfterCardPlayed(AbstractCard card) {
        if (card.hasTag(BaseCard.CustomTags.PLANT)) {
            if (card instanceof Suika && ((Suika) card).autoTriggered) {
                ((Suika) card).autoTriggered = false;
                return;
            }
            flash();

            ArrayList<AbstractCard> plantCards = new ArrayList<>();
            for (AbstractCard c : CardLibrary.cards.values()) {
                if (c.hasTag(BaseCard.CustomTags.PLANT)) {
                    plantCards.add(c);
                }
            }

            if (!plantCards.isEmpty()) {
                for (int i = 0; i < amount; i++) {
                    addToBot(new AbstractGameAction() {
                        @Override
                        public void update() {
                            AbstractCard c = plantCards.get(
                                    AbstractDungeon.cardRandomRng.random(plantCards.size() - 1)
                            ).makeStatEquivalentCopy();
                            if (upgraded && c.canUpgrade()) {
                                c.upgrade();
                            }
                            GeneralUtils.makeTemporary(c);
                            AbstractDungeon.player.hand.addToTop(c);
                            AbstractDungeon.player.hand.refreshHandLayout();
                            this.isDone = true;
                        }
                    });
                }
            }
        }
    }

    @Override
    public void updateDescription() {
        this.description = DESCRIPTIONS[upgraded ? 1 : 0];
    }
}
