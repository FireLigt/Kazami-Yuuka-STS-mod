package YuukaMod.cards.power;

import YuukaMod.cards.BaseCard;
import YuukaMod.character.KazamiYuuka;
import YuukaMod.powers.MeetRetsuPower;
import YuukaMod.util.CardStats;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class Meet_Retsu extends BaseCard {
    public static final String ID = makeID(Meet_Retsu.class.getSimpleName());
    private static final CardStats info = new CardStats(
            KazamiYuuka.Meta.YUUKA_COLOR,
            CardType.POWER,
            CardRarity.RARE,
            CardTarget.SELF,
            2
    );

    public Meet_Retsu() {
        super(ID, info);
        tags.add(CustomTags.UNIQUE);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        if (!p.hasPower(MeetRetsuPower.POWER_ID)) {
            addToBot(new ApplyPowerAction(p, p, new MeetRetsuPower(p, upgraded), 1));
        }
    }

    @Override
    public AbstractCard makeCopy() {
        return new Meet_Retsu();
    }
}
