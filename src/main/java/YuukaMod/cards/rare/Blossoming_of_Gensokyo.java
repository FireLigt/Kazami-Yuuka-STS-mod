package YuukaMod.cards.rare;

import YuukaMod.cards.BaseCard;
import YuukaMod.character.KazamiYuuka;
import YuukaMod.util.CardStats;
import YuukaMod.util.GeneralUtils;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class Blossoming_of_Gensokyo extends BaseCard {
    public static final String ID = makeID(Blossoming_of_Gensokyo.class.getSimpleName());
    private static final CardStats info = new CardStats(
            KazamiYuuka.Meta.YUUKA_COLOR,
            CardType.ATTACK,
            CardRarity.RARE,
            CardTarget.ENEMY,
            2
    );
    private static final int DAMAGE = 15;
    private static final int UPG_DAMAGE = 5;

    public Blossoming_of_Gensokyo() {
        super(ID, info);
        setDamage(DAMAGE, UPG_DAMAGE);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot(new DamageAction(m, new DamageInfo(p, this.damage, this.damageTypeForTurn), AbstractGameAction.AttackEffect.BLUNT_HEAVY));
        addToBot(new AbstractGameAction() {
            @Override
            public void update() {
                AbstractCard copy = Blossoming_of_Gensokyo.this.makeCopy();
                if (Blossoming_of_Gensokyo.this.upgraded) {
                    copy.upgrade();
                }
                copy.baseDamage = Blossoming_of_Gensokyo.this.baseDamage * 2;
                copy.damage = copy.baseDamage;
                copy.rawDescription = cardStrings.EXTENDED_DESCRIPTION[0];
                GeneralUtils.makeTemporary(copy);
                p.hand.addToTop(copy);
                p.hand.refreshHandLayout();
                p.hand.applyPowers();
                this.isDone = true;
            }
        });
    }

    @Override
    public void upgrade() {
        if (!this.upgraded) {
            super.upgrade();
        }
    }

    @Override
    public AbstractCard makeCopy() {
        return new Blossoming_of_Gensokyo();
    }
}
