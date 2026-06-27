package YuukaMod.cards.rare;

import YuukaMod.cards.BaseCard;
import YuukaMod.character.KazamiYuuka;
import YuukaMod.util.CardStats;
import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.actions.common.GainEnergyAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class Fragrance extends BaseCard {
    public static final String ID = makeID(Fragrance.class.getSimpleName());
    private static final CardStats info = new CardStats(
            KazamiYuuka.Meta.YUUKA_COLOR,
            CardType.SKILL,
            CardRarity.RARE,
            CardTarget.NONE,
            0
    );

    private int timesUsed = 0;

    public Fragrance() {
        super(ID, info);
        setMagic(1, 1);
        setSecondMagic(2, 0);
        setExhaust(true, false);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        timesUsed++;
        addToBot(new GainEnergyAction(this.magicNumber));
        addToBot(new DrawCardAction(this.magicNumber));
        if (timesUsed >= this.secondMagicNumber) {
            this.exhaust = true;
        }
    }

    @Override
    public AbstractCard makeCopy() {
        return new Fragrance();
    }
}
