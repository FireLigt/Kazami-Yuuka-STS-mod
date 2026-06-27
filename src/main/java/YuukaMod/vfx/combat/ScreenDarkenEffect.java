package YuukaMod.vfx.combat;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Interpolation;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;

public class ScreenDarkenEffect extends AbstractGameEffect {
    private float x;
    private float y;
    private float w;
    private float h;
    private Texture img;
    private boolean playedIn = false;
    private boolean fadeOut = false;

    public ScreenDarkenEffect() {
        this.img = ImageMaster.WHITE_SQUARE_IMG;
        this.color = Color.BLACK.cpy();
        this.color.a = 0f;
        this.duration = 0.5f;
        this.startingDuration = this.duration;
        this.x = 0f;
        this.y = 0f;
        this.w = Settings.WIDTH;
        this.h = Settings.HEIGHT;
    }

    @Override
    public void update() {
        this.duration -= Gdx.graphics.getDeltaTime();
        if (!fadeOut) {
            this.color.a = Interpolation.pow2In.apply(0.7f, 0f, this.duration / this.startingDuration);
            if (this.duration <= 0f) {
                this.duration = 0.3f;
                this.startingDuration = this.duration;
                fadeOut = true;
            }
        } else {
            this.color.a = Interpolation.pow2Out.apply(0f, 0.7f, this.duration / this.startingDuration);
            if (this.duration <= 0f) {
                this.isDone = true;
            }
        }
    }

    @Override
    public void render(SpriteBatch sb) {
        sb.setColor(this.color);
        sb.draw(this.img, this.x, this.y, this.w, this.h);
    }

    @Override
    public void dispose() {
    }
}
