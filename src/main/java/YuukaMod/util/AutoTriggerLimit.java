package YuukaMod.util;

import YuukaMod.cards.BaseCard;
import com.megacrit.cardcrawl.actions.utility.NewQueueCardAction;
import com.megacrit.cardcrawl.actions.utility.WaitAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class AutoTriggerLimit {
    private static final int MAX_TRIGGERS = 3;

    private static final Set<UUID> autoTriggeredCards = new HashSet<>();
    private static final Set<UUID> magicCannonTriggeredDanmakuCards = new HashSet<>();
    private static int lastClearedTurn = -1;

    public static void reset() {
        autoTriggeredCards.clear();
        magicCannonTriggeredDanmakuCards.clear();
        lastClearedTurn = -1;
    }

    private static void clearIfNewTurn() {
        int turn = com.megacrit.cardcrawl.actions.GameActionManager.turn;
        if (turn > lastClearedTurn) {
            autoTriggeredCards.clear();
            magicCannonTriggeredDanmakuCards.clear();
            lastClearedTurn = turn;
        }
    }

    public static void onMagicCannonPlayed(AbstractCard sourceCard) {
        AbstractPlayer p = AbstractDungeon.player;
        if (p == null) return;

        clearIfNewTurn();

        List<AbstractCard> targets = collectDanmakuTargets(p);
        if (targets.isEmpty()) return;

        for (AbstractCard target : targets) {
            magicCannonTriggeredDanmakuCards.add(target.uuid);
        }

        HitoutArea.startSequence(sourceCard, targets);

        for (AbstractCard target : targets) {
            fireCard(target);
        }
    }

    public static void onFlowerPlayed(AbstractCard sourceCard) {
        AbstractPlayer p = AbstractDungeon.player;
        if (p == null) return;

        clearIfNewTurn();

        List<AbstractCard> targets = collectPlantTargets(p);
        if (targets.isEmpty()) return;

        HitoutArea.startSequence(sourceCard, targets);

        for (AbstractCard target : targets) {
            fireCard(target);
        }
    }

    public static void triggerAll(AbstractPlayer p, AbstractCard sourceCard) {
        if (p == null) return;

        clearIfNewTurn();

        List<AbstractCard> allTargets = new ArrayList<>();

        for (AbstractCard c : p.hand.group) {
            if (isTriggerable(c) && !autoTriggeredCards.contains(c.uuid)) {
                allTargets.add(c);
            }
        }

        for (AbstractCard c : p.drawPile.group) {
            if (c.upgraded && isTriggerable(c) && !autoTriggeredCards.contains(c.uuid)) {
                allTargets.add(c);
            }
        }

        if (allTargets.isEmpty()) return;

        HitoutArea.startSequence(sourceCard, allTargets);

        for (AbstractCard target : allTargets) {
            fireCard(target);
        }
    }

    private static List<AbstractCard> collectDanmakuTargets(AbstractPlayer p) {
        List<AbstractCard> result = new ArrayList<>();
        for (AbstractCard c : p.hand.group) {
            if (c.hasTag(BaseCard.CustomTags.DANMAKU) && !autoTriggeredCards.contains(c.uuid)) {
                result.add(c);
                if (result.size() >= MAX_TRIGGERS) break;
            }
        }
        if (result.size() < MAX_TRIGGERS) {
            for (AbstractCard c : p.drawPile.group) {
                if (c.upgraded && c.hasTag(BaseCard.CustomTags.DANMAKU) && !autoTriggeredCards.contains(c.uuid)) {
                    result.add(c);
                    if (result.size() >= MAX_TRIGGERS) break;
                }
            }
        }
        return result;
    }

    private static List<AbstractCard> collectPlantTargets(AbstractPlayer p) {
        List<AbstractCard> result = new ArrayList<>();
        for (AbstractCard c : p.hand.group) {
            if (c.hasTag(BaseCard.CustomTags.PLANT) && !autoTriggeredCards.contains(c.uuid)) {
                result.add(c);
                if (result.size() >= MAX_TRIGGERS) break;
            }
        }
        if (result.size() < MAX_TRIGGERS) {
            for (AbstractCard c : p.drawPile.group) {
                if (c.upgraded && c.hasTag(BaseCard.CustomTags.PLANT) && !autoTriggeredCards.contains(c.uuid)) {
                    result.add(c);
                    if (result.size() >= MAX_TRIGGERS) break;
                }
            }
        }
        return result;
    }

    private static boolean isTriggerable(AbstractCard c) {
        return c.hasTag(BaseCard.CustomTags.DANMAKU) || c.hasTag(BaseCard.CustomTags.PLANT);
    }

    public static void fireCard(AbstractCard card) {
        AbstractPlayer p = AbstractDungeon.player;
        if (p == null) return;
        if (p.drawPile.contains(card)) {
            p.drawPile.removeCard(card);
        }
        card.freeToPlayOnce = true;
        markAutoTriggered(card);

        AbstractDungeon.actionManager.addToBottom(new NewQueueCardAction(card, true, false, false));
        AbstractDungeon.actionManager.addToBottom(new WaitAction(0.3F));
    }

    public static void markAutoTriggered(AbstractCard card) {
        if (card != null) {
            autoTriggeredCards.add(card.uuid);
        }
    }

    public static boolean isAutoTriggered(AbstractCard card) {
        return card != null && autoTriggeredCards.contains(card.uuid);
    }

    public static boolean consumeAutoTriggered(AbstractCard card) {
        return card != null && autoTriggeredCards.remove(card.uuid);
    }

    public static void markMagicCannonTriggeredDanmaku(AbstractCard card) {
        if (card != null) {
            magicCannonTriggeredDanmakuCards.add(card.uuid);
        }
    }

    public static boolean consumeMagicCannonTriggeredDanmaku(AbstractCard card) {
        return card != null && magicCannonTriggeredDanmakuCards.remove(card.uuid);
    }
}
