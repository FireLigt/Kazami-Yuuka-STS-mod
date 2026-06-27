package YuukaMod.cards.common;

import YuukaMod.cards.BaseCard;
import YuukaMod.character.KazamiYuuka;
import YuukaMod.powers.SunflowerPower;
import YuukaMod.util.AutoTriggerLimit;
import YuukaMod.util.CardStats;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class Sunflower extends BaseCard {
    public static final String ID = makeID(Sunflower.class.getSimpleName());
    private static final CardStats info = new CardStats(
            KazamiYuuka.Meta.YUUKA_COLOR,
            CardType.SKILL,
            CardRarity.COMMON,
            CardTarget.NONE,
            1
    );

    public Sunflower() {
        super(ID, info);
        setExhaust(true);
        setCostUpgrade(0);

        tags.add(CustomTags.FLOWER);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot(new ApplyPowerAction(p, p, new SunflowerPower(p, 2), 2));
        AutoTriggerLimit.onFlowerPlayed(this);
    }

    @Override
    public AbstractCard makeCopy() {
        return new Sunflower();
    }
}
