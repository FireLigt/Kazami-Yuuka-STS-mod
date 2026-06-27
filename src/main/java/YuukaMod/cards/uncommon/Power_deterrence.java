package YuukaMod.cards.uncommon;

import YuukaMod.cards.BaseCard;
import YuukaMod.character.KazamiYuuka;
import YuukaMod.util.CardStats;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.WeakPower;

public class Power_deterrence extends BaseCard {
    public static final String ID = makeID(Power_deterrence.class.getSimpleName());
    private static final CardStats info = new CardStats(
            KazamiYuuka.Meta.YUUKA_COLOR,
            CardType.SKILL,
            CardRarity.UNCOMMON,
            CardTarget.NONE,
            1
    );
    private static final int DRAW = 3;
    private static final int UPG_DRAW = 1;

    public Power_deterrence() {
        super(ID, info);
        setMagic(DRAW,UPG_DRAW);
        setExhaust(true);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        int drawCount = this.magicNumber;
        int weakAmt = this.magicNumber;
        addToBot(new DrawCardAction(drawCount, new AbstractGameAction() {
            @Override
            public void update() {
                for (AbstractCard card : DrawCardAction.drawnCards) {
                    if (card.hasTag(CustomTags.MAGICCANNON)) {
                        for (AbstractMonster mo : AbstractDungeon.getCurrRoom().monsters.monsters) {
                            if (!mo.isDeadOrEscaped()) {
                                addToTop(new ApplyPowerAction(mo, AbstractDungeon.player, new WeakPower(mo, weakAmt, false), weakAmt));
                            }
                        }
                    }
                }
                this.isDone = true;
            }
        }));
    }

    @Override
    public AbstractCard makeCopy() {
        return new Power_deterrence();
    }
}
