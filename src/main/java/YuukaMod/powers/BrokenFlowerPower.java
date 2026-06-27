package YuukaMod.powers;

import YuukaMod.Yuukamod;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;

public class BrokenFlowerPower extends BasePower {
    public static final String POWER_ID = Yuukamod.makeID(BrokenFlowerPower.class.getSimpleName());

    public BrokenFlowerPower(AbstractCreature owner, int amount) {
        super(POWER_ID, PowerType.BUFF, false, owner, amount);
    }

    @Override
    public float atDamageGive(float damage, DamageInfo.DamageType type) {
        if (amount > 0 && type == DamageInfo.DamageType.NORMAL) {
            return damage * 3;
        }
        return damage;
    }

    @Override
    public void onUseCard(AbstractCard card, UseCardAction action) {
        if (amount > 0 && card.type == AbstractCard.CardType.ATTACK) {
            flash();
            amount--;
            card.exhaustOnUseOnce = true;
            action.exhaustCard = true;
            updateDescription();
            if (amount <= 0) {
                addToTop(new RemoveSpecificPowerAction(owner, owner, this));
            }
        }
    }

    @Override
    public void updateDescription() {
        this.description = DESCRIPTIONS[0];
    }
}
