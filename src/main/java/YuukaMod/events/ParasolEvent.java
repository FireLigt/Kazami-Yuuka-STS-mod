package YuukaMod.events;

import basemod.abstracts.events.PhasedEvent;
import basemod.abstracts.events.phases.TextPhase;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.EventStrings;
import YuukaMod.relics.ParasolBuffRelic;
import YuukaMod.relics.ParasolDebuffRelic;

import static YuukaMod.Yuukamod.makeID;
import static YuukaMod.Yuukamod.imagePath;

public class ParasolEvent extends PhasedEvent {
    public static final String ID = makeID("ParasolEvent");
    private static final EventStrings eventStrings = CardCrawlGame.languagePack.getEventString(ID);

    private static final String NAME = eventStrings.NAME;
    private static final String[] DESCRIPTIONS = eventStrings.DESCRIPTIONS;
    private static final String[] OPTIONS = eventStrings.OPTIONS;

    public ParasolEvent() {
        super(ID, NAME, imagePath("events/ParasolEvent.png"));

        registerPhase("intro", new TextPhase(DESCRIPTIONS[0])
                .addOption(OPTIONS[0], i -> transitionKey("choose")));

        registerPhase("choose", new TextPhase(DESCRIPTIONS[1])
                .addOption(new TextPhase.OptionInfo(OPTIONS[1], new ParasolBuffRelic())
                        .setOptionResult(i -> {
                            AbstractDungeon.getCurrRoom().spawnRelicAndObtain(Settings.WIDTH / 2.0F, Settings.HEIGHT / 2.0F, new ParasolBuffRelic());
                            transitionKey("take");
                        }))
                .addOption(new TextPhase.OptionInfo(OPTIONS[2], new ParasolDebuffRelic())
                        .setOptionResult(i -> {
                            AbstractDungeon.getCurrRoom().spawnRelicAndObtain(Settings.WIDTH / 2.0F, Settings.HEIGHT / 2.0F, new ParasolDebuffRelic());
                            transitionKey("take");
                        }))
                .addOption(OPTIONS[3], i -> transitionKey("decline")));

        registerPhase("take", new TextPhase(DESCRIPTIONS[2]).addOption(OPTIONS[4], i -> openMap()));
        registerPhase("decline", new TextPhase(DESCRIPTIONS[3]).addOption(OPTIONS[4], i -> openMap()));

        transitionKey("intro");
    }
}
