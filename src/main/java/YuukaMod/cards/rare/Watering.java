package YuukaMod.cards.rare;

import YuukaMod.cards.BaseCard;
import YuukaMod.character.KazamiYuuka;
import YuukaMod.util.CardStats;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInDrawPileAction;
import com.megacrit.cardcrawl.actions.common.ShuffleAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import java.util.UUID;

public class Watering extends BaseCard {
    public static final String ID = makeID(Watering.class.getSimpleName());
    private static final CardStats info = new CardStats(
            KazamiYuuka.Meta.YUUKA_COLOR,
            CardType.SKILL,
            CardRarity.RARE,
            CardTarget.NONE,
            2
    );

    private static final int MAX_USES = 5;

    public Watering() {
        super(ID, info);
        setCostUpgrade(1);
        this.misc = MAX_USES;
        this.baseMagicNumber = this.magicNumber = this.misc;
    }

    @Override
    public void applyPowers() {
        this.baseMagicNumber = this.magicNumber = this.misc;
        super.applyPowers();
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        if (upgraded) {
            this.misc--;
            this.baseMagicNumber = this.magicNumber = this.misc;
        }

        addToBot(new AbstractGameAction() {
            @Override
            public void update() {
                while (!p.discardPile.isEmpty()) {
                    AbstractCard c = p.discardPile.getTopCard();
                    p.discardPile.removeCard(c);
                    p.drawPile.addToTop(c);
                }
                isDone = true;
            }
        });

        if (upgraded) {
            addToBot(new AbstractGameAction() {
                @Override
                public void update() {
                    for (AbstractCard c : p.exhaustPile.group.toArray(new AbstractCard[0])) {
                        if (isInMasterDeck(c)) {
                            c.unhover();
                            p.exhaustPile.removeCard(c);
                            addToBot(new MakeTempCardInDrawPileAction(c, 1, false, true, false));
                        }
                    }
                    isDone = true;
                }
            });
        }

        addToBot(new ShuffleAction(p.drawPile, false));

        if (upgraded && this.misc <= 0) {
            UUID myUuid = Watering.this.uuid;
            addToBot(new AbstractGameAction() {
                @Override
                public void update() {
                    p.masterDeck.group.removeIf(c -> c.uuid.equals(myUuid));
                    p.hand.group.removeIf(c -> c.uuid.equals(myUuid));
                    p.drawPile.group.removeIf(c -> c.uuid.equals(myUuid));
                    p.discardPile.group.removeIf(c -> c.uuid.equals(myUuid));
                    p.exhaustPile.group.removeIf(c -> c.uuid.equals(myUuid));
                    p.limbo.group.removeIf(c -> c.uuid.equals(myUuid));
                    isDone = true;
                }
            });
        }
    }

    private static boolean isInMasterDeck(AbstractCard c) {
        for (AbstractCard mc : AbstractDungeon.player.masterDeck.group) {
            if (mc.cardID.equals(c.cardID)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public AbstractCard makeStatEquivalentCopy() {
        AbstractCard copy = super.makeStatEquivalentCopy();
        copy.baseMagicNumber = copy.magicNumber = copy.misc;
        return copy;
    }

    @Override
    public AbstractCard makeCopy() {
        return new Watering();
    }
}
