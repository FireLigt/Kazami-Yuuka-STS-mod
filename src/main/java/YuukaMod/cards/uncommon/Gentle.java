package YuukaMod.cards.uncommon;

import YuukaMod.cards.BaseCard;
import YuukaMod.character.KazamiYuuka;
import YuukaMod.powers.NoMannersPower;
import YuukaMod.util.CardStats;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class Gentle extends BaseCard {
    public static final String ID = makeID(Gentle.class.getSimpleName());
    private static final CardStats info = new CardStats(
            KazamiYuuka.Meta.YUUKA_COLOR,
            CardType.SKILL,
            CardRarity.UNCOMMON,
            CardTarget.SELF,
            2
    );

    public Gentle() {
        super(ID, info);
        setBlock(12, 3);
        setExhaust(true);
    }

    @Override
    public boolean canUse(AbstractPlayer p, AbstractMonster m) {
        if (!super.canUse(p, m)) return false;
        boolean attackedThisTurn = false;
        for (AbstractCard c : AbstractDungeon.actionManager.cardsPlayedThisTurn) {
            if (c.type == CardType.ATTACK) {
                attackedThisTurn = true;
                break;
            }
        }
        if (attackedThisTurn) {
            cantUseMessage = "I can't use this after attacking.";
            return false;
        }
        return true;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot(new GainBlockAction(p, p, this.block));

        boolean enemyAttacking = false;
        for (AbstractMonster mon : AbstractDungeon.getMonsters().monsters) {
            if (!mon.isDeadOrEscaped() && mon.getIntentBaseDmg() >= 0) {
                enemyAttacking = true;
                break;
            }
        }
        if (enemyAttacking) {
            for (AbstractMonster mon : AbstractDungeon.getMonsters().monsters) {
                if (!mon.isDeadOrEscaped() && mon.getIntentBaseDmg() >= 0) {
                    addToBot(new ApplyPowerAction(mon, p, new NoMannersPower(mon)));
                }
            }
        }
    }

    @Override
    public AbstractCard makeCopy() {
        return new Gentle();
    }
}
