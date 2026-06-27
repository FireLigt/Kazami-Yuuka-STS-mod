package YuukaMod.cards.special;

import YuukaMod.cards.BaseCard;
import YuukaMod.character.KazamiYuuka;
import YuukaMod.util.CardStats;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInHandAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;

public class Shinki extends BaseCard {
    public static final String ID = makeID(Shinki.class.getSimpleName());
    private static final CardStats info = new CardStats(
            KazamiYuuka.Meta.YUUKA_COLOR,
            CardType.SKILL,
            CardRarity.SPECIAL,
            CardTarget.SELF,
            0
    );

    public Shinki() {
        super(ID, info);
        setCostUpgrade(1);
        setExhaust(true);
        setEthereal(true);
    }

    @Override
    public void onChoseThisOption() {
        addToBot(new MakeTempCardInHandAction(this.makeStatEquivalentCopy()));
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot(new AbstractGameAction() {
            @Override
            public void update() {
                for (AbstractPower pow : p.powers) {
                    if (pow.type == AbstractPower.PowerType.BUFF && pow.amount > 0) {
                        if (Shinki.this.upgraded) {
                            pow.amount *= 2;
                        } else {
                            pow.amount += (pow.amount + 1) / 2;
                        }
                        pow.updateDescription();
                    }
                }
                this.isDone = true;
            }
        });
    }

    @Override
    public void upgrade() {
        if (!upgraded) {
            super.upgrade();
            initializeDescription();
        }
    }

    @Override
    public AbstractCard makeCopy() {
        return new Shinki();
    }
}
