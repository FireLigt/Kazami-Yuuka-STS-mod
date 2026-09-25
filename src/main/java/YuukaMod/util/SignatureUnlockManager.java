package YuukaMod.util;

import YuukaMod.Yuukamod;
import YuukaMod.cards.BaseCard;
import YuukaMod.powers.LLSPC98formPower;
import YuukaMod.powers.TheBeautiesOfNaturePower;
import com.evacipated.cardcrawl.modthespire.lib.SpireConfig;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.common.HealAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.DexterityPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import me.antileaf.signature.utils.SignatureHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;

public class SignatureUnlockManager {
    private static final Logger logger = LogManager.getLogger(Yuukamod.modID + ":SignatureUnlock");

    public static final String PURSUIT_ID = Yuukamod.makeID("Pursuit");
    public static final String FINISHING_MOVE_ID = Yuukamod.makeID("Finishing_move");
    public static final String BLOSSOMING_ID = Yuukamod.makeID("Blossoming_of_Gensokyo");
    public static final String CONTEMPT_ID = Yuukamod.makeID("Contempt");
    public static final String SMILING_ID = Yuukamod.makeID("Smiling");
    public static final String RELOAD_ID = Yuukamod.makeID("Reload");
    public static final String MANIPULATING_FLOWER_ID = Yuukamod.makeID("Manipulating_flower");
    public static final String LOW_SPEED_ID = Yuukamod.makeID("Low_speed");
    public static final String GENTLE_ID = Yuukamod.makeID("Gentle");
    public static final String FRAGRANCE_ID = Yuukamod.makeID("Fragrance");
    public static final String FAMED_FAR_ID = Yuukamod.makeID("Famed_far");
    public static final String WORTHY_OPPONENT_ID = Yuukamod.makeID("Worthy_opponent");
    public static final String BEAUTIES_ID = Yuukamod.makeID("The_beauties_of_nature");
    public static final String PARASOL_ID = Yuukamod.makeID("Parasol");
    public static final String LLS_PC98_ID = Yuukamod.makeID("LLS_PC98_form");
    public static final String GENSOU_SHUNKA_ID = Yuukamod.makeID("Gensou_shunka");

    private static final String CONFIG_NAME = "SignatureUnlockData";

    private static int smilingHealTotal = 0;
    private static int gentleUseCount = 0;
    private static int fragranceUseCount = 0;
    private static int parasolUseCount = 0;
    private static int gensouShunkaUseCount = 0;

    private static int pursuitKillsThisTurn = 0;
    private static String currentCardId = null;
    private static int blossomDamageThisUse = 0;
    private static int reloadDanmakuMoved = 0;
    private static int manipulatingFlowerDrawn = 0;
    private static int beautiesPowerAmount = 0;
    private static int famedFarDiscards = 0;

    private static boolean reloadTrackingActive = false;
    private static boolean manipFlowerTrackingActive = false;
    private static boolean beautiesTrackingActive = false;
    private static boolean famedFarTrackingActive = false;
    private static boolean worthyOpponentPlayed = false;
    private static boolean llsPC98PlayedThisBattle = false;

    private static final Set<String> unlocked = new HashSet<>();
    private static final Set<String> newlyUnlocked = new HashSet<>();

    public static void onBattleStart() {
        pursuitKillsThisTurn = 0;
        currentCardId = null;
        blossomDamageThisUse = 0;
        reloadDanmakuMoved = 0;
        manipulatingFlowerDrawn = 0;
        beautiesPowerAmount = 0;
        famedFarDiscards = 0;
        reloadTrackingActive = false;
        manipFlowerTrackingActive = false;
        beautiesTrackingActive = false;
        famedFarTrackingActive = false;
        worthyOpponentPlayed = false;
        llsPC98PlayedThisBattle = false;
    }

    public static void onTurnStart() {
        pursuitKillsThisTurn = 0;
        currentCardId = null;
    }

    public static void loadRunStats() {
        try {
            SpireConfig config = new SpireConfig(Yuukamod.modID, CONFIG_NAME);
            config.load();
            smilingHealTotal = config.getInt("smilingHealTotal");
            gentleUseCount = config.getInt("gentleUseCount");
            fragranceUseCount = config.getInt("fragranceUseCount");
            parasolUseCount = config.getInt("parasolUseCount");
            gensouShunkaUseCount = config.getInt("gensouShunkaUseCount");
        } catch (Exception e) {
            smilingHealTotal = 0;
            gentleUseCount = 0;
            fragranceUseCount = 0;
            parasolUseCount = 0;
            gensouShunkaUseCount = 0;
        }
    }

