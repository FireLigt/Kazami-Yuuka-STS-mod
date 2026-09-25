package YuukaMod.powers;

import YuukaMod.Yuukamod;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.LoseHPAction;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.core.AbstractCreature;

public class TryYourBestPower extends BasePower {
    public static final String POWER_ID = Yuukamod.makeID(TryYourBestPower.class.getSimpleName());
    private static final com.megacrit.cardcrawl.localization.PowerStrings powerStrings =
            com.megacrit.cardcrawl.core.CardCrawlGame.languagePack.getPowerStrings(POWER_ID);

    private boolean revived = false;
    private boolean upgraded;
    private boolean selfKill = false;

    public TryYourBestPower(AbstractCreature owner, boolean upgraded) {
        super(POWER_ID, PowerType.BUFF, false, owner, -1);
        this.upgraded = upgraded;
    }

    @Override
    public int onLoseHp(int damage) {
        if (selfKill) return damage;
        if (damage >= owner.currentHealth && !revived) {
            revived = true;
            flash();
            owner.heal(owner.maxHealth);
            return 0;
        }
        return damage;
    }

    @Override
    public void atStartOfTurn() {
        if (revived) {
            addToTop(new RemoveSpecificPowerAction(owner, owner, this));
        }
    }

    @Override
    public void atEndOfTurn(boolean isPlayer) {
        if (!isPlayer && !revived && !upgraded && owner.currentHealth > 0) {
            selfKill = true;
            flash();
            addToBot(new AbstractGameAction() {
                @Override
                public void update() {
                    addToTop(new LoseHPAction(owner, owner, owner.currentHealth));
                    this.isDone = true;
                }
            });
        }
    }

    @Override
    public void updateDescription() {
        this.description = DESCRIPTIONS[upgraded ? 1 : 0];
    }
}
