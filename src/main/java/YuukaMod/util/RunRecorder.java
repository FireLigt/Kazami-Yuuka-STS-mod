package YuukaMod.util;

import YuukaMod.Yuukamod;
import com.evacipated.cardcrawl.modthespire.lib.SpireConfig;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.powers.AbstractPower;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class RunRecorder {
    private static final Logger logger = LogManager.getLogger(RunRecorder.class.getName());
    private static final String CONFIG_KEY_DECK = "lastRunDeck";
    private static final String CONFIG_KEY_MAX_HP = "lastRunMaxHP";
    private static final String CONFIG_KEY_CHARACTER = "lastRunCharacter";
    private static final String CONFIG_KEY_POWERS = "lastRunPowers";
    private static final Gson gson = new Gson();

    private static final Map<String, Integer> sampledPowers = new HashMap<>();

    public static class RunData {
        public List<CardSaveData> deck;
        public int maxHP;
        public String characterClass;
        public List<PowerSaveData> powers;

        public RunData() {
            this.deck = new ArrayList<>();
            this.maxHP = 0;
            this.characterClass = "";
            this.powers = new ArrayList<>();
        }
    }

    public static class CardSaveData {
        public String id;
        public boolean upgraded;

        public CardSaveData(String id, boolean upgraded) {
            this.id = id;
            this.upgraded = upgraded;
        }
    }

    public static class PowerSaveData {
        public String powerClass;
        public int amount;

        public PowerSaveData(String powerClass, int amount) {
            this.powerClass = powerClass;
            this.amount = amount;
        }
    }

    private static SpireConfig createConfig() throws Exception {
        return new SpireConfig(Yuukamod.modID, "YuukaModConfig");
    }

    public static void recordPower(AbstractPower power) {
        if (power == null) {
            return;
        }
        String className = power.getClass().getName();
        sampledPowers.merge(className, power.amount, Math::max);
    }

    public static void recordAllPowers(AbstractPlayer player) {
        if (player == null || player.powers == null) {
            return;
        }
        for (AbstractPower power : player.powers) {
            recordPower(power);
        }
    }

    public static void clearSampledPowers() {
        sampledPowers.clear();
    }

    public static void saveRunData(AbstractPlayer player) {
        try {
            SpireConfig config = createConfig();

            RunData data = new RunData();
            data.maxHP = player.maxHealth;
            data.characterClass = player.chosenClass.name();

            data.deck = new ArrayList<>();
            for (AbstractCard card : player.masterDeck.group) {
                data.deck.add(new CardSaveData(card.cardID, card.upgraded));
            }

            data.powers = new ArrayList<>();
            Set<String> seenClasses = new HashSet<>();
            for (AbstractPower power : player.powers) {
                if (power != null) {
                    seenClasses.add(power.getClass().getName());
                    data.powers.add(new PowerSaveData(power.getClass().getName(), power.amount));
                }
            }
            for (String className : sampledPowers.keySet()) {
                if (!seenClasses.contains(className)) {
                    data.powers.add(new PowerSaveData(className, 0));
                }
            }

            String deckJson = gson.toJson(data.deck);
            config.setString(CONFIG_KEY_DECK, deckJson);
            config.setInt(CONFIG_KEY_MAX_HP, data.maxHP);
            config.setString(CONFIG_KEY_CHARACTER, data.characterClass);
            String powersJson = gson.toJson(data.powers);
            config.setString(CONFIG_KEY_POWERS, powersJson);
            config.save();

            logger.info("RunRecorder: Saved run data - " + data.characterClass + ", " + data.maxHP + " HP, " + data.deck.size() + " cards");
        } catch (Exception e) {
            logger.error("RunRecorder: Failed to save run data", e);
        }
    }

    public static RunData loadRunData() {
        try {
            SpireConfig config = createConfig();
            config.load();

            String deckJson = config.getString(CONFIG_KEY_DECK);
            if (deckJson == null || deckJson.isEmpty()) {
                logger.info("RunRecorder: No saved run data found");
                return null;
            }

            RunData data = new RunData();
            data.deck = gson.fromJson(deckJson, new TypeToken<List<CardSaveData>>() {}.getType());
            data.maxHP = config.getInt(CONFIG_KEY_MAX_HP);
            data.characterClass = config.getString(CONFIG_KEY_CHARACTER);

            if (data.deck == null) {
                data.deck = new ArrayList<>();
            } else {
                data.deck.removeIf(card -> card == null || card.id == null || card.id.isEmpty());
            }
            if (data.characterClass == null) {
                data.characterClass = "";
            }

            String powersJson = config.getString(CONFIG_KEY_POWERS);
            if (powersJson != null && !powersJson.isEmpty()) {
                data.powers = gson.fromJson(powersJson, new TypeToken<List<PowerSaveData>>() {}.getType());
            }
            if (data.powers == null) {
                data.powers = new ArrayList<>();
            } else {
                data.powers.removeIf(power -> power == null || power.powerClass == null || power.powerClass.isEmpty());
            }

            logger.info("RunRecorder: Loaded run data - " + data.characterClass + ", " + data.maxHP + " HP, " + data.deck.size() + " cards, " + data.powers.size() + " powers");
            return data;
        } catch (Exception e) {
            logger.error("RunRecorder: Failed to load run data", e);
            return null;
        }
    }

    public static boolean hasSavedRunData() {
        try {
            SpireConfig config = createConfig();
            config.load();
            String deckJson = config.getString(CONFIG_KEY_DECK);
            return deckJson != null && !deckJson.isEmpty();
        } catch (Exception e) {
            return false;
        }
    }
}
