package YuukaMod.util;

import YuukaMod.Yuukamod;
import me.antileaf.signature.utils.SignatureHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Registers all card signature art with SignatureLib.
 * <p>
 * Each entry maps a card ID to its 512x512 and 1024x1024 signature images.
 */
public class YuukaSignatureHelper {
    private static final Logger logger = LogManager.getLogger(Yuukamod.modID + ":Signature");

    private static final String MOD = Yuukamod.modID;
    private static final String SIG = "YuukaMod/images/signature";

    public static void registerAll() {
        logger.info("Registering card signatures...");

        SignatureHelper.noDebuggingPrefix(MOD + ":");

        // --- Attack signatures ---
        register("Pursuit", "attack");
        register("Finishing_move", "attack");
        register("Blossoming_of_Gensokyo", "attack");

        // --- Skill signatures ---
        register("Contempt", "skill");
        register("Smiling", "skill");
        register("Reload", "skill");
        register("Manipulating_flower", "skill");
        register("Low_speed", "skill");
        register("Gentle", "skill");
        register("Fragrance", "skill");
        register("Famed_far", "skill");

        // --- Power signatures ---
        register("Worthy_opponent", "power");
        register("The_beauties_of_nature", "power");
        register("Parasol", "power");
        register("LLS_PC98_form", "power");
        register("Gensou_shunka", "power");

        logger.info("Card signature registration complete.");
    }

    private static void register(String name, String type) {
        String id = MOD + ":" + name;
        String img = SIG + "/" + type + "/" + name + "_s.png";
        String portrait = SIG + "/" + type + "/" + name + "_s_p.png";

        SignatureHelper.register(id, new SignatureHelper.Info(
                img,
                portrait,
                card -> true
        ));
        logger.info("Registered signature: {} -> {}", id, img);
    }
}
