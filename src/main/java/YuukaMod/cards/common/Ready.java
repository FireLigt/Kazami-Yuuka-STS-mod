package YuukaMod.cards.common;

import YuukaMod.cards.BaseCard;
import YuukaMod.character.KazamiYuuka;
import YuukaMod.util.CardStats;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.vfx.SpeechBubble;

import java.util.ArrayList;

public class Ready extends BaseCard {
    public static final String ID = makeID(Ready.class.getSimpleName());
    private static final CardStats info = new CardStats(
            KazamiYuuka.Meta.YUUKA_COLOR,
            CardType.SKILL,
            CardRarity.COMMON,
            CardTarget.NONE,
            0
    );

    private static final int DRAW = 1;
    private static final int UPG_DRAW = 1;

    private boolean warnedNoCannon = false;

    public Ready() {
        super(ID, info);
        setMagic(DRAW, UPG_DRAW);
    }

    @Override
    public boolean canUse(AbstractPlayer p, AbstractMonster m) {
        if (p.hoveredCard != this) warnedNoCannon = false;
        if (!super.canUse(p, m)) return false;

        boolean hasCannon = false;
        for (AbstractCard c : p.drawPile.group) {
            if (c.hasTag(CustomTags.MAGICCANNON)) {
                hasCannon = true;
                break;
            }
        }

        if (!hasCannon) {
            cantUseMessage = "no magic cannon in my draw pile";
            if (hb.clickStarted && !warnedNoCannon) {
                warnedNoCannon = true;
                AbstractDungeon.effectList.add(new SpeechBubble(
                        AbstractDungeon.player.dialogX,
                        AbstractDungeon.player.dialogY,
                        2.5F,
                        "no magic cannon in my draw pile",
                        true
                ));
            }
            return false;
        }

        warnedNoCannon = false;
        return true;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        ArrayList<AbstractCard> magicCannons = new ArrayList<>();
        for (AbstractCard c : p.drawPile.group) {
            if (c.hasTag(CustomTags.MAGICCANNON)) {
                magicCannons.add(c);
            }
        }

        if (!magicCannons.isEmpty()) {
            int count = Math.min(this.magicNumber, magicCannons.size());
            for (int i = 0; i < count; i++) {
                int index = AbstractDungeon.cardRandomRng.random(magicCannons.size() - 1);
                p.drawPile.moveToHand(magicCannons.remove(index));
            }
        }
    }

    @Override
    public AbstractCard makeCopy() {
        return new Ready();
    }
}