    public static void saveRunStats() {
        try {
            SpireConfig config = new SpireConfig(Yuukamod.modID, CONFIG_NAME);
            config.setInt("smilingHealTotal", smilingHealTotal);
            config.setInt("gentleUseCount", gentleUseCount);
            config.setInt("fragranceUseCount", fragranceUseCount);
            config.setInt("parasolUseCount", parasolUseCount);
            config.setInt("gensouShunkaUseCount", gensouShunkaUseCount);
            config.save();
        } catch (Exception e) {
            logger.error("Failed to save signature unlock data", e);
        }
    }

    public static void resetRunStats() {
        smilingHealTotal = 0;
        gentleUseCount = 0;
        fragranceUseCount = 0;
        parasolUseCount = 0;
        gensouShunkaUseCount = 0;
        unlocked.clear();
        newlyUnlocked.clear();
        saveRunStats();
    }

    public static void onNewRun() {
        resetRunStats();
    }

    public static void onCardPlayed(AbstractCard card) {
        if (card == null) return;
        String id = card.cardID;
        currentCardId = id;

        if (LOW_SPEED_ID.equals(id)) {
            AbstractPlayer p = AbstractDungeon.player;
            if (p != null) {
                AbstractPower dex = p.getPower(DexterityPower.POWER_ID);
                if (dex != null && dex.amount <= -1) {
                    tryUnlock(LOW_SPEED_ID);
                }
            }
        }

        if (GENTLE_ID.equals(id)) {
            gentleUseCount++;
            saveRunStats();
            if (gentleUseCount >= 5) tryUnlock(GENTLE_ID);
        }

        if (FRAGRANCE_ID.equals(id)) {
            fragranceUseCount++;
            saveRunStats();
            if (fragranceUseCount >= 20) tryUnlock(FRAGRANCE_ID);
        }

        if (PARASOL_ID.equals(id)) {
            parasolUseCount++;
            saveRunStats();
            if (parasolUseCount >= 10) tryUnlock(PARASOL_ID);
        }

        if (GENSOU_SHUNKA_ID.equals(id)) {
            gensouShunkaUseCount++;
            saveRunStats();
            if (gensouShunkaUseCount >= 10) tryUnlock(GENSOU_SHUNKA_ID);
        }

        if (LLS_PC98_ID.equals(id)) {
            llsPC98PlayedThisBattle = true;
        }

        if (WORTHY_OPPONENT_ID.equals(id)) {
            worthyOpponentPlayed = true;
        }

        if (RELOAD_ID.equals(id)) {
            reloadTrackingActive = true;
            reloadDanmakuMoved = 0;
        }

        if (MANIPULATING_FLOWER_ID.equals(id)) {
            manipFlowerTrackingActive = true;
            manipulatingFlowerDrawn = 0;
        }

        if (BEAUTIES_ID.equals(id)) {
            beautiesTrackingActive = true;
            beautiesPowerAmount = 0;
        }

        if (FAMED_FAR_ID.equals(id)) {
            famedFarTrackingActive = true;
            famedFarDiscards = 0;
        }

        if (BLOSSOMING_ID.equals(id)) {
            blossomDamageThisUse = 0;
        }
    }

    public static void onDamageAction(DamageAction action) {
        if (!(action.target instanceof AbstractMonster)) return;
        AbstractMonster target = (AbstractMonster) action.target;

        DamageInfo info = null;
        try {
            Field infoField = DamageAction.class.getDeclaredField("info");
            infoField.setAccessible(true);
            info = (DamageInfo) infoField.get(action);
        } catch (Exception e) {
            logger.error("Failed to access DamageAction.info", e);
            return;
        }
        if (info == null) return;

        if (PURSUIT_ID.equals(currentCardId) && target.isDeadOrEscaped()) {
            pursuitKillsThisTurn++;
            if (pursuitKillsThisTurn >= 3) tryUnlock(PURSUIT_ID);
        }

        if (FINISHING_MOVE_ID.equals(currentCardId) && target.isDeadOrEscaped()) {
            tryUnlock(FINISHING_MOVE_ID);
        }

        if (BLOSSOMING_ID.equals(currentCardId)) {
            blossomDamageThisUse += info.output;
            if (blossomDamageThisUse >= 100) tryUnlock(BLOSSOMING_ID);
        }
    }

