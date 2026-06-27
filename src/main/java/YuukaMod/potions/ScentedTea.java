package YuukaMod.potions;

import com.badlogic.gdx.graphics.Color;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.CardHelper;
import com.megacrit.cardcrawl.powers.AbstractPower;

import java.util.ArrayList;

import static YuukaMod.Yuukamod.makeID;

public class ScentedTea extends BasePotion {
    public static final String ID = makeID(ScentedTea.class.getSimpleName());
    private static final int POTENCY = 0;

    private static final Color LIQUID_COLOR = CardHelper.getColor(200, 160, 255);
    private static final Color HYBRID_COLOR = CardHelper.getColor(150, 100, 220);
    private static final Color SPOTS_COLOR = CardHelper.getColor(255, 200, 255);

    public ScentedTea() {
        super(ID, POTENCY, PotionRarity.RARE, PotionSize.BOTTLE, LIQUID_COLOR, HYBRID_COLOR, SPOTS_COLOR);
        labOutlineColor = Settings.PURPLE_RELIC_COLOR;
    }

    @Override
    public void use(AbstractCreature target) {
        addToBot(new AbstractGameAction() {
            @Override
            public void update() {
                ArrayList<AbstractPower> buffs = new ArrayList<>();
                for (AbstractPower power : AbstractDungeon.player.powers) {
                    if (power.type == AbstractPower.PowerType.BUFF) {
                        buffs.add(power);
                    }
                }
                if (!buffs.isEmpty()) {
                    AbstractPower selected = buffs.get(AbstractDungeon.miscRng.random(buffs.size() - 1));
                    selected.amount *= 2;
                    selected.flash();
                    selected.updateDescription();
                }
                this.isDone = true;
            }
        });
    }

    @Override
    public String getDescription() {
        return potionStrings.DESCRIPTIONS[0];
    }
}
