package YuukaMod.powers;

import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.PowerStrings;

import static YuukaMod.Yuukamod.makeID;

public class HoneyMilkPower extends BasePower {
    public static final String POWER_ID = makeID("HoneyMilkPower");
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);

    private int hpOnUse;

    public HoneyMilkPower(AbstractCreature owner, int hpOnUse) {
        super(POWER_ID, PowerType.BUFF, false, owner, 0);
        this.hpOnUse = hpOnUse;
        updateDescription();
    }

    @Override
    public void stackPower(int stackAmount) {
        this.hpOnUse = owner.currentHealth;
        updateDescription();
    }

    @Override
    public void wasHPLost(DamageInfo info, int damageAmount) {
        updateDescription();
    }

    @Override
    public int onLoseHp(int damageAmount) {
        addToBot(new com.megacrit.cardcrawl.actions.AbstractGameAction() {
            @Override
            public void update() {
                updateDescription();
                this.isDone = true;
            }
        });
        return damageAmount;
    }

    public void applyHeal() {
        int healAmount = Math.max(0, hpOnUse - owner.currentHealth);
        if (healAmount > 0) {
            owner.heal(healAmount);
        }
    }

    @Override
    public void updateDescription() {
        int healAmount = Math.max(0, hpOnUse - owner.currentHealth);
        this.amount = healAmount;
        description = powerStrings.DESCRIPTIONS[0] + healAmount + powerStrings.DESCRIPTIONS[1];
    }
}
