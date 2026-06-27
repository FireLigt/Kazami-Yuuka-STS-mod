package YuukaMod.events;

import basemod.abstracts.events.PhasedEvent;
import basemod.abstracts.events.phases.TextPhase;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.EventStrings;
import com.megacrit.cardcrawl.vfx.UpgradeShineEffect;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndObtainEffect;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardBrieflyEffect;

import static YuukaMod.Yuukamod.makeID;
import static YuukaMod.Yuukamod.imagePath;

public class TataraBlacksmith extends PhasedEvent {
    public static final String ID = makeID("TataraBlacksmith");
    private static final EventStrings eventStrings = CardCrawlGame.languagePack.getEventString(ID);

    private static final String[] DESCRIPTIONS = eventStrings.DESCRIPTIONS;
    private static final String[] OPTIONS = eventStrings.OPTIONS;

    public TataraBlacksmith() {
        super(ID, eventStrings.NAME, imagePath("events/Blacksmith.png"));

        registerPhase("intro", new TextPhase(DESCRIPTIONS[0])
                .addOption(OPTIONS[0], i -> {
                    imageEventText.loadImage(imagePath("events/Tatara.png"));
                    transitionKey("choose");
                }));

        registerPhase("choose", new TextPhase(DESCRIPTIONS[1])
                .addOption(OPTIONS[1], i -> transitionKey("action")));

        registerPhase("action", new TextPhase(DESCRIPTIONS[2])
                .addOption(new TextPhase.OptionInfo(OPTIONS[2])
                        .enabledCondition(TataraBlacksmith::hasUpgradeableCard, OPTIONS[10])
                        .cardSelectOption("upgrade_reward", () -> AbstractDungeon.player.masterDeck, OPTIONS[11], 1, true, false, false, false,
                                (cards) -> {
                                    AbstractCard c = cards.get(0);
                                    c.upgrade();
                                    AbstractDungeon.player.bottledCardUpgradeCheck(c);
                                    AbstractDungeon.topLevelEffects.add(new UpgradeShineEffect(Settings.WIDTH / 2.0F, Settings.HEIGHT / 2.0F));
                                    AbstractDungeon.effectList.add(new ShowCardBrieflyEffect(c.makeStatEquivalentCopy()));
                                }))
                .addOption(new TextPhase.OptionInfo(OPTIONS[3])
                        .enabledCondition(TataraBlacksmith::canDuplicateCommon, OPTIONS[8])
                        .cardSelectOption("common_uncommon_reward", TataraBlacksmith::getCommonUncommonCards, OPTIONS[7], 1, false, false, false, false,
                                (cards) -> {
                                    AbstractCard selected = cards.get(0);
                                    AbstractDungeon.player.loseGold(50);
                                    AbstractCard copy = selected.makeStatEquivalentCopy();
                                    AbstractDungeon.effectList.add(new ShowCardAndObtainEffect(copy, Settings.WIDTH / 2.0F, Settings.HEIGHT / 2.0F));
                                }))
                .addOption(new TextPhase.OptionInfo(OPTIONS[4])
                        .enabledCondition(TataraBlacksmith::canDuplicateRare, OPTIONS[9])
                        .cardSelectOption("rare_reward", TataraBlacksmith::getRareCards, OPTIONS[7], 1, false, false, false, false,
                                (cards) -> {
                                    AbstractCard selected = cards.get(0);
                                    AbstractDungeon.player.loseGold(100);
                                    AbstractCard copy = selected.makeStatEquivalentCopy();
                                    AbstractDungeon.effectList.add(new ShowCardAndObtainEffect(copy, Settings.WIDTH / 2.0F, Settings.HEIGHT / 2.0F));
                                }))
                .addOption(OPTIONS[5], i -> transitionKey("leave")));

        registerPhase("upgrade_reward", new TextPhase(DESCRIPTIONS[3])
                .addOption(OPTIONS[6], i -> openMap()));

        registerPhase("common_uncommon_reward", new TextPhase(DESCRIPTIONS[4])
                .addOption(OPTIONS[6], i -> openMap()));

        registerPhase("rare_reward", new TextPhase(DESCRIPTIONS[5])
                .addOption(OPTIONS[6], i -> openMap()));

        registerPhase("leave", new TextPhase(DESCRIPTIONS[6])
                .addOption(OPTIONS[6], i -> openMap()));

        transitionKey("intro");
    }

    private static boolean canDuplicateCommon() {
        if (AbstractDungeon.player.gold < 50) return false;
        for (AbstractCard c : AbstractDungeon.player.masterDeck.group) {
            if (c.rarity == AbstractCard.CardRarity.COMMON || c.rarity == AbstractCard.CardRarity.UNCOMMON) return true;
        }
        return false;
    }

    private static boolean canDuplicateRare() {
        if (AbstractDungeon.player.gold < 100) return false;
        for (AbstractCard c : AbstractDungeon.player.masterDeck.group) {
            if (c.rarity == AbstractCard.CardRarity.RARE) return true;
        }
        return false;
    }

    private static boolean hasUpgradeableCard() {
        for (AbstractCard c : AbstractDungeon.player.masterDeck.group) {
            if (c.canUpgrade()) return true;
        }
        return false;
    }

    private static CardGroup getCommonUncommonCards() {
        CardGroup group = new CardGroup(CardGroup.CardGroupType.UNSPECIFIED);
        for (AbstractCard c : AbstractDungeon.player.masterDeck.group) {
            if (c.rarity == AbstractCard.CardRarity.COMMON || c.rarity == AbstractCard.CardRarity.UNCOMMON) {
                group.addToTop(c);
            }
        }
        return group;
    }

    private static CardGroup getRareCards() {
        CardGroup group = new CardGroup(CardGroup.CardGroupType.UNSPECIFIED);
        for (AbstractCard c : AbstractDungeon.player.masterDeck.group) {
            if (c.rarity == AbstractCard.CardRarity.RARE) {
                group.addToTop(c);
            }
        }
        return group;
    }
}
