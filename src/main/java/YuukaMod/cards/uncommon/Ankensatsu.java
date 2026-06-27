package YuukaMod.cards.uncommon;

import YuukaMod.cards.BaseCard;
import YuukaMod.character.KazamiYuuka;
import YuukaMod.util.CardStats;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.common.GainEnergyAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class Ankensatsu extends BaseCard {
    public static final String ID = makeID(Ankensatsu.class.getSimpleName());
    private static final CardStats info = new CardStats(
            KazamiYuuka.Meta.YUUKA_COLOR,
            CardType.ATTACK,
            CardRarity.UNCOMMON,
            CardTarget.ENEMY,
            3
    );

    private static final int DAMAGE = 23;
    private static final int UPG_DAMAGE = 4;

    public Ankensatsu() {
        super(ID, info);
        setDamage(DAMAGE, UPG_DAMAGE);

        tags.add(CustomTags.PHYSICAL_ARTS);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot(new DamageAction(m,
                new DamageInfo(p, this.damage, this.damageTypeForTurn),
                AbstractGameAction.AttackEffect.SLASH_HEAVY));
        addToBot(new GainEnergyAction(1));

        if (this.upgraded && AbstractDungeon.actionManager.cardsPlayedThisTurn.size() > 1) {
            AbstractCard lastCard = AbstractDungeon.actionManager.cardsPlayedThisTurn.get(
                    AbstractDungeon.actionManager.cardsPlayedThisTurn.size() - 2);
            if (lastCard.hasTag(CustomTags.PHYSICAL_ARTS)) {
                addToBot(new GainEnergyAction(2));
            }
        }
    }

    @Override
    public void triggerOnGlowCheck() {
        boolean physicalArtsChain = this.upgraded
                && AbstractDungeon.actionManager.cardsPlayedThisTurn.size() > 1
                && AbstractDungeon.actionManager.cardsPlayedThisTurn.get(
                        AbstractDungeon.actionManager.cardsPlayedThisTurn.size() - 2)
                        .hasTag(CustomTags.PHYSICAL_ARTS);

        if (physicalArtsChain) {
            this.glowColor = AbstractCard.GOLD_BORDER_GLOW_COLOR.cpy();
            this.rightGlowColor = null;
        } else {
            this.glowColor = AbstractCard.BLUE_BORDER_GLOW_COLOR.cpy();
            this.rightGlowColor = null;
        }
    }

    @Override
    public AbstractCard makeCopy() {
        return new Ankensatsu();
    }
}
