package YuukaMod.cards.power;

import YuukaMod.cards.BaseCard;
import YuukaMod.character.KazamiYuuka;
import YuukaMod.powers.TheBeautiesOfNaturePower;
import YuukaMod.util.CardStats;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.relics.ChemicalX;

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
        boolean isXCost = this.cost == -1 && this.costForTurn == -1;
        int effect = isXCost ? this.energyOnUse : 0;

        if (isXCost) {
            if (p.hasRelic(ChemicalX.ID)) {
                effect += ChemicalX.BOOST;
                p.getRelic(ChemicalX.ID).flash();
            }
            if (!this.freeToPlayOnce) {
                p.energy.use(this.energyOnUse);
            }
        }

        if (effect > 0) {
            int amount = upgraded ? (effect + 1) / 2 : effect / 2;
            addToBot(new ApplyPowerAction(
                    p,
                    p,
                    new TheBeautiesOfNaturePower(p, amount),
                    amount
            ));
        }
    }

    @Override
    public AbstractCard makeCopy() {
        return new The_beauties_of_nature();
    }
}
