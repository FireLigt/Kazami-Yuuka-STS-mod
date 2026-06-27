package YuukaMod.potions;

import YuukaMod.powers.GreenTeaPower;
import com.badlogic.gdx.graphics.Color;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.CardHelper;

import static YuukaMod.Yuukamod.makeID;

public class GreenTea extends BasePotion {
    public static final String ID = makeID(GreenTea.class.getSimpleName());
    private static final int POTENCY = 0;

    private static final Color LIQUID_COLOR = CardHelper.getColor(140, 220, 120);
    private static final Color HYBRID_COLOR = CardHelper.getColor(60, 160, 60);
    private static final Color SPOTS_COLOR = CardHelper.getColor(200, 255, 180);

    public GreenTea() {
        super(ID, POTENCY, PotionRarity.UNCOMMON, PotionSize.M, LIQUID_COLOR, HYBRID_COLOR, SPOTS_COLOR);
        labOutlineColor = Settings.GREEN_RELIC_COLOR;
    }

    @Override
    public void use(AbstractCreature target) {
        AbstractDungeon.player.addPower(new GreenTeaPower(AbstractDungeon.player, 1));
    }

    @Override
    public String getDescription() {
        return potionStrings.DESCRIPTIONS[0];
    }
}
