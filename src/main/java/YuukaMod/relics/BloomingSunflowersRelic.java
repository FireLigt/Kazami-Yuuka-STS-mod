package YuukaMod.relics;

import YuukaMod.util.AutoTriggerLimit;
import com.megacrit.cardcrawl.actions.common.GainEnergyAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.rooms.TreasureRoomBoss;

import static YuukaMod.Yuukamod.makeID;

public class BloomingSunflowersRelic extends BaseRelic {
    public static final String ID = makeID("BloomingSunflowersRelic");
    private static final int ENERGY_THRESHOLD = 3;

    private int energySpentThisTurn;
    private int bonusEnergy;
    private int energyGrantedTotal;

    public BloomingSunflowersRelic() {
        super(ID, "BloomingSunflowersRelic", RelicTier.BOSS, LandingSound.FLAT);
    }

    @Override
    public boolean canSpawn() {
        return AbstractDungeon.player != null && AbstractDungeon.player.hasRelic(SunflowerRelic.ID);
    }

    @Override
    public void bossObtainLogic() {
        this.instantObtain(AbstractDungeon.player, 0, true);
        this.isObtained = true;
        if (AbstractDungeon.getCurrRoom() instanceof TreasureRoomBoss) {
            AbstractDungeon.overlayMenu.proceedButton.show();
        }
    }

    @Override
    public void onEquip() {
    }

    @Override
    public void atBattleStart() {
        energySpentThisTurn = 0;
        bonusEnergy = 0;
        energyGrantedTotal = 0;
        setCounter(0);
    }

    @Override
    public void atTurnStart() {
        if (bonusEnergy > 0) {
            flash();
            AbstractDungeon.actionManager.addToBottom(new GainEnergyAction(bonusEnergy));
            energyGrantedTotal += bonusEnergy;
        }
        if (!AbstractDungeon.player.hasRelic(YuukaFumoRelic.ID)) {
            energySpentThisTurn = 0;
        }
        bonusEnergy = 0;
        setCounter(energySpentThisTurn);
    }

    @Override
    public void onPlayCard(AbstractCard c, AbstractMonster m) {
        if (c.costForTurn > 0 && !AutoTriggerLimit.isAutoTriggered(c)) {
            energySpentThisTurn += c.costForTurn;
            setCounter(energySpentThisTurn);
        }
    }

    @Override
    public void onPlayerEndTurn() {
        if (AbstractDungeon.player.hasRelic(YuukaFumoRelic.ID)) {
            bonusEnergy = Math.max(0, energySpentThisTurn / ENERGY_THRESHOLD - energyGrantedTotal);
        } else {
            bonusEnergy = energySpentThisTurn / ENERGY_THRESHOLD;
        }
    }

    @Override
    public String getUpdatedDescription() {
        return DESCRIPTIONS[0];
    }

    @Override
    public AbstractRelic makeCopy() {
        return new BloomingSunflowersRelic();
    }
}
