package YuukaMod.cards.uncommon;

import YuukaMod.cards.BaseCard;
import YuukaMod.character.KazamiYuuka;
import YuukaMod.util.AutoTriggerLimit;
import YuukaMod.util.CardStats;
import com.megacrit.cardcrawl.actions.common.GainGoldAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.vfx.GainGoldTextEffect;

public class Marigold extends BaseCard {
    public static final String ID = makeID(Marigold.class.getSimpleName());
    private static final CardStats info = new CardStats(
            KazamiYuuka.Meta.YUUKA_COLOR,
            CardType.SKILL,
            CardRarity.UNCOMMON,
            CardTarget.NONE,
            1
    );

    private static final int GOLD = 15;
    private static final int UPG_GOLD = 15;

    public Marigold() {
        super(ID, info);
        setMagic(GOLD, UPG_GOLD);
        setExhaust(true);

        tags.add(CustomTags.FLOWER);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        AbstractDungeon.effectList.add(new GainGoldTextEffect(this.magicNumber));
        addToBot(new GainGoldAction(this.magicNumber));
        AutoTriggerLimit.onFlowerPlayed(this);
    }

    @Override
    public AbstractCard makeCopy() {
        return new Marigold();
    }
}
