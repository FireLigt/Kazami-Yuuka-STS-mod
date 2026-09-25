package YuukaMod.util;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.megacrit.cardcrawl.actions.GameActionManager;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.common.HealAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class SignatureUnlockPatch {

    @SpirePatch(clz = AbstractPlayer.class, method = "useCard",
            paramtypez = {AbstractCard.class, AbstractMonster.class, int.class})
    public static class UseCardPatch {
        @SpirePrefixPatch
        public static void Prefix(AbstractPlayer __instance, AbstractCard c, AbstractMonster monster, int energyOnUse) {
            if (c != null) {
                SignatureUnlockManager.onCardPlayed(c);
            }
        }
    }

    @SpirePatch(clz = DamageAction.class, method = "update")
    public static class DamageActionPatch {
        @SpirePostfixPatch
        public static void Postfix(DamageAction __instance) {
            if (__instance.isDone) {
                SignatureUnlockManager.onDamageAction(__instance);
            }
        }
    }

    @SpirePatch(clz = HealAction.class, method = "update")
    public static class HealActionPatch {
        @SpirePostfixPatch
        public static void Postfix(HealAction __instance) {
            if (__instance.isDone) {
                SignatureUnlockManager.onHealAction(__instance);
            }
        }
    }

    @SpirePatch(clz = AbstractPlayer.class, method = "applyStartOfTurnRelics")
    public static class TurnStartPatch {
        @SpirePostfixPatch
        public static void Postfix(AbstractPlayer __instance) {
            SignatureUnlockManager.onTurnStart();
        }
    }

    @SpirePatch(clz = GameActionManager.class, method = "endTurn")
    public static class TurnEndPatch {
        @SpirePostfixPatch
        public static void Postfix() {
            SignatureUnlockManager.onTurnEnd();
        }
    }

    @SpirePatch(clz = CardGroup.class, method = "moveToHand",
            paramtypez = {AbstractCard.class})
    public static class CardMovedToHandPatch {
        @SpirePostfixPatch
        public static void Postfix(CardGroup __instance, AbstractCard card) {
            if (__instance == AbstractDungeon.player.drawPile) {
                SignatureUnlockManager.onCardMovedToHand(card);
            }
        }
    }

    @SpirePatch(clz = CardGroup.class, method = "moveToDeck",
            paramtypez = {AbstractCard.class, boolean.class})
    public static class CardMovedToDrawPilePatch {
        @SpirePostfixPatch
        public static void Postfix(CardGroup __instance, AbstractCard card, boolean binarySearch) {
            if (__instance == AbstractDungeon.player.discardPile) {
                SignatureUnlockManager.onCardMovedToDrawPile(card);
            }
        }
    }

    @SpirePatch(clz = CardGroup.class, method = "addToTop",
            paramtypez = {AbstractCard.class})
    public static class CardAddedToDiscardPatch {
        @SpirePostfixPatch
        public static void Postfix(CardGroup __instance, AbstractCard card) {
            if (__instance == AbstractDungeon.player.discardPile) {
                SignatureUnlockManager.onCardAddedToDiscard(card);
            }
        }
    }
}