    public static void onHealAction(HealAction action) {
        if (SMILING_ID.equals(currentCardId)) {
            smilingHealTotal += action.amount;
            saveRunStats();
            if (smilingHealTotal >= 50) tryUnlock(SMILING_ID);
        }
    }

    public static void onCardMovedToHand(AbstractCard card) {
        if (card == null) return;
        if (manipFlowerTrackingActive && MANIPULATING_FLOWER_ID.equals(currentCardId)) {
            manipulatingFlowerDrawn++;
            if (manipulatingFlowerDrawn >= 7) tryUnlock(MANIPULATING_FLOWER_ID);
        }
    }

    public static void onCardMovedToDrawPile(AbstractCard card) {
        if (card == null) return;
        if (reloadTrackingActive && RELOAD_ID.equals(currentCardId)) {
            if (card.hasTag(BaseCard.CustomTags.DANMAKU)) {
                reloadDanmakuMoved++;
                if (reloadDanmakuMoved >= 3) tryUnlock(RELOAD_ID);
            }
        }
    }

    public static void onCardAddedToDiscard(AbstractCard card) {
        if (card == null) return;
        if (famedFarTrackingActive && FAMED_FAR_ID.equals(currentCardId)) {
            if (!FAMED_FAR_ID.equals(card.cardID)) {
                famedFarDiscards++;
            }
        }
    }

    public static void onPowerApplied(AbstractPower power, com.megacrit.cardcrawl.core.AbstractCreature target,
                                       com.megacrit.cardcrawl.core.AbstractCreature source) {
        if (beautiesTrackingActive && BEAUTIES_ID.equals(currentCardId)) {
            if (target instanceof AbstractPlayer && target == source) {
                if (TheBeautiesOfNaturePower.POWER_ID.equals(power.ID)) {
                    beautiesPowerAmount += power.amount;
                    if (beautiesPowerAmount >= 3) tryUnlock(BEAUTIES_ID);
                }
            }
        }

        if (llsPC98PlayedThisBattle && LLSPC98formPower.POWER_ID.equals(power.ID)) {
            if (LLSPC98formPower.isLocalBossMusicActive()) {
                tryUnlock(LLS_PC98_ID);
            }
        }
    }

    public static void onTurnEnd() {
        if (famedFarTrackingActive && famedFarDiscards == 0) {
            tryUnlock(FAMED_FAR_ID);
        }

        reloadTrackingActive = false;
        manipFlowerTrackingActive = false;
        beautiesTrackingActive = false;
        famedFarTrackingActive = false;

        if (!SignatureHelper.isUnlocked(CONTEMPT_ID)) {
            AbstractPlayer p = AbstractDungeon.player;
            if (p != null) {
                boolean contemptPlayed = false;
                for (AbstractCard c : AbstractDungeon.actionManager.cardsPlayedThisTurn) {
                    if (CONTEMPT_ID.equals(c.cardID)) {
                        contemptPlayed = true;
                        break;
                    }
                }
                if (!contemptPlayed) {
                    for (AbstractCard c : p.hand.group) {
                        if (CONTEMPT_ID.equals(c.cardID)) {
                            tryUnlock(CONTEMPT_ID);
                            break;
                        }
                    }
                }
            }
        }
    }

    public static void onBattleEnd() {
        if (worthyOpponentPlayed) {
            AbstractPlayer p = AbstractDungeon.player;
            if (p != null) {
                AbstractPower str = p.getPower(StrengthPower.POWER_ID);
                if (str != null && str.amount >= 25) {
                    tryUnlock(WORTHY_OPPONENT_ID);
                }
            }
        }

        if (llsPC98PlayedThisBattle && LLSPC98formPower.isLocalBossMusicActive()) {
            tryUnlock(LLS_PC98_ID);
        }

        saveRunStats();
    }

    private static void tryUnlock(String cardId) {
        if (unlocked.contains(cardId)) return;
        if (SignatureHelper.isUnlocked(cardId)) {
            unlocked.add(cardId);
            return;
        }

        SignatureHelper.unlock(cardId, true);
        SignatureHelper.enable(cardId, true);
        unlocked.add(cardId);
        newlyUnlocked.add(cardId);
        logger.info("Signature unlocked: {}", cardId);
    }

    public static Set<String> getNewlyUnlocked() {
        return new HashSet<>(newlyUnlocked);
    }
}
