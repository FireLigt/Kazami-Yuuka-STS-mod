package YuukaMod.relics;

import YuukaMod.cards.special.Mega_magic_cannon;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInHandAction;
import com.megacrit.cardcrawl.actions.common.RelicAboveCreatureAction;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.AbstractRelic;

import static YuukaMod.Yuukamod.makeID;

public class MegaMagicCannonRelic extends BaseRelic {
    public static final String ID = makeID("MegaMagicCannonRelic");
    private static final int TURN_TRIGGER = 3;
    private int turnsElapsed = 0;

    public MegaMagicCannonRelic() {
        super(ID, "MegaMagicCannonRelic", RelicTier.STARTER, LandingSound.HEAVY);
    }

    @Override
    public void atTurnStart() {
        turnsElapsed++;
        if (turnsElapsed == TURN_TRIGGER) {
            flash();
            addToBot(new RelicAboveCreatureAction(AbstractDungeon.player, this));
            addToBot(new MakeTempCardInHandAction(new Mega_magic_cannon()));
        }
    }

    @Override
    public void atBattleStart() {
        turnsElapsed = 0;
    }

    @Override
    public void onEquip() {
        turnsElapsed = 0;
    }

    @Override
    public String getUpdatedDescription() {
        return DESCRIPTIONS[0];
    }

    @Override
    public AbstractRelic makeCopy() {
        return new MegaMagicCannonRelic();
    }
}
