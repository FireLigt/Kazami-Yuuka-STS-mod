package YuukaMod.cards.power;

import YuukaMod.cards.BaseCard;
import YuukaMod.character.KazamiYuuka;
import YuukaMod.powers.MorningHazePower;
import YuukaMod.util.CardStats;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class Morning_haze extends BaseCard {
    public static final String ID = makeID(Morning_haze.class.getSimpleName());
    private static final CardStats info = new CardStats(
            KazamiYuuka.Meta.YUUKA_COLOR,
            CardType.POWER,
            CardRarity.RARE,
            CardTarget.SELF,
            2
    );

    public Morning_haze() {
        super(ID, info);

        tags.add(CustomTags.UNIQUE);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot(new ApplyPowerAction(
                p,
                p,
                new MorningHazePower(p, upgraded),
                1
        ));
    }

    @Override
    public AbstractCard makeCopy() {
        return new Morning_haze();
    }
}
