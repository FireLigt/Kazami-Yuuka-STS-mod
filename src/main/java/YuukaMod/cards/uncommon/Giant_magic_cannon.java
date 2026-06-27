package YuukaMod.cards.uncommon;

import YuukaMod.cards.BaseCard;
import YuukaMod.character.KazamiYuuka;
import YuukaMod.powers.EnergyDebtPower;
import YuukaMod.util.AutoTriggerLimit;
import YuukaMod.util.CardStats;
import YuukaMod.util.MagicCannonComboTracker;
import YuukaMod.util.VFXHelper;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.DamageAllEnemiesAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.ui.panels.EnergyPanel;

public class Giant_magic_cannon extends BaseCard {
    public static final String ID = makeID(Giant_magic_cannon.class.getSimpleName());
    private static final CardStats info = new CardStats(
            KazamiYuuka.Meta.YUUKA_COLOR,
            CardType.ATTACK,
            CardRarity.UNCOMMON,
            CardTarget.ALL_ENEMY,
            5
    );
    private static final int DAMAGE = 45;
    private static final int UPG_DAMAGE = 15;
    private static final int MIN_ENERGY_TO_PLAY = 3;

    private int preUseEnergy = -1;

    public Giant_magic_cannon() {
        super(ID, info);
        setDamage(DAMAGE, UPG_DAMAGE);
        this.isMultiDamage = true;
        tags.add(CustomTags.MAGICCANNON);
    }

    @Override
    public boolean hasEnoughEnergy() {
        if (this.freeToPlayOnce || this.ignoreEnergyOnUse) {
            this.preUseEnergy = 0;
            return true;
        }
        if (this.costForTurn <= 0) {
            this.preUseEnergy = EnergyPanel.totalCount;
            return true;
        }
        int current = EnergyPanel.totalCount;
        if (current < MIN_ENERGY_TO_PLAY) return false;
        if (super.hasEnoughEnergy()) {
            this.preUseEnergy = current;
            return true;
        }
        boolean canPlay = current >= MIN_ENERGY_TO_PLAY && current < this.costForTurn;
        if (canPlay) {
            this.preUseEnergy = current;
        }
        return canPlay;
    }

    @Override
    public boolean canUse(AbstractPlayer p, AbstractMonster m) {
        if (p == null) return false;
        if (this.freeToPlayOnce || this.ignoreEnergyOnUse) return this.cardPlayable(m);
        if (this.costForTurn <= 0) return this.cardPlayable(m);
        if (EnergyPanel.totalCount < MIN_ENERGY_TO_PLAY) return false;
        return this.cardPlayable(m);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        if (p == null) return;

        final int currentEnergy = this.preUseEnergy >= 0 ? this.preUseEnergy : EnergyPanel.totalCount;
        final int cost = this.costForTurn;
        this.preUseEnergy = -1;

        // Guard: if somehow we get here with <3 energy and cost >0, abort
        if (!this.freeToPlayOnce && !this.ignoreEnergyOnUse && cost > 0 && currentEnergy < MIN_ENERGY_TO_PLAY) {
            return;
        }

        VFXHelper.giantBeamToAll(p);
        addToBot(new DamageAllEnemiesAction(p, this.multiDamage, this.damageTypeForTurn, AbstractGameAction.AttackEffect.NONE));
        MagicCannonComboTracker.record(this);
        AutoTriggerLimit.onMagicCannonPlayed(this);

        if (!this.freeToPlayOnce && !this.ignoreEnergyOnUse && cost > currentEnergy) {
            final int debtAmount = cost - currentEnergy;
            addToBot(new ApplyPowerAction(p, p, new EnergyDebtPower(p, debtAmount)));
        }
    }

    @Override
    public void triggerOnCardPlayed(AbstractCard cardPlayed) {
        if (cardPlayed != this) {
            MagicCannonComboTracker.onAnyCardPlayed(cardPlayed);
        }
    }

    @Override
    public AbstractCard makeCopy() {
        return new Giant_magic_cannon();
    }
}
