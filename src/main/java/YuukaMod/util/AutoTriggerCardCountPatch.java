package YuukaMod.util;

import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.actions.GameActionManager;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

@SpirePatch(clz = GameActionManager.class, method = "getNextAction", paramtypez = {})
public class AutoTriggerCardCountPatch {

    @SpirePostfixPatch
    public static void Postfix(GameActionManager __instance) {
        if (__instance.cardsPlayedThisTurn.isEmpty()) return;

        AbstractCard lastCard = __instance.cardsPlayedThisTurn.get(
                __instance.cardsPlayedThisTurn.size() - 1);
        if (AutoTriggerLimit.isAutoTriggered(lastCard)) {
            __instance.cardsPlayedThisTurn.remove(
                    __instance.cardsPlayedThisTurn.size() - 1);
            __instance.cardsPlayedThisCombat.remove(
                    __instance.cardsPlayedThisCombat.size() - 1);
            AbstractDungeon.player.cardsPlayedThisTurn--;
        }
    }
}
