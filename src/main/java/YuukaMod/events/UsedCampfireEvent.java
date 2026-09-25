package YuukaMod.events;

import basemod.abstracts.events.PhasedEvent;
import basemod.abstracts.events.phases.TextPhase;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.CardLibrary;
import com.megacrit.cardcrawl.localization.EventStrings;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.relics.AbstractRelic;

import static YuukaMod.Yuukamod.makeID;
import static YuukaMod.Yuukamod.imagePath;

public class UsedCampfireEvent extends PhasedEvent {
    public static final String ID = makeID("UsedCampfire");
    private static final EventStrings eventStrings = CardCrawlGame.languagePack.getEventString(ID);

    private static final String NAME = eventStrings.NAME;
    private static final String[] DESCRIPTIONS = eventStrings.DESCRIPTIONS;
    private static final String[] OPTIONS = eventStrings.OPTIONS;

    private AbstractCard cardPreview1;
    private AbstractCard cardPreview2;
    private AbstractRelic relicPreview;

    public UsedCampfireEvent() {
        super(ID, NAME, imagePath("events/UsedCampfire.png"));

        cardPreview1 = getRandomIroncladCard();
        cardPreview2 = getRandomIroncladCard();
        relicPreview = AbstractDungeon.returnRandomScreenlessRelic(AbstractRelic.RelicTier.UNCOMMON);

        registerPhase("intro", new TextPhase(DESCRIPTIONS[0])
                .addOption(OPTIONS[0], i -> transitionKey("choice")));

        registerPhase("choice", new TextPhase(DESCRIPTIONS[1])
                .addOption(new TextPhase.OptionInfo(OPTIONS[1], cardPreview1)
                        .setOptionResult(i -> {
                            giveCardReward(cardPreview1);
                            transitionKey("ironclad_reward");
                        }))
                .addOption(new TextPhase.OptionInfo(OPTIONS[2], cardPreview2)
                        .setOptionResult(i -> {
                            giveCardReward(cardPreview2);
                            transitionKey("ironclad_reward");
                        }))
                .addOption(OPTIONS[3], i -> {
                            this.imageEventText.loadImage(imagePath("events/IroncladReward.png"));
                            transitionKey("return_it");
                        }));

        registerPhase("ironclad_reward", new TextPhase(DESCRIPTIONS[2])
                .addOption(OPTIONS[6], i -> openMap()));

        registerPhase("return_it", new TextPhase(DESCRIPTIONS[3])
                .addOption(new TextPhase.OptionInfo(OPTIONS[4], relicPreview)
                        .setOptionResult(i -> {
                            AbstractDungeon.getCurrRoom().spawnRelicAndObtain(Settings.WIDTH / 2.0F, Settings.HEIGHT / 2.0F, relicPreview);
                            AbstractDungeon.player.gainGold(50);
                            transitionKey("get_relic_reward");
                        }))
                .addOption(OPTIONS[5], i -> transitionKey("refuse")));

        registerPhase("refuse", new TextPhase(DESCRIPTIONS[4])
                .addOption(OPTIONS[6], i -> openMap()));

        registerPhase("get_relic_reward", new TextPhase(DESCRIPTIONS[5])
                .addOption(OPTIONS[6], i -> openMap()));

        transitionKey("intro");
    }

    private AbstractCard getRandomIroncladCard() {
        java.util.ArrayList<AbstractCard> pool = CardLibrary.getCardList(CardLibrary.LibraryType.RED);
        return pool.get(AbstractDungeon.cardRandomRng.random(pool.size() - 1)).makeCopy();
    }

    private void giveCardReward(AbstractCard card) {
        AbstractDungeon.player.masterDeck.addToTop(card.makeCopy());
    }
}
