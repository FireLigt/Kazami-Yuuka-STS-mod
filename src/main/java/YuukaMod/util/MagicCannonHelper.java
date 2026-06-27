package YuukaMod.util;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;

import YuukaMod.cards.BaseCard.CustomTags;

public class MagicCannonHelper {

    /*
     * Count MAGICCANNON cards in the deck
     */
    public static int countMagicCannons(AbstractPlayer p) {

        int amount = 0;

        for (AbstractCard c : p.masterDeck.group) {

            if (c.hasTag(CustomTags.MAGICCANNON)) {
                amount++;
            }
        }

        return amount;
    }
}
