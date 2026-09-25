package YuukaMod.powers;

import YuukaMod.Yuukamod;
import YuukaMod.character.KazamiYuuka;
import YuukaMod.util.TogetherInSpireCompat;
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
    private static final com.megacrit.cardcrawl.localization.PowerStrings powerStrings =
            com.megacrit.cardcrawl.core.CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    private static final String BGM_PATH = "YuukaMod/audio/music/th04_15_86.ogg";
    private static Music bossMusic = null;

    private static final float FADE_IN_SECONDS = 2.0f;
    private static final float FADE_OUT_SECONDS = 1.5f;
    private static volatile boolean fadingIn = false;
    private static volatile boolean fadingOut = false;

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

    public static void applyPlayerAnimation(AbstractPlayer player, String animPath) {
        try {
            SpriterAnimation anim = new SpriterAnimation(Yuukamod.characterPath(animPath));
            Field field = getAnimationField(player.getClass());
            if (field != null) {
                field.set(player, anim);
            }
        } catch (Exception e) {
            Yuukamod.logger.error("Failed to set player animation", e);
        }
    }

    private static String lastBroadcastAnimPath = null;

    private static void setAnimation(String animPath) {
        AbstractPlayer p = AbstractDungeon.player;
        if (!isYuuka(p)) return;
        applyPlayerAnimation(p, animPath);
        lastBroadcastAnimPath = animPath;
        TogetherInSpireCompat.broadcastPlayerAnimation(animPath);
    }

    public static void announceCurrentState() {
        if (lastBroadcastAnimPath != null) {
            TogetherInSpireCompat.broadcastPlayerAnimation(lastBroadcastAnimPath);
        }
        if (isLocalBossMusicActive()) {
            TogetherInSpireCompat.broadcastBossMusicStart();
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
        if (owner == AbstractDungeon.player) {
            setAnimation("animation/pc98.scml");
        }
        if (owner == AbstractDungeon.player && owner instanceof AbstractPlayer && isYuuka((AbstractPlayer) owner)
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
                    fadeInBossMusic();
                }
            } catch (Exception e) {
                Yuukamod.logger.error("Failed to play boss BGM", e);
                myBossMusicStarted = false;
            }
            if (myBossMusicStarted) {
                TogetherInSpireCompat.broadcastBossMusicStart();
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

    public static boolean isLocalBossMusicActive() {
        AbstractPlayer p = AbstractDungeon.player;
        if (!isYuuka(p)) {
            return false;
        }
        com.megacrit.cardcrawl.powers.AbstractPower power = p.getPower(POWER_ID);
        return power instanceof LLSPC98formPower && ((LLSPC98formPower) power).myBossMusicStarted;
    }

    private static boolean remoteMusicActive = false;

    public static void startRemoteBossMusic() {
        if (remoteMusicActive || isLocalBossMusicActive()) {
            return;
        }
        if (!(AbstractDungeon.getCurrRoom() instanceof MonsterRoomBoss)) {
            return;
        }
        remoteMusicActive = true;
        try {
            CardCrawlGame.music.silenceTempBgmInstantly();
            if (bossMusic == null) {
                if (Gdx.files.classpath(BGM_PATH).exists()) {
                    bossMusic = Gdx.audio.newMusic(Gdx.files.classpath(BGM_PATH));
                    bossMusic.setLooping(true);
                }
            }
            if (bossMusic != null) {
                fadeInBossMusic();
            }
        } catch (Exception e) {
            Yuukamod.logger.error("Failed to play remote boss BGM", e);
            remoteMusicActive = false;
        }
    }

    public static void stopRemoteBossMusic() {
        if (!remoteMusicActive) {
            return;
        }
        remoteMusicActive = false;
        fadeOutBossMusic();
    }

    public static void reconcileRemoteMusic() {
        if (remoteMusicActive && !(AbstractDungeon.getCurrRoom() instanceof MonsterRoomBoss)) {
            stopRemoteBossMusic();
        }
    }

    public static void stopBossMusic() {
        fadingIn = false;
        fadingOut = false;
        remoteMusicActive = false;
        Music old = bossMusic;
        bossMusic = null;
        if (old != null) {
            try {
                old.stop();
                old.dispose();
            } catch (Exception e) {
                Yuukamod.logger.warn("Failed to stop/dispose boss music", e);
            }
        }
    }

    public static void fadeOutBossMusic() {
        if (bossMusic == null || fadingOut) {
            return;
        }
        fadingIn = false;
        fadingOut = true;

        final long startTime = System.currentTimeMillis();
        final long fadeMs = (long) (FADE_OUT_SECONDS * 1000);
        final float startVolume;

        try {
            startVolume = bossMusic.getVolume();
        } catch (Exception e) {
            Yuukamod.logger.warn("Failed to get boss music volume", e);
            fadingOut = false;
            return;
        }

        Gdx.app.postRunnable(new Runnable() {
            @Override
            public void run() {
                if (!fadingOut) {
                    return;
                }
                try {
                    long elapsed = System.currentTimeMillis() - startTime;
                    float progress = Math.min(1.0f, (float) elapsed / fadeMs);
                    float vol = startVolume * (1.0f - progress);
                    if (vol <= 0.0f) {
                        Music toStop = bossMusic;
                        if (toStop != null) {
                            try {
                                toStop.stop();
                                toStop.dispose();
                            } catch (Exception e) {
                                Yuukamod.logger.warn("Failed to stop/dispose boss music in fade-out", e);
                            }
                        }
                        bossMusic = null;
                        fadingOut = false;
                    } else {
                        if (bossMusic != null) {
                            bossMusic.setVolume(vol);
                        }
                        Gdx.app.postRunnable(this);
                    }
                } catch (Exception e) {
                    Yuukamod.logger.warn("Boss music fade-out error", e);
                    fadingOut = false;
                }
            }
        });
    }

    private static void fadeInBossMusic() {
        if (bossMusic == null) {
            return;
        }
        fadingOut = false;
        fadingIn = true;

        try {
            bossMusic.setVolume(0.0f);
            bossMusic.play();
        } catch (Exception e) {
            Yuukamod.logger.warn("Failed to start boss music", e);
            fadingIn = false;
            return;
        }

        final long startTime = System.currentTimeMillis();
        final long fadeMs = (long) (FADE_IN_SECONDS * 1000);

        Gdx.app.postRunnable(new Runnable() {
            @Override
            public void run() {
                if (!fadingIn) {
                    return;
                }
                try {
                    if (bossMusic == null) {
                        fadingIn = false;
                        return;
                    }
                    long elapsed = System.currentTimeMillis() - startTime;
                    float progress = Math.min(1.0f, (float) elapsed / fadeMs);
                    bossMusic.setVolume(progress);
                    if (progress < 1.0f) {
                        Gdx.app.postRunnable(this);
                    } else {
                        fadingIn = false;
                    }
                } catch (Exception e) {
                    Yuukamod.logger.warn("Boss music fade-in error", e);
                    fadingIn = false;
                }
            }
        });
    }

    @Override
    public void onRemove() {
        restoreDefaultAnimation();
        if (myBossMusicStarted) {
            myBossMusicStarted = false;
            fadeOutBossMusic();
            TogetherInSpireCompat.broadcastBossMusicStop();
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

    @SpirePatch(clz = AbstractDungeon.class, method = "nextRoomTransition", paramtypez = {com.megacrit.cardcrawl.saveAndContinue.SaveFile.class})
    public static class RoomEntrySync {
        public static void Postfix() {
            TogetherInSpireCompat.onLocalRoomEntered();
        }
    }
}
