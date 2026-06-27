package YuukaMod.cards.common;

import YuukaMod.cards.BaseCard;
import YuukaMod.character.KazamiYuuka;
import YuukaMod.util.AutoTriggerLimit;
import YuukaMod.util.CardStats;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class Flower_pot extends BaseCard {
    public static final String ID = makeID(Flower_pot.class.getSimpleName());
    private static final CardStats info = new CardStats(
            KazamiYuuka.Meta.YUUKA_COLOR,
            CardType.SKILL,
            CardRarity.COMMON,
            CardTarget.NONE,
            1
    );
    private static final int BLOCK = 6;
    private static final int UPG_BLOCK = 3;

    public Flower_pot() {
        super(ID, info);
        setBlock(BLOCK, UPG_BLOCK);
        tags.add(CustomTags.FLOWER);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        AbstractDungeon.actionManager.addToBottom(new GainBlockAction(p, p, this.block));
        AutoTriggerLimit.onFlowerPlayed(this);
    }

    @Override
    public AbstractCard makeCopy() {
        return new Flower_pot();
    }
}
