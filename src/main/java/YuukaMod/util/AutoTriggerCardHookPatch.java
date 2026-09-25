package YuukaMod.util;

import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

@SpirePatch(clz = AbstractCard.class, method = "onPlayCard", paramtypez = {AbstractCard.class, AbstractMonster.class})
public class AutoTriggerCardHookPatch {

    @SpirePrefixPatch
    public static SpireReturn<?> Prefix(AbstractCard __instance, AbstractCard card, AbstractMonster m) {
        if (AutoTriggerLimit.isAutoTriggered(card)) {
            return SpireReturn.Return(null);
        }
        return SpireReturn.Continue();
    }
}
