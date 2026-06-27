package YuukaMod.potions;

import com.badlogic.gdx.graphics.Color;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.CardHelper;
import com.megacrit.cardcrawl.powers.AbstractPower;

import static YuukaMod.Yuukamod.makeID;

public class PureWater extends BasePotion {
    public static final String ID = makeID(PureWater.class.getSimpleName());
    private static final int POTENCY = 0;

    private static final Color LIQUID_COLOR = CardHelper.getColor(130, 200, 255);
    private static final Color HYBRID_COLOR = CardHelper.getColor(60, 150, 240);
    private static final Color SPOTS_COLOR = CardHelper.getColor(200, 235, 255);

    public PureWater() {
        super(ID, POTENCY, PotionRarity.UNCOMMON, PotionSize.M, LIQUID_COLOR, HYBRID_COLOR, SPOTS_COLOR);
        labOutlineColor = Settings.BLUE_RELIC_COLOR;
    }

    @Override
    public void use(AbstractCreature target) {
        for (AbstractPower power : AbstractDungeon.player.powers.toArray(new AbstractPower[0])) {
            if (power.type == AbstractPower.PowerType.DEBUFF) {
                addToBot(new RemoveSpecificPowerAction(AbstractDungeon.player, AbstractDungeon.player, power));
            }
        }
    }

    @Override
    public String getDescription() {
        return potionStrings.DESCRIPTIONS[0];
    }
}
