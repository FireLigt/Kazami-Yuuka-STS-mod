package YuukaMod.patches;

import YuukaMod.Yuukamod;
import YuukaMod.character.KazamiYuuka;
import com.badlogic.gdx.graphics.Texture;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.cutscenes.Cutscene;
import com.megacrit.cardcrawl.cutscenes.CutscenePanel;
import com.megacrit.cardcrawl.helpers.ImageMaster;

import java.lang.reflect.Field;
import java.util.ArrayList;

public class YuukaCutscenePatch {
    @SpirePatch(clz = Cutscene.class, method = SpirePatch.CONSTRUCTOR,
            paramtypez = {AbstractPlayer.PlayerClass.class})
    public static class CutsceneConstructorPatch {
        @SpirePostfixPatch
        public static void Postfix(Cutscene __instance, AbstractPlayer.PlayerClass playerClass) {
            if (playerClass != KazamiYuuka.Meta.FLOWER_FIELD_TYRANT) {
                return;
            }
            try {
                Field bgImgField = Cutscene.class.getDeclaredField("bgImg");
                bgImgField.setAccessible(true);
                Texture oldBg = (Texture) bgImgField.get(__instance);
                if (oldBg != null) {
                    oldBg.dispose();
                }
                Texture bg = ImageMaster.loadImage("images/scenes/greenBg.jpg");
                if (bg != null) {
                    bgImgField.set(__instance, bg);
                }

                Field panelsField = Cutscene.class.getDeclaredField("panels");
                panelsField.setAccessible(true);
                @SuppressWarnings("unchecked")
                ArrayList<CutscenePanel> panels = (ArrayList<CutscenePanel>) panelsField.get(__instance);
                if (panels != null) {
                    for (CutscenePanel p : panels) {
                        p.dispose();
                    }
                    panels.clear();
                    panels.add(new CutscenePanel(Yuukamod.imagePath("scenes/Yuuka 1.png"), "ATTACK_HEAVY"));
                    panels.add(new CutscenePanel(Yuukamod.imagePath("scenes/Yuuka 2.png")));
                    panels.add(new CutscenePanel(Yuukamod.imagePath("scenes/Yuuka 3.png")));
                }
            } catch (Exception e) {
                Yuukamod.logger.info("YuukaCutscenePatch failed: " + e.getMessage());
            }
        }
    }
}
