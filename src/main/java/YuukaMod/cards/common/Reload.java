package YuukaMod.cards.common;

import YuukaMod.cards.BaseCard;
import YuukaMod.character.KazamiYuuka;
import YuukaMod.util.CardStats;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

import java.util.ArrayList;

public class Reload extends BaseCard {
    public static final String ID = makeID(Reload.class.getSimpleName());
    private static final CardStats info = new CardStats(
            KazamiYuuka.Meta.YUUKA_COLOR,
            CardType.SKILL,
            CardRarity.COMMON,
            CardTarget.NONE,
            0
    );

    public Reload() {
        super(ID, info);
        setSelfRetain(false, true);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot(new AbstractGameAction() {
            @Override
            public void update() {
                ArrayList<AbstractCard> danmakuCards = new ArrayList<>();
                for (AbstractCard c : p.discardPile.group) {
                    if (c.hasTag(CustomTags.DANMAKU)) {
                        danmakuCards.add(c);
                    }
                }
                for (AbstractCard c : danmakuCards) {
                    p.discardPile.moveToDeck(c, true);
                }
                this.isDone = true;
            }
        });
    }

    @Override
    public AbstractCard makeCopy() {
        return new Reload();
    }
}
