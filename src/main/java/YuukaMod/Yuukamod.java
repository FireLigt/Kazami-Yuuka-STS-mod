package YuukaMod;

import YuukaMod.cards.BaseCard;
import YuukaMod.events.MagicCannonEvent;
import YuukaMod.events.ParasolEvent;
import YuukaMod.events.RecordPlayerEvent;
import YuukaMod.events.TataraBlacksmith;
import YuukaMod.events.UsedCampfireEvent;
import YuukaMod.monsters.RecordPlayer;
import YuukaMod.cards.power.LLS_PC98_form;
import YuukaMod.cards.rare.Infinite_spiral;
import YuukaMod.cards.special.Mega_magic_cannon;
import YuukaMod.cards.special.Mima;
import YuukaMod.cards.special.Shinki;
import YuukaMod.cards.special.Small_suika;
import YuukaMod.cards.special.Yumemi;
import YuukaMod.cards.uncommon.Bouquet;
import YuukaMod.cards.uncommon.Suika;
import YuukaMod.character.KazamiYuuka;
import YuukaMod.powers.HoneyMilkPower;
import YuukaMod.powers.LLSPC98formCooldown;
import YuukaMod.powers.LLSPC98formPower;
import YuukaMod.powers.YumemiPower;
import YuukaMod.potions.BlackTea;
import YuukaMod.potions.BombPotion;
import YuukaMod.potions.GreenTea;
import YuukaMod.potions.HoneyMilk;
import YuukaMod.potions.PureWater;
import YuukaMod.potions.ScentedTea;
import YuukaMod.potions.Tea;
import YuukaMod.relics.MegaMagicCannonRelic;
import YuukaMod.relics.ParasolBuffRelic;
import YuukaMod.relics.ParasolDebuffRelic;
import YuukaMod.relics.BloomingSunflowersRelic;
import YuukaMod.relics.ChrysanthemumRelic;
import YuukaMod.relics.DaisyRelic;
import YuukaMod.relics.RoseRelic;
import YuukaMod.relics.SunflowerRelic;
import YuukaMod.relics.VioletRelic;
import YuukaMod.relics.YuukaFumoRelic;
import com.megacrit.cardcrawl.cutscenes.CutscenePanel;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.evacipated.cardcrawl.modthespire.lib.SpireConfig;
import basemod.AutoAdd;
import basemod.BaseMod;
import basemod.ModLabel;
import basemod.ModPanel;
import basemod.eventUtil.AddEventParams;

import basemod.interfaces.*;
import com.badlogic.gdx.graphics.Color;
import YuukaMod.util.AutoTriggerLimit;
import YuukaMod.util.GeneralUtils;
import YuukaMod.util.SignatureUnlockManager;
import YuukaMod.util.SignatureUnlockSubscriber;
import YuukaMod.util.YuukaSignatureHelper;
import YuukaMod.util.KeywordInfo;
import YuukaMod.util.MagicCannonComboTracker;
import YuukaMod.util.RunRecorder;
import YuukaMod.util.Sounds;
import YuukaMod.util.TogetherInSpireCompat;
import YuukaMod.util.TextureLoader;
import com.badlogic.gdx.Files;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl.LwjglFileHandle;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.evacipated.cardcrawl.modthespire.Loader;
import com.evacipated.cardcrawl.modthespire.ModInfo;
import com.evacipated.cardcrawl.modthespire.Patcher;
import com.evacipated.cardcrawl.modthespire.lib.SpireInitializer;
import com.google.gson.Gson;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.rooms.MonsterRoomBoss;

import com.megacrit.cardcrawl.rewards.RewardItem;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.relics.AbstractRelic.RelicTier;
import com.megacrit.cardcrawl.localization.*;
import com.badlogic.gdx.math.MathUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.scannotation.AnnotationDB;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.charset.StandardCharsets;
import java.util.*;

