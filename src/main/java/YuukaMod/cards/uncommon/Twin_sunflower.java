package YuukaMod.cards.uncommon;

import YuukaMod.cards.BaseCard;
import YuukaMod.character.KazamiYuuka;
import YuukaMod.powers.TwinSunflowerPower;
import YuukaMod.util.AutoTriggerLimit;
import YuukaMod.util.CardStats;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class Twin_sunflower extends BaseCard {
    public static final String ID = makeID(Twin_sunflower.class.getSimpleName());
    private static final CardStats info = new CardStats(
            KazamiYuuka.Meta.YUUKA_COLOR,
            CardType.SKILL,
            CardRarity.UNCOMMON,
            CardTarget.NONE,
            2
    );

    public Twin_sunflower() {
        super(ID, info);
        setExhaust(true);
        setCostUpgrade(1);

        tags.add(CustomTags.FLOWER);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot(new ApplyPowerAction(p, p, new TwinSunflowerPower(p, 2), 2));
        AutoTriggerLimit.onFlowerPlayed(this);
    }

    @Override
    public AbstractCard makeCopy() {
        return new Twin_sunflower();
    }
}
