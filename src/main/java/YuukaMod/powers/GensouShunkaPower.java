package YuukaMod.powers;

import YuukaMod.Yuukamod;
import YuukaMod.cards.BaseCard;
import YuukaMod.util.GeneralUtils;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.CardLibrary;

import java.util.ArrayList;

public class GensouShunkaPower extends BasePower {
    public static final String POWER_ID = Yuukamod.makeID(GensouShunkaPower.class.getSimpleName());
    private static final com.megacrit.cardcrawl.localization.PowerStrings powerStrings =
            com.megacrit.cardcrawl.core.CardCrawlGame.languagePack.getPowerStrings(POWER_ID);

    private boolean upgraded;

    public GensouShunkaPower(AbstractCreature owner, int amount, boolean upgraded) {
        super(POWER_ID, PowerType.BUFF, false, owner, amount);
        this.upgraded = upgraded;
    }

    @Override
    public void atStartOfTurnPostDraw() {
        flash();

        ArrayList<AbstractCard> flowers = new ArrayList<>();
        for (AbstractCard c : CardLibrary.cards.values()) {
            if (c.hasTag(BaseCard.CustomTags.FLOWER)) {
                flowers.add(c);
            }
        }

        if (!flowers.isEmpty()) {
            for (int i = 0; i < amount; i++) {
                AbstractCard card = flowers.get(
                        AbstractDungeon.cardRandomRng.random(flowers.size() - 1)
                ).makeCopy();
                boolean shouldUpgrade = upgraded && card.canUpgrade();
                addToBot(new AbstractGameAction() {
                    @Override
                    public void update() {
                        AbstractCard c = card.makeStatEquivalentCopy();
                        if (shouldUpgrade) {
                            c.upgrade();
                        }
                        GeneralUtils.makeTemporary(c);
                        AbstractDungeon.player.hand.addToTop(c);
                        AbstractDungeon.player.hand.refreshHandLayout();
                        this.isDone = true;
                    }
                });
            }
        }
    }

    @Override
    public void updateDescription() {
        this.description = DESCRIPTIONS[upgraded ? 1 : 0];
    }
}
