package YuukaMod.cards.power;

import YuukaMod.cards.BaseCard;
import YuukaMod.character.KazamiYuuka;
import YuukaMod.powers.DanmakuFestivalPower;
import YuukaMod.util.CardStats;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class Danmaku_festival extends BaseCard {
    public static final String ID = makeID(Danmaku_festival.class.getSimpleName());
    private static final CardStats info = new CardStats(
            KazamiYuuka.Meta.YUUKA_COLOR,
            CardType.POWER,
            CardRarity.RARE,
            CardTarget.SELF,
            4
    );

    public Danmaku_festival() {
        super(ID, info);
        setEthereal(true, false);
        tags.add(CustomTags.UNIQUE);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        if (!p.hasPower(DanmakuFestivalPower.POWER_ID)) {
            addToBot(new ApplyPowerAction(p, p, new DanmakuFestivalPower(p)));
        }
    }

    @Override
    public AbstractCard makeCopy() {
        return new Danmaku_festival();
    }
}
