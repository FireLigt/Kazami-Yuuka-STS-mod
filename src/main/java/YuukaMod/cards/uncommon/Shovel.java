package YuukaMod.cards.uncommon;

import YuukaMod.cards.BaseCard;
import YuukaMod.character.KazamiYuuka;
import YuukaMod.util.CardStats;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

import java.util.ArrayList;

public class Shovel extends BaseCard {
    public static final String ID = makeID(Shovel.class.getSimpleName());
    private static final CardStats info = new CardStats(
            KazamiYuuka.Meta.YUUKA_COLOR,
            CardType.SKILL,
            CardRarity.UNCOMMON,
            CardTarget.NONE,
            1
    );

    private static final int RETRIEVE = 1;
    private static final int UPG_RETRIEVE = 1;

    public Shovel() {
        super(ID, info);
        setExhaust(true);
        setMagic(RETRIEVE, UPG_RETRIEVE);
    }

    @Override
    public boolean canUse(AbstractPlayer p, AbstractMonster m) {
        if (!super.canUse(p, m)) {
            return false;
        }

        boolean hasFlower = false;
        for (AbstractCard c : p.exhaustPile.group) {
            if (c.hasTag(CustomTags.FLOWER)) {
                hasFlower = true;
                break;
            }
        }

        if (!hasFlower) {
            CardStrings strings = CardCrawlGame.languagePack.getCardStrings(cardID);
            String msg = strings != null && strings.EXTENDED_DESCRIPTION != null && strings.EXTENDED_DESCRIPTION.length > 1
                    ? strings.EXTENDED_DESCRIPTION[1]
                    : "There is no Flower card in the exhaust pile...";
            cantUseMessage = msg;
            return false;
        }

        return true;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot(new AbstractGameAction() {
            private int remaining = magicNumber;
            private boolean choosing = false;
            private final ArrayList<java.util.UUID> selectedCardIDs = new ArrayList<>();

            @Override
            public void update() {
                if (remaining <= 0) {
                    this.isDone = true;
                    return;
                }

                if (AbstractDungeon.isScreenUp) {
                    return;
                }

                if (choosing) {
                    choosing = false;
                    if (AbstractDungeon.gridSelectScreen.selectedCards.isEmpty()) {
                        this.isDone = true;
                        return;
                    }
                    for (AbstractCard c : AbstractDungeon.gridSelectScreen.selectedCards) {
                        selectedCardIDs.add(c.uuid);
                        AbstractCard cardRef = c;
                        addToBot(new AbstractGameAction() {
                            @Override
                            public void update() {
                                retrieveToHand(p, cardRef);
                                this.isDone = true;
                            }
                        });
                    }
                    AbstractDungeon.gridSelectScreen.selectedCards.clear();
                    remaining--;
                    if (remaining <= 0) {
                        this.isDone = true;
                        return;
                    }
                }

                ArrayList<AbstractCard> available = new ArrayList<>();
                for (AbstractCard c : p.exhaustPile.group) {
                    if (c.hasTag(CustomTags.FLOWER) && !selectedCardIDs.contains(c.uuid)) {
                        available.add(c);
                    }
                }

                if (available.isEmpty()) {
                    this.isDone = true;
                    return;
                }

                CardGroup group = new CardGroup(CardGroup.CardGroupType.UNSPECIFIED);
                for (AbstractCard c : available) {
                    group.addToTop(c);
                }
                String prompt = cardStrings.EXTENDED_DESCRIPTION != null && cardStrings.EXTENDED_DESCRIPTION.length > 0
                        ? cardStrings.EXTENDED_DESCRIPTION[0]
                        : "Choose Flower cards to retrieve.";
                AbstractDungeon.gridSelectScreen.open(group, 1, prompt, false, false, true, false);
                choosing = true;
            }
        });
    }

    private static void retrieveToHand(AbstractPlayer p, AbstractCard c) {
        if (p.exhaustPile.contains(c)) {
            p.exhaustPile.removeCard(c);
            p.hand.addToHand(c);
            p.hand.refreshHandLayout();
        }
    }

    @Override
    public AbstractCard makeCopy() {
        return new Shovel();
    }
}
