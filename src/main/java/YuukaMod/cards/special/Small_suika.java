package YuukaMod.cards.special;

import YuukaMod.cards.BaseCard;
import YuukaMod.cards.uncommon.Suika;
import YuukaMod.character.KazamiYuuka;
import YuukaMod.util.CardStats;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

import java.util.Iterator;

public class Small_suika extends BaseCard {
    public static final String ID = makeID(Small_suika.class.getSimpleName());
    private static final CardStats info = new CardStats(
            KazamiYuuka.Meta.YUUKA_COLOR,
            CardType.ATTACK,
            CardRarity.SPECIAL,
            CardTarget.ENEMY,
            1
    );

    private static final int DMG = 10;
    private static final int UPG_DMG = 3;

    public Small_suika() {
        super(ID, info);
        setDamage(DMG, UPG_DMG);
        tags.add(CustomTags.PLANT);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot(new DamageAction(m,
                new DamageInfo(p, this.damage, this.damageTypeForTurn),
                AbstractGameAction.AttackEffect.SLASH_DIAGONAL));
        addToBot(new AbstractGameAction() {
            @Override
            public void update() {
                checkAndMerge(p);
                this.isDone = true;
            }
        });
    }

    private static void checkAndMerge(AbstractPlayer p) {
        int count = 0;
        for (AbstractCard c : p.discardPile.group) {
            if (c instanceof Small_suika) count++;
        }
        if (count < 3) return;

        boolean upgraded = false;
        int removed = 0;
        Iterator<AbstractCard> it = p.discardPile.group.iterator();
        while (it.hasNext()) {
            AbstractCard c = it.next();
            if (c instanceof Small_suika && removed < 3) {
                if (c.upgraded) upgraded = true;
                it.remove();
                removed++;
            }
        }

        Suika suika = new Suika();
        if (upgraded) suika.upgrade();
        p.discardPile.addToTop(suika);
    }

    @Override
    public void triggerOnManualDiscard() {
        scheduleMergeCheck();
    }

    @Override
    public void onMoveToDiscard() {
        scheduleMergeCheck();
    }

    @Override
    public void triggerOnEndOfPlayerTurn() {
        scheduleMergeCheck();
    }

    @Override
    public void triggerWhenDrawn() {
        scheduleMergeCheck();
    }

    @Override
    public void triggerOnEndOfTurnForPlayingCard() {
        scheduleMergeCheck();
    }

    private void scheduleMergeCheck() {
        AbstractDungeon.actionManager.addToBottom(new AbstractGameAction() {
            @Override
            public void update() {
                checkAndMerge(AbstractDungeon.player);
                this.isDone = true;
            }
        });
    }

    @Override
    public AbstractCard makeCopy() {
        return new Small_suika();
    }
}
