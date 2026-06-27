package YuukaMod.cards.power;

import YuukaMod.cards.BaseCard;
import YuukaMod.character.KazamiYuuka;
import YuukaMod.powers.MatureGangPower;
import YuukaMod.Yuukamod;
import YuukaMod.util.CardStats;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class Mature_gang extends BaseCard {
    public static final String ID = makeID(Mature_gang.class.getSimpleName());
    private static final CardStats info = new CardStats(
            KazamiYuuka.Meta.YUUKA_COLOR,
            CardType.POWER,
            CardRarity.UNCOMMON,
            CardTarget.SELF,
            2
    );

    private static final int DRAW = 2;

    public Mature_gang() {
        super(ID, info);
        setMagic(DRAW, 1);
        setInnate(false, true);
        tags.add(CustomTags.UNIQUE);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        if (!p.hasPower(MatureGangPower.POWER_ID)) {
            addToBot(new ApplyPowerAction(p, p, new MatureGangPower(p, magicNumber), magicNumber));
        } else {
            Yuukamod.logger.info("Mature_gang: player already has MatureGangPower; not applying again");
        }
    }

    @Override
    public AbstractCard makeCopy() {
        return new Mature_gang();
    }
}
