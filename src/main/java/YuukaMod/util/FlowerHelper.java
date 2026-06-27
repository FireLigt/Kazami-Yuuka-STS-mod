package YuukaMod.util;

import YuukaMod.cards.BaseCard.CustomTags;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;

public class FlowerHelper {

    /*
     * 统计牌组中的“Flower”卡数量
     */
    public static int countFlower(AbstractPlayer p) {

        int amount = 0;

        for (AbstractCard c : p.masterDeck.group) {

            if (c.hasTag(CustomTags.FLOWER)) {
                amount++;
            }
        }

        return amount;
    }
}
