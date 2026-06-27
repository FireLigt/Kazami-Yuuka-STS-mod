package YuukaMod.cards.power;

import YuukaMod.cards.BaseCard;
import YuukaMod.character.KazamiYuuka;
import YuukaMod.powers.CrazyYuukaPower;
import YuukaMod.util.CardStats;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class Crazy_yuuka extends BaseCard {
    public static final String ID = makeID(Crazy_yuuka.class.getSimpleName());
    private static final CardStats info = new CardStats(
            KazamiYuuka.Meta.YUUKA_COLOR,
            CardType.POWER,
            CardRarity.UNCOMMON,
            CardTarget.SELF,
            1
    );

    public Crazy_yuuka() {
        super(ID, info);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        int amount = 1;
        addToBot(new ApplyPowerAction(
                p,
                p,
                new CrazyYuukaPower(p, amount, upgraded),
                amount
        ));
    }

    @Override
    public AbstractCard makeCopy() {
        return new Crazy_yuuka();
    }
}
