package YuukaMod.util;

import com.evacipated.cardcrawl.modthespire.Loader;
import com.evacipated.cardcrawl.modthespire.ModInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.powers.AbstractPower;
import YuukaMod.Yuukamod;
import org.clapper.util.classutil.ClassFinder;
import org.clapper.util.classutil.ClassFilter;
import org.clapper.util.classutil.ClassInfo;

import java.io.File;
import java.lang.reflect.Modifier;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class PowerLibrary {
    private static List<Class<? extends AbstractPower>> buffClasses;

    private static final Set<String> EXCLUDED = new HashSet<>(Arrays.asList(
            "com.megacrit.cardcrawl.powers.MinionPower",
            "com.megacrit.cardcrawl.powers.ModeShiftPower",
            "com.megacrit.cardcrawl.powers.SplitPower",
            "com.megacrit.cardcrawl.powers.RegrowPower",
            "com.megacrit.cardcrawl.powers.TimeWarpPower",
            "com.megacrit.cardcrawl.powers.StasisPower",
            "com.megacrit.cardcrawl.powers.UnawakenedPower",
            "com.megacrit.cardcrawl.powers.GrowthPower",
            "com.megacrit.cardcrawl.powers.SporeCloudPower",
            "com.megacrit.cardcrawl.powers.CuriosityPower",
            "com.megacrit.cardcrawl.powers.RegenerateMonsterPower",
            "com.megacrit.cardcrawl.powers.ExplosivePower",
            "com.megacrit.cardcrawl.powers.FadingPower",
            "com.megacrit.cardcrawl.powers.ReactivePower",
            "com.megacrit.cardcrawl.powers.FlightPower",
            "com.megacrit.cardcrawl.powers.CurlUpPower",
            "com.megacrit.cardcrawl.powers.InvinciblePower",
            "com.megacrit.cardcrawl.powers.RepairPower",
            "com.megacrit.cardcrawl.powers.ResurrectPower",
            "com.megacrit.cardcrawl.powers.RechargingCorePower",
            "com.megacrit.cardcrawl.powers.ShiftingPower",
            "com.megacrit.cardcrawl.powers.StrikeUpPower",
            "com.megacrit.cardcrawl.powers.ThieveryPower",
            "com.megacrit.cardcrawl.powers.TimeMazePower",
            "com.megacrit.cardcrawl.powers.HelloPower",
            "com.megacrit.cardcrawl.powers.CollectPower",
            "com.megacrit.cardcrawl.powers.ConservePower",
            "com.megacrit.cardcrawl.powers.NoDrawPower",
            "com.megacrit.cardcrawl.powers.HexPower",
            "com.megacrit.cardcrawl.powers.PainfulStabsPower",
            "com.megacrit.cardcrawl.powers.AttackBurnPower",
            "com.megacrit.cardcrawl.powers.SkillBurnPower",
            "com.megacrit.cardcrawl.powers.SharpHidePower",
            "com.megacrit.cardcrawl.powers.ForcefieldPower",
            "com.megacrit.cardcrawl.powers.TheBombPower"
    ));

    private PowerLibrary() {
    }

    public static List<Class<? extends AbstractPower>> getBuffClasses(AbstractCreature owner) {
        if (buffClasses == null || buffClasses.isEmpty()) {
            buffClasses = buildBuffPool(owner);
        }
        return buffClasses;
    }

    public static AbstractPower instantiate(Class<? extends AbstractPower> powerClass, AbstractCreature owner) {
        try {
            AbstractPower power = powerClass.getConstructor(AbstractCreature.class, int.class).newInstance(owner, 1);
            ensureImage(power);
            return power;
        } catch (Throwable t) {
            try {
                AbstractPower power = powerClass.getConstructor(AbstractCreature.class).newInstance(owner);
                ensureImage(power);
                return power;
            } catch (Throwable t2) {
                return null;
            }
        }
    }

    private static void ensureImage(AbstractPower power) {
        if (power.img == null && power.region48 == null && power.region128 == null) {
            power.img = TextureLoader.getTexture(Yuukamod.imagePath("missing.png"));
        }
    }

    private static List<Class<? extends AbstractPower>> buildBuffPool(AbstractCreature owner) {
        List<Class<? extends AbstractPower>> result = new ArrayList<>();
        try {
            ClassFinder finder = new ClassFinder();
            addJars(finder);
            final String abstractPowerName = AbstractPower.class.getName();
            List<ClassInfo> candidates = new ArrayList<>();
            finder.findClasses(candidates, new ClassFilter() {
                @Override
                public boolean accept(ClassInfo classInfo, ClassFinder classFinder) {
                    String name = classInfo.getClassName();
                    if (name == null || name.equals(abstractPowerName)) {
                        return false;
                    }
                    int flags = classInfo.getModifier();
                    if ((flags & (Modifier.ABSTRACT | Modifier.INTERFACE)) != 0) {
                        return false;
                    }
                    Map<String, ClassInfo> superClasses = new HashMap<>();
                    try {
                        classFinder.findAllSuperClasses(classInfo, superClasses);
                    } catch (Throwable t) {
                        return false;
                    }
                    return superClasses.containsKey(abstractPowerName);
                }
            });

            ClassLoader loader = getLoader();
            for (ClassInfo classInfo : candidates) {
                if (EXCLUDED.contains(classInfo.getClassName())) {
                    continue;
                }
                try {
                    Class<?> clazz = Class.forName(classInfo.getClassName(), false, loader);
                    if (AbstractPower.class.isAssignableFrom(clazz)) {
                        AbstractPower power = instantiate((Class<? extends AbstractPower>) clazz, owner);
                        if (power != null && power.type == AbstractPower.PowerType.BUFF) {
                            result.add((Class<? extends AbstractPower>) clazz);
                        }
                    }
                } catch (Throwable t) {
                    // ignore powers that cannot be loaded or constructed generically
                }
            }
        } catch (Throwable t) {
            // leave the pool empty; callers fall back to a default buff
        }
        return result;
    }

    private static void addJars(ClassFinder finder) {
        try {
            if (Loader.STS_JAR != null) {
                File gameJar = new File(Loader.STS_JAR);
                if (gameJar.isFile()) {
                    finder.add(gameJar);
                }
            }
        } catch (Throwable t) {
            // ignore
        }
        try {
            if (Loader.MODINFOS != null) {
                for (ModInfo modInfo : Loader.MODINFOS) {
                    if (modInfo == null || modInfo.jarURL == null) {
                        continue;
                    }
                    try {
                        File modJar = new File(modInfo.jarURL.toURI());
                        if (modJar.isFile()) {
                            finder.add(modJar);
                        }
                    } catch (URISyntaxException | IllegalArgumentException e) {
                        // ignore
                    }
                }
            }
        } catch (Throwable t) {
            // ignore
        }
    }

    private static ClassLoader getLoader() {
        try {
            ClassLoader loader = Loader.getClassPool().getClassLoader();
            if (loader != null) {
                return loader;
            }
        } catch (Throwable t) {
            // fall through
        }
        return AbstractPower.class.getClassLoader();
    }
}
