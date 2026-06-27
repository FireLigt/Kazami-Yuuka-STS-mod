package YuukaMod.cards.common;

import YuukaMod.cards.BaseCard;
import YuukaMod.character.KazamiYuuka;
import YuukaMod.util.CardStats;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.actions.utility.ScryAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.DexterityPower;

public class Plantern extends BaseCard {
    public static final String ID = makeID(Plantern.class.getSimpleName());
    private static final CardStats info = new CardStats(
            KazamiYuuka.Meta.YUUKA_COLOR,
            CardType.SKILL,
            CardRarity.COMMON,
            CardTarget.NONE,
            1
    );

    private static final int SCRY = 3;
    private static final int BLOCK_PER_CARD = 4;
    private static final int BLOCK_UPG = 1;

    public Plantern() {
        super(ID, info);
        setMagic(SCRY);
        setSecondMagic(BLOCK_PER_CARD, BLOCK_UPG);
        tags.add(CustomTags.PLANT);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        int cardsBefore = p.discardPile.size();

        addToBot(new ScryAction(this.magicNumber));

        addToBot(new AbstractGameAction() {
            @Override
            public void update() {
                int diff = p.discardPile.size() - cardsBefore;
                if (diff > 0) {
                    int blockPer = secondMagicNumber;
                    if (p.hasPower(DexterityPower.POWER_ID)) {
                        blockPer += p.getPower(DexterityPower.POWER_ID).amount;
                    }
                    addToBot(new GainBlockAction(p, p, diff * blockPer));
                }
                this.isDone = true;
            }
        });
    }

    @Override
    public AbstractCard makeCopy() {
        return new Plantern();
    }
}
