package YuukaMod.monsters;

import YuukaMod.Yuukamod;
import YuukaMod.character.KazamiYuuka;
import YuukaMod.util.EnemyCardFilter;
import YuukaMod.util.PhantomPlayer;
import YuukaMod.util.PhantomTurnGuard;
import YuukaMod.util.PowerMirror;
import YuukaMod.util.RunRecorder;
import basemod.abstracts.CustomPlayer;
import basemod.animations.AbstractAnimation;
import basemod.animations.SpineAnimation;
import basemod.animations.SpriterAnimation;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.evacipated.cardcrawl.mod.stslib.powers.StunMonsterPower;
import com.esotericsoftware.spine.AnimationState;
import com.esotericsoftware.spine.AnimationStateData;
import com.esotericsoftware.spine.Skeleton;
import com.esotericsoftware.spine.SkeletonData;
import com.megacrit.cardcrawl.actions.animations.TalkAction;
import com.megacrit.cardcrawl.actions.utility.WaitAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.rewards.RewardItem;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.lang.reflect.Field;

public class RecordPlayer extends AbstractMonster {
    private static final Logger logger = LogManager.getLogger(RecordPlayer.class.getName());
    public static final String ID = Yuukamod.makeID("RecordPlayer");

    private static MonsterStrings monsterStrings;
    private static String NAME;
    private static String[] MOVES;
    private static String[] DIALOG;

    private static void ensureStrings() {
        if (monsterStrings == null) {
            monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
            if (monsterStrings != null) {
                NAME = monsterStrings.NAME;
                MOVES = monsterStrings.MOVES;
                DIALOG = monsterStrings.DIALOG;
            } else {
                NAME = "Record Player";
                MOVES = new String[]{"Memory"};
                DIALOG = new String[]{"I remember this..."};
            }
        }
    }

    private RunRecorder.RunData runData;
    private CardGroup enemyDeck;
    private ArrayList<AbstractCard> currentTurnCards;
    private int energyLimit;
    private int turnCount;
    private String savedCharacterClass;
    private SpriterAnimation animation;

    public RecordPlayer(RunRecorder.RunData data) {
        super(
            data.characterClass + " Phantom",
            ID,
            computeMaxHp(data),
            -10.0F,
            0.0F,
            200.0F,
            250.0F,
            null,
            -20.0F,
            0.0F
        );

        this.runData = data;
        this.savedCharacterClass = data.characterClass;
        this.energyLimit = 0;
        this.turnCount = 0;
        this.currentTurnCards = new ArrayList<>();

        this.enemyDeck = EnemyCardFilter.buildEnemyDeck(data);

        if (this.enemyDeck.size() == 0) {
            logger.error("RecordPlayer: Enemy deck is empty!");
        }

        PowerMirror.applyPowersToMonster(this, data.powers);

        this.type = EnemyType.BOSS;
        this.dialogX = (this.drawX + 0.0F * Settings.scale);
        this.dialogY = (this.drawY + 220.0F * Settings.scale);

        loadCharacterImage();
        loadAnimation();
    }

    private void loadAnimation() {
        AbstractPlayer recorded = getRecordedCharacter();

        if (recorded instanceof CustomPlayer) {
            AbstractAnimation anim = readCharacterAnimation(recorded);
            if (anim instanceof SpriterAnimation) {
                if (recorded == AbstractDungeon.player && recorded instanceof KazamiYuuka) {
                    this.animation = new SpriterAnimation(Yuukamod.characterPath("animation/default.scml"));
                } else {
                    this.animation = (SpriterAnimation) anim;
                }
                this.img = null;
                applyMirrorFlip();
                logger.info("RecordPlayer: Using recorded Spriter animation for " + savedCharacterClass);
                return;
            }
            if (anim instanceof SpineAnimation) {
                if (loadRecordedSpine(recorded, (SpineAnimation) anim)) {
                    logger.info("RecordPlayer: Using recorded Spine animation for " + savedCharacterClass);
                    return;
                }
            }
            if (loadMirroredSpine(recorded)) {
                logger.info("RecordPlayer: Mirroring recorded spine animation for " + savedCharacterClass);
                return;
            }
            if (recorded.img != null) {
                this.img = recorded.img;
                this.animation = null;
                applyMirrorFlip();
                logger.info("RecordPlayer: Using recorded character image for " + savedCharacterClass);
                return;
            }
        }

        if (recorded != null && loadVanillaSpine(recorded.chosenClass.name())) {
            logger.info("RecordPlayer: Using vanilla spine animation for " + savedCharacterClass);
            return;
        }

        loadDefaultAnimation();
    }

