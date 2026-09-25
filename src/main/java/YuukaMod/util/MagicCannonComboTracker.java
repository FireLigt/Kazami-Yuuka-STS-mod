package YuukaMod.util;

import YuukaMod.Yuukamod;
import YuukaMod.cards.BaseCard;
import YuukaMod.cards.basic.Small_magic_cannon;
import YuukaMod.cards.common.Magic_cannon;
import YuukaMod.cards.common.Second_magic_cannon;
import YuukaMod.cards.uncommon.Big_magic_cannon;
import YuukaMod.cards.uncommon.Large_magic_cannon;
import YuukaMod.cards.uncommon.Giant_magic_cannon;
import YuukaMod.cards.rare.Ultra_magic_cannon;
import YuukaMod.cards.special.Mega_magic_cannon;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInHandAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

import java.util.HashMap;
import java.util.Map;

public class MagicCannonComboTracker {
    private static final Map<AbstractPlayer, Integer> playerSteps = new HashMap<>();

    private static final Class<?>[] SEQUENCE = {
        Small_magic_cannon.class,
        Magic_cannon.class,
        Second_magic_cannon.class,
        Big_magic_cannon.class,
        Large_magic_cannon.class,
        Giant_magic_cannon.class,
        Ultra_magic_cannon.class
    };

    private static AbstractPlayer currentPlayer() {
        return AbstractDungeon.player;
    }

    private static int getStep() {
        Integer s = playerSteps.get(currentPlayer());
        return s == null ? 0 : s;
    }

    private static void setStep(int step) {
        if (step == 0) {
            playerSteps.remove(currentPlayer());
        } else {
            playerSteps.put(currentPlayer(), step);
        }
    }

    public static void record(AbstractCard card) {
        if (AbstractDungeon.getCurrRoom() == null) return;

        boolean anyAlive = false;
        for (AbstractMonster m : AbstractDungeon.getMonsters().monsters) {
            if (!m.isDeadOrEscaped()) {
                anyAlive = true;
                break;
            }
        }
        if (!anyAlive) return;

        int step = getStep();
        if (step < SEQUENCE.length && card.getClass().equals(SEQUENCE[step])) {
            step++;
            if (step == SEQUENCE.length) {
                AbstractDungeon.actionManager.addToBottom(new MakeTempCardInHandAction(new Mega_magic_cannon()));
                step = 0;
                Yuukamod.onEasterEggTriggered();
            }
            setStep(step);
        } else if (!card.getClass().equals(SEQUENCE[0])) {
            setStep(0);
        }
    }

    public static void onAnyCardPlayed(AbstractCard cardPlayed) {
        int step = getStep();
        if (step > 0 && !cardPlayed.hasTag(BaseCard.CustomTags.MAGICCANNON)) {
            if (cardPlayed.freeToPlayOnce) {
                return;
            }
            setStep(0);
        }
    }

    public static void reset() {
        AbstractPlayer p = currentPlayer();
        if (p != null) {
            playerSteps.remove(p);
        }
    }

    public static void clearAll() {
        playerSteps.clear();
    }
}
