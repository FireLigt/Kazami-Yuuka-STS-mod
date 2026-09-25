package YuukaMod.util;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.EnergyManager;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.Hitbox;
import com.megacrit.cardcrawl.helpers.Prefs;
import com.megacrit.cardcrawl.localization.CharacterStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.screens.CharSelectInfo;
import com.megacrit.cardcrawl.screens.stats.CharStat;

import java.util.ArrayList;

/**
 * Stand-in AbstractPlayer that makes the RecordPlayer phantom execute real card code
 * (card.use/card.calculateCardDamage) as if it were the player. It shares the host
 * monster's powers list and routes block to the host, and acts as the DamageInfo.owner
 * for the phantom's attacks so RecordPlayer can recognize and ignore self-inflicted AoE.
 */
public class PhantomPlayer extends AbstractPlayer {
    private final AbstractMonster host;
    private final AbstractPlayer realPlayer;

    public PhantomPlayer(AbstractMonster host, AbstractPlayer realPlayer) {
        super((host == null || host.name == null) ? "Phantom" : host.name, realPlayer.chosenClass);
        this.host = host;
        this.realPlayer = realPlayer;
        this.currentHealth = host.currentHealth;
        this.maxHealth = host.maxHealth;
        this.currentBlock = host.currentBlock;
        this.energy = new EnergyManager(3);
        if (host.powers != null) {
            this.powers = host.powers;
        }
        if (host.hb != null) {
            this.hb_w = host.hb.width;
            this.hb_h = host.hb.height;
            this.hb = new Hitbox(this.hb_w, this.hb_h);
            this.hb.move(host.hb.cX, host.hb.cY);
            this.healthHb = new Hitbox(this.hb.width, 72.0F * Settings.scale);
        }
    }

    public AbstractMonster getHost() {
        return host;
    }

    public AbstractPlayer getRealPlayer() {
        return realPlayer;
    }

    @Override
    public void addBlock(int blockAmount) {
        host.addBlock(blockAmount);
    }

    @Override
    public void loseBlock(int blockAmount) {
        host.loseBlock(blockAmount);
    }

    @Override
    public void loseBlock(int blockAmount, boolean noAnimation) {
        host.loseBlock(blockAmount, noAnimation);
    }

    @Override
    public void loseBlock(boolean noAnimation) {
        host.loseBlock(noAnimation);
    }

    @Override
    public String getPortraitImageName() {
        return null;
    }

    @Override
    public ArrayList<String> getStartingDeck() {
        return new ArrayList<>();
    }

    @Override
    public ArrayList<String> getStartingRelics() {
        return new ArrayList<>();
    }

    @Override
    public CharSelectInfo getLoadout() {
        return null;
    }

    @Override
    public String getTitle(PlayerClass playerClass) {
        return null;
    }

    @Override
    public AbstractCard.CardColor getCardColor() {
        return AbstractCard.CardColor.COLORLESS;
    }

    @Override
    public Color getCardRenderColor() {
        return Color.WHITE;
    }

    @Override
    public String getAchievementKey() {
        return null;
    }

    @Override
    public ArrayList<AbstractCard> getCardPool(ArrayList<AbstractCard> list) {
        return new ArrayList<>();
    }

    @Override
    public AbstractCard getStartCardForEvent() {
        return null;
    }

    @Override
    public Color getCardTrailColor() {
        return Color.WHITE;
    }

    @Override
    public String getLeaderboardCharacterName() {
        return null;
    }

    @Override
    public Texture getEnergyImage() {
        return null;
    }

    @Override
    public int getAscensionMaxHPLoss() {
        return 0;
    }

    @Override
    public BitmapFont getEnergyNumFont() {
        return null;
    }

    @Override
    public void renderOrb(SpriteBatch sb, boolean enabled, float current_x, float current_y) {
    }

    @Override
    public void updateOrb(int orbCount) {
    }

