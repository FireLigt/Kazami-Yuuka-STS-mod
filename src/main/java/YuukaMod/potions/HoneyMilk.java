package YuukaMod.potions;

import YuukaMod.character.KazamiYuuka;
import YuukaMod.powers.HoneyMilkPower;
import com.badlogic.gdx.graphics.Color;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.CardHelper;

import static YuukaMod.Yuukamod.makeID;

public class HoneyMilk extends BasePotion {
    public static final String ID = makeID(HoneyMilk.class.getSimpleName());
    private static final int POTENCY = 0;

    private static final Color LIQUID_COLOR = CardHelper.getColor(255, 220, 180);
    private static final Color HYBRID_COLOR = CardHelper.getColor(200, 150, 100);
    private static final Color SPOTS_COLOR = null;

    public HoneyMilk() {
        super(ID, POTENCY, PotionRarity.RARE, PotionSize.BOTTLE, LIQUID_COLOR, HYBRID_COLOR, SPOTS_COLOR);
        playerClass = KazamiYuuka.Meta.FLOWER_FIELD_TYRANT;
        labOutlineColor = Settings.GREEN_RELIC_COLOR;
    }

    @Override
    public void use(AbstractCreature target) {
        AbstractDungeon.player.addPower(new HoneyMilkPower(AbstractDungeon.player, AbstractDungeon.player.currentHealth));
    }

    @Override
    public String getDescription() {
        return potionStrings.DESCRIPTIONS[0];
    }
}
