package YuukaMod.cards.power;

import YuukaMod.cards.BaseCard;
import YuukaMod.character.KazamiYuuka;
import YuukaMod.Yuukamod;
import YuukaMod.powers.SeamlessComboPower;
import YuukaMod.util.CardStats;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class Seamless_combo extends BaseCard {
    public static final String ID = makeID(Seamless_combo.class.getSimpleName());
    private static final CardStats info = new CardStats(
            KazamiYuuka.Meta.YUUKA_COLOR,
            CardType.POWER,
            CardRarity.UNCOMMON,
            CardTarget.SELF,
            2
    );

    public Seamless_combo() {
        super(ID, info);
        tags.add(CustomTags.UNIQUE);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        if (!p.hasPower(SeamlessComboPower.POWER_ID)) {
            addToBot(new ApplyPowerAction(
                    p,
                    p,
                    new SeamlessComboPower(p, upgraded),
                    1
            ));
        } else {
            Yuukamod.logger.info("Seamless Combo: player already has SeamlessComboPower; not applying again");
        }
    }

    @Override
    public AbstractCard makeCopy() {
        return new Seamless_combo();
    }
}
