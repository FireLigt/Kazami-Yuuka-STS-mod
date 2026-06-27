package YuukaMod.cards.special;

import YuukaMod.cards.BaseCard;
import YuukaMod.character.KazamiYuuka;
import YuukaMod.util.CardStats;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.GainEnergyAction;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInHandAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.IntangiblePlayerPower;


public class Mima extends BaseCard {
    public static final String ID = makeID(Mima.class.getSimpleName());
    private static final CardStats info = new CardStats(
            KazamiYuuka.Meta.YUUKA_COLOR,
            CardType.SKILL,
            CardRarity.SPECIAL,
            CardTarget.SELF,
            0
    );

    private static final int ENERGY = 1;
    private static final int INTANGIBLE = 1;
    private static final int UPG_ENERGY = 1;

    public Mima() {
        super(ID, info);
        setMagic(INTANGIBLE, 0);
        setExhaust(true);
        setEthereal(true);
    }

    @Override
    public void onChoseThisOption() {
        addToBot(new MakeTempCardInHandAction(this.makeStatEquivalentCopy()));
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        int energyGain = ENERGY + (upgraded ? UPG_ENERGY : 0);
        addToBot(new GainEnergyAction(energyGain));
        addToBot(new ApplyPowerAction(p, p, new IntangiblePlayerPower(p, magicNumber), magicNumber));
    }

    @Override
    public AbstractCard makeCopy() {
        return new Mima();
    }
}
