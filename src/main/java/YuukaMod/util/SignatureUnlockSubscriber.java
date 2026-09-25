package YuukaMod.util;

import com.megacrit.cardcrawl.screens.GameOverScreen;
import me.antileaf.signature.interfaces.EasyUnlockSubscriber;
import me.antileaf.signature.utils.EasyUnlock;

import java.util.ArrayList;
import java.util.Set;

public class SignatureUnlockSubscriber implements EasyUnlockSubscriber {
    @Override
    public EasyUnlock receiveOnGameOver(GameOverScreen screen) {
        return null;
    }

    @Override
    public ArrayList<EasyUnlock> receiveOnGameOverMultiUnlocks(GameOverScreen screen) {
        Set<String> newly = SignatureUnlockManager.getNewlyUnlocked();
        if (newly.isEmpty()) {
            return null;
        }

        EasyUnlock unlock = new EasyUnlock()
                .IDs(new ArrayList<>(newly));

        return new ArrayList<EasyUnlock>() {{
            add(unlock);
        }};
    }
}
