package YuukaMod.relics;

import com.megacrit.cardcrawl.actions.common.RelicAboveCreatureAction;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import java.util.ArrayList;

import static YuukaMod.Yuukamod.makeID;

public class ParasolDebuffRelic extends BaseRelic {
    public static final String ID = makeID("ParasolDebuffRelic");

    public ParasolDebuffRelic() {
        super(ID, "ParasolDebuffRelic", RelicTier.SPECIAL, LandingSound.CLINK);
    }

    @Override
    public boolean canSpawn() {
        return !AbstractDungeon.player.hasRelic(ParasolBuffRelic.ID);
    }

    @Override
    public void atTurnStart() {
        ArrayList<AbstractPower> debuffs = new ArrayList<>();
        for (AbstractPower power : AbstractDungeon.player.powers) {
            if (power.type == AbstractPower.PowerType.DEBUFF) {
                debuffs.add(power);
            }
        }
        if (debuffs.isEmpty()) return;

        flash();
        addToBot(new RelicAboveCreatureAction(AbstractDungeon.player, this));

        AbstractPower target = debuffs.get(AbstractDungeon.cardRandomRng.random(debuffs.size() - 1));
        target.amount--;
        if (target.amount <= 0) {
            addToBot(new RemoveSpecificPowerAction(AbstractDungeon.player, AbstractDungeon.player, target));
        } else {
            target.updateDescription();
        }
    }

    @Override
    public String getUpdatedDescription() {
        return DESCRIPTIONS[0];
    }

    @Override
    public AbstractRelic makeCopy() {
        return new ParasolDebuffRelic();
    }
}
