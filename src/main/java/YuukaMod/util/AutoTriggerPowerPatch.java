package YuukaMod.util;

import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;

@SpirePatch(clz = AbstractPower.class, method = "onPlayCard", paramtypez = {AbstractCard.class, AbstractMonster.class})
public class AutoTriggerPowerPatch {

    @SpirePrefixPatch
    public static SpireReturn<?> Prefix(AbstractPower __instance, AbstractCard card, AbstractMonster m) {
        if (AutoTriggerLimit.isAutoTriggered(card)) {
            return SpireReturn.Return(null);
        }
        return SpireReturn.Continue();
    }
}
