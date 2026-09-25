package YuukaMod.powers;

import YuukaMod.Yuukamod;
import YuukaMod.cards.BaseCard;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class ChargeUpPower extends BasePower {
    public static final String POWER_ID = Yuukamod.makeID(ChargeUpPower.class.getSimpleName());
    private static final com.megacrit.cardcrawl.localization.PowerStrings powerStrings =
            com.megacrit.cardcrawl.core.CardCrawlGame.languagePack.getPowerStrings(POWER_ID);

    private int cardsToDiscount;
    private final Set<UUID> discountedCards;

    public ChargeUpPower(AbstractCreature owner, int amount) {
        super(POWER_ID, PowerType.BUFF, false, owner, amount);
        cardsToDiscount = 0;
        discountedCards = new HashSet<>();
    }

    @Override
    public void onAfterCardPlayed(AbstractCard card) {
        if (!card.hasTag(BaseCard.CustomTags.PHYSICAL_ARTS)) {
            return;
        }
        flash();
        makePhysicalArtsCardsGlow();
        cardsToDiscount = 2;
        addToBot(new DrawCardAction(2));
        addToBot(new AbstractGameAction() {
            @Override
            public void update() {
                for (AbstractCard c : DrawCardAction.drawnCards) {
                    discountDrawnCard(c);
                }
                cardsToDiscount = 0;
                discountedCards.clear();
                this.isDone = true;
            }
        });
        amount--;
        if (amount <= 0) {
            addToBot(new RemoveSpecificPowerAction(owner, owner, this));
        } else {
            updateDescription();
        }
    }

    @Override
    public void onCardDraw(AbstractCard card) {
        if (cardsToDiscount > 0 && card.hasTag(BaseCard.CustomTags.PHYSICAL_ARTS)) {
            makeCardGlow(card);
            discountDrawnCard(card);
        }
    }

    @Override
    public void atEndOfTurn(boolean isPlayer) {
        if (AbstractDungeon.player != null) {
            for (AbstractCard c : AbstractDungeon.player.hand.group) {
                c.stopGlowing();
            }
        }
    }

    private void makePhysicalArtsCardsGlow() {
        AbstractPlayer p = AbstractDungeon.player;
        if (p == null) return;
        for (AbstractCard c : p.hand.group) {
            if (c.hasTag(BaseCard.CustomTags.PHYSICAL_ARTS)) {
                makeCardGlow(c);
            }
        }
    }

    private static void makeCardGlow(AbstractCard c) {
        c.glowColor = BaseCard.goldGlowColor();
        c.beginGlowing();
    }

    private void discountDrawnCard(AbstractCard card) {
        if (card != null
                && !discountedCards.contains(card.uuid)
                && card.hasTag(BaseCard.CustomTags.PHYSICAL_ARTS)
                && card.costForTurn > 0) {
            card.setCostForTurn(card.costForTurn - 1);
            discountedCards.add(card.uuid);
        }
    }

    @Override
    public void updateDescription() {
        this.description = DESCRIPTIONS[0] + "#b" + amount + DESCRIPTIONS[1];
    }
}
