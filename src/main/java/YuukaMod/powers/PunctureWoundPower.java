package YuukaMod.powers;

import YuukaMod.cards.BaseCard;
import YuukaMod.Yuukamod;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.GainStrengthPower;
import com.megacrit.cardcrawl.powers.StrengthPower;

public class PunctureWoundPower extends BasePower {
    public static final String POWER_ID = Yuukamod.makeID(PunctureWoundPower.class.getSimpleName());
    private static final com.megacrit.cardcrawl.localization.PowerStrings powerStrings =
            com.megacrit.cardcrawl.core.CardCrawlGame.languagePack.getPowerStrings(POWER_ID);

    public PunctureWoundPower(AbstractCreature owner, int amount) {
        super(POWER_ID, PowerType.BUFF, false, owner, amount);
    }

    @Override
    public void onUseCard(AbstractCard card, UseCardAction action) {
        if (card.hasTag(BaseCard.CustomTags.MAGICCANNON) && this.amount > 0) {
            this.flash();
            makeMagicCannonCardsGlow();
            for (AbstractMonster mo : AbstractDungeon.getCurrRoom().monsters.monsters) {
                if (!mo.isDeadOrEscaped()) {
                    int dmg = card.damage;
                    if (dmg > 0) {
                        addToBot(new ApplyPowerAction(mo, AbstractDungeon.player,
                                new StrengthPower(mo, -dmg), -dmg));
                        addToBot(new ApplyPowerAction(mo, AbstractDungeon.player,
                                new GainStrengthPower(mo, dmg), dmg));
                    }
                }
            }
            addToBot(new RemoveSpecificPowerAction(this.owner, this.owner, POWER_ID));
        }
    }

    @Override
    public void atEndOfTurn(boolean isPlayer) {
        if (AbstractDungeon.player != null) {
            for (AbstractCard c : AbstractDungeon.player.hand.group) {
                c.stopGlowing();
            }
        }
    }

    private void makeMagicCannonCardsGlow() {
        AbstractPlayer p = AbstractDungeon.player;
        if (p == null) return;
        for (AbstractCard c : p.hand.group) {
            if (c.hasTag(BaseCard.CustomTags.MAGICCANNON)) {
                c.glowColor = BaseCard.redGlowColor();
                c.beginGlowing();
            }
        }
    }

    @Override
    public void updateDescription() {
        this.description = DESCRIPTIONS[0];
    }
}
