package YuukaMod.cards.uncommon;

import YuukaMod.Yuukamod;
import YuukaMod.cards.BaseCard;
import YuukaMod.character.KazamiYuuka;
import YuukaMod.util.CardStats;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.MinionPower;
import com.megacrit.cardcrawl.powers.VulnerablePower;
import com.megacrit.cardcrawl.powers.WeakPower;
import com.megacrit.cardcrawl.vfx.combat.SmokeBombEffect;

import java.lang.reflect.Field;

public class Blover extends BaseCard {
    public static final String ID = makeID(Blover.class.getSimpleName());
    private static final CardStats info = new CardStats(
            KazamiYuuka.Meta.YUUKA_COLOR,
            CardType.SKILL,
            CardRarity.UNCOMMON,
            CardTarget.NONE,
            2
    );

    public Blover() {
        super(ID, info);
        setExhaust(true);
        setCostUpgrade(1);

        tags.add(CustomTags.PLANT);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot(new AbstractGameAction() {
            private boolean triggered = false;

            @Override
            public void update() {
                if (!triggered) {
                    for (AbstractMonster mo : AbstractDungeon.getMonsters().monsters) {
                        if (mo.isDeadOrEscaped()) continue;
                        if (isMinion(mo)) {
                            returnStolenCard(mo, p);
                            AbstractDungeon.effectsQueue.add(new SmokeBombEffect(mo.hb.cX, mo.hb.cY));
                            mo.escape();
                            mo.escaped = true;
                        } else {
                            addToBot(new ApplyPowerAction(mo, p, new WeakPower(mo, 2, false), 2));
                            addToBot(new ApplyPowerAction(mo, p, new VulnerablePower(mo, 2, false), 2));
                        }
                    }
                    this.duration = 0.5f;
                    this.startDuration = 0.5f;
                    triggered = true;
                }
                tickDuration();
            }
        });
    }

    private static void returnStolenCard(AbstractMonster mo, AbstractPlayer p) {
        try {
            StolenCardRef ref = findStolenCard(mo);
            if (ref == null) {
                Yuukamod.logger.info("No stolen card found on minion: " + mo.id);
                return;
            }
            AbstractCard stolen = ref.card;
            stolen.unhover();
            stolen.stopGlowing();
            if (p.hand.size() < Settings.MAX_HAND_SIZE) {
                p.hand.addToTop(stolen);
                p.hand.refreshHandLayout();
            } else {
                p.discardPile.addToTop(stolen);
            }
            ref.clear();
        } catch (Exception e) {
            Yuukamod.logger.info("Not a card-stealing monster or no stolenCard field: " + mo.id);
        }
    }

    private static StolenCardRef findStolenCard(AbstractMonster mo) throws IllegalAccessException {
        StolenCardRef ref = findCardField(mo);
        if (ref != null) {
            return ref;
        }

        for (AbstractPower power : mo.powers) {
            ref = findCardField(power);
            if (ref != null) {
                return ref;
            }
        }

        return null;
    }

    private static StolenCardRef findCardField(Object target) throws IllegalAccessException {
        Class<?> cls = target.getClass();
        Class<?> current = cls;
        while (current != null) {
            for (Field field : current.getDeclaredFields()) {
                if (!AbstractCard.class.isAssignableFrom(field.getType())) {
                    continue;
                }
                field.setAccessible(true);
                Object value = field.get(target);
                if (value instanceof AbstractCard) {
                    return new StolenCardRef(target, field, (AbstractCard) value);
                }
            }
            current = current.getSuperclass();
        }
        return null;
    }

    private static class StolenCardRef {
        private final Object target;
        private final Field field;
        private final AbstractCard card;

        private StolenCardRef(Object target, Field field, AbstractCard card) {
            this.target = target;
            this.field = field;
            this.card = card;
        }

        private void clear() throws IllegalAccessException {
            field.set(target, null);
        }
    }

    private static boolean isMinion(AbstractMonster m) {
        return m.hasPower(MinionPower.POWER_ID);
    }

    @Override
    public AbstractCard makeCopy() {
        return new Blover();
    }
}
