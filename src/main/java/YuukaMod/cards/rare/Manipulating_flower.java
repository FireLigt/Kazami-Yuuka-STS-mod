package YuukaMod.cards.rare;

import YuukaMod.cards.BaseCard;
import YuukaMod.character.KazamiYuuka;
import YuukaMod.util.CardStats;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.actions.common.EmptyDeckShuffleAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.watcher.VigorPower;

import java.util.ArrayList;

public class Manipulating_flower extends BaseCard {
    public static final String ID = makeID(Manipulating_flower.class.getSimpleName());
    private static final CardStats info = new CardStats(
            KazamiYuuka.Meta.YUUKA_COLOR,
            CardType.SKILL,
            CardRarity.RARE,
            CardTarget.NONE,
            1
    );

    public Manipulating_flower() {
        super(ID, info);
        setExhaust(true);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot(new DrawUntilFullAction(p, upgraded, false));
    }

    private static class DrawUntilFullAction extends AbstractGameAction {
        private final AbstractPlayer player;
        private final boolean upgraded;
        private final boolean alreadyShuffled;

        private DrawUntilFullAction(AbstractPlayer player, boolean upgraded, boolean alreadyShuffled) {
            this.player = player;
            this.upgraded = upgraded;
            this.alreadyShuffled = alreadyShuffled;
        }

        @Override
        public void update() {
            int handSpace = Settings.MAX_HAND_SIZE - player.hand.size();
            if (handSpace <= 0) {
                this.isDone = true;
                return;
            }

            if (player.drawPile.isEmpty() && !player.discardPile.isEmpty() && !alreadyShuffled) {
                addToTop(new DrawUntilFullAction(player, upgraded, true));
                addToTop(new EmptyDeckShuffleAction());
                this.isDone = true;
                return;
            }

            ArrayList<AbstractCard> flowers = new ArrayList<>();
            for (AbstractCard c : player.drawPile.group) {
                if (c.hasTag(CustomTags.FLOWER)) {
                    flowers.add(c);
                }
            }

            int moved = 0;
            for (AbstractCard c : flowers) {
                if (moved >= handSpace) break;
                if (player.drawPile.contains(c)) {
                    player.drawPile.moveToHand(c);
                    moved++;
                }
            }

            int remaining = handSpace - moved;
            if (remaining > 0) {
                final int flowerDrawn = moved;
                addToTop(new DrawCardAction(remaining, new AbstractGameAction() {
                    @Override
                    public void update() {
                        int totalDrawn = flowerDrawn + DrawCardAction.drawnCards.size();
                        gainVigor(totalDrawn);
                        this.isDone = true;
                    }
                }));
            } else {
                gainVigor(moved);
            }

            this.isDone = true;
        }

        private void gainVigor(int totalDrawn) {
            if (upgraded && totalDrawn > 0) {
                addToTop(new ApplyPowerAction(
                        player, player,
                        new VigorPower(player, totalDrawn),
                        totalDrawn
                ));
            }
        }
    }

    @Override
    public AbstractCard makeCopy() {
        return new Manipulating_flower();
    }
}
