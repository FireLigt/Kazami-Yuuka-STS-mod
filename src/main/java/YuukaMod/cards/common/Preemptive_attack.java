package YuukaMod.cards.common;

import YuukaMod.cards.BaseCard;
import YuukaMod.character.KazamiYuuka;
import YuukaMod.util.CardStats;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class Preemptive_attack extends BaseCard {
    public static final String ID = makeID(Preemptive_attack.class.getSimpleName());
    private static final CardStats info = new CardStats(
            KazamiYuuka.Meta.YUUKA_COLOR,
            CardType.ATTACK,
            CardRarity.COMMON,
            CardTarget.ENEMY,
            2
    );

    private static final int DAMAGE = 6;
    private static final int UPG_DAMAGE = 3;
    private static final int PER_USE_BONUS = 1;

    public Preemptive_attack() {
        super(ID, info);
        setCostUpgrade(1);
        setDamage(DAMAGE, UPG_DAMAGE);
        updateDamageFromMisc();
        tags.add(CustomTags.PHYSICAL_ARTS);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot(new DamageAction(m,
                new DamageInfo(p, this.damage, this.damageTypeForTurn),
                AbstractGameAction.AttackEffect.BLUNT_HEAVY));
        addToBot(new AbstractGameAction() {
            @Override
            public void update() {
                Preemptive_attack.this.misc += PER_USE_BONUS;
                Preemptive_attack.this.updateDamageFromMisc();
                Preemptive_attack.this.syncMasterDeckCard(p);
                this.isDone = true;
            }
        });
    }

    @Override
    public void applyPowers() {
        updateDamageFromMisc();
        super.applyPowers();
    }

    @Override
    public void calculateCardDamage(AbstractMonster m) {
        updateDamageFromMisc();
        super.calculateCardDamage(m);
    }

    @Override
    public AbstractCard makeStatEquivalentCopy() {
        AbstractCard copy = super.makeStatEquivalentCopy();
        if (copy instanceof Preemptive_attack) {
            ((Preemptive_attack) copy).updateDamageFromMisc();
        }
        return copy;
    }

    private void syncMasterDeckCard(AbstractPlayer p) {
        for (AbstractCard c : p.masterDeck.group) {
            if (c.uuid.equals(this.uuid) && c instanceof Preemptive_attack) {
                ((Preemptive_attack) c).misc = this.misc;
                ((Preemptive_attack) c).updateDamageFromMisc();
                return;
            }
        }
    }

    private void updateDamageFromMisc() {
        this.baseDamage = DAMAGE + this.misc + (this.upgraded ? UPG_DAMAGE : 0);
        this.damage = this.baseDamage;
    }

    @Override
    public void onLoadedMisc() {
        updateDamageFromMisc();
    }

    @Override
    public AbstractCard makeCopy() {
        return new Preemptive_attack();
    }
}
