package YuukaMod.cards.rare;

import YuukaMod.cards.BaseCard;
import YuukaMod.character.KazamiYuuka;
import YuukaMod.util.CardStats;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class Infinite_spiral extends BaseCard {
    public static final String ID = makeID(Infinite_spiral.class.getSimpleName());
    private static final CardStats info = new CardStats(
            KazamiYuuka.Meta.YUUKA_COLOR,
            CardType.ATTACK,
            CardRarity.RARE,
            CardTarget.ALL_ENEMY,
            -2
    );
    private static final int DAMAGE = 400;
    private static final int UPG_DAMAGE = 314;

    private static boolean rewardPending = false;
    private static boolean triggeredThisTurn = false;
    private static int lastTriggeredTurn = -1;

    public static boolean isRewardPending() {
        return rewardPending;
    }

    public static void clearRewardPending() {
        rewardPending = false;
    }

    private int turnsRemaining;

    public Infinite_spiral() {
        super(ID, info);
        setDamage(DAMAGE, UPG_DAMAGE);
        setMagic(6);
        setSelfRetain(true);
        setInnate(true);
        turnsRemaining = 6;
    }

    @Override
    public boolean canUse(AbstractPlayer p, AbstractMonster m) {
        return false;
    }

    @Override
    public void resetAttributes() {
        super.resetAttributes();
        this.magicNumber = this.turnsRemaining;
        this.baseMagicNumber = this.turnsRemaining;
    }

    @Override
    public void onRetained() {
        int currentTurn = AbstractDungeon.actionManager.turn;
        if (currentTurn != lastTriggeredTurn) {
            lastTriggeredTurn = currentTurn;
            triggeredThisTurn = false;
        }

        if (triggeredThisTurn) {
            initializeDescription();
            return;
        }

        turnsRemaining--;
        magicNumber = turnsRemaining;
        baseMagicNumber = turnsRemaining;
        if (turnsRemaining <= 0) {
            triggeredThisTurn = true;
            rewardPending = true;
            addToBot(new AbstractGameAction() {
                @Override
                public void update() {
                    for (AbstractMonster m : AbstractDungeon.getCurrRoom().monsters.monsters) {
                        if (!m.isDeadOrEscaped()) {
                            m.damage(new DamageInfo(AbstractDungeon.player, Infinite_spiral.this.damage, DamageInfo.DamageType.THORNS));
                        }
                    }

                    removeSingleCopy(AbstractDungeon.player.masterDeck, true);
                    removeSingleCopy(AbstractDungeon.player.hand, false);
                    removeSingleCopy(AbstractDungeon.player.drawPile, false);
                    removeSingleCopy(AbstractDungeon.player.discardPile, false);
                    removeSingleCopy(AbstractDungeon.player.exhaustPile, false);
                    removeSingleCopy(AbstractDungeon.player.limbo, false);
                    this.isDone = true;
                }
            });
        }
        initializeDescription();
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
    }

    @Override
    public AbstractCard makeCopy() {
        return new Infinite_spiral();
    }

    @Override
    public AbstractCard makeStatEquivalentCopy() {
        AbstractCard copy = super.makeStatEquivalentCopy();
        if (copy instanceof Infinite_spiral) {
            ((Infinite_spiral) copy).turnsRemaining = this.turnsRemaining;
        }
        return copy;
    }

    private void removeSingleCopy(com.megacrit.cardcrawl.cards.CardGroup group, boolean allowCardIdFallback) {
        AbstractCard fallback = null;
        for (AbstractCard c : group.group) {
            if (c == this || c.uuid.equals(this.uuid)) {
                group.removeCard(c);
                return;
            }
            if (allowCardIdFallback && fallback == null && c.cardID.equals(this.cardID)) {
                fallback = c;
            }
        }
        if (fallback != null) {
            group.removeCard(fallback);
        }
    }
}
