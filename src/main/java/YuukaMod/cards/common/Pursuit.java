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

public class Pursuit extends BaseCard {
    public static final String ID = makeID(Pursuit.class.getSimpleName());
    private static final CardStats info = new CardStats(
            KazamiYuuka.Meta.YUUKA_COLOR,
            CardType.ATTACK,
            CardRarity.COMMON,
            CardTarget.ENEMY,
            2
    );

    private static final int DAMAGE = 15;
    private static final int UPG_DAMAGE = 4;

    public Pursuit() {
        super(ID, info);
        setDamage(DAMAGE, UPG_DAMAGE);
        tags.add(BaseCard.CustomTags.PHYSICAL_ARTS);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        Pursuit.this.returnToHand = false;
        final boolean targetWasAlive = m != null && !m.isDeadOrEscaped();
        addToBot(new DamageAction(m,
                new DamageInfo(p, this.damage, this.damageTypeForTurn),
                AbstractGameAction.AttackEffect.BLUNT_HEAVY));
        addToBot(new AbstractGameAction() {
            @Override
            public void update() {
                if (targetWasAlive && m.isDeadOrEscaped()) {
                    Pursuit.this.returnToHand = true;
                    AbstractDungeon.player.gainEnergy(3);
                }
                this.isDone = true;
            }
        });
    }

    @Override
    public AbstractCard makeCopy() {
        return new Pursuit();
    }
}
