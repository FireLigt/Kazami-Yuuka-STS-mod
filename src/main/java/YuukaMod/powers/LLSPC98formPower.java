package YuukaMod.powers;

import YuukaMod.Yuukamod;
import YuukaMod.character.KazamiYuuka;
import basemod.animations.AbstractAnimation;
import basemod.animations.SpriterAnimation;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.rooms.MonsterRoomBoss;

import java.lang.reflect.Field;

public class LLSPC98formPower extends BasePower {
    public static final String POWER_ID = Yuukamod.makeID(LLSPC98formPower.class.getSimpleName());
    private static final String BGM_PATH = "YuukaMod/audio/music/th04_15_86.ogg";
    private static Music bossMusic = null;

    private int maxPerTurn = 3;
    private boolean myBossMusicStarted = false;

    public LLSPC98formPower(AbstractCreature owner, int amount) {
        super(POWER_ID, PowerType.BUFF, false, owner, amount);
    }

    private static boolean isYuuka(AbstractPlayer player) {
        return player != null
                && player.chosenClass == KazamiYuuka.Meta.FLOWER_FIELD_TYRANT;
    }

    public static void restoreDefaultAnimation() {
        setAnimation("animation/default.scml");
    }

    private static void setAnimation(String animPath) {
        AbstractPlayer p = AbstractDungeon.player;
        if (!isYuuka(p)) return;
        try {
            SpriterAnimation anim = new SpriterAnimation(Yuukamod.characterPath(animPath));
            Field field = getAnimationField(p.getClass());
            if (field != null) {
                field.set(p, anim);
            }
        } catch (Exception e) {
            Yuukamod.logger.error("Failed to set player animation", e);
        }
    }

    private static Field animationField = null;

    private static Field getAnimationField(Class<?> startClass) {
        if (animationField != null) return animationField;
        Class<?> clazz = startClass;
        while (clazz != null && animationField == null) {
            for (Field f : clazz.getDeclaredFields()) {
                if (AbstractAnimation.class.isAssignableFrom(f.getType())) {
                    animationField = f;
                    animationField.setAccessible(true);
                    break;
                }
            }
            clazz = clazz.getSuperclass();
        }
        if (animationField == null) {
            Yuukamod.logger.error("Failed to find AbstractAnimation field in player class hierarchy");
        }
        return animationField;
    }

    @Override
    public void onInitialApplication() {
        this.maxPerTurn = this.amount;
        updateDescription();
        setAnimation("animation/pc98.scml");
        if (owner instanceof AbstractPlayer && isYuuka((AbstractPlayer) owner)
                && AbstractDungeon.getCurrRoom() instanceof MonsterRoomBoss && !myBossMusicStarted) {
            myBossMusicStarted = true;
            try {
                CardCrawlGame.music.silenceTempBgmInstantly();
                if (bossMusic == null) {
                    if (Gdx.files.classpath(BGM_PATH).exists()) {
                        bossMusic = Gdx.audio.newMusic(Gdx.files.classpath(BGM_PATH));
                        bossMusic.setLooping(true);
                    }
                }
                if (bossMusic != null) {
                    bossMusic.play();
                }
            } catch (Exception e) {
                Yuukamod.logger.error("Failed to play boss BGM", e);
                myBossMusicStarted = false;
            }
        }
    }

    @Override
    public void stackPower(int stackAmount) {
        super.stackPower(stackAmount);
        maxPerTurn += stackAmount;
        updateDescription();
    }

    @Override
    public void atStartOfTurn() {
        this.amount = maxPerTurn;
        updateDescription();
    }

    @Override
    public float atDamageGive(float damage, DamageInfo.DamageType type) {
        if (amount > 0 && type == DamageInfo.DamageType.NORMAL) {
            return damage * 2;
        }
        return damage;
    }

    @Override
    public void onAfterCardPlayed(AbstractCard card) {
        if (card.type == AbstractCard.CardType.ATTACK && amount > 0) {
            amount--;
            flash();
            updateDescription();
        }
    }

    public static void stopBossMusic() {
        if (bossMusic != null) {
            bossMusic.stop();
            bossMusic.dispose();
            bossMusic = null;
        }
    }

    @Override
    public void onRemove() {
        restoreDefaultAnimation();
        if (myBossMusicStarted) {
            myBossMusicStarted = false;
            stopBossMusic();
        }
    }

    @Override
    public void updateDescription() {
        this.description = DESCRIPTIONS[0] + amount + DESCRIPTIONS[1];
    }

    @SpirePatch(clz = CardCrawlGame.class, method = "startOver")
    public static class StopMusicOnMainMenu {
        public static void Postfix() {
            stopBossMusic();
        }
    }
}
