package YuukaMod.util;

import YuukaMod.monsters.RecordPlayer;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.common.DamageAllEnemiesAction;
import com.megacrit.cardcrawl.actions.common.DiscardAction;
import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.actions.common.GainEnergyAction;
import com.megacrit.cardcrawl.actions.common.GainGoldAction;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInHandAction;
import com.megacrit.cardcrawl.actions.unique.SwordBoomerangAction;
import com.megacrit.cardcrawl.actions.utility.ScryAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.CardLibrary;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardBrieflyEffect;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class EnemyCardFilter {
    private static final Logger logger = LogManager.getLogger(EnemyCardFilter.class.getName());

    public static CardGroup buildEnemyDeck(RunRecorder.RunData runData) {
        CardGroup enemyDeck = new CardGroup(CardGroup.CardGroupType.MASTER_DECK);

        if (runData == null || runData.deck == null) {
            logger.warn("EnemyCardFilter: No run data or deck available");
            return enemyDeck;
        }

        for (RunRecorder.CardSaveData cardData : runData.deck) {
            if (cardData == null || cardData.id == null || cardData.id.isEmpty()) {
                continue;
            }

            AbstractCard card = CardLibrary.getCopy(cardData.id);
            if (card == null) {
                logger.warn("EnemyCardFilter: Could not find card with ID: " + cardData.id);
                continue;
            }

            String ignoreReason = ignorableEffectReason(card);
            if (ignoreReason != null && !hasWorkableEffect(card)) {
                logger.info("EnemyCardFilter: Skipping card with only ignorable " + ignoreReason + " effect: " + card.name);
                continue;
            }

            if (cardData.upgraded) {
                card.upgrade();
            }

            enemyDeck.addToBottom(card);
        }

        logger.info("EnemyCardFilter: Built enemy deck with " + enemyDeck.size() + " cards");
        return enemyDeck;
    }

    public static void executeCardForEnemy(AbstractCard card, AbstractMonster enemy, AbstractPlayer target) {
        if (card == null || enemy == null || target == null || AbstractDungeon.player == null) {
            logger.warn("EnemyCardFilter: Invalid execution context, skipping card");
            return;
        }

        PhantomPlayer proxy = new PhantomPlayer(enemy, target);
        PhantomPlayer.PlayerMonster wrapper = new PhantomPlayer.PlayerMonster(target);

        if (!PhantomTurnGuard.isActive()) {
            PhantomTurnGuard.activate(proxy);
        }

        int actionQueueStart = AbstractDungeon.actionManager.actions.size();

        applyYoumuComboCount(card, enemy);

        for (AbstractPower power : snapshotPowers(enemy)) {
            try {
                power.onPlayCard(card, wrapper);
            } catch (Throwable t) {
                logger.warn("EnemyCardFilter: onPlayCard failed for power " + power.ID + " - " + t.getMessage());
            }
        }

        mirrorCardPlayVfx(card, enemy);

        executeCardUse(card, proxy, wrapper);

        guardPlayerSideEffects(actionQueueStart, enemy, proxy, wrapper);

        if (card.type == AbstractCard.CardType.POWER && enemy instanceof RecordPlayer) {
            ((RecordPlayer) enemy).removeCardFromEnemyDeck(card);
        }

        for (AbstractPower power : snapshotPowers(enemy)) {
            try {
                power.onAfterCardPlayed(card);
            } catch (Throwable t) {
                logger.warn("EnemyCardFilter: onAfterCardPlayed failed for power " + power.ID + " - " + t.getMessage());
            }
        }
    }

    /**
     * Runs the card's real code (card.use) as if the phantom were the player: a PhantomPlayer
     * proxy takes the player slot (so damage/power hooks and AbstractDungeon.player reads inside
     * the card act on the phantom), while a PlayerMonster wrapper stands in as the card target
     * and forwards all damage/debuffs to the real player. card.calculateCardDamage is applied
     * before use so the phantom's powers (e.g. LLS PC98 form doubling) are baked into this.damage.
     */
    private static void executeCardUse(AbstractCard card, PhantomPlayer proxy, PhantomPlayer.PlayerMonster wrapper) {
        AbstractPlayer realPlayer = AbstractDungeon.player;
        try {
            AbstractDungeon.player = proxy;
            card.calculateCardDamage(wrapper);
            if (card.multiDamage != null) {
                for (int i = 0; i < card.multiDamage.length; i++) {
                    card.multiDamage[i] = card.damage;
                }
            }
            card.use(proxy, wrapper);
        } catch (Throwable t) {
            logger.warn("EnemyCardFilter: card.use failed for " + card.name + " - " + t.getMessage(), t);
        } finally {
            AbstractDungeon.player = realPlayer;
        }
    }

    private static void mirrorCardPlayVfx(AbstractCard card, AbstractMonster enemy) {
        try {
            AbstractDungeon.effectList.add(new ShowCardBrieflyEffect(
                    card.makeStatEquivalentCopy(),
                    enemy.drawX,
                    enemy.drawY + enemy.hb.height + 200.0F * Settings.scale));
        } catch (Throwable t) {
            logger.warn("EnemyCardFilter: Could not mirror card play VFX - " + t.getMessage());
        }
    }

    /**
     * The phantom is a monster, so any player-side resource effect its card queued (draw,
     * energy, gold, cards into hand) must be neutralized. Scry/Discard effects that would read
     * the real player's hand/draw pile at update time (and open selection screens against them)
     * are skipped. Self-targeted effects keep the phantom as their target (the phantom is the
     * "player" for the card's perspective), so self-debuffs land on the phantom, i.e. the boss.
     *
     * Damage dealt to the real player is attributed to the host monster: single-target attacks
     * get their DamageInfo.owner rewritten from the PhantomPlayer proxy to the host, and AoE
     * damage is routed to the real player as a separate attack owned by the host. This makes
     * player-side defenses that key off the attacker (e.g. Alice dolls, which block only when
     * DamageInfo.owner is an AbstractMonster) treat phantom attacks exactly like real monster
     * attacks.
     */
    private static void guardPlayerSideEffects(int startIndex, AbstractMonster enemy, PhantomPlayer proxy, PhantomPlayer.PlayerMonster wrapper) {
        ArrayList<AbstractGameAction> queue = AbstractDungeon.actionManager.actions;
        int endIndex = queue.size();
        for (int i = startIndex; i < endIndex; i++) {
            AbstractGameAction action = queue.get(i);
            if (action instanceof ScryAction) {
                action.isDone = true;
            } else if (action instanceof DiscardAction) {
                action.amount = 0;
            } else if (action instanceof DrawCardAction || action instanceof MakeTempCardInHandAction || action instanceof GainGoldAction) {
                action.amount = 0;
            } else if (action instanceof GainEnergyAction) {
                neutralizeEnergyGain((GainEnergyAction) action);
            } else if (action instanceof DamageAction) {
                DamageAction damageAction = (DamageAction) action;
                if (damageAction.target == wrapper) {
                    rewriteDamageOwner(damageAction, proxy, enemy);
                }
            } else if (action instanceof SwordBoomerangAction) {
                SwordBoomerangAction sba = (SwordBoomerangAction) action;
                if (sba.target == enemy) {
                    i += redirectSwordBoomerang(sba, i, queue, enemy, proxy, wrapper);
                }
            } else if (action instanceof DamageAllEnemiesAction) {
                DamageAllEnemiesAction aoe = (DamageAllEnemiesAction) action;
                int playerDamage = (aoe.damage != null && aoe.damage.length > 0) ? aoe.damage[0] : 0;
                if (playerDamage > 0) {
                    queue.add(i + 1, new DamageAction(wrapper, new DamageInfo(enemy, playerDamage, aoe.damageType), aoe.attackEffect));
                    i++;
                }
            }
        }
    }

    private static void rewriteDamageOwner(DamageAction action, PhantomPlayer proxy, AbstractMonster enemy) {
        try {
            Field field = DamageAction.class.getDeclaredField("info");
            field.setAccessible(true);
            DamageInfo info = (DamageInfo) field.get(action);
            if (info != null && info.owner == proxy) {
                info.owner = enemy;
            }
        } catch (Throwable t) {
            logger.warn("EnemyCardFilter: Could not attribute phantom damage to host - " + t.getMessage());
        }
    }

    /**
     * SwordBoomerangAction (vanilla Sword Boomerang, Youmu SanzenSekai, etc.) resolves its target
     * from AbstractDungeon.getMonsters().getRandomMonster and re-rolls a new random monster on
     * every hit. In the phantom fight the only monster is the phantom itself, so the action would
     * hit the phantom's own body and get ignored by RecordPlayer.damage(). Replace it with one
     * DamageAction per hit aimed at the real player's wrapper (VFX lands on the player), computing
     * the damage the same way the action would so powers apply exactly once.
     */
    private static int redirectSwordBoomerang(SwordBoomerangAction sba, int index, ArrayList<AbstractGameAction> queue,
            AbstractMonster enemy, PhantomPlayer proxy, PhantomPlayer.PlayerMonster wrapper) {
        try {
            DamageInfo info = readSwordBoomerangInfo(sba);
            int times = readSwordBoomerangTimes(sba);
            if (info == null || times <= 0) {
                return 0;
            }
            if (info.owner == proxy) {
                info.owner = enemy;
            }
            DamageInfo computed = new DamageInfo(enemy, info.base, info.type);
            computed.applyPowers(enemy, wrapper);
            int damage = computed.output;
            queue.remove(index);
            index--;
            for (int k = 0; k < times; k++) {
                queue.add(index + 1, new DamageAction(wrapper, new DamageInfo(enemy, damage, info.type), sba.attackEffect));
                index++;
            }
            return times;
        } catch (Throwable t) {
            logger.warn("EnemyCardFilter: Could not redirect SwordBoomerangAction to real player - " + t.getMessage());
            return 0;
        }
    }

    private static DamageInfo readSwordBoomerangInfo(SwordBoomerangAction sba) {
        try {
            Field field = SwordBoomerangAction.class.getDeclaredField("info");
            field.setAccessible(true);
            return (DamageInfo) field.get(sba);
        } catch (Throwable t) {
            logger.warn("EnemyCardFilter: Could not read SwordBoomerangAction info - " + t.getMessage());
            return null;
        }
    }

    private static int readSwordBoomerangTimes(SwordBoomerangAction sba) {
        try {
            Field field = SwordBoomerangAction.class.getDeclaredField("numTimes");
            field.setAccessible(true);
            return field.getInt(sba);
        } catch (Throwable t) {
            logger.warn("EnemyCardFilter: Could not read SwordBoomerangAction numTimes - " + t.getMessage());
            return 0;
        }
    }

    private static void neutralizeEnergyGain(GainEnergyAction action) {
        try {
            Field field = GainEnergyAction.class.getDeclaredField("energyGain");
            field.setAccessible(true);
            field.setInt(action, 0);
        } catch (Throwable t) {
            logger.warn("EnemyCardFilter: Could not neutralize energy gain - " + t.getMessage());
        }
    }

    private static List<AbstractPower> snapshotPowers(AbstractMonster enemy) {
        if (enemy.powers == null || enemy.powers.isEmpty()) {
            return new ArrayList<>();
        }
        return new ArrayList<>(enemy.powers);
    }

    /**
     * Replicates KonpakuYoumu's combo-counter progression for the phantom's own card plays.
     * The real player's combo is granted by YMmod.receiveCardUsed (keyed on AbstractDungeon.player),
     * which never fires for the phantom; this keeps a Youmu phantom's combos in sync with its own
     * attacks/skills instead of (or rather: no longer) the real player's.
     */
    private static void applyYoumuComboCount(AbstractCard card, AbstractMonster enemy) {
        if (!(enemy instanceof RecordPlayer)) {
            return;
        }
        String characterClass = ((RecordPlayer) enemy).getSavedCharacterClass();
        if (characterClass == null || !characterClass.equalsIgnoreCase("YOUMU")) {
            return;
        }
        String base = comboBaseForType(card.type);
        if (base == null || enemy.powers == null) {
            return;
        }
        String id = base;
        if (enemy.hasPower(id)) {
            for (int i = 2; i <= 6; i++) {
                id = base + i;
                if (!enemy.hasPower(id)) {
                    break;
                }
            }
        }
        AbstractPower countPower = instantiateCountPower("powers." + id, enemy);
        if (countPower == null) {
            return;
        }
        AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(enemy, enemy, countPower, 1));
    }

    private static String comboBaseForType(AbstractCard.CardType type) {
        switch (type) {
            case ATTACK:
                return "AttackCountPower";
            case SKILL:
                return "SkillCountPower";
            case POWER:
                return "PowerCountPower";
            case STATUS:
                return "StatusCountPower";
            case CURSE:
                return "CurseCountPower";
            default:
                return null;
        }
    }

    private static AbstractPower instantiateCountPower(String className, AbstractMonster enemy) {
        try {
            Class<?> cls = Class.forName(className);
            Constructor<?> ctor = cls.getDeclaredConstructor(AbstractCreature.class);
            ctor.setAccessible(true);
            Object instance = ctor.newInstance(enemy);
            if (instance instanceof AbstractPower) {
                return (AbstractPower) instance;
            }
        } catch (Throwable t) {
            logger.warn("EnemyCardFilter: Could not apply Youmu combo power " + className + " - " + t.getMessage());
        }
        return null;
    }

    private static String ignorableEffectReason(AbstractCard card) {
        if (card.exhaust) {
            return "exhaust";
        }

        String description = card.rawDescription;
        if (description == null || description.isEmpty()) {
            return null;
        }
        String s = description.toLowerCase(Locale.ROOT);

        if (s.contains("draw")) {
            return "draw";
        }
        if (s.contains("exhaust")) {
            return "exhaust";
        }
        if (s.contains("discard")) {
            return "discard";
        }
        if (s.contains("energy")) {
            return "energy";
        }
        if (s.contains("scry")) {
            return "scry";
        }
        if (s.contains("cost") && (s.contains("-1") || s.contains("reduc") || s.contains("lower"))) {
            return "reduce energy used";
        }
        if (isDeckManipulating(s)) {
            return "deck manipulation";
        }
        return null;
    }

    private static boolean isDeckManipulating(String s) {
        return s.contains("merge")
                || s.contains("remove this copy")
                || s.contains("shuffle")
                || s.contains("pile");
    }

    private static boolean hasWorkableEffect(AbstractCard card) {
        if (card.baseDamage > 0 || card.baseBlock > 0) {
            return true;
        }
        String desc = card.rawDescription;
        if (desc == null) {
            return false;
        }
        return desc.contains("Weak") || desc.contains("Vulnerable")
                || desc.contains("Strength") || desc.contains("No Manners") || desc.contains("no_manners")
                || desc.contains("Fragile") || desc.contains("fragile");
    }
}
