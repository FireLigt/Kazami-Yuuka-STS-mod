package YuukaMod.cards.common;

import YuukaMod.cards.BaseCard;
import YuukaMod.character.KazamiYuuka;
import YuukaMod.util.CardStats;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class Danmaku extends BaseCard {
    public static final String ID = makeID(Danmaku.class.getSimpleName());
    private static final CardStats info = new CardStats(
            KazamiYuuka.Meta.YUUKA_COLOR,
            CardType.ATTACK,
            CardRarity.COMMON,
            CardTarget.ENEMY,
            1
    );
    private static final int DAMAGE = 7;
    private static final int UPG_DAMAGE = 4;

    public Danmaku() {
        super(ID, info);

        setDamage(DAMAGE, UPG_DAMAGE);

        tags.add(CustomTags.DANMAKU);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        AbstractDungeon.actionManager.addToBottom(new DamageAction(m,
                new DamageInfo(p, this.damage, this.damageTypeForTurn),
                AbstractGameAction.AttackEffect.BLUNT_LIGHT));
    }

    @Override
    public AbstractCard makeCopy() {
        return new Danmaku();
    }
}
