package YuukaMod.relics;

import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.AbstractRelic;

import static YuukaMod.Yuukamod.makeID;

public class VioletRelic extends BaseRelic {
    public static final String ID = makeID("VioletRelic");

    public VioletRelic() {
        super(ID, "VioletRelic", RelicTier.RARE, LandingSound.FLAT);
    }

    @Override
    public void onUsePotion() {
        int chance = AbstractDungeon.player.hasRelic(YuukaFumoRelic.ID) ? 60 : 40;
        if (AbstractDungeon.miscRng.random(99) < chance) {
            flash();
            AbstractDungeon.player.obtainPotion(AbstractDungeon.returnRandomPotion());
        }
    }

    @Override
    public String getUpdatedDescription() {
        return DESCRIPTIONS[0];
    }

    @Override
    public AbstractRelic makeCopy() {
        return new VioletRelic();
    }
}
