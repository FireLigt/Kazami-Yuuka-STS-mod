package YuukaMod.cards.rare;

import YuukaMod.cards.BaseCard;
import YuukaMod.character.KazamiYuuka;
import YuukaMod.util.CardStats;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.utility.WaitAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class Uninterrupted_combo extends BaseCard {
    public static final String ID = makeID(Uninterrupted_combo.class.getSimpleName());
    private static final CardStats info = new CardStats(
            KazamiYuuka.Meta.YUUKA_COLOR,
            CardType.ATTACK,
            CardRarity.RARE,
            CardTarget.ENEMY,
            2
    );
    private static final int DAMAGE = 1;
    private static final int HITS = 13;
    private static final int UPG_HITS = 2;


    public Uninterrupted_combo() {
        super(ID, info);

        setDamage(DAMAGE);
        setMagic(HITS,UPG_HITS);

        tags.add(CustomTags.PHYSICAL_ARTS);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        DamageInfo.DamageType chargeType = this.damageTypeForTurn;
        for (int i = 0; i < this.magicNumber; i++)
            AbstractDungeon.actionManager.addToBottom(new DamageAction(m,
                    new DamageInfo(p, this.damage, chargeType),
                    AbstractGameAction.AttackEffect.BLUNT_LIGHT));
        addToBot(new WaitAction(0.1F));
    }

    @Override
    public AbstractCard makeCopy() {
        return new Uninterrupted_combo();
    }
}
