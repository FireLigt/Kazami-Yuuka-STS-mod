package YuukaMod.vfx.combat;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Interpolation;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;

public class ScreenFlashEffect extends AbstractGameEffect {
    private float x;
    private float y;
    private float w;
    private float h;
    private Texture img;

    public ScreenFlashEffect() {
        this.img = ImageMaster.WHITE_SQUARE_IMG;
        this.color = Color.WHITE.cpy();
        this.color.a = 0f;
        this.duration = 0.4f;
        this.startingDuration = this.duration;
        this.x = 0f;
        this.y = 0f;
        this.w = Settings.WIDTH;
        this.h = Settings.HEIGHT;
    }

    @Override
    public void update() {
        this.duration -= Gdx.graphics.getDeltaTime();
        float p = this.duration / this.startingDuration;
        this.color.a = Interpolation.pow5In.apply(1f, 0f, p);
        if (this.duration <= 0f) {
            this.isDone = true;
        }
    }

    @Override
    public void render(SpriteBatch sb) {
        sb.setBlendFunction(770, 1);
        sb.setColor(this.color);
        sb.draw(this.img, this.x, this.y, this.w, this.h);
        sb.setBlendFunction(770, 771);
    }

    @Override
    public void dispose() {
    }
}
