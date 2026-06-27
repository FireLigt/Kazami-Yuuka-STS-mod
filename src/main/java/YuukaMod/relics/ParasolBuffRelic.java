package YuukaMod.relics;

import com.megacrit.cardcrawl.actions.common.RelicAboveCreatureAction;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import java.util.ArrayList;

import static YuukaMod.Yuukamod.makeID;

public class ParasolBuffRelic extends BaseRelic {
    public static final String ID = makeID("ParasolBuffRelic");

    public ParasolBuffRelic() {
        super(ID, "ParasolBuffRelic", RelicTier.SPECIAL, LandingSound.CLINK);
    }

    @Override
    public void atTurnStart() {
        ArrayList<AbstractPower> buffs = new ArrayList<>();
        for (AbstractPower power : AbstractDungeon.player.powers) {
            if (power.type == AbstractPower.PowerType.BUFF) {
                buffs.add(power);
            }
        }
        if (buffs.isEmpty()) return;

        flash();
        addToBot(new RelicAboveCreatureAction(AbstractDungeon.player, this));

        AbstractPower target = buffs.get(AbstractDungeon.cardRandomRng.random(buffs.size() - 1));
        target.amount++;
        target.updateDescription();
    }

    @Override
    public boolean canSpawn() {
        return !AbstractDungeon.player.hasRelic(ParasolDebuffRelic.ID);
    }

    @Override
    public String getUpdatedDescription() {
        return DESCRIPTIONS[0];
    }

    @Override
    public AbstractRelic makeCopy() {
        return new ParasolBuffRelic();
    }
}
