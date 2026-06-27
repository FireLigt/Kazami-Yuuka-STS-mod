package YuukaMod.cards.common;

import YuukaMod.cards.BaseCard;
import YuukaMod.character.KazamiYuuka;
import YuukaMod.util.CardStats;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.GainStrengthPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import com.megacrit.cardcrawl.powers.VulnerablePower;

public class Backspin_kick extends BaseCard {
    public static final String ID = makeID(Backspin_kick.class.getSimpleName());
    private static final CardStats info = new CardStats(
            KazamiYuuka.Meta.YUUKA_COLOR,
            CardType.ATTACK,
            CardRarity.COMMON,
            CardTarget.ENEMY,
            2
    );

    private static final int DAMAGE = 10;
    private static final int UPG_DAMAGE = 4;
    private static final int VULN = 1;

    public Backspin_kick() {
        super(ID, info);
        setDamage(DAMAGE, UPG_DAMAGE);
        setMagic(VULN);

        tags.add(CustomTags.PHYSICAL_ARTS);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot(new DamageAction(m,
                new DamageInfo(p, this.damage, this.damageTypeForTurn),
                AbstractGameAction.AttackEffect.BLUNT_HEAVY));
        addToBot(new ApplyPowerAction(m, p,
                new VulnerablePower(m, this.magicNumber, false),
                this.magicNumber));
        addToBot(new ApplyPowerAction(m, p,
                new StrengthPower(m, -this.damage),
                -this.damage));
        addToBot(new ApplyPowerAction(m, p,
                new GainStrengthPower(m, this.damage),
                this.damage));
    }

    @Override
    public AbstractCard makeCopy() {
        return new Backspin_kick();
    }
}
