package YuukaMod.powers;

import YuukaMod.Yuukamod;
import com.megacrit.cardcrawl.core.AbstractCreature;

public class LLSPC98formCooldown extends BasePower {
    public static final String POWER_ID = Yuukamod.makeID(LLSPC98formCooldown.class.getSimpleName());
    private static final com.megacrit.cardcrawl.localization.PowerStrings powerStrings =
            com.megacrit.cardcrawl.core.CardCrawlGame.languagePack.getPowerStrings(POWER_ID);

    public LLSPC98formCooldown(AbstractCreature owner, int remaining) {
        super(POWER_ID, PowerType.DEBUFF, false, owner, remaining);
        this.isTurnBased = true;
    }

    @Override
    public void onRemove() {
    }

    @Override
    public void updateDescription() {
        if (amount == 1) {
            this.description = DESCRIPTIONS[0] + " " + amount + " " + DESCRIPTIONS[1];
        } else {
            this.description = DESCRIPTIONS[0] + " " + amount + " " + DESCRIPTIONS[2];
        }
    }
}
