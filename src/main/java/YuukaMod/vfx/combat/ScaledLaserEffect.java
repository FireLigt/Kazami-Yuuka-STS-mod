package YuukaMod.vfx.combat;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;

public class ScaledLaserEffect extends AbstractGameEffect {
    private float srcX;
    private float srcY;
    private float dstX;
    private float dstY;
    private TextureAtlas.AtlasRegion img;
    private boolean playedSfx = false;

    public ScaledLaserEffect(float srcX, float srcY, float dstX, float dstY, String textureKey, float scale) {
        this.img = ImageMaster.vfxAtlas.findRegion(textureKey);
        if (this.img == null) {
            this.img = ImageMaster.vfxAtlas.findRegion("combat/lightning");
            if (this.img == null) {
                this.isDone = true;
            }
        }
        this.srcX = srcX;
        this.srcY = srcY;
        this.dstX = dstX;
        this.dstY = dstY;
        this.color = Color.CYAN.cpy();
        this.scale = scale;
        this.duration = 0.2f + scale * 0.3f;
        this.startingDuration = this.duration;
    }

    @Override
    public void update() {
        if (!playedSfx) {
            CardCrawlGame.sound.playA("ATTACK_MAGIC_BEAM", 1.0f / this.scale);
            playedSfx = true;
        }
        this.duration -= Gdx.graphics.getDeltaTime();
        if (this.duration < 0f) {
            this.isDone = true;
        }
    }

    @Override
    public void render(SpriteBatch sb) {
        sb.setBlendFunction(770, 1);
        float alpha = Interpolation.pow2In.apply(1f, 0f, this.duration / this.startingDuration);
        if (this.duration < 0.2f) {
            alpha *= Interpolation.bounceIn.apply(this.duration * 5f);
        }
        this.color.a = alpha;
        sb.setColor(this.color);

        float angle = MathUtils.atan2(dstY - srcY, dstX - srcX) * MathUtils.radDeg;
        float dist = Vector2.dst(srcX, srcY, dstX, dstY);
        float midX = (srcX + dstX) / 2f;
        float midY = (srcY + dstY) / 2f;
        float h = img.packedHeight * this.scale * Settings.scale;

        sb.draw(img, midX - dist / 2f, midY - h / 2f, dist / 2f, h / 2f, dist, h, 1f, 1f, angle);

        sb.setBlendFunction(770, 771);
    }

    @Override
    public void dispose() {
    }
}
