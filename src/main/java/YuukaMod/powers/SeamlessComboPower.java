package YuukaMod.powers;

import YuukaMod.cards.BaseCard;
import YuukaMod.Yuukamod;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

import java.util.ArrayList;

public class SeamlessComboPower extends BasePower {
    public static final String POWER_ID = Yuukamod.makeID(SeamlessComboPower.class.getSimpleName());

    private boolean upgraded;

    public SeamlessComboPower(AbstractCreature owner, boolean upgraded) {
        super(POWER_ID, PowerType.BUFF, false, owner, -1);
        this.upgraded = upgraded;
    }

    @Override
    public void onAfterCardPlayed(AbstractCard card) {
        if (card.hasTag(BaseCard.CustomTags.PHYSICAL_ARTS)) {
            flash();
            if (!(owner instanceof AbstractPlayer)) return;
            AbstractPlayer p = (AbstractPlayer) owner;

            ArrayList<AbstractCard> physicalArtsCards = new ArrayList<>();
            for (AbstractCard c : p.drawPile.group) {
                if (c.hasTag(BaseCard.CustomTags.PHYSICAL_ARTS)) {
                    physicalArtsCards.add(c);
                }
            }

            if (!physicalArtsCards.isEmpty()) {
                AbstractCard drawnCard = physicalArtsCards.get(
                        AbstractDungeon.cardRandomRng.random(physicalArtsCards.size() - 1)
                );

                p.drawPile.moveToHand(drawnCard);

                if (upgraded) {
                    drawnCard.setCostForTurn(Math.max(0, drawnCard.costForTurn - 1));
                }
            }
        }
    }

    @Override
    public void updateDescription() {
        this.description = DESCRIPTIONS[upgraded ? 1 : 0];
    }
}
