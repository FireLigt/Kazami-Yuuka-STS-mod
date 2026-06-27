package YuukaMod.cards.power;

import YuukaMod.cards.BaseCard;
import YuukaMod.character.KazamiYuuka;
import YuukaMod.powers.WorthyOpponentPower;
import YuukaMod.util.CardStats;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class Worthy_opponent extends BaseCard {
    public static final String ID = makeID(Worthy_opponent.class.getSimpleName());
    private static final CardStats info = new CardStats(
            KazamiYuuka.Meta.YUUKA_COLOR,
            CardType.POWER,
            CardRarity.RARE,
            CardTarget.SELF,
            3
    );
    private static final int STRENGTH_GAIN = 1;

    public Worthy_opponent() {
        super(ID, info);
        setMagic(STRENGTH_GAIN, 0);
        setEthereal(true, false);
        setInnate(false, true);

        tags.add(CustomTags.UNIQUE);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot(new ApplyPowerAction(
                p,
                p,
                new WorthyOpponentPower(p, this.magicNumber),
                this.magicNumber
        ));
    }

    @Override
    public AbstractCard makeCopy() {
        return new Worthy_opponent();
    }
}
