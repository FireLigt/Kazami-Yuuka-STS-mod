package YuukaMod.util;

import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PowerMirror {
    private static final Logger logger = LogManager.getLogger(PowerMirror.class.getName());

    private static final Set<String> SKIPPED_POWER_CLASSES = new HashSet<>(Arrays.asList(
            "YuukaMod.powers.LLSPC98formPower",
            "YuukaMod.powers.LLSPC98formCooldown",
            "YuukaMod.powers.HoneyMilkPower"
    ));

    static {
        // KonpakuYoumu combo counters (powers.AttackCountPower .. CurseCountPower6) must build
        // only from the phantom's own card plays, so they are never mirrored from the real player.
        String[] comboBases = {"AttackCountPower", "SkillCountPower", "PowerCountPower", "StatusCountPower", "CurseCountPower"};
        for (String base : comboBases) {
            SKIPPED_POWER_CLASSES.add("powers." + base);
            for (int i = 2; i <= 6; i++) {
                SKIPPED_POWER_CLASSES.add("powers." + base + i);
            }
        }
    }

    public static void applyPowersToMonster(AbstractMonster monster, List<RunRecorder.PowerSaveData> savedPowers) {
        if (monster == null || savedPowers == null || savedPowers.isEmpty()) {
            return;
        }
        int applied = 0;
        for (RunRecorder.PowerSaveData saved : savedPowers) {
            if (saved == null || saved.powerClass == null || saved.powerClass.isEmpty()) {
                continue;
            }
            if (SKIPPED_POWER_CLASSES.contains(saved.powerClass)) {
                logger.info("PowerMirror: Skipping persistent power " + saved.powerClass);
                continue;
            }
            AbstractPower power = instantiate(saved.powerClass, monster, saved.amount);
            if (power != null) {
                monster.powers.add(power);
                applied++;
                logger.info("PowerMirror: Mirrored power " + power.ID + " (" + saved.powerClass + ", " + saved.amount + ") onto " + monster.name);
            }
        }
        if (applied > 0) {
            logger.info("PowerMirror: Applied " + applied + " mirrored powers onto " + monster.name);
        }
    }

    /**
     * Instantiates a fresh copy of a power class onto the given monster.
     */
    private static AbstractPower instantiate(String powerClass, AbstractMonster monster, int amount) {
        Class<?> cls;
        try {
            cls = Class.forName(powerClass);
        } catch (Throwable t) {
            logger.warn("PowerMirror: Power class not available: " + powerClass + " - " + t.getMessage());
            return null;
        }

        Constructor<?> best = null;
        Object[] bestArgs = null;
        for (Constructor<?> ctor : cls.getDeclaredConstructors()) {
            Object[] args = matchArgs(ctor, monster, amount);
            if (args != null && (best == null || ctor.getParameterCount() < best.getParameterCount())) {
                best = ctor;
                bestArgs = args;
            }
        }

        if (best == null) {
            logger.warn("PowerMirror: No supported constructor for power " + powerClass);
            return null;
        }

        try {
            best.setAccessible(true);
            Object instance = best.newInstance(bestArgs);
            if (instance instanceof AbstractPower) {
                return (AbstractPower) instance;
            }
        } catch (Throwable t) {
            logger.warn("PowerMirror: Failed to instantiate power " + powerClass + " - " + t.getMessage());
        }
        return null;
    }

    private static Object[] matchArgs(Constructor<?> ctor, AbstractMonster monster, int amount) {
        Class<?>[] params = ctor.getParameterTypes();
        if (params.length == 0) {
            return null;
        }
        if (!AbstractCreature.class.isAssignableFrom(params[0])) {
            return null;
        }
        Object[] args = new Object[params.length];
        args[0] = monster;
        for (int i = 1; i < params.length; i++) {
            Class<?> p = params[i];
            if (p == int.class || p == Integer.class) {
                args[i] = amount;
            } else if (p == float.class || p == Float.class) {
                args[i] = (float) amount;
            } else if (AbstractCreature.class.isAssignableFrom(p)) {
                args[i] = monster;
            } else {
                return null;
            }
        }
        return args;
    }
}
