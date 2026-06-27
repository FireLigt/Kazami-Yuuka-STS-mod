package YuukaMod.cards.uncommon;

import YuukaMod.cards.BaseCard;
import YuukaMod.character.KazamiYuuka;
import YuukaMod.Yuukamod;
import YuukaMod.powers.ContemptPower;
import YuukaMod.util.CardStats;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class Contempt extends BaseCard {
    public static final String ID = makeID(Contempt.class.getSimpleName());
    private static final CardStats info = new CardStats(
            KazamiYuuka.Meta.YUUKA_COLOR,
            CardType.SKILL,
            CardRarity.UNCOMMON,
            CardTarget.NONE,
            2
    );

    public Contempt() {
        super(ID, info);
        tags.add(CustomTags.UNIQUE);
        setExhaust(true);
        setCostUpgrade(1);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        // Grant a one-turn power that reduces cost of deck cards (and drawn cards) by 1. Power does not stack.
        if (!p.hasPower(ContemptPower.POWER_ID)) {
            addToBot(new ApplyPowerAction(p, p, new ContemptPower(p), 1));
        } else {
            Yuukamod.logger.info("Contempt: player already has ContemptPower; not applying again");
        }
    }

    @Override
    public AbstractCard makeCopy() {
        return new Contempt();
    }
}
