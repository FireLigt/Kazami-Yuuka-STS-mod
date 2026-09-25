package YuukaMod.events;

import basemod.abstracts.events.PhasedEvent;
import basemod.abstracts.events.phases.TextPhase;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.RelicLibrary;
import com.megacrit.cardcrawl.localization.EventStrings;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import YuukaMod.relics.RoseRelic;
import YuukaMod.relics.ChrysanthemumRelic;
import YuukaMod.relics.VioletRelic;
import YuukaMod.relics.DaisyRelic;

import java.util.ArrayList;

import static YuukaMod.Yuukamod.makeID;
import static YuukaMod.Yuukamod.imagePath;

public class FlowerGarden extends PhasedEvent {
    public static final String ID = makeID("FlowerGarden");
    private static final EventStrings eventStrings = CardCrawlGame.languagePack.getEventString(ID);

    private static final String NAME = eventStrings.NAME;
    private static final String[] DESCRIPTIONS = eventStrings.DESCRIPTIONS;
    private static final String[] OPTIONS = eventStrings.OPTIONS;

    public FlowerGarden() {
        super(ID, NAME, imagePath("events/FlowerField.png"));

        registerPhase("intro", new TextPhase(DESCRIPTIONS[0])
                .addOption(OPTIONS[0], i -> transitionKey("step")));

        registerPhase("step", new TextPhase(DESCRIPTIONS[1])
                .addOption(OPTIONS[1], i -> {
                    imageEventText.loadImage(imagePath("events/FourFlower.png"));
                    transitionKey("center");
                }));

        registerPhase("center", new TextPhase(DESCRIPTIONS[2])
                .addOption(new TextPhase.OptionInfo(OPTIONS[2])
                        .enabledCondition(FlowerGarden::hasAnyFlowerRelic, OPTIONS[5])
                        .setOptionResult(i -> {
                            AbstractRelic relic = getRandomFlowerRelic();
                            if (relic != null) {
                                AbstractDungeon.getCurrRoom().spawnRelicAndObtain(
                                        Settings.WIDTH / 2.0F, Settings.HEIGHT / 2.0F, relic);
                            }
                            int dmg = AbstractDungeon.player.currentHealth * 20 / 100;
                            if (dmg > 0) {
                                AbstractDungeon.player.damage(new DamageInfo(null, dmg, DamageInfo.DamageType.HP_LOSS));
                            }
                            transitionKey("reward");
                        }))
                .addOption(new TextPhase.OptionInfo(OPTIONS[3])
                        .setOptionResult(i -> {
                            int lostHp = AbstractDungeon.player.maxHealth - AbstractDungeon.player.currentHealth;
                            if (lostHp > 0) {
                                AbstractDungeon.player.heal(lostHp / 2);
                            }
                            transitionKey("decline");
                        })));

        registerPhase("reward", new TextPhase(DESCRIPTIONS[3])
                .addOption(OPTIONS[4], i -> openMap()));

        registerPhase("decline", new TextPhase(DESCRIPTIONS[4])
                .addOption(OPTIONS[4], i -> openMap()));

        transitionKey("intro");
    }

    private static boolean hasAnyFlowerRelic() {
        return !AbstractDungeon.player.hasRelic(RoseRelic.ID)
            || !AbstractDungeon.player.hasRelic(ChrysanthemumRelic.ID)
            || !AbstractDungeon.player.hasRelic(VioletRelic.ID)
            || !AbstractDungeon.player.hasRelic(DaisyRelic.ID);
    }

    private AbstractRelic getRandomFlowerRelic() {
        ArrayList<AbstractRelic> available = new ArrayList<>();
        String[] relicIDs = {RoseRelic.ID, ChrysanthemumRelic.ID, VioletRelic.ID, DaisyRelic.ID};

        for (String id : relicIDs) {
            if (!AbstractDungeon.player.hasRelic(id)) {
                available.add(RelicLibrary.getRelic(id).makeCopy());
            }
        }

        if (available.isEmpty()) {
            return null;
        }
        return available.get(AbstractDungeon.miscRng.random(available.size() - 1));
    }
}
