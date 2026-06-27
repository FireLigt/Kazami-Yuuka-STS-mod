package YuukaMod.cards.special;

import YuukaMod.cards.BaseCard;
import YuukaMod.character.KazamiYuuka;
import YuukaMod.powers.YumemiPower;
import YuukaMod.util.CardStats;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInHandAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class Yumemi extends BaseCard {
    public static final String ID = makeID(Yumemi.class.getSimpleName());
    private static final CardStats info = new CardStats(
            KazamiYuuka.Meta.YUUKA_COLOR,
            CardType.SKILL,
            CardRarity.SPECIAL,
            CardTarget.SELF,
            1
    );

    public Yumemi() {
        super(ID, info);
        setExhaust(true);
        setEthereal(true);
        setCostUpgrade(0);
    }

    @Override
    public void onChoseThisOption() {
        addToBot(new MakeTempCardInHandAction(this.makeStatEquivalentCopy()));
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot(new ApplyPowerAction(p, p, new YumemiPower(p), 1));
    }

    @Override
    public AbstractCard makeCopy() {
        return new Yumemi();
    }
}
