package YuukaMod.powers;

import YuukaMod.Yuukamod;
import com.megacrit.cardcrawl.core.AbstractCreature;

public class YumemiPower extends BasePower {
    public static final String POWER_ID = Yuukamod.makeID(YumemiPower.class.getSimpleName());
    private static final com.megacrit.cardcrawl.localization.PowerStrings powerStrings =
            com.megacrit.cardcrawl.core.CardCrawlGame.languagePack.getPowerStrings(POWER_ID);

    public YumemiPower(AbstractCreature owner) {
        super(POWER_ID, PowerType.BUFF, false, owner, -1);
    }

    @Override
    public void updateDescription() {
        this.description = DESCRIPTIONS[0];
    }
}
