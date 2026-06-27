package YuukaMod.cards.power;

import YuukaMod.cards.BaseCard;
import YuukaMod.character.KazamiYuuka;
import YuukaMod.powers.GensouShunkaPower;
import YuukaMod.util.CardStats;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class Gensou_shunka extends BaseCard {
    public static final String ID = makeID(Gensou_shunka.class.getSimpleName());
    private static final CardStats info = new CardStats(
            KazamiYuuka.Meta.YUUKA_COLOR,
            CardType.POWER,
            CardRarity.UNCOMMON,
            CardTarget.SELF,
            1
    );

    public Gensou_shunka() {
        super(ID, info);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        int amount = 1;
        addToBot(new ApplyPowerAction(
                p,
                p,
                new GensouShunkaPower(p, amount, upgraded),
                amount
        ));
    }

    @Override
    public AbstractCard makeCopy() {
        return new Gensou_shunka();
    }
}
