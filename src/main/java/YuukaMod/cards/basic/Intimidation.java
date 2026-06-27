package YuukaMod.cards.basic;

import YuukaMod.cards.BaseCard;
import YuukaMod.character.KazamiYuuka;
import YuukaMod.util.CardStats;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.GainEnergyAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.StrengthPower;
import com.megacrit.cardcrawl.powers.WeakPower;

public class Intimidation extends BaseCard {
    public static final String ID = makeID(Intimidation.class.getSimpleName());
    private static final CardStats info = new CardStats(
            KazamiYuuka.Meta.YUUKA_COLOR,
            CardType.SKILL,
            CardRarity.BASIC,
            CardTarget.ENEMY,
            1
    );

    private static final int STRENGTH = 1;
    private static final int UPG_STR = 1;
    private static final int WEAK = 2;

    public Intimidation() {
        super(ID, info);
        setMagic(STRENGTH, UPG_STR);
        setSecondMagic(WEAK);
        setCostUpgrade(0);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        if (m != null) {
            addToBot(new ApplyPowerAction(p, p, new StrengthPower(p, this.magicNumber), this.magicNumber));
            addToBot(new ApplyPowerAction(m, p, new WeakPower(m, this.secondMagicNumber, false), this.secondMagicNumber));
        }
    }

    @Override
    public AbstractCard makeCopy() {
        return new Intimidation();
    }
}