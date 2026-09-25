package YuukaMod.util;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

/**
 * Tracks whether a RecordPlayer phantom turn batch is currently executing its queued
 * card actions. While active, GameActionManager.update() is patched (see
 * PhantomTurnGuardPatches) to swap AbstractDungeon.player to the phantom proxy for the
 * duration of each frame's action processing, so custom mod actions that read player
 * state lazily at update-time (draw/discard/energy/gold/hand pickups) act on the isolated
 * phantom instead of the real player.
 *
 * Lifecycle:
 *  - Activated when the first card of the batch is queued (EnemyCardFilter), capturing
 *    the then-current real player.
 *  - Deactivated by the EndMarkerAction appended after the batch (RecordPlayer.takeTurn).
 *  - Deactivated immediately when any engine screen (reward/grid/hand-select) opens, so
 *    screen input and post-screen action continuations operate against the real player
 *    (e.g. a ChooseOne reward is delivered to the real player's hand).
 *
 * The swap is paired per update() frame: swapIn() in the prefix marks swapPending, and
 * swapOut() in the postfix restores the real player exactly once. This also guarantees
 * the phantom is never left as AbstractDungeon.player even if the guard is deactivated
 * mid-frame (screen open / marker).
 */
public class PhantomTurnGuard {
    private static boolean active = false;
    private static boolean swapPending = false;
    private static PhantomPlayer phantom;
    private static AbstractPlayer realPlayer;

    public static boolean isActive() {
        return active;
    }

    public static void activate(PhantomPlayer proxy) {
        active = true;
        phantom = proxy;
        realPlayer = AbstractDungeon.player;
    }

    public static void swapIn() {
        if (active && phantom != null) {
            AbstractDungeon.player = phantom;
            swapPending = true;
        }
    }

    public static void swapOut() {
        if (swapPending) {
            if (realPlayer != null) {
                AbstractDungeon.player = realPlayer;
            }
            swapPending = false;
        }
    }

    /**
     * Restores the real player immediately (used while the swap is still pending, e.g.
     * from a screen-open postfix or the EndMarkerAction, both of which run inside the
     * actionManager.update() frame).
     */
    public static void deactivateAndRestore() {
        swapOut();
        deactivate();
    }

    public static void deactivate() {
        active = false;
        phantom = null;
        realPlayer = null;
        swapPending = false;
    }

    /**
     * Sentinel action appended after the last card of a RecordPlayer turn batch. Runs
     * once all of the batch's actions have executed (the queue is FIFO), so it marks the
     * exact end of the guarded window.
     */
    public static class EndMarkerAction extends AbstractGameAction {
        @Override
        public void update() {
            deactivateAndRestore();
            this.isDone = true;
        }
    }
}
