package YuukaMod.cards.uncommon;

import YuukaMod.cards.BaseCard;
import YuukaMod.character.KazamiYuuka;
import YuukaMod.util.CardStats;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class Bridge_suplex extends BaseCard {
    public static final String ID = makeID(Bridge_suplex.class.getSimpleName());
    private static final CardStats info = new CardStats(
            KazamiYuuka.Meta.YUUKA_COLOR,
            CardType.ATTACK,
            CardRarity.UNCOMMON,
            CardTarget.ENEMY,
            2
    );

    public Bridge_suplex() {
        super(ID, info);
        setDamage(16, 4);
        setCostUpgrade(1);
        tags.add(CustomTags.PHYSICAL_ARTS);
    }

    @Override
    public void onMoveToDiscard() {
        resetCostForTurn();
    }

    @Override
    public void applyPowers() {
        super.applyPowers();
        updateCostFromLastPlayed();
    }

    @Override
    public void triggerOnCardPlayed(AbstractCard cardPlayed) {
        if (cardPlayed == this) {
            return;
        }
        updateCostForPreviousCard(cardPlayed);
    }

    @Override
    public void atTurnStart() {
        resetCostForTurn();
    }

    private void updateCostFromLastPlayed() {
        if (AbstractDungeon.actionManager.cardsPlayedThisTurn.isEmpty()) {
            if (discountApplied) {
                this.costForTurn++;
                this.discountApplied = false;
                this.isCostModifiedForTurn = true;
            }
            return;
        }

        AbstractCard lastCard = AbstractDungeon.actionManager.cardsPlayedThisTurn.get(
                AbstractDungeon.actionManager.cardsPlayedThisTurn.size() - 1);
        updateCostForPreviousCard(lastCard);
    }

    private boolean discountApplied = false;

    private void updateCostForPreviousCard(AbstractCard previousCard) {
        if (discountApplied) {
            this.costForTurn++;
            this.discountApplied = false;
        }
        if (previousCard != null && previousCard.hasTag(CustomTags.PHYSICAL_ARTS) && this.costForTurn > 0) {
            this.costForTurn--;
            this.discountApplied = true;
            this.isCostModifiedForTurn = true;
        }
    }

    private void resetCostForTurn() {
        if (discountApplied) {
            this.costForTurn++;
            this.discountApplied = false;
        }
        this.costForTurn = this.cost;
        this.isCostModifiedForTurn = false;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot(new DamageAction(m,
                new DamageInfo(p, this.damage, this.damageTypeForTurn),
                AbstractGameAction.AttackEffect.BLUNT_HEAVY));
    }

    @Override
    public AbstractCard makeCopy() {
        return new Bridge_suplex();
    }
}
