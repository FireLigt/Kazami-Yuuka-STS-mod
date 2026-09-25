package YuukaMod.cards.uncommon;

import YuukaMod.cards.BaseCard;
import YuukaMod.character.KazamiYuuka;
import YuukaMod.util.CardStats;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.LoseStrengthPower;
import com.megacrit.cardcrawl.powers.StrengthPower;

public class Prepare extends BaseCard {
    public static final String ID = makeID(Prepare.class.getSimpleName());
    private static final String DANMAKU_COUNT = "DANMAKU";
    private static final CardStats info = new CardStats(
            KazamiYuuka.Meta.YUUKA_COLOR,
            CardType.SKILL,
            CardRarity.UNCOMMON,
            CardTarget.SELF,
            1
    );


    public Prepare() {
        super(ID, info);
        setSelfRetain(false, true);
        setCustomVar(DANMAKU_COUNT, countDanmaku());
        setVarCalculation(DANMAKU_COUNT, (c, m, base) -> countDanmaku());
    }

    private static int countDanmaku() {
        if (AbstractDungeon.player == null) {
            return 0;
        }
        int amount = 0;
        for (AbstractCard card : AbstractDungeon.player.masterDeck.group) {
            if (card.hasTag(CustomTags.DANMAKU)) {
                amount++;
            }
        }
        return amount;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {

        int amount = countDanmaku();

        addToBot(new ApplyPowerAction(p, p, new StrengthPower(p, amount), amount));
        addToBot(new ApplyPowerAction(p, p, new LoseStrengthPower(p, amount), amount));
    }

    @Override
    public AbstractCard makeCopy() {
        return new Prepare();
    }
}
