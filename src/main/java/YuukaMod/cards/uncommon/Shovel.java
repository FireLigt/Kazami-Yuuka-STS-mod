package YuukaMod.cards.uncommon;

import YuukaMod.cards.BaseCard;
import YuukaMod.character.KazamiYuuka;
import YuukaMod.util.CardStats;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
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
    public void use(AbstractPlayer p, AbstractMonster m) {
        int amount = magicNumber;

        addToBot(new AbstractGameAction() {
            @Override
            public void update() {
                ArrayList<AbstractCard> flowerCards = new ArrayList<>();
                for (AbstractCard c : p.exhaustPile.group) {
                    if (c.hasTag(CustomTags.FLOWER)) {
                        flowerCards.add(c);
                    }
                }

                if (flowerCards.isEmpty()) {
                    this.isDone = true;
                    return;
                }

                if (flowerCards.size() <= amount) {
                    for (AbstractCard c : flowerCards) {
                        retrieveFromExhaust(p, c);
                    }
                    this.isDone = true;
                    return;
                }

                CardGroup cardGroup = new CardGroup(CardGroup.CardGroupType.UNSPECIFIED);
                for (AbstractCard c : flowerCards) {
                    cardGroup.addToTop(c);
                }
                String prompt = cardStrings.EXTENDED_DESCRIPTION != null && cardStrings.EXTENDED_DESCRIPTION.length > 0
                        ? cardStrings.EXTENDED_DESCRIPTION[0]
                        : "Choose Flower cards to retrieve.";
                AbstractDungeon.gridSelectScreen.open(cardGroup, amount, prompt, false, false, false, false);
                addToBot(new AbstractGameAction() {
                    @Override
                    public void update() {
                        if (AbstractDungeon.isScreenUp) {
                            return;
                        }
                        if (!AbstractDungeon.gridSelectScreen.selectedCards.isEmpty()) {
                            int retrieved = 0;
                            for (AbstractCard c : AbstractDungeon.gridSelectScreen.selectedCards) {
                                if (retrieved >= amount) break;
                                retrieveFromExhaust(p, c);
                                retrieved++;
                            }
                            AbstractDungeon.gridSelectScreen.selectedCards.clear();
                        }
                        this.isDone = true;
                    }
                });
                this.isDone = true;
            }
        });
    }

    private static void retrieveFromExhaust(AbstractPlayer p, AbstractCard c) {
        c.unhover();
        c.stopGlowing();
        p.exhaustPile.removeCard(c);
        p.drawPile.addToRandomSpot(c);
        p.drawPile.refreshHandLayout();
        p.exhaustPile.refreshHandLayout();
    }

    @Override
    public AbstractCard makeCopy() {
        return new Shovel();
    }
}
