package YuukaMod.relics;

import YuukaMod.Yuukamod;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.PowerTip;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.relics.HappyFlower;
import com.megacrit.cardcrawl.relics.MagicFlower;
import com.megacrit.cardcrawl.relics.VioletLotus;

import java.lang.reflect.Method;

public class YuukaFumoDescriptionPatches {
    private static final String HINTS_KEY = Yuukamod.makeID("FumoHints");

    private static boolean isAffected(String relicId) {
        return HappyFlower.ID.equals(relicId)
                || VioletLotus.ID.equals(relicId)
                || MagicFlower.ID.equals(relicId)
                || SunflowerRelic.ID.equals(relicId)
                || DaisyRelic.ID.equals(relicId)
                || ChrysanthemumRelic.ID.equals(relicId)
                || RoseRelic.ID.equals(relicId)
                || VioletRelic.ID.equals(relicId)
                || BloomingSunflowersRelic.ID.equals(relicId);
    }

    private static boolean hasFumo() {
        return AbstractDungeon.player != null && AbstractDungeon.player.hasRelic(YuukaFumoRelic.ID);
    }

    private static int hintIndex(String relicId) {
        if (HappyFlower.ID.equals(relicId)) {
            return 0;
        }
        if (VioletLotus.ID.equals(relicId)) {
            return 1;
        }
        if (MagicFlower.ID.equals(relicId)) {
            return 2;
        }
        if (SunflowerRelic.ID.equals(relicId)) {
            return 3;
        }
        if (DaisyRelic.ID.equals(relicId)) {
            return 4;
        }
        if (ChrysanthemumRelic.ID.equals(relicId)) {
            return 5;
        }
        if (RoseRelic.ID.equals(relicId)) {
            return 6;
        }
        if (VioletRelic.ID.equals(relicId)) {
            return 7;
        }
        return 8;
    }

    private static String getHint(String relicId) {
        if (!isAffected(relicId)) {
            return null;
        }
        UIStrings ui = CardCrawlGame.languagePack.getUIString(HINTS_KEY);
        if (ui.TEXT == null || ui.TEXT.length < 9) {
            return null;
        }
        return ui.TEXT[hintIndex(relicId)];
    }

    private static String appendHint(String base, AbstractRelic relic) {
        if (!hasFumo()) {
            return base;
        }
        String hint = getHint(relic.relicId);
        if (hint == null) {
            return base;
        }
        return base + " NL #g" + hint;
    }

    private static void refreshRelic(AbstractRelic relic) {
        relic.description = relic.getUpdatedDescription();
        relic.tips.clear();
        relic.tips.add(new PowerTip(relic.name, relic.description));
        try {
            Method m = AbstractRelic.class.getDeclaredMethod("initializeTips");
            m.setAccessible(true);
            m.invoke(relic);
        } catch (Exception e) {
            Yuukamod.logger.error("Failed to rebuild tips for relic " + relic.relicId, e);
        }
    }

    private static void refreshAffectedRelics() {
        if (AbstractDungeon.player == null) {
            return;
        }
        for (AbstractRelic relic : AbstractDungeon.player.relics) {
            if (isAffected(relic.relicId)) {
                refreshRelic(relic);
            }
        }
    }

    @SpirePatch(clz = VioletLotus.class, method = "getUpdatedDescription")
    public static class VioletLotusDescriptionPatch {
        @SpirePostfixPatch
        public static String Postfix(String __result, VioletLotus __instance) {
            return appendHint(__result, __instance);
        }
    }

    @SpirePatch(clz = MagicFlower.class, method = "getUpdatedDescription")
    public static class MagicFlowerDescriptionPatch {
        @SpirePostfixPatch
        public static String Postfix(String __result, MagicFlower __instance) {
            return appendHint(__result, __instance);
        }
    }

    @SpirePatch(clz = HappyFlower.class, method = "setDescription", paramtypez = {AbstractPlayer.PlayerClass.class})
    public static class HappyFlowerDescriptionPatch {
        @SpirePostfixPatch
        public static String Postfix(String __result, HappyFlower __instance) {
            return appendHint(__result, __instance);
        }
    }

    @SpirePatch(clz = SunflowerRelic.class, method = "getUpdatedDescription")
    public static class SunflowerDescriptionPatch {
        @SpirePostfixPatch
        public static String Postfix(String __result, SunflowerRelic __instance) {
            return appendHint(__result, __instance);
        }
    }

    @SpirePatch(clz = DaisyRelic.class, method = "getUpdatedDescription")
    public static class DaisyDescriptionPatch {
        @SpirePostfixPatch
        public static String Postfix(String __result, DaisyRelic __instance) {
            return appendHint(__result, __instance);
        }
    }

    @SpirePatch(clz = ChrysanthemumRelic.class, method = "getUpdatedDescription")
    public static class ChrysanthemumDescriptionPatch {
        @SpirePostfixPatch
        public static String Postfix(String __result, ChrysanthemumRelic __instance) {
            return appendHint(__result, __instance);
        }
    }

    @SpirePatch(clz = RoseRelic.class, method = "getUpdatedDescription")
    public static class RoseDescriptionPatch {
        @SpirePostfixPatch
        public static String Postfix(String __result, RoseRelic __instance) {
            return appendHint(__result, __instance);
        }
    }

    @SpirePatch(clz = VioletRelic.class, method = "getUpdatedDescription")
    public static class VioletRelicDescriptionPatch {
        @SpirePostfixPatch
        public static String Postfix(String __result, VioletRelic __instance) {
            return appendHint(__result, __instance);
        }
    }

    @SpirePatch(clz = BloomingSunflowersRelic.class, method = "getUpdatedDescription")
    public static class BloomingSunflowersDescriptionPatch {
        @SpirePostfixPatch
        public static String Postfix(String __result, BloomingSunflowersRelic __instance) {
            return appendHint(__result, __instance);
        }
    }

    @SpirePatch(clz = AbstractRelic.class, method = "relicTip")
    public static class RefreshPatch {
        @SpirePostfixPatch
        public static void Postfix(AbstractRelic __instance) {
            if (!hasFumo() || !isAffected(__instance.relicId)) {
                return;
            }
            refreshAffectedRelics();
        }
    }
}
