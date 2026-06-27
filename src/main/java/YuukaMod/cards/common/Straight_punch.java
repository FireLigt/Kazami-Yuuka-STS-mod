package YuukaMod.cards.common;

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

public class Straight_punch extends BaseCard {
    public static final String ID = makeID(Straight_punch.class.getSimpleName());
    private static final CardStats info = new CardStats(
            KazamiYuuka.Meta.YUUKA_COLOR,
            CardType.ATTACK,
            CardRarity.COMMON,
            CardTarget.ENEMY,
            1
    );

    private static final int DAMAGE = 8;
    private static final int UPG_DAMAGE = 4;

    public Straight_punch() {
        super(ID, info);
        setDamage(DAMAGE, UPG_DAMAGE);
        setMagic(1, 1);
        tags.add(BaseCard.CustomTags.PHYSICAL_ARTS);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot(new DamageAction(m,
                new DamageInfo(p, this.damage, this.damageTypeForTurn),
                AbstractGameAction.AttackEffect.BLUNT_LIGHT));

        addToBot(new AbstractGameAction() {
            @Override
            public void update() {
                for (int i = 0; i < magicNumber; i++) {
                    if (!p.discardPile.isEmpty()) {
                        AbstractCard c = p.discardPile.getRandomCard(AbstractDungeon.cardRandomRng);
                        p.discardPile.moveToHand(c);
                    }
                }
                this.isDone = true;
            }
        });
    }

    @Override
    public AbstractCard makeCopy() {
        return new Straight_punch();
    }
}
