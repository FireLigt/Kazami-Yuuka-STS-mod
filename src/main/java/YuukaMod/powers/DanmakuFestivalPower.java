package YuukaMod.powers;

import YuukaMod.cards.BaseCard;
import YuukaMod.Yuukamod;
import YuukaMod.util.AutoTriggerLimit;
import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.actions.common.GainEnergyAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;

public class DanmakuFestivalPower extends BasePower {
    public static final String POWER_ID = Yuukamod.makeID(DanmakuFestivalPower.class.getSimpleName());

    public DanmakuFestivalPower(AbstractCreature owner) {
        super(POWER_ID, PowerType.BUFF, false, owner, -1);
    }

    @Override
    public void onAfterCardPlayed(AbstractCard card) {
        if (card.hasTag(BaseCard.CustomTags.DANMAKU)
                && AutoTriggerLimit.consumeMagicCannonTriggeredDanmaku(card)) {
            flash();
            addToBot(new GainEnergyAction(1));
            addToBot(new DrawCardAction(owner, 1));
        }
    }

    @Override
    public void updateDescription() {
        this.description = DESCRIPTIONS[0];
    }
}
