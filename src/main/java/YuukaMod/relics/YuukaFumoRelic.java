package YuukaMod.relics;

import com.megacrit.cardcrawl.relics.AbstractRelic;

import static YuukaMod.Yuukamod.makeID;

public class YuukaFumoRelic extends BaseRelic {
    public static final String ID = makeID("YuukaFumoRelic");

    public YuukaFumoRelic() {
        super(ID, "YuukaFumoRelic", RelicTier.BOSS, LandingSound.FLAT);
    }

    @Override
    public String getUpdatedDescription() {
        return DESCRIPTIONS[0];
    }

    @Override
    public AbstractRelic makeCopy() {
        return new YuukaFumoRelic();
    }
}
