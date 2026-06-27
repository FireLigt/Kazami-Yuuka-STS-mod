package YuukaMod.cards.uncommon;

import YuukaMod.cards.BaseCard;
import YuukaMod.cards.special.Small_suika;
import YuukaMod.character.KazamiYuuka;
import YuukaMod.util.AutoTriggerLimit;
import YuukaMod.util.CardStats;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class Suika extends BaseCard {
    public static final String ID = makeID(Suika.class.getSimpleName());
    private static final CardStats info = new CardStats(
            KazamiYuuka.Meta.YUUKA_COLOR,
            CardType.ATTACK,
            CardRarity.UNCOMMON,
            CardTarget.ENEMY,
            3
    );

    private static final int DMG = 30;
    private static final int UPG_DMG = 10;
    private static final int REDUCED_DMG = 15;

    public boolean autoTriggered = false;

    public Suika() {
        super(ID, info);
        setDamage(DMG, UPG_DMG);
        setMagic(REDUCED_DMG, 5);
        tags.add(CustomTags.PLANT);
        cardsToPreview = new Small_suika();
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        boolean triggeredByFlower = AutoTriggerLimit.consumeAutoTriggered(this);
        autoTriggered = triggeredByFlower;
        if (triggeredByFlower) {
            addToBot(new DamageAction(m,
                    new DamageInfo(p, this.magicNumber, DamageInfo.DamageType.NORMAL),
                    AbstractGameAction.AttackEffect.BLUNT_HEAVY));
        } else {
            this.purgeOnUse = true;
            addToBot(new DamageAction(m,
                    new DamageInfo(p, this.damage, this.damageTypeForTurn),
                    AbstractGameAction.AttackEffect.BLUNT_HEAVY));
            addToBot(new AbstractGameAction() {
                @Override
                public void update() {
                    for (int i = 0; i < 3; i++) {
                        Small_suika small = new Small_suika();
                        if (Suika.this.upgraded) small.upgrade();
                        small.exhaust = Suika.this.exhaust;
                        AbstractDungeon.player.drawPile.addToRandomSpot(small);
                    }
                    this.isDone = true;
                }
            });
        }
    }

    @Override
    public void upgrade() {
        super.upgrade();
        Small_suika preview = new Small_suika();
        preview.upgrade();
        cardsToPreview = preview;
    }

    @Override
    public AbstractCard makeCopy() {
        return new Suika();
    }
}