@SpireInitializer
public class Yuukamod implements
        EditCardsSubscriber,
        EditCharactersSubscriber,
        EditStringsSubscriber,
        EditKeywordsSubscriber,
        EditRelicsSubscriber,
        AddAudioSubscriber,
        OnStartBattleSubscriber,
        PostBattleSubscriber,
        PostInitializeSubscriber,
        StartGameSubscriber,
        PostPowerApplySubscriber {
    public static ModInfo info;
    public static String modID; //Edit your pom.xml to change this
    static { loadModInfo(); }
    private static final String resourcesFolder = checkResourcesPath();
    public static final Logger logger = LogManager.getLogger(modID); //Used to output to the console.

    public static SpireConfig modConfig;
    private static boolean easterEggEverTriggered = false;
    private static boolean megaCannonRelicMode = false;

    static {
        try {
            modConfig = new SpireConfig(modID, "YuukaModConfig");
            modConfig.load();
            easterEggEverTriggered = modConfig.getBool("easterEggEverTriggered");
            megaCannonRelicMode = modConfig.getBool("megaCannonRelicMode");
        } catch (Exception e) {
            logger.error("Failed to load mod config, using defaults", e);
            if (modConfig != null) {
                modConfig.setBool("easterEggEverTriggered", false);
                modConfig.setBool("megaCannonRelicMode", false);
                try { modConfig.save(); } catch (Exception ignored) {}
            }
        }
    }

    public static void onEasterEggTriggered() {
        if (!easterEggEverTriggered) {
            easterEggEverTriggered = true;
            if (modConfig != null) {
                modConfig.setBool("easterEggEverTriggered", true);
                try { modConfig.save(); } catch (Exception e) {
                    logger.error("Failed to save easter egg flag", e);
                }
            }
        }
    }

    public static boolean isEasterEggTriggered() {
        return easterEggEverTriggered;
    }

    public static boolean isMegaCannonRelicMode() {
        return megaCannonRelicMode;
    }

    //This is used to prefix the IDs of various objects like cards and relics,
    //to avoid conflicts between different mods using the same name for things.
    public static String makeID(String id) {
        return modID + ":" + id;
    }

    //This will be called by ModTheSpire because of the @SpireInitializer annotation at the top of the class.
    public static void initialize() {
        new Yuukamod();
        KazamiYuuka.Meta.registerColor();
    }

    public Yuukamod() {
        BaseMod.subscribe(this); //This will make BaseMod trigger all the subscribers at their appropriate times.
        logger.info(modID + " subscribed to BaseMod.");
    }

    @Override
    public void receivePostInitialize() {
        TogetherInSpireCompat.init();

        Texture badgeTexture = TextureLoader.getTexture(imagePath("badge.png"));

        ModPanel panel = new ModPanel();

        ModLabel titleLabel = new ModLabel("Yuuka Mod Settings", 350.0f, 750.0f, panel, (l) -> {});
        panel.addUIElement(titleLabel);

        ModLabel statusLabel = new ModLabel("", 350.0f, 710.0f, panel, (l) -> {
            if (easterEggEverTriggered) {
                String mode = megaCannonRelicMode ? "ON" : "OFF";
                l.text = "Easter egg unlocked! Starter relic mode: " + mode + " (edit YuukaModConfig.properties to change).";
            } else {
                l.text = "Easter egg not yet triggered. Complete the magic cannon combo to unlock.";
            }
        });
        panel.addUIElement(statusLabel);

        BaseMod.registerModBadge(badgeTexture, info.Name, GeneralUtils.arrToString(info.Authors), info.Description, panel);

        BaseMod.addEvent(new AddEventParams.Builder(ParasolEvent.ID, ParasolEvent.class)
                .bonusCondition(() -> AbstractDungeon.player != null && AbstractDungeon.player.chosenClass != KazamiYuuka.Meta.FLOWER_FIELD_TYRANT)
                .create());

        BaseMod.addEvent(new AddEventParams.Builder(UsedCampfireEvent.ID, UsedCampfireEvent.class)
                .bonusCondition(() -> AbstractDungeon.player != null && AbstractDungeon.player.chosenClass != AbstractPlayer.PlayerClass.IRONCLAD)
                .create());

        BaseMod.addEvent(new AddEventParams.Builder(TataraBlacksmith.ID, TataraBlacksmith.class)
                .create());

        BaseMod.addMonster(RecordPlayer.ID, () -> {
            RunRecorder.RunData data = RunRecorder.loadRunData();
            if (data == null) {
                data = new RunRecorder.RunData();
                data.characterClass = "DEFECT";
                data.maxHP = 60;
            }
            return new RecordPlayer(data);
        });

        BaseMod.addEvent(new AddEventParams.Builder(RecordPlayerEvent.ID, RecordPlayerEvent.class)
                .bonusCondition(() -> RunRecorder.hasSavedRunData() && AbstractDungeon.actNum == 3)
                .create());

        BaseMod.addEvent(new AddEventParams.Builder(MagicCannonEvent.ID, MagicCannonEvent.class)
                .create());

        YuukaSignatureHelper.registerAll();
        SignatureUnlockManager.loadRunStats();
        me.antileaf.signature.utils.SignatureHelper.registerEasyUnlock(new SignatureUnlockSubscriber());
    }

    /*----------Localization----------*/

    //This is used to load the appropriate localization files based on language.
    private static String getLangString()
    {
        return Settings.language.name().toLowerCase();
    }
    private static final String defaultLanguage = "eng";

    public static final Map<String, KeywordInfo> keywords = new HashMap<>();

    @Override
    public void receiveEditStrings() {
        /*
            First, load the default localization.
            Then, if the current language is different, attempt to load localization for that language.
            This results in the default localization being used for anything that might be missing.
            The same process is used to load keywords slightly below.
        */
        loadLocalization(defaultLanguage); //no exception catching for default localization; you better have at least one that works.
        String lang = getLangString();
        if (!defaultLanguage.equals(lang) && localizationExists(lang)) {
            try {
                loadLocalization(lang);
            }
            catch (GdxRuntimeException e) {
                logger.warn(modID + " does not support " + lang + " strings.");
            }
        }
    }

    private boolean localizationExists(String lang) {
        return Gdx.files.internal(localizationPath(lang, "CardStrings.json")).exists();
    }

    private void loadLocalization(String lang) {
        //While this does load every type of localization, most of these files are just outlines so that you can see how they're formatted.
        //Feel free to comment out/delete any that you don't end up using.
        BaseMod.loadCustomStringsFile(CardStrings.class,
                localizationPath(lang, "CardStrings.json"));
        BaseMod.loadCustomStringsFile(CharacterStrings.class,
                localizationPath(lang, "CharacterStrings.json"));
        BaseMod.loadCustomStringsFile(EventStrings.class,
                localizationPath(lang, "EventStrings.json"));
        BaseMod.loadCustomStringsFile(OrbStrings.class,
                localizationPath(lang, "OrbStrings.json"));
        BaseMod.loadCustomStringsFile(PotionStrings.class,
                localizationPath(lang, "PotionStrings.json"));
        BaseMod.loadCustomStringsFile(PowerStrings.class,
                localizationPath(lang, "PowerStrings.json"));
        BaseMod.loadCustomStringsFile(RelicStrings.class,
                localizationPath(lang, "RelicStrings.json"));
        BaseMod.loadCustomStringsFile(UIStrings.class,
                localizationPath(lang, "UIStrings.json"));
    }

    @Override
    public void receiveEditKeywords()
    {
        Gson gson = new Gson();
        String json = Gdx.files.internal(localizationPath(defaultLanguage, "Keywords.json")).readString(String.valueOf(StandardCharsets.UTF_8));
        KeywordInfo[] keywords = gson.fromJson(json, KeywordInfo[].class);
        for (KeywordInfo keyword : keywords) {
            keyword.prep();
            registerKeyword(keyword);
        }

        if (!defaultLanguage.equals(getLangString())) {
            try
            {
                json = Gdx.files.internal(localizationPath(getLangString(), "Keywords.json")).readString(String.valueOf(StandardCharsets.UTF_8));
                keywords = gson.fromJson(json, KeywordInfo[].class);
                for (KeywordInfo keyword : keywords) {
                    keyword.prep();
                    registerKeyword(keyword);
                }
            }
            catch (Exception e)
            {
                logger.warn(modID + " does not support " + getLangString() + " keywords.");
            }
        }
    }

    private void registerKeyword(KeywordInfo info) {
        BaseMod.addKeyword(modID.toLowerCase(), info.PROPER_NAME, info.NAMES, info.DESCRIPTION, info.COLOR);
        if (!info.ID.isEmpty())
        {
            keywords.put(info.ID, info);
        }
    }

    @Override
    public void receiveAddAudio() {
        loadAudio(Sounds.class);
    }

    private static final String[] AUDIO_EXTENSIONS = { ".ogg", ".wav", ".mp3" };
    private void loadAudio(Class<?> cls) {
        try {
            Field[] fields = cls.getDeclaredFields();
            for (Field f : fields) {
                int modifiers = f.getModifiers();
                if (Modifier.isStatic(modifiers) && Modifier.isPublic(modifiers) && f.getType().equals(String.class)) {
                    String s = (String) f.get(null);
                    if (s == null) {
                        s = audioPath(f.getName());
                        for (String ext : AUDIO_EXTENSIONS) {
                            String testPath = s + ext;
                            if (resourceExists(testPath)) {
                                s = testPath;
                                BaseMod.addAudio(s, s);
                                f.set(null, s);
                                break;
                            }
                        }
                    } else {
                        if (resourceExists(s)) {
                            BaseMod.addAudio(s, s);
                        }
                    }
                }
            }
        } catch (Exception e) {
            logger.error("Exception occurred in loadAudio: ", e);
        }
    }

    private static boolean resourceExists(String path) {
        try {
            return Yuukamod.class.getClassLoader().getResource(path) != null;
        } catch (Exception e) {
            return false;
        }
    }

    //These methods are used to generate the correct filepaths to various parts of the resources folder.
    public static String localizationPath(String lang, String file) {
        return resourcesFolder + "/localization/" + lang + "/" + file;
    }

    public static String audioPath(String file) {
        return resourcesFolder + "/audio/" + file;
    }
    public static String musicPath(String file) {
        return resourcesFolder + "/" + file;
    }
    public static String imagePath(String file) {
        return resourcesFolder + "/images/" + file;
    }
    public static String characterPath(String file) {
        return resourcesFolder + "/images/character/" + file;
    }
    public static String powerPath(String file) {
        return resourcesFolder + "/images/powers/" + file;
    }
    public static String relicPath(String file) {
        return resourcesFolder + "/images/relics/" + file;
    }

    /**
     * Checks the expected resources path based on the package name.
     */
    private static String checkResourcesPath() {
        String name = Yuukamod.class.getName(); //getPackage can be iffy with patching, so class name is used instead.
        int separator = name.indexOf('.');
        if (separator > 0)
            name = name.substring(0, separator);

        FileHandle resources = new LwjglFileHandle(name, Files.FileType.Internal);

        if (!resources.exists()) {
            throw new RuntimeException("\n\tFailed to find resources folder; expected it to be at  \"resources/" + name + "\"." +
                    " Either make sure the folder under resources has the same name as your mod's package, or change the line\n" +
                    "\t\"private static final String resourcesFolder = checkResourcesPath();\"\n" +
                    "\tat the top of the " + Yuukamod.class.getSimpleName() + " java file.");
        }
        if (!resources.child("images").exists()) {
            throw new RuntimeException("\n\tFailed to find the 'images' folder in the mod's 'resources/" + name + "' folder; Make sure the " +
                    "images folder is in the correct location.");
        }
        if (!resources.child("localization").exists()) {
            throw new RuntimeException("\n\tFailed to find the 'localization' folder in the mod's 'resources/" + name + "' folder; Make sure the " +
                    "localization folder is in the correct location.");
        }

        return name;
    }

    /**
     * This determines the mod's ID based on information stored by ModTheSpire.
     */
    private static void loadModInfo() {
        Optional<ModInfo> infos = Arrays.stream(Loader.MODINFOS).filter((modInfo)->{
            AnnotationDB annotationDB = Patcher.annotationDBMap.get(modInfo.jarURL);
            if (annotationDB == null)
                return false;
            Set<String> initializers = annotationDB.getAnnotationIndex().getOrDefault(SpireInitializer.class.getName(), Collections.emptySet());
            return initializers.contains(Yuukamod.class.getName());
        }).findFirst();
        if (infos.isPresent()) {
            info = infos.get();
            modID = info.ID;
        }
        else {
            throw new RuntimeException("Failed to determine mod info/ID based on initializer.");
        }
    }

    @Override
    public void receiveOnBattleStart(com.megacrit.cardcrawl.rooms.AbstractRoom room) {
        LLSPC98formPower.stopBossMusic();
        AutoTriggerLimit.reset();
        MagicCannonComboTracker.reset();
        SignatureUnlockManager.onBattleStart();
        if (AbstractDungeon.player != null) {
            AbstractPower cooldown = AbstractDungeon.player.getPower(LLSPC98formCooldown.POWER_ID);
            if (cooldown != null) {
                cooldown.onRemove();
                AbstractDungeon.player.powers.remove(cooldown);
            }
            if (LLS_PC98_form.getBlockedCombatCount() > 0) {
                int remaining = LLS_PC98_form.getBlockedCombatCount();
                AbstractDungeon.player.addPower(new LLSPC98formCooldown(AbstractDungeon.player, remaining));
                LLS_PC98_form.decrementBlockedCombatCount();
            }
        }
        if (AbstractDungeon.actNum == 1 && AbstractDungeon.floorNum <= 1) {
            Bouquet.clearRewardPending();
            Infinite_spiral.clearRewardPending();
        }
    }

    @Override
    public void receivePostPowerApplySubscriber(AbstractPower power, com.megacrit.cardcrawl.core.AbstractCreature target, com.megacrit.cardcrawl.core.AbstractCreature source) {
        SignatureUnlockManager.onPowerApplied(power, target, source);
        if (AbstractDungeon.actNum == 4 && AbstractDungeon.getCurrRoom() instanceof MonsterRoomBoss
                && target instanceof AbstractPlayer) {
            RunRecorder.recordPower(power);
        }
    }

    @Override
    public void receiveStartGame() {
        RunRecorder.clearSampledPowers();
        MagicCannonComboTracker.clearAll();
        LLS_PC98_form.clearBlockedCombatCounts();
        SignatureUnlockManager.onNewRun();
    }

    @Override
    public void receivePostBattle(com.megacrit.cardcrawl.rooms.AbstractRoom room) {
        SignatureUnlockManager.onBattleEnd();
        if (AbstractDungeon.actNum == 4 && AbstractDungeon.getCurrRoom() instanceof MonsterRoomBoss) {
            if (AbstractDungeon.player != null) {
                logger.info("Act IV boss defeated! Saving run data for Record Player enemy.");
                RunRecorder.saveRunData(AbstractDungeon.player);
            }
        }

        LLSPC98formPower.restoreDefaultAnimation();
        if (LLSPC98formPower.isLocalBossMusicActive()) {
            LLSPC98formPower.fadeOutBossMusic();
            TogetherInSpireCompat.broadcastBossMusicStop();
        }
        if (AbstractDungeon.player != null) {
            AbstractPower cooldown = AbstractDungeon.player.getPower(LLSPC98formCooldown.POWER_ID);
            if (cooldown != null) {
                cooldown.onRemove();
                AbstractDungeon.player.powers.remove(cooldown);
            }

            HoneyMilkPower honeyMilk = (HoneyMilkPower) AbstractDungeon.player.getPower(HoneyMilkPower.POWER_ID);
            if (honeyMilk != null) {
                honeyMilk.applyHeal();
                AbstractDungeon.player.powers.remove(honeyMilk);
            }
        }
        AutoTriggerLimit.reset();
        logger.info("Bouquet pending? " + Bouquet.isRewardPending());
        if (Bouquet.isRewardPending()) {
            logger.info("Bouquet: adding rare reward");
            RewardItem reward = new RewardItem();
            reward.type = RewardItem.RewardType.CARD;
            reward.cards = new ArrayList<>();
            java.util.function.Supplier<AbstractCard> getRare = () -> {
                for (int i = 0; i < 20; i++) {
                    AbstractCard c = AbstractDungeon.returnTrulyRandomCardInCombat();
                    if (c != null && c.rarity == AbstractCard.CardRarity.RARE) return c.makeCopy();
                }
                AbstractCard fallback = AbstractDungeon.returnTrulyRandomCardInCombat();
                return fallback == null ? null : fallback.makeCopy();
            };
            AbstractCard c1 = getRare.get();
            AbstractCard c2 = getRare.get();
            AbstractCard c3 = getRare.get();
            logger.info("Bouquet: got cards: " + (c1!=null?c1.cardID:"null") + ", " + (c2!=null?c2.cardID:"null") + ", " + (c3!=null?c3.cardID:"null"));
            if (c1 != null) reward.cards.add(c1);
            if (c2 != null) reward.cards.add(c2);
            if (c3 != null) reward.cards.add(c3);
            if (AbstractDungeon.getCurrRoom() != null) {
                AbstractDungeon.getCurrRoom().rewards.add(reward);
                logger.info("Bouquet: added reward to currRoom.rewards");
            } else {
                AbstractDungeon.combatRewardScreen.rewards.add(reward);
                logger.warn("Bouquet: currRoom null, added to combatRewardScreen.rewards (fallback)");
            }
            Bouquet.clearRewardPending();
        }

        if (Infinite_spiral.isRewardPending()) {
            logger.info("Infinite Spiral: adding relic reward");
            RelicTier tier = MathUtils.random(0, 2) == 0 ? RelicTier.COMMON : (MathUtils.randomBoolean() ? RelicTier.UNCOMMON : RelicTier.RARE);
            AbstractRelic relic = AbstractDungeon.returnRandomRelic(tier);
            if (relic != null) {
                RewardItem reward = new RewardItem(relic);
                if (AbstractDungeon.getCurrRoom() != null) {
                    AbstractDungeon.getCurrRoom().rewards.add(reward);
                    logger.info("Infinite Spiral: added reward to currRoom.rewards");
                } else {
                    AbstractDungeon.combatRewardScreen.rewards.add(reward);
                    logger.warn("Infinite Spiral: currRoom null, added to combatRewardScreen.rewards (fallback)");
                }
            } else {
                logger.warn("Infinite Spiral: no relic available for tier " + tier + ", skipping relic reward");
            }

            RewardItem bouquetReward = new RewardItem();
            bouquetReward.type = RewardItem.RewardType.CARD;
            bouquetReward.cards = new ArrayList<>();
            bouquetReward.cards.add(new Bouquet());
            if (AbstractDungeon.getCurrRoom() != null) {
                AbstractDungeon.getCurrRoom().rewards.add(bouquetReward);
                logger.info("Infinite Spiral: added Bouquet reward to currRoom.rewards");
            } else {
                AbstractDungeon.combatRewardScreen.rewards.add(bouquetReward);
                logger.warn("Infinite Spiral: currRoom null, added Bouquet to combatRewardScreen.rewards (fallback)");
            }
            Infinite_spiral.clearRewardPending();
        }

        if (AbstractDungeon.player != null) {
            AbstractPower yumemiPower = AbstractDungeon.player.getPower(YumemiPower.POWER_ID);
            if (yumemiPower != null) {
                RewardItem reward = new RewardItem();
                reward.type = RewardItem.RewardType.CARD;
                reward.cards = new ArrayList<>();
                AbstractCard yumemiCard = AbstractDungeon.returnTrulyRandomCardInCombat();
                if (yumemiCard != null) reward.cards.add(yumemiCard.makeCopy());
                yumemiCard = AbstractDungeon.returnTrulyRandomCardInCombat();
                if (yumemiCard != null) reward.cards.add(yumemiCard.makeCopy());
                yumemiCard = AbstractDungeon.returnTrulyRandomCardInCombat();
                if (yumemiCard != null) reward.cards.add(yumemiCard.makeCopy());
                if (AbstractDungeon.getCurrRoom() != null) {
                    AbstractDungeon.getCurrRoom().rewards.add(reward);
                } else {
                    AbstractDungeon.combatRewardScreen.rewards.add(reward);
                }
                // Ensure proper removal (call onRemove hook) before removing from player's power list
                try {
                    yumemiPower.onRemove();
                } catch (Exception e) {
                    logger.warn("Error calling yumemiPower.onRemove(): ", e);
                }
                AbstractDungeon.player.powers.remove(yumemiPower);
            }
        }
    }

    @Override
    public void receiveEditCharacters() {
        KazamiYuuka.Meta.registerCharacter();
    }

    @Override
    public void receiveEditCards() {
        new AutoAdd(modID) //Loads files from this mod
                .packageFilter(BaseCard.class) //In the same package as this class
                .setDefaultSeen(true) //And marks them as seen in the compendium
                .filter((ci, cf) ->
                    !ci.getClassName().equals(Mima.class.getName()) &&
                    !ci.getClassName().equals(Shinki.class.getName()) &&
                    !ci.getClassName().equals(Small_suika.class.getName()) &&
                    !ci.getClassName().equals(Yumemi.class.getName()) &&
                    !ci.getClassName().equals(Mega_magic_cannon.class.getName())
                )
                .cards(); //Adds the cards
    }

    @Override
    public void receiveEditRelics() {
        BaseMod.addRelic(new VioletRelic(), basemod.helpers.RelicType.SHARED);
        BaseMod.addRelic(new BloomingSunflowersRelic(), basemod.helpers.RelicType.SHARED);
        BaseMod.addRelic(new DaisyRelic(), basemod.helpers.RelicType.SHARED);
        BaseMod.addRelic(new ChrysanthemumRelic(), basemod.helpers.RelicType.SHARED);
        BaseMod.addRelic(new RoseRelic(), basemod.helpers.RelicType.SHARED);
        BaseMod.addRelic(new SunflowerRelic(), basemod.helpers.RelicType.SHARED);
        if (easterEggEverTriggered) {
            BaseMod.addRelic(new MegaMagicCannonRelic(), basemod.helpers.RelicType.SHARED);
        }
        BaseMod.addRelic(new YuukaFumoRelic(), basemod.helpers.RelicType.SHARED);
        BaseMod.addRelic(new ParasolDebuffRelic(), basemod.helpers.RelicType.SHARED);
        BaseMod.addRelic(new ParasolBuffRelic(), basemod.helpers.RelicType.SHARED);
        BaseMod.addPotion(PureWater.class, Color.WHITE, Color.WHITE, Color.WHITE, PureWater.ID);
        BaseMod.addPotion(Tea.class, new Color(0.65f, 0.5f, 0.2f, 1f), new Color(0.45f, 0.35f, 0.12f, 1f), new Color(0.8f, 0.75f, 0.6f, 1f), Tea.ID);
        BaseMod.addPotion(HoneyMilk.class, null, null, null, HoneyMilk.ID, KazamiYuuka.Meta.FLOWER_FIELD_TYRANT);
        BaseMod.addPotion(ScentedTea.class,
                new Color(200f/255f, 160f/255f, 1f, 1f),
                new Color(150f/255f, 100f/255f, 220f/255f, 1f),
                new Color(1f, 200f/255f, 1f, 1f),
                ScentedTea.ID);
        BaseMod.addPotion(BlackTea.class,
                new Color(60f/255f, 30f/255f, 10f/255f, 1f),
                new Color(140f/255f, 80f/255f, 30f/255f, 1f),
                new Color(220f/255f, 180f/255f, 80f/255f, 1f),
                BlackTea.ID);
        BaseMod.addPotion(BombPotion.class,
                new Color(40f/255f, 40f/255f, 40f/255f, 1f),
                new Color(200f/255f, 60f/255f, 40f/255f, 1f),
                new Color(1f, 200f/255f, 60f/255f, 1f),
                BombPotion.ID);
        BaseMod.addPotion(GreenTea.class,
                new Color(140f/255f, 220f/255f, 120f/255f, 1f),
                new Color(60f/255f, 160f/255f, 60f/255f, 1f),
                new Color(200f/255f, 1f, 180f/255f, 1f),
                GreenTea.ID);
    }
}
