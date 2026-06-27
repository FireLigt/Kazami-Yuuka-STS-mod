package YuukaMod.util;

import YuukaMod.character.KazamiYuuka;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;

import java.util.ArrayList;
import java.util.List;

/**
 * Manages the visual sequence when MagicCannon triggers Danmaku or Flower triggers Plant.
 * Displays cards in a stack: source card resolves first, then triggered cards resolve one by one.
 * Purely visual — operates on card copies and does not interfere with game logic.
 */
public class HitoutArea {
    private static TriggerSequenceEffect currentEffect;

    public static void startSequence(AbstractCard sourceCard, List<AbstractCard> targets) {
        AbstractPlayer p = AbstractDungeon.player;
        if (p == null || p.chosenClass != KazamiYuuka.Meta.FLOWER_FIELD_TYRANT) return;
        if (targets.isEmpty()) return;

        if (currentEffect != null && !currentEffect.isDone) {
            currentEffect.isDone = true;
        }
        currentEffect = new TriggerSequenceEffect(sourceCard, targets);
        AbstractDungeon.effectList.add(currentEffect);
    }

    public static boolean isProcessing() {
        return currentEffect != null && !currentEffect.isDone;
    }

    public static void clear() {
        if (currentEffect != null) {
            currentEffect.isDone = true;
            currentEffect = null;
        }
    }

    private static class TriggerSequenceEffect extends AbstractGameEffect {
        private static final float CENTER_X = Settings.WIDTH / 2f;
        private static final float CENTER_Y = Settings.HEIGHT * 0.50f;
        private static final float DISCARD_X = Settings.WIDTH * 0.90f;
        private static final float DISCARD_Y = Settings.HEIGHT * 0.15f;
        private static final float EXHAUST_Y = CENTER_Y + 120f * Settings.scale;
        private static final float SOURCE_DURATION = 0.8f;
        private static final float TARGET_INTERVAL = 0.5f;
        private static final float FLY_OUT_DURATION = 0.4f;

        private final ArrayList<VisualCard> cards;
        private int currentIndex;
        private float phaseTimer;
        private boolean sourceShown;

        TriggerSequenceEffect(AbstractCard sourceCard, List<AbstractCard> targets) {
            cards = new ArrayList<>();
            cards.add(new VisualCard(sourceCard.makeStatEquivalentCopy(), sourceCard.exhaust || sourceCard.exhaustOnUseOnce));
            for (AbstractCard t : targets) {
                cards.add(new VisualCard(t.makeStatEquivalentCopy(), t.exhaust || t.exhaustOnUseOnce));
            }
            currentIndex = 0;
            phaseTimer = 0.3f;
            sourceShown = false;
        }

        @Override
        public void update() {
            if (cards.isEmpty() || currentIndex >= cards.size()) {
                isDone = true;
                currentEffect = null;
                return;
            }

            float dt = Gdx.graphics.getDeltaTime();
            phaseTimer -= dt;

            VisualCard current = cards.get(currentIndex);

            if (current.state == VisualCard.State.FLYING_OUT) {
                current.flyOutTimer -= dt;
                if (current.flyOutTimer <= 0f) {
                    current.state = VisualCard.State.DONE;
                    currentIndex++;
                    if (currentIndex < cards.size()) {
                        phaseTimer = TARGET_INTERVAL;
                    } else {
                        isDone = true;
                        currentEffect = null;
                    }
                }
                return;
            }

            if (currentIndex == 0 && !sourceShown) {
                // Source card phase: show at center briefly
                if (phaseTimer > 0f) {
                    current.state = VisualCard.State.AT_CENTER;
                } else {
                    sourceShown = true;
                    phaseTimer = SOURCE_DURATION;
                }
                return;
            }

            if (currentIndex == 0 && sourceShown) {
                // Source card is resolving
                if (phaseTimer > 0f) {
                    current.state = VisualCard.State.AT_CENTER;
                } else {
                    current.state = VisualCard.State.FLYING_OUT;
                    current.flyOutTimer = FLY_OUT_DURATION;
                }
                return;
            }

            if (currentIndex > 0) {
                // Target card phase
                if (phaseTimer > TARGET_INTERVAL * 0.6f) {
                    current.state = VisualCard.State.AT_CENTER;
                } else if (phaseTimer > 0f) {
                    current.state = VisualCard.State.AT_CENTER;
                } else {
                    current.state = VisualCard.State.FLYING_OUT;
                    current.flyOutTimer = FLY_OUT_DURATION;
                }
            }
        }

        @Override
        public void render(SpriteBatch sb) {
            if (cards.isEmpty()) return;

            // Render completed cards (fading, at discard)
            for (int i = 0; i < currentIndex && i < cards.size(); i++) {
                VisualCard vc = cards.get(i);
                AbstractCard c = vc.card;
                if (vc.state == VisualCard.State.DONE) {
                    float t = vc.exhausts ? 1f : 0.3f;
                    c.current_x = DISCARD_X;
                    c.current_y = DISCARD_Y;
                    c.drawScale = 0.2f * Settings.scale;
                    c.transparency = t * 0.2f;
                    c.render(sb);
                }
            }

            // Render pending cards (stacked behind current)
            for (int i = cards.size() - 1; i > currentIndex; i--) {
                VisualCard vc = cards.get(i);
                AbstractCard c = vc.card;
                int offset = i - currentIndex - 1;
                c.current_x = CENTER_X + offset * 12f * Settings.scale;
                c.current_y = CENTER_Y - (offset + 1) * 30f * Settings.scale;
                c.drawScale = (0.75f - offset * 0.08f) * Settings.scale;
                c.transparency = Math.max(0.25f, 0.8f - offset * 0.15f);
                c.angle = offset * 2f;
                c.render(sb);
            }

            // Render current card
            if (currentIndex < cards.size()) {
                VisualCard vc = cards.get(currentIndex);
                AbstractCard c = vc.card;

                if (vc.state == VisualCard.State.AT_CENTER) {
                    c.current_x = CENTER_X;
                    c.current_y = CENTER_Y;
                    c.drawScale = 0.9f * Settings.scale;
                    c.angle = 0f;
                    c.transparency = 1f;
                } else if (vc.state == VisualCard.State.FLYING_OUT) {
                    float progress = 1f - (vc.flyOutTimer / FLY_OUT_DURATION);
                    float eased = Interpolation.pow2In.apply(progress);
                    c.current_x = MathUtils.lerp(CENTER_X, DISCARD_X, eased);
                    c.current_y = MathUtils.lerp(CENTER_Y, DISCARD_Y, eased);
                    c.drawScale = MathUtils.lerp(0.9f, 0.15f, eased) * Settings.scale;
                    c.angle = eased * 10f;
                    if (vc.exhausts) {
                        c.transparency = MathUtils.lerp(1f, 0f, eased);
                        c.current_y = MathUtils.lerp(CENTER_Y, EXHAUST_Y, eased);
                        c.current_x = CENTER_X;
                    } else {
                        c.transparency = MathUtils.lerp(1f, 0.2f, eased);
                    }
                }

                c.render(sb);
            }
        }

        @Override
        public void dispose() {}

        private static class VisualCard {
            enum State { AT_CENTER, FLYING_OUT, DONE }
            final AbstractCard card;
            final boolean exhausts;
            State state;
            float flyOutTimer;

            VisualCard(AbstractCard card, boolean exhausts) {
                this.card = card;
                this.exhausts = exhausts;
                this.state = State.AT_CENTER;
            }
        }
    }
}