    @Override
    public Prefs getPrefs() {
        return null;
    }

    @Override
    public void loadPrefs() {
    }

    @Override
    public CharStat getCharStat() {
        return null;
    }

    @Override
    public int getUnlockedCardCount() {
        return 0;
    }

    @Override
    public int getSeenCardCount() {
        return 0;
    }

    @Override
    public int getCardCount() {
        return 0;
    }

    @Override
    public boolean saveFileExists() {
        return false;
    }

    @Override
    public String getWinStreakKey() {
        return null;
    }

    @Override
    public String getLeaderboardWinStreakKey() {
        return null;
    }

    @Override
    public void renderStatScreen(SpriteBatch sb, float width, float height) {
    }

    @Override
    public void doCharSelectScreenSelectEffect() {
    }

    @Override
    public String getCustomModeCharacterButtonSoundKey() {
        return null;
    }

    @Override
    public Texture getCustomModeCharacterButtonImage() {
        return null;
    }

    @Override
    public CharacterStrings getCharacterString() {
        return null;
    }

    @Override
    public String getLocalizedCharacterName() {
        return null;
    }

    @Override
    public void refreshCharStat() {
    }

    @Override
    public AbstractPlayer newInstance() {
        return this;
    }

    @Override
    public TextureAtlas.AtlasRegion getOrb() {
        return null;
    }

    @Override
    public String getSpireHeartText() {
        return null;
    }

    @Override
    public Color getSlashAttackColor() {
        return Color.WHITE;
    }

    @Override
    public AbstractGameAction.AttackEffect[] getSpireHeartSlashEffect() {
        return new AbstractGameAction.AttackEffect[0];
    }

    @Override
    public String getVampireText() {
        return null;
    }

    /**
     * Stand-in AbstractMonster representing the real player as the card's target, so
     * cards can execute their normal code (DamageAction/ApplyPowerAction targeting "m")
     * and everything routes to the actual player. The powers list is shared with the
     * real player (ApplyPowerAction writes straight into target.powers), and every other
     * mutator/delegator is forwarded so "m" behaves exactly like the player.
     */
    public static class PlayerMonster extends AbstractMonster {
        private final AbstractPlayer realPlayer;

        public PlayerMonster(AbstractPlayer realPlayer) {
            super(realPlayer.name, "PlayerProxy", realPlayer.currentHealth, 0.0F, 0.0F, 300.0F, 300.0F, (String) null);
            this.realPlayer = realPlayer;
            if (realPlayer.powers != null) {
                this.powers = realPlayer.powers;
            }
            this.isPlayer = true;
            this.currentHealth = realPlayer.currentHealth;
            this.maxHealth = realPlayer.maxHealth;
            this.currentBlock = realPlayer.currentBlock;
            if (realPlayer.hb != null && this.hb != null) {
                this.hb.move(realPlayer.hb.cX, realPlayer.hb.cY);
            }
        }

        @Override
        public void takeTurn() {
        }

        @Override
        protected void getMove(int roll) {
        }

        @Override
        public void damage(DamageInfo info) {
            realPlayer.damage(info);
        }

        @Override
        public void addBlock(int blockAmount) {
            realPlayer.addBlock(blockAmount);
        }

        @Override
        public void loseBlock(int blockAmount) {
            realPlayer.loseBlock(blockAmount);
        }

        @Override
        public void loseBlock(int blockAmount, boolean noAnimation) {
            realPlayer.loseBlock(blockAmount, noAnimation);
        }

        @Override
        public void heal(int healAmount) {
            realPlayer.heal(healAmount);
        }

        @Override
        public void addPower(AbstractPower powerToApply) {
            realPlayer.addPower(powerToApply);
        }

        @Override
        public AbstractPower getPower(String powerId) {
            return realPlayer.getPower(powerId);
        }

        @Override
        public boolean hasPower(String powerId) {
            return realPlayer.hasPower(powerId);
        }
    }
}
