package YuukaMod.potions;

import static YuukaMod.Yuukamod.makeID;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInHandAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

public class Tea extends BasePotion {
    public static final String ID = makeID(Tea.class.getSimpleName());
    private static final int POTENCY = 0;

    public Tea() {
        // Use default size/color constructor
        super(ID, POTENCY, PotionRarity.UNCOMMON, PotionSize.S, PotionColor.EXPLOSIVE);
    }

    @Override
    public void use(AbstractCreature target) {
        // Let the player pick a card from their master deck and create a copy in hand
        addToBot(new AbstractGameAction() {
            @Override
            public void update() {
                AbstractPlayer p = AbstractDungeon.player;
                if (p == null) { this.isDone = true; return; }

                if (p.masterDeck == null || p.masterDeck.group.isEmpty()) {
                    this.isDone = true;
                    return;
                }

                if (p.masterDeck.group.size() == 1) {
                    AbstractCard c = p.masterDeck.group.get(0);
                    AbstractCard copy = c.makeStatEquivalentCopy();
                    addToBot(new MakeTempCardInHandAction(copy, 1));
                    this.isDone = true;
                    return;
                }

                CardGroup cardGroup = new CardGroup(CardGroup.CardGroupType.UNSPECIFIED);
                for (AbstractCard c : p.masterDeck.group) {
                    cardGroup.addToTop(c);
                }

                AbstractDungeon.gridSelectScreen.open(cardGroup, 1, "Choose a card from your deck.", false, false, true, false);

                addToBot(new AbstractGameAction() {
                    @Override
                    public void update() {
                        if (AbstractDungeon.isScreenUp) return;
                        if (!AbstractDungeon.gridSelectScreen.selectedCards.isEmpty()) {
                            for (AbstractCard c : AbstractDungeon.gridSelectScreen.selectedCards) {
                                c.unhover();
                                AbstractCard copy = c.makeStatEquivalentCopy();
                                addToBot(new MakeTempCardInHandAction(copy, 1));
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

    @Override
    public String getDescription() {
        return potionStrings.DESCRIPTIONS[0];
    }
}
