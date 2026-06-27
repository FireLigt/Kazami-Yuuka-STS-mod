package YuukaMod.cards.rare;

import YuukaMod.cards.BaseCard;
import YuukaMod.cards.special.Mima;
import YuukaMod.cards.special.Shinki;
import YuukaMod.cards.special.Yumemi;
import YuukaMod.character.KazamiYuuka;
import YuukaMod.util.CardStats;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

import java.util.ArrayList;

public class Call_for_help extends BaseCard {
    public static final String ID = makeID(Call_for_help.class.getSimpleName());
    private static final CardStats info = new CardStats(
            KazamiYuuka.Meta.YUUKA_COLOR,
            CardType.SKILL,
            CardRarity.RARE,
            CardTarget.SELF,
            2
    );

    private AbstractCard[] previews;

    public Call_for_help() {
        super(ID, info);
        setExhaust(true);
        previews = new AbstractCard[]{new Mima(), new Shinki(), new Yumemi()};
        cardsToPreview = previews[0];
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        final boolean upgraded = this.upgraded;
        addToBot(new AbstractGameAction() {
            private boolean opened = false;

            @Override
            public void update() {
                if (!opened) {
                    Mima mima = new Mima();
                    Shinki shinki = new Shinki();
                    Yumemi yumemi = new Yumemi();
                    if (upgraded) {
                        mima.upgrade();
                        shinki.upgrade();
                        yumemi.upgrade();
                    }

                    ArrayList<AbstractCard> cards = new ArrayList<>();
                    cards.add(mima);
                    cards.add(shinki);
                    cards.add(yumemi);

                    AbstractDungeon.cardRewardScreen.chooseOneOpen(cards);
                    opened = true;
                }

                if (!AbstractDungeon.isScreenUp) {
                    this.isDone = true;
                }
            }
        });
    }

    @Override
    public void upgrade() {
        super.upgrade();
        for (int i = 0; i < previews.length; i++) {
            AbstractCard c;
            if (i == 0) c = new Mima();
            else if (i == 1) c = new Shinki();
            else c = new Yumemi();
            c.upgrade();
            previews[i] = c;
        }
        cardsToPreview = previews[0];
    }

    @Override
    public void renderCardPreviewInSingleView(SpriteBatch sb) {
        cardsToPreview = previews[0];
        super.renderCardPreviewInSingleView(sb);
        float px = previews[0].current_x;
        float py = previews[0].current_y;
        float ps = previews[0].drawScale;
        float gap = 40.0f * Settings.scale;
        float vStep = AbstractCard.IMG_HEIGHT * ps + gap;
        float hStep = AbstractCard.IMG_WIDTH * ps + gap;

        previews[1].current_x = px - hStep;
        previews[1].current_y = py;
        previews[1].drawScale = ps;
        previews[1].render(sb);

        previews[2].current_x = px - hStep;
        previews[2].current_y = py - vStep;
        previews[2].drawScale = ps;
        previews[2].render(sb);
    }

    @Override
    public AbstractCard makeCopy() {
        return new Call_for_help();
    }
}
