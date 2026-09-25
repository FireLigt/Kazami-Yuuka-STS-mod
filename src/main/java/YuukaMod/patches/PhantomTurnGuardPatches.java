package YuukaMod.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.actions.GameActionManager;
import com.megacrit.cardcrawl.core.OverlayMenu;
import com.megacrit.cardcrawl.ui.panels.EnergyPanel;
import YuukaMod.util.PhantomTurnGuard;

/**
 * Engine hooks that keep the RecordPlayer phantom's action batch isolated from the real
 * player while it drains (see PhantomTurnGuard).
 *
 * GameActionManager.update(): swaps AbstractDungeon.player to the phantom only for
 * EXECUTING_ACTIONS frames (i.e. while queued actions run). WAITING_ON_USER frames are
 * left untouched so the guard never leaks into other monsters' takeTurn() calls or the
 * card queue, and so cards queued during a phantom batch still capture the real player as
 * their target.
 *
 * OverlayMenu.showBlackScreen(): every engine pick screen (reward/grid/hand-select) opens
 * through here. Hard-deactivating the guard on screen open ensures screen input and the
 * post-screen continuation of a ChooseOne-style action run against the real player (e.g.
 * a picked card is delivered to the real player's hand, never the phantom's).
 *
 * EnergyPanel: static energy mutations during a guarded frame would otherwise write to the
 * real player's displayed energy; they are no-ops while the guard is active (phantom energy
 * changes go through AbstractDungeon.player.energy and are isolated to the phantom).
 */
public class PhantomTurnGuardPatches {

    @SpirePatch(clz = GameActionManager.class, method = "update")
    public static class ActionManagerUpdatePatch {
        @SpirePrefixPatch
        public static void Prefix(GameActionManager __instance) {
            if (__instance.phase == GameActionManager.Phase.EXECUTING_ACTIONS) {
                PhantomTurnGuard.swapIn();
            }
        }

        @SpirePostfixPatch
        public static void Postfix(GameActionManager __instance) {
            PhantomTurnGuard.swapOut();
        }
    }

    @SpirePatch(clz = OverlayMenu.class, method = "showBlackScreen", paramtypez = {})
    public static class ShowBlackScreenPatch {
        @SpirePostfixPatch
        public static void Postfix(OverlayMenu __instance) {
            PhantomTurnGuard.deactivateAndRestore();
        }
    }

    @SpirePatch(clz = OverlayMenu.class, method = "showBlackScreen", paramtypez = {float.class})
    public static class ShowBlackScreenFloatPatch {
        @SpirePostfixPatch
        public static void Postfix(OverlayMenu __instance, float time) {
            PhantomTurnGuard.deactivateAndRestore();
        }
    }

    @SpirePatch(clz = EnergyPanel.class, method = "setEnergy", paramtypez = {int.class})
    public static class EnergyPanelSetPatch {
        @SpirePrefixPatch
        public static SpireReturn<Void> Prefix(int amount) {
            if (PhantomTurnGuard.isActive()) {
                return SpireReturn.Return();
            }
            return SpireReturn.Continue();
        }
    }

    @SpirePatch(clz = EnergyPanel.class, method = "addEnergy", paramtypez = {int.class})
    public static class EnergyPanelAddPatch {
        @SpirePrefixPatch
        public static SpireReturn<Void> Prefix(int amount) {
            if (PhantomTurnGuard.isActive()) {
                return SpireReturn.Return();
            }
            return SpireReturn.Continue();
        }
    }

    @SpirePatch(clz = EnergyPanel.class, method = "useEnergy", paramtypez = {int.class})
    public static class EnergyPanelUsePatch {
        @SpirePrefixPatch
        public static SpireReturn<Void> Prefix(int amount) {
            if (PhantomTurnGuard.isActive()) {
                return SpireReturn.Return();
            }
            return SpireReturn.Continue();
        }
    }
}