    private AbstractPlayer getRecordedCharacter() {
        if (savedCharacterClass == null || savedCharacterClass.isEmpty()) {
            return null;
        }
        try {
            AbstractPlayer.PlayerClass playerClass = AbstractPlayer.PlayerClass.valueOf(savedCharacterClass);
            if (CardCrawlGame.characterManager == null) {
                return null;
            }
            return CardCrawlGame.characterManager.getCharacter(playerClass);
        } catch (Exception e) {
            logger.warn("RecordPlayer: Could not resolve recorded character class '" + savedCharacterClass + "' - " + e.getMessage());
            return null;
        }
    }

    private static AbstractAnimation readCharacterAnimation(AbstractPlayer character) {
        try {
            Field f = CustomPlayer.class.getDeclaredField("animation");
            f.setAccessible(true);
            Object value = f.get(character);
            return (value instanceof AbstractAnimation) ? (AbstractAnimation) value : null;
        } catch (Exception e) {
            logger.warn("RecordPlayer: Could not read character animation - " + e.getMessage());
            return null;
        }
    }

    private boolean loadRecordedSpine(AbstractPlayer recorded, SpineAnimation spine) {
        try {
            this.loadAnimation(spine.atlasUrl, spine.skeletonUrl, spine.scale);
            if (this.state != null) {
                String animName = findAnimationName(recorded, this.skeleton);
                if (animName != null) {
                    this.state.setAnimation(0, animName, true);
                }
            }
            this.img = null;
            applyMirrorFlip();
            return true;
        } catch (Exception e) {
            logger.warn("RecordPlayer: Could not load spine animation for " + savedCharacterClass + " - " + e.getMessage());
            this.atlas = null;
            this.skeleton = null;
            this.state = null;
            return false;
        }
    }

    private boolean loadMirroredSpine(AbstractPlayer recorded) {
        try {
            Field atlasField = AbstractCreature.class.getDeclaredField("atlas");
            Field skeletonField = AbstractCreature.class.getDeclaredField("skeleton");
            atlasField.setAccessible(true);
            skeletonField.setAccessible(true);
            TextureAtlas atlas = (TextureAtlas) atlasField.get(recorded);
            Skeleton skeleton = (Skeleton) skeletonField.get(recorded);
            if (atlas == null || skeleton == null || recorded.state == null) {
                return false;
            }
            SkeletonData data = skeleton.getData();
            this.atlas = atlas;
            this.skeleton = new Skeleton(data);
            this.stateData = new AnimationStateData(data);
            this.state = new AnimationState(this.stateData);
            String animName = null;
            if (recorded.state.getCurrent(0) != null && recorded.state.getCurrent(0).getAnimation() != null) {
                animName = recorded.state.getCurrent(0).getAnimation().getName();
            }
            if (animName == null || !hasAnimation(this.skeleton, animName)) {
                animName = hasAnimation(this.skeleton, "Idle")
                        ? "Idle"
                        : (this.skeleton.getData().getAnimations().size > 0 ? this.skeleton.getData().getAnimations().get(0).getName() : null);
            }
            if (animName != null) {
                this.state.setAnimation(0, animName, true);
            }
            this.img = null;
            applyMirrorFlip();
            return true;
        } catch (Exception e) {
            logger.warn("RecordPlayer: Could not mirror spine animation for " + savedCharacterClass + " - " + e.getMessage());
            return false;
        }
    }

