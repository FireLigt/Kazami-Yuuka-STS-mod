package YuukaMod.cards.power;

import YuukaMod.cards.BaseCard;
import YuukaMod.character.KazamiYuuka;
import YuukaMod.powers.TheBeautiesOfNaturePower;
import YuukaMod.util.CardStats;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class The_beauties_of_nature extends BaseCard {
    public static final String ID = makeID(The_beauties_of_nature.class.getSimpleName());
    private static final CardStats info = new CardStats(
            KazamiYuuka.Meta.YUUKA_COLOR,
            CardType.POWER,
            CardRarity.RARE,
            CardTarget.SELF,
            -1
    );

    public The_beauties_of_nature() {
        super(ID, info);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        if (!this.freeToPlayOnce) {
            p.energy.use(this.energyOnUse);
        }
        int amount = upgraded ? (this.energyOnUse + 1) / 2 : this.energyOnUse / 2;
        addToBot(new ApplyPowerAction(
                p,
                p,
                new TheBeautiesOfNaturePower(p, amount),
                amount
        ));
    }

    @Override
    public AbstractCard makeCopy() {
        return new The_beauties_of_nature();
    }
}
