package YuukaMod.relics;

import com.badlogic.gdx.math.MathUtils;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.actions.common.GainEnergyAction;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.HappyFlower;
import com.megacrit.cardcrawl.relics.MagicFlower;
import com.megacrit.cardcrawl.relics.VioletLotus;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.stances.AbstractStance;

public class YuukaFumoPatches {

    @SpirePatch(clz = HappyFlower.class, method = "atTurnStart")
    public static class HappyFlowerPatch {
        @SpirePostfixPatch
        public static void Postfix(HappyFlower __instance) {
            if (AbstractDungeon.player != null && AbstractDungeon.player.hasRelic(YuukaFumoRelic.ID)) {
                if (__instance.counter == 0) {
                    AbstractDungeon.actionManager.addToBottom(new GainEnergyAction(1));
                }
            }
        }
    }

    @SpirePatch(clz = VioletLotus.class, method = "onChangeStance", paramtypez = {
            AbstractStance.class,
            AbstractStance.class
    })
    public static class VioletLotusPatch {
        @SpirePostfixPatch
        public static void Postfix(VioletLotus __instance, AbstractStance prevStance, AbstractStance newStance) {
            if (AbstractDungeon.player != null && AbstractDungeon.player.hasRelic(YuukaFumoRelic.ID)) {
                if (!prevStance.ID.equals(newStance.ID) && prevStance.ID.equals("Calm")) {
                    AbstractDungeon.actionManager.addToBottom(new GainEnergyAction(1));
                }
            }
        }
    }

    @SpirePatch(clz = MagicFlower.class, method = "onPlayerHeal")
    public static class MagicFlowerPatch {
        @SpirePostfixPatch
        public static int Postfix(int __result, MagicFlower __instance, int healAmount) {
            if (AbstractDungeon.player != null && AbstractDungeon.player.hasRelic(YuukaFumoRelic.ID)) {
                if (AbstractDungeon.currMapNode != null && AbstractDungeon.getCurrRoom().phase == AbstractRoom.RoomPhase.COMBAT) {
                    return MathUtils.round((float) healAmount * 1.75F);
                }
            }
            return __result;
        }
    }
}