    private static String findAnimationName(AbstractPlayer recorded, Skeleton skeleton) {
        if (recorded != null && recorded.state != null && recorded.state.getCurrent(0) != null
                && recorded.state.getCurrent(0).getAnimation() != null) {
            String recordedName = recorded.state.getCurrent(0).getAnimation().getName();
            if (hasAnimation(skeleton, recordedName)) {
                return recordedName;
            }
        }
        if (hasAnimation(skeleton, "Idle")) {
            return "Idle";
        }
        if (skeleton != null && skeleton.getData().getAnimations().size > 0) {
            return skeleton.getData().getAnimations().get(0).getName();
        }
        return null;
    }

    private static boolean hasAnimation(Skeleton skeleton, String name) {
        return skeleton != null && skeleton.getData().findAnimation(name) != null;
    }

    private boolean loadVanillaSpine(String characterClass) {
        String atlasUrl;
        String skeletonUrl;
        switch (characterClass) {
            case "IRONCLAD":
                atlasUrl = "images/characters/ironclad/idle/skeleton.atlas";
                skeletonUrl = "images/characters/ironclad/idle/skeleton.json";
                break;
            case "THE_SILENT":
                atlasUrl = "images/characters/silent/idle/skeleton.atlas";
                skeletonUrl = "images/characters/silent/idle/skeleton.json";
                break;
            case "DEFECT":
                atlasUrl = "images/characters/defect/idle/skeleton.atlas";
                skeletonUrl = "images/characters/defect/idle/skeleton.json";
                break;
            case "WATCHER":
                atlasUrl = "images/characters/watcher/idle/skeleton.atlas";
                skeletonUrl = "images/characters/watcher/idle/skeleton.json";
                break;
            default:
                return false;
        }

        try {
            this.loadAnimation(atlasUrl, skeletonUrl, 1.0F);
            if (this.state != null) {
                String animName = findAnimationName(null, this.skeleton);
                if (animName != null) {
                    this.state.setAnimation(0, animName, true);
                }
            }
            this.img = null;
            applyMirrorFlip();
            return true;
        } catch (Exception e) {
            logger.warn("RecordPlayer: Could not load spine animation for " + characterClass + " - " + e.getMessage());
            this.atlas = null;
            this.skeleton = null;
            this.state = null;
            return false;
        }
    }

    private void loadDefaultAnimation() {
        try {
            this.animation = new SpriterAnimation(Yuukamod.characterPath("animation/default.scml"));
            this.img = null;
            applyMirrorFlip();
            logger.info("RecordPlayer: Using default character animation");
        } catch (Exception e) {
            logger.warn("RecordPlayer: Could not load character animation - " + e.getMessage());
            this.animation = null;
        }
    }

    private void applyMirrorFlip() {
        if (AbstractDungeon.player != null) {
            this.flipHorizontal = !AbstractDungeon.player.flipHorizontal;
        }
    }

    private static int computeMaxHp(RunRecorder.RunData data) {
        int savedDeckSize = (data.deck == null) ? 0 : data.deck.size();
        int playerDeckSize;
        if (AbstractDungeon.player != null) {
            playerDeckSize = AbstractDungeon.player.masterDeck.size();
        } else {
            playerDeckSize = savedDeckSize;
        }

        float mult = 1.5f;
        if (savedDeckSize > 0 && savedDeckSize < playerDeckSize) {
            float ratio = (float) playerDeckSize / (float) savedDeckSize;
            mult = Math.min(2.0f, 1.5f + (ratio - 1.0f) * 0.5f);
        }

        return (int) (data.maxHP * mult);
    }

    private void loadCharacterImage() {
        Texture loadedImg = null;

        switch (savedCharacterClass) {
            case "IRONCLAD":
                loadedImg = safeLoad("images/characters/ironclad/idle/sprite.png");
                break;
            case "THE_SILENT":
                loadedImg = safeLoad("images/characters/silent/idle/sprite.png");
                break;
            case "DEFECT":
                loadedImg = safeLoad("images/characters/defect/idle/sprite.png");
                break;
            case "WATCHER":
                loadedImg = safeLoad("images/characters/watcher/idle/sprite.png");
                break;
            default:
                loadedImg = safeLoad(Yuukamod.characterPath("idle/sprite.png"));
                break;
        }

        if (loadedImg != null) {
            this.img = loadedImg;
        } else {
            this.img = ImageMaster.loadImage("images/ui/identify.png");
        }
    }

