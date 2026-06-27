package YuukaMod.cards.power;

import YuukaMod.cards.BaseCard;
import YuukaMod.character.KazamiYuuka;
import YuukaMod.powers.LLSPC98formCooldown;
import YuukaMod.powers.LLSPC98formPower;
import YuukaMod.util.CardStats;
import basemod.helpers.BaseModCardTags;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

import java.util.HashMap;
import java.util.Map;

public class LLS_PC98_form extends BaseCard {
    public static final String ID = makeID(LLS_PC98_form.class.getSimpleName());
    private static final CardStats info = new CardStats(
            KazamiYuuka.Meta.YUUKA_COLOR,
            CardType.POWER,
            CardRarity.RARE,
            CardTarget.SELF,
            3
    );

    private static final Map<AbstractPlayer, Integer> blockedCombatCountMap = new HashMap<>();

    public static int getBlockedCombatCount() {
        return blockedCombatCountMap.getOrDefault(AbstractDungeon.player, 0);
    }

    public static void incrementBlockedCombatCount() {
        AbstractPlayer p = AbstractDungeon.player;
        blockedCombatCountMap.put(p, blockedCombatCountMap.getOrDefault(p, 0) + 1);
    }

    public static void decrementBlockedCombatCount() {
        AbstractPlayer p = AbstractDungeon.player;
        int count = blockedCombatCountMap.getOrDefault(p, 0);
        if (count > 0) {
            blockedCombatCountMap.put(p, count - 1);
        }
    }

    public LLS_PC98_form() {
        super(ID, info);
        tags.add(BaseModCardTags.FORM);
        setMagic(3);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot(new ApplyPowerAction(p, p, new LLSPC98formPower(p, magicNumber)));
        if (!upgraded) {
            incrementBlockedCombatCount();
        }
    }

    @Override
    public boolean canUse(AbstractPlayer p, AbstractMonster m) {
        if (!super.canUse(p, m)) return false;
        if (p.hasPower(LLSPC98formCooldown.POWER_ID)) {
            CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(cardID);
            if (cardStrings != null && cardStrings.EXTENDED_DESCRIPTION != null && cardStrings.EXTENDED_DESCRIPTION.length > 0) {
                cantUseMessage = cardStrings.EXTENDED_DESCRIPTION[0];
            } else {
                cantUseMessage = "Cannot use: on cooldown.";
            }
            return false;
        }
        return true;
    }

    @Override
    public AbstractCard makeCopy() {
        return new LLS_PC98_form();
    }
}
