package YuukaMod.potions;

import YuukaMod.util.PowerLibrary;
import com.badlogic.gdx.graphics.Color;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.CardHelper;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.StrengthPower;

import java.util.List;

import static YuukaMod.Yuukamod.makeID;

public class BlackTea extends BasePotion {
    public static final String ID = makeID(BlackTea.class.getSimpleName());
    private static final int POTENCY = 0;

    private static final Color LIQUID_COLOR = CardHelper.getColor(60, 30, 10);
    private static final Color HYBRID_COLOR = CardHelper.getColor(140, 80, 30);
    private static final Color SPOTS_COLOR = CardHelper.getColor(220, 180, 80);

    public BlackTea() {
        super(ID, POTENCY, PotionRarity.COMMON, PotionSize.S, LIQUID_COLOR, HYBRID_COLOR, SPOTS_COLOR);
        labOutlineColor = Settings.CREAM_COLOR;
    }

    @Override
    public void use(AbstractCreature target) {
        addToBot(new AbstractGameAction() {
            @Override
            public void update() {
                AbstractCreature p = AbstractDungeon.player;

                List<Class<? extends AbstractPower>> buffs = PowerLibrary.getBuffClasses(p);
                AbstractPower power = null;
                if (!buffs.isEmpty()) {
                    Class<? extends AbstractPower> chosen = buffs.get(AbstractDungeon.miscRng.random(buffs.size() - 1));
                    power = PowerLibrary.instantiate(chosen, p);
                }
                if (power == null) {
                    power = new StrengthPower(p, 1);
                }
                addToTop(new ApplyPowerAction(p, p, power));

                this.isDone = true;
            }
        });
    }

    @Override
    public String getDescription() {
        return potionStrings.DESCRIPTIONS[0];
    }
}
