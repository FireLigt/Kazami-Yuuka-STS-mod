package YuukaMod.cards.uncommon;

import YuukaMod.cards.BaseCard;
import YuukaMod.character.KazamiYuuka;
import YuukaMod.util.CardStats;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class Famed_far extends BaseCard {
    public static final String ID = makeID(Famed_far.class.getSimpleName());
    private static final CardStats info = new CardStats(
            KazamiYuuka.Meta.YUUKA_COLOR,
            CardType.SKILL,
            CardRarity.UNCOMMON,
            CardTarget.SELF,
            2
    );

    public Famed_far() {
        super(ID, info);
        setCostUpgrade(1);
        setExhaust(true);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot(new AbstractGameAction() {
            @Override
            public void update() {
                while (!p.drawPile.isEmpty()) {
                    AbstractCard card = p.drawPile.getTopCard();
                    p.drawPile.removeCard(card);

                    if (p.hand.size() < 10) {
                        p.hand.addToHand(card);
                    } else {
                        p.discardPile.addToTop(card);
                    }

                    if (card.type == AbstractCard.CardType.POWER) {
                        break;
                    }
                }
                this.isDone = true;
            }
        });
    }

    @Override
    public AbstractCard makeCopy() {
        return new Famed_far();
    }
}
