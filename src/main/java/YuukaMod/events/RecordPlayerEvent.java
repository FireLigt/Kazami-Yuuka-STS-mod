package YuukaMod.events;

import YuukaMod.monsters.RecordPlayer;
import basemod.abstracts.events.PhasedEvent;
import basemod.abstracts.events.phases.CombatPhase;
import basemod.abstracts.events.phases.EventPhase;
import basemod.abstracts.events.phases.TextPhase;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.EventStrings;

import java.util.ArrayList;

import static YuukaMod.Yuukamod.imagePath;
import static YuukaMod.Yuukamod.makeID;

public class RecordPlayerEvent extends PhasedEvent {
    public static final String ID = makeID("RecordPlayerEvent");
    private static final EventStrings eventStrings = CardCrawlGame.languagePack.getEventString(ID);

    private static final String NAME = eventStrings.NAME;
    private static final String[] DESCRIPTIONS = eventStrings.DESCRIPTIONS;
    private static final String[] OPTIONS = eventStrings.OPTIONS;

    public RecordPlayerEvent() {
        super(ID, NAME, imagePath("events/PhantomCome.png"));

        registerPhase("intro", new TextPhase(DESCRIPTIONS[0])
                .addOption(OPTIONS[0], i -> transitionKey("meet")));

        registerPhase("meet", new TextPhase(DESCRIPTIONS[1])
                .addOption(OPTIONS[1], i -> transitionKey("choose")));

        registerPhase("choose", new TextPhase(DESCRIPTIONS[2])
                .addOption(new TextPhase.OptionInfo(OPTIONS[2])
                        .enabledCondition(RecordPlayerEvent::hasForgettableCard, OPTIONS[5])
                        .setOptionResult(i -> {
                            forgetRandomCard();
                            transitionKey("forget");
                        }))
                .addOption(new TextPhase.OptionInfo(OPTIONS[3])
                        .setOptionResult(i -> transitionKey("fight"))));

        registerPhase("forget", new TextPhase(DESCRIPTIONS[3])
                .addOption(OPTIONS[4], i -> openMap()));

        registerPhase("fight", new CombatPhase(RecordPlayer.ID)
                .addRewards(false, room -> {})
                .setNextKey("victory"));

        registerPhase("victory", new TextPhase(DESCRIPTIONS[4])
                .addOption(OPTIONS[4], i -> openMap()));

        transitionKey("intro");
    }

    @Override
    public void transitionPhase(EventPhase phase) {
        if (phase == getPhase("intro")) {
            this.imageEventText.loadImage(imagePath("events/PhantomCome.png"));
        } else if (phase == getPhase("victory")) {
            this.imageEventText.loadImage(imagePath("events/PhantomGo.png"));
        }
        super.transitionPhase(phase);
    }

    private static boolean hasForgettableCard() {
        for (AbstractCard c : AbstractDungeon.player.masterDeck.group) {
            if (c.rarity == AbstractCard.CardRarity.UNCOMMON || c.rarity == AbstractCard.CardRarity.RARE) {
                return true;
            }
        }
        return false;
    }

    private static void forgetRandomCard() {
        ArrayList<AbstractCard> pool = new ArrayList<>();
        for (AbstractCard c : AbstractDungeon.player.masterDeck.group) {
            if (c.rarity == AbstractCard.CardRarity.UNCOMMON || c.rarity == AbstractCard.CardRarity.RARE) {
                pool.add(c);
            }
        }
        if (!pool.isEmpty()) {
            AbstractCard toRemove = pool.get(AbstractDungeon.miscRng.random(pool.size() - 1));
            AbstractDungeon.player.masterDeck.removeCard(toRemove);
        }
    }
}
