package YuukaMod.potions;

import com.badlogic.gdx.graphics.Color;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.CardHelper;
import YuukaMod.powers.BombPower;

import static YuukaMod.Yuukamod.makeID;

public class BombPotion extends BasePotion {
    public static final String ID = makeID(BombPotion.class.getSimpleName());
    private static final int POTENCY = 0;

    private static final Color LIQUID_COLOR = CardHelper.getColor(40, 40, 40);
    private static final Color HYBRID_COLOR = CardHelper.getColor(200, 60, 40);
    private static final Color SPOTS_COLOR = CardHelper.getColor(255, 200, 60);

    public BombPotion() {
        super(ID, POTENCY, PotionRarity.RARE, PotionSize.SPHERE, LIQUID_COLOR, HYBRID_COLOR, SPOTS_COLOR);
        labOutlineColor = Settings.RED_RELIC_COLOR;
    }

    @Override
    public void use(AbstractCreature target) {
        AbstractCreature p = AbstractDungeon.player;
        addToBot(new ApplyPowerAction(p, p, new BombPower(p)));
    }

    @Override
    public String getDescription() {
        return potionStrings.DESCRIPTIONS[0];
    }
}
