package YuukaMod.cards.uncommon;

import YuukaMod.cards.BaseCard;
import YuukaMod.character.KazamiYuuka;
import YuukaMod.util.CardStats;
import YuukaMod.Yuukamod;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

import java.util.HashMap;
import java.util.Map;

public class Bouquet extends BaseCard {
    public static final String ID = makeID(Bouquet.class.getSimpleName());
    private static final CardStats info = new CardStats(
            KazamiYuuka.Meta.YUUKA_COLOR,
            CardType.SKILL,
            CardRarity.UNCOMMON,
            CardTarget.NONE,
            3
    );

    private static boolean rewardPending = false;

    public Bouquet() {
        super(ID, info);
        setExhaust(true);
        setCostUpgrade(2);
        this.misc = 5;
        this.baseMagicNumber = this.magicNumber = this.misc;
    }

    public static boolean isRewardPending() {
        return rewardPending;
    }

    public static void clearRewardPending() {
        rewardPending = false;
    }

    @Override
    public void applyPowers() {
        this.baseMagicNumber = this.magicNumber = this.misc;
        super.applyPowers();
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        this.misc--;
        this.baseMagicNumber = this.magicNumber = this.misc;

        boolean updated = false;
        for (AbstractCard c : p.masterDeck.group) {
            if (c.uuid.equals(this.uuid)) {
                c.misc = this.misc;
                c.baseMagicNumber = c.magicNumber = this.misc;
                updated = true;
                break;
            }
        }
        if (!updated) {
            // Try to sync with a deck copy that had the previous misc (typical case when UUIDs differ)
            for (AbstractCard c : p.masterDeck.group) {
                if (c.cardID.equals(Bouquet.ID) && c.misc == this.misc + 1) {
                    c.misc = this.misc;
                    c.baseMagicNumber = c.magicNumber = this.misc;
                    updated = true;
                    Yuukamod.logger.info("Bouquet: synced masterDeck by cardID and previous misc");
                    break;
                }
            }
        }
        if (!updated) {
            // Fallback: sync first Bouquet in deck by cardID (if only one exists this is fine)
            for (AbstractCard c : p.masterDeck.group) {
                if (c.cardID.equals(Bouquet.ID)) {
                    c.misc = this.misc;
                    c.baseMagicNumber = c.magicNumber = this.misc;
                    updated = true;
                    Yuukamod.logger.info("Bouquet: synced masterDeck by cardID fallback");
                    break;
                }
            }
        }
        if (!updated) {
            Yuukamod.logger.warn("Bouquet: failed to sync masterDeck; deck size=" + p.masterDeck.group.size());
        }

        if (this.misc <= 0) {
            rewardPending = true;
            addToBot(new AbstractGameAction() {
                @Override
                public void update() {
                    AbstractPlayer pl = AbstractDungeon.player;
                    boolean removed = false;
                    for (AbstractCard c : new java.util.ArrayList<>(pl.masterDeck.group)) {
                        // Prefer removing a Bouquet whose misc has reached 0 (handles different UUID instances)
                        if (c.cardID.equals(Bouquet.ID) && c.misc <= 0) {
                            pl.masterDeck.removeCard(c);
                            removed = true;
                            Yuukamod.logger.info("Bouquet: removed card from masterDeck (misc<=0)");
                            break;
                        }
                    }
                    if (!removed) {
                        // Fallback: try to remove by UUID (original behavior) and log for debugging
                        for (AbstractCard c : new java.util.ArrayList<>(pl.masterDeck.group)) {
                            if (c.uuid.equals(Bouquet.this.uuid)) {
                                pl.masterDeck.removeCard(c);
                                removed = true;
                                Yuukamod.logger.info("Bouquet: removed card from masterDeck by UUID");
                                break;
                            }
                        }
                    }
                    if (!removed) {
                        Yuukamod.logger.warn("Bouquet: failed to remove card from masterDeck. Deck size=" + pl.masterDeck.group.size());
                    }
                    this.isDone = true;
                }
            });
        }
    }

    @Override
    public AbstractCard makeStatEquivalentCopy() {
        AbstractCard copy = super.makeStatEquivalentCopy();
        copy.baseMagicNumber = copy.magicNumber = copy.misc;
        return copy;
    }

    @Override
    public AbstractCard makeCopy() {
        return new Bouquet();
    }
}
