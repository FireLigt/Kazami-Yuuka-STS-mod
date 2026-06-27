package YuukaMod.cards.uncommon;

import YuukaMod.cards.BaseCard;
import YuukaMod.character.KazamiYuuka;
import YuukaMod.util.CardStats;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.WeakPower;

public class Finishing_move extends BaseCard {
    public static final String ID = makeID(Finishing_move.class.getSimpleName());
    private static final CardStats info = new CardStats(
            KazamiYuuka.Meta.YUUKA_COLOR,
            CardType.ATTACK,
            CardRarity.UNCOMMON,
            CardTarget.ENEMY,
            2
    );

    private static final int DAMAGE = 15;
    private static final int UPG_DAMAGE = 5;
    private static final int WEAK = 1;
    private static final int UPG_WEAK = 1;

    public Finishing_move() {
        super(ID, info);
        setDamage(DAMAGE, UPG_DAMAGE);
        setMagic(WEAK, UPG_WEAK);
        tags.add(CustomTags.PHYSICAL_ARTS);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        boolean physicalArtsUsed = false;
        for (AbstractCard c : AbstractDungeon.actionManager.cardsPlayedThisTurn) {
            if (c != this && c.hasTag(CustomTags.PHYSICAL_ARTS)) {
                physicalArtsUsed = true;
                break;
            }
        }

        int finalDamage = physicalArtsUsed ? this.damage * 2 : this.damage;
        addToBot(new DamageAction(m,
                new DamageInfo(p, finalDamage, this.damageTypeForTurn),
                AbstractGameAction.AttackEffect.BLUNT_HEAVY));

        if (physicalArtsUsed) {
            addToBot(new ApplyPowerAction(m, p,
                    new WeakPower(m, this.magicNumber, false),
                    this.magicNumber));
        }

        addToBot(new AbstractGameAction() {
            @Override
            public void update() {
                AbstractDungeon.player.endTurnQueued = true;
                this.isDone = true;
            }
        });
    }

    @Override
    public AbstractCard makeCopy() {
        return new Finishing_move();
    }
}