    private Texture safeLoad(String path) {
        try {
            return ImageMaster.loadImage(path);
        } catch (Exception e) {
            logger.warn("RecordPlayer: Could not load image: " + path + " - " + e.getMessage());
            return null;
        }
    }

    @Override
    public void takeTurn() {
        if (this.hasPower(StunMonsterPower.POWER_ID)) {
            return;
        }

        if (this.currentTurnCards.size() > 0) {
            try {
                for (AbstractCard card : this.currentTurnCards) {
                    AbstractPlayer player = AbstractDungeon.player;

                    EnemyCardFilter.executeCardForEnemy(card.makeStatEquivalentCopy(), this, player);

                    AbstractDungeon.actionManager.addToBottom(new WaitAction(0.3F));
                }
            } finally {
                AbstractDungeon.actionManager.addToBottom(new PhantomTurnGuard.EndMarkerAction());
            }
        }

        this.currentTurnCards.clear();
        this.rollMove();
        this.createIntent();
    }

    @Override
    protected void getMove(int roll) {
        ensureStrings();

        if (this.currentTurnCards.isEmpty()) {
            this.setMove(MOVES[0], (byte) 0, Intent.SLEEP);
            return;
        }

        boolean hasAttack = false;
        boolean hasBlock = false;

        for (AbstractCard card : this.currentTurnCards) {
            if (card.type == AbstractCard.CardType.ATTACK) {
                hasAttack = true;
            }
            if (card.type == AbstractCard.CardType.SKILL && card.baseBlock > 0) {
                hasBlock = true;
            }
        }

        String moveName = this.currentTurnCards.get(0).name;
        if (this.currentTurnCards.size() > 1) {
            moveName += " +" + (this.currentTurnCards.size() - 1);
        }

        int attackDmg = 0;
        for (AbstractCard card : this.currentTurnCards) {
            if (card.type == AbstractCard.CardType.ATTACK) {
                attackDmg += card.baseDamage;
            }
        }

        if (hasAttack && hasBlock) {
            this.setMove(moveName, (byte) 1, Intent.ATTACK_DEFEND, attackDmg);
        } else if (hasAttack) {
            this.setMove(moveName, (byte) 2, Intent.ATTACK, attackDmg);
        } else if (hasBlock) {
            this.setMove(moveName, (byte) 3, Intent.DEFEND);
        } else {
            this.setMove(moveName, (byte) 4, Intent.BUFF);
        }
    }

    @Override
    public void rollMove() {
        this.turnCount++;
        if (this.turnCount == 6) {
            ensureStrings();
            String hint = (DIALOG.length > 1) ? DIALOG[1] : "...";
            AbstractDungeon.actionManager.addToBottom(new TalkAction(this, hint, 0.5F, 2.0F));
        }
        this.energyLimit = this.turnCount;

        ArrayList<AbstractCard> playableCards = new ArrayList<>();
        for (AbstractCard card : this.enemyDeck.group) {
            if (card.cost >= 0 && card.cost <= this.energyLimit) {
                playableCards.add(card);
            }
        }

        if (playableCards.size() == 0) {
            logger.warn("RecordPlayer: No playable cards for energy limit " + this.energyLimit);
            ensureStrings();
            this.setMove(MOVES[0], (byte) 0, Intent.SLEEP);
            this.createIntent();
            return;
        }

        int numCardsToDraw = AbstractDungeon.cardRandomRng.random(1, 3);
        if (this.turnCount == 7) {
            numCardsToDraw = 5;
        }
        numCardsToDraw = Math.min(numCardsToDraw, playableCards.size());

        this.currentTurnCards.clear();
        ArrayList<AbstractCard> tempPool = new ArrayList<>(playableCards);
        for (int i = 0; i < numCardsToDraw; i++) {
            int index = AbstractDungeon.cardRandomRng.random(tempPool.size() - 1);
            this.currentTurnCards.add(tempPool.get(index));
            tempPool.remove(index);
        }

        this.getMove(0);
        this.createIntent();
    }

