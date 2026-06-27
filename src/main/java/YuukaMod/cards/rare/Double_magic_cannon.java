package YuukaMod.cards.rare;

import YuukaMod.cards.BaseCard;
import YuukaMod.character.KazamiYuuka;
import YuukaMod.powers.DoubleMagicCannonPower;
import YuukaMod.util.CardStats;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.utility.WaitAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class Double_magic_cannon extends BaseCard {
    public static final String ID = makeID(Double_magic_cannon.class.getSimpleName());
    private static final CardStats info = new CardStats(
            KazamiYuuka.Meta.YUUKA_COLOR,
            CardType.SKILL,
            CardRarity.RARE,
            CardTarget.NONE,
            1
    );

    private static final int TRIGGER = 1;

    public Double_magic_cannon() {
        super(ID, info);
        setCostUpgrade(0);
        setMagic(TRIGGER);
        setExhaust(false);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot(new ApplyPowerAction(
                p,
                p,
                new DoubleMagicCannonPower(p, this.magicNumber),
                this.magicNumber
        ));
        addToBot(new WaitAction(0.1F));
    }

    @Override
    public AbstractCard makeCopy() {
        return new Double_magic_cannon();
    }
}
