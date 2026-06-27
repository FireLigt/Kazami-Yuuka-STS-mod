package YuukaMod.util;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

public class ContemptUtil {
    public static boolean isMasterDeckCard(AbstractCard card) {
        if (card == null || AbstractDungeon.player == null) return false;
        for (AbstractCard mc : AbstractDungeon.player.masterDeck.group) {
            if (mc.cardID.equals(card.cardID)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isTemporaryCard(AbstractCard card) {
        if (card == null) return false;
        return card.exhaust && card.isEthereal;
    }

    public static boolean shouldReduceCost(AbstractCard card) {
        if (card == null) return false;
        if (!isMasterDeckCard(card)) return false;
        if (isTemporaryCard(card)) return false;
        if (card.costForTurn <= 0 || card.cost < 0) return false;
        return true;
    }

    public static void reduceCostForTurn(AbstractCard card) {
        if (shouldReduceCost(card)) {
            card.setCostForTurn(Math.max(0, card.costForTurn - 1));
        }
    }
}