    @Override
    public void render(SpriteBatch sb) {
        if (this.animation != null && !this.isDeadOrEscaped()) {
            this.animation.setFlip(this.flipHorizontal, this.flipVertical);
            this.animation.renderSprite(sb, this.drawX + this.animX, this.drawY + this.animY);
        }
        super.render(sb);
        if (!this.isDeadOrEscaped() && this.currentTurnCards != null && !this.currentTurnCards.isEmpty()) {
            renderIntentCards(sb);
        }
    }

    private void renderIntentCards(SpriteBatch sb) {
        if (this.hasPower(StunMonsterPower.POWER_ID)) {
            return;
        }

        float cardScale = 0.5f * Settings.scale;
        int n = this.currentTurnCards.size();
        float spacing = 250.0f * cardScale * 1.15f;
        float totalWidth = n * spacing;
        float startX = this.drawX - totalWidth / 2.0f + spacing / 2.0f;
        float baseY = this.drawY + 260.0f * Settings.scale;

        for (int i = 0; i < n; i++) {
            AbstractCard card = this.currentTurnCards.get(i);
            card.current_x = startX + i * spacing;
            card.current_y = baseY;
            card.drawScale = cardScale;
            card.render(sb);
        }
    }

    @Override
    public void usePreBattleAction() {
        this.powers.clear();
        logger.info("RecordPlayer: Cleared all mirrored buffs at battle start");
    }

    @Override
    public void damage(DamageInfo info) {
        if (info != null && info.owner instanceof PhantomPlayer && ((PhantomPlayer) info.owner).getHost() == this) {
            logger.info("RecordPlayer: Ignoring self-inflicted damage from own card play");
            return;
        }
        super.damage(info);
        if (info.output > 0 && this.currentHealth <= 0 && !this.isDying) {
            this.isDying = true;
        }
    }

    private boolean bossRewardGiven = false;

    @Override
    public void die() {
        super.die();
        giveBossReward();
    }

    private void giveBossReward() {
        if (bossRewardGiven) {
            return;
        }
        bossRewardGiven = true;

        AbstractRoom room = AbstractDungeon.getCurrRoom();
        ArrayList<LinkedRelicReward> rewards = new ArrayList<>();

        for (int i = 0; i < 3; i++) {
            LinkedRelicReward reward = new LinkedRelicReward(AbstractDungeon.returnRandomRelic(AbstractRelic.RelicTier.RARE));
            rewards.add(reward);
            room.rewards.add(reward);
        }

        for (LinkedRelicReward reward : rewards) {
            for (LinkedRelicReward other : rewards) {
                if (other != reward) {
                    reward.addLinkedReward(other);
                }
            }
        }

        room.rewards.add(buildDeckCardReward());
    }

    private RewardItem buildDeckCardReward() {
        RewardItem reward = new RewardItem();
        reward.cards = new ArrayList<>();
        ArrayList<String> seenCardIds = new ArrayList<>();

        for (AbstractCard card : this.enemyDeck.group) {
            if (!seenCardIds.contains(card.cardID)) {
                seenCardIds.add(card.cardID);
                reward.cards.add(card.makeStatEquivalentCopy());
            }
        }

        return reward;
    }

    private static class LinkedRelicReward extends RewardItem {
        private final ArrayList<RewardItem> linkedRewards = new ArrayList<>();

        LinkedRelicReward(AbstractRelic relic) {
            super(relic);
        }

        void addLinkedReward(RewardItem other) {
            this.linkedRewards.add(other);
        }

        @Override
        public boolean claimReward() {
            boolean result = super.claimReward();
            for (RewardItem other : this.linkedRewards) {
                other.isDone = true;
                other.ignoreReward = true;
            }
            return result;
        }
    }

    public int getEnergyLimit() {
        return this.energyLimit;
    }

    public String getSavedCharacterClass() {
        return this.savedCharacterClass;
    }

    public void removeCardFromEnemyDeck(AbstractCard card) {
        for (int i = 0; i < this.enemyDeck.group.size(); i++) {
            if (this.enemyDeck.group.get(i).cardID.equals(card.cardID)) {
                this.enemyDeck.group.remove(i);
                logger.info("RecordPlayer: Removed used power card from enemy deck: " + card.name);
                return;
            }
        }
    }

    public ArrayList<AbstractCard> getCurrentTurnCards() {
        return this.currentTurnCards;
    }
}
