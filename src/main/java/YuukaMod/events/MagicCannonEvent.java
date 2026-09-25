package YuukaMod.events;

import YuukaMod.cards.basic.Small_magic_cannon;
import YuukaMod.cards.common.Magic_cannon;
import YuukaMod.cards.common.Second_magic_cannon;
import YuukaMod.cards.rare.Ultra_magic_cannon;
import YuukaMod.cards.uncommon.Big_magic_cannon;
import YuukaMod.cards.uncommon.Giant_magic_cannon;
import YuukaMod.cards.uncommon.Large_magic_cannon;
import basemod.abstracts.events.PhasedEvent;
import basemod.abstracts.events.phases.TextPhase;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.CardLibrary;
import com.megacrit.cardcrawl.localization.EventStrings;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndObtainEffect;

import java.util.ArrayList;

import static YuukaMod.Yuukamod.imagePath;
import static YuukaMod.Yuukamod.makeID;

public class MagicCannonEvent extends PhasedEvent {
    public static final String ID = makeID("MagicCannonEvent");
    private static final EventStrings eventStrings = CardCrawlGame.languagePack.getEventString(ID);

    private static final String NAME = eventStrings.NAME;
    private static final String[] DESCRIPTIONS = eventStrings.DESCRIPTIONS;
    private static final String[] OPTIONS = eventStrings.OPTIONS;

    private static final String[] CANNON_IDS = {
            Small_magic_cannon.ID,
            Magic_cannon.ID,
            Second_magic_cannon.ID,
            Large_magic_cannon.ID,
            Big_magic_cannon.ID,
            Giant_magic_cannon.ID,
            Ultra_magic_cannon.ID
    };

    public MagicCannonEvent() {
        super(ID, NAME, imagePath("events/MagicCannonEnergy.png"));

        registerPhase("intro", new TextPhase(DESCRIPTIONS[0])
                .addOption(OPTIONS[0], i -> transitionKey("choose")));

        registerPhase("choose", new TextPhase(DESCRIPTIONS[1])
                .addOption(new TextPhase.OptionInfo(OPTIONS[1])
                        .enabledCondition(MagicCannonEvent::hasAvailableCannon, OPTIONS[3])
                        .setOptionResult(i -> {
                            obtainRandomCannon();
                            transitionKey("obtain");
                        }))
                .addOption(OPTIONS[2], i -> openMap()));

        registerPhase("obtain", new TextPhase(DESCRIPTIONS[2])
                .addOption(OPTIONS[2], i -> openMap()));

        transitionKey("intro");
    }

    private static boolean hasAvailableCannon() {
        for (String id : CANNON_IDS) {
            if (!deckHasCard(id)) {
                return true;
            }
        }
        return false;
    }

    private static boolean deckHasCard(String id) {
        for (AbstractCard c : AbstractDungeon.player.masterDeck.group) {
            if (c.cardID.equals(id)) {
                return true;
            }
        }
        return false;
    }

    private static void obtainRandomCannon() {
        ArrayList<String> pool = new ArrayList<>();
        for (String id : CANNON_IDS) {
            if (!deckHasCard(id)) {
                pool.add(id);
            }
        }
        if (!pool.isEmpty()) {
            String pick = pool.get(AbstractDungeon.miscRng.random(pool.size() - 1));
            AbstractCard card = CardLibrary.getCopy(pick);
            AbstractDungeon.topLevelEffectsQueue.add(
                new ShowCardAndObtainEffect(card, Settings.WIDTH / 2.0F, Settings.HEIGHT / 2.0F));
        }
    }
}
