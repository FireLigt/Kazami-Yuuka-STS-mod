package YuukaMod.util;

import YuukaMod.Yuukamod;
import YuukaMod.character.KazamiYuuka;
import YuukaMod.powers.LLSPC98formPower;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class TogetherInSpireCompat {
    public static final String REQUEST_ANIMATION = "YuukaMod_LLS_PC98_ANIM";
    public static final String REQUEST_MUSIC_START = "YuukaMod_LLS_PC98_MUSIC_START";
    public static final String REQUEST_MUSIC_STOP = "YuukaMod_LLS_PC98_MUSIC_STOP";
    public static final String REQUEST_MUSIC_SYNC = "YuukaMod_LLS_PC98_MUSIC_SYNC_REQUEST";

    private static final String TIS_MOD_CLASS = "spireTogether.SpireTogetherMod";
    private static final String TIS_SUBSCRIBERS_CLASS = "spireTogether.subscribers.TiSSubscribers";
    private static final String TIS_SUBSCRIBER_INTERFACE = "spireTogether.subscribers.TiSNetworkMessageSubscriber";
    private static final String TIS_SUBSCRIBER_MARKER = "spireTogether.subscribers.ITogetherInSpireSubscriber";
    private static final String TIS_P2P_MANAGER = "spireTogether.network.P2P.P2PManager";
    private static final String TIS_P2P_PLAYER = "spireTogether.network.P2P.P2PPlayer";
    private static final String TIS_CHAR_PRESET = "spireTogether.monsters.playerChars.NetworkCharPreset";
    private static final String TIS_REQUESTS = "spireTogether.network.P2P.P2PRequests";
    private static final String TIS_REQUEST_CHANGED_LOCATION_FALLBACK = "changedPlayerLocation";

    private static boolean initialized = false;
    private static boolean available = false;

    private static Method subscribeMethod;
    private static Method sendDataMethod;
    private static Method getPlayerMethod;
    private static Method getSelfMethod;
    private static Method getEntityMethod;
    private static Method isConnectedMethod;
    private static Field connectedField;
    private static Field sourceField;
    private static Field locationField;
    private static String changedPlayerLocationRequest;

    public static void init() {
        if (initialized) {
            return;
        }
        initialized = true;
        try {
            Class<?> tisMod = Class.forName(TIS_MOD_CLASS);
            Class<?> subscribers = Class.forName(TIS_SUBSCRIBERS_CLASS);
            Class<?> subscriberInterface = Class.forName(TIS_SUBSCRIBER_INTERFACE);
            Class<?> subscriberMarker = Class.forName(TIS_SUBSCRIBER_MARKER);
            Class<?> p2pManager = Class.forName(TIS_P2P_MANAGER);
            Class<?> p2pPlayer = Class.forName(TIS_P2P_PLAYER);
            Class<?> charPreset = Class.forName(TIS_CHAR_PRESET);

            subscribeMethod = subscribers.getMethod("subscribe", subscriberMarker);
            sendDataMethod = p2pManager.getMethod("SendData", String.class, Object[].class);
            getPlayerMethod = p2pManager.getMethod("GetPlayer", Integer.class);
            getSelfMethod = p2pManager.getMethod("GetSelf");
            getEntityMethod = p2pPlayer.getMethod("GetEntity");

            try {
                connectedField = tisMod.getDeclaredField("isConnected");
                connectedField.setAccessible(true);
            } catch (NoSuchFieldException e) {
                isConnectedMethod = tisMod.getMethod("isConnectedToGame");
            }

            sourceField = charPreset.getField("source");
            locationField = p2pPlayer.getField("location");

            try {
                Class<?> requests = Class.forName(TIS_REQUESTS);
                changedPlayerLocationRequest = (String) requests.getField("changedPlayerLocation").get(null);
            } catch (Exception e) {
                changedPlayerLocationRequest = TIS_REQUEST_CHANGED_LOCATION_FALLBACK;
            }

            Object proxy = java.lang.reflect.Proxy.newProxyInstance(
                    TogetherInSpireCompat.class.getClassLoader(),
                    new Class<?>[]{subscriberInterface, subscriberMarker},
                    (p, method, args) -> {
                        String name = method.getName();
                        if (name.equals("onMessageReceive")) {
                            handleMessage((String) args[1], args[2], (Integer) args[3]);
                            return null;
                        }
                        if (name.equals("toString")) {
                            return "YuukaMod.TogetherInSpireCompat";
                        }
                        return null;
                    });

            subscribeMethod.invoke(null, proxy);
            available = true;
            Yuukamod.logger.info("TogetherInSpire detected - multiplayer PC98 form sync enabled");
        } catch (Throwable t) {
            available = false;
            Yuukamod.logger.info("TogetherInSpire not detected - PC98 form sync disabled");
        }
    }

    public static boolean isAvailable() {
        return available;
    }

    public static boolean isConnected() {
        if (!available) {
            return false;
        }
        try {
            if (connectedField != null) {
                return connectedField.getBoolean(null);
            }
            if (isConnectedMethod != null) {
                return (Boolean) isConnectedMethod.invoke(null);
            }
        } catch (Exception e) {
            Yuukamod.logger.warn("TogetherInSpire: failed to read connection state", e);
        }
        return false;
    }

    public static void broadcastPlayerAnimation(String animPath) {
        sendData(REQUEST_ANIMATION, animPath);
    }

    public static void broadcastBossMusicStart() {
        sendData(REQUEST_MUSIC_START);
    }

    public static void broadcastBossMusicStop() {
        sendData(REQUEST_MUSIC_STOP);
    }

    public static void onLocalRoomEntered() {
        if (!isConnected()) {
            return;
        }
        sendData(REQUEST_MUSIC_SYNC);
        LLSPC98formPower.announceCurrentState();
        LLSPC98formPower.reconcileRemoteMusic();
    }

    private static void sendData(String request, Object... payload) {
        if (!isConnected()) {
            return;
        }
        try {
            sendDataMethod.invoke(null, request, payload);
        } catch (Exception e) {
            Yuukamod.logger.warn("TogetherInSpire: failed to send " + request, e);
        }
    }

    private static void handleMessage(String request, Object object, Integer senderID) {
        if (request == null || senderID == null) {
            return;
        }
        try {
            switch (request) {
                case REQUEST_ANIMATION:
                    if (object instanceof String) {
                        applyRemoteAnimation(senderID, (String) object);
                    }
                    break;
                case REQUEST_MUSIC_START:
                    LLSPC98formPower.startRemoteBossMusic();
                    break;
                case REQUEST_MUSIC_STOP:
                    LLSPC98formPower.stopRemoteBossMusic();
                    break;
                case REQUEST_MUSIC_SYNC:
                    LLSPC98formPower.announceCurrentState();
                    break;
                default:
                    if (changedPlayerLocationRequest != null && changedPlayerLocationRequest.equals(request)) {
                        if (isSameRoomAsSelf(senderID) && LLSPC98formPower.isLocalBossMusicActive()) {
                            LLSPC98formPower.announceCurrentState();
                        }
                    }
                    break;
            }
        } catch (Exception e) {
            Yuukamod.logger.warn("TogetherInSpire: failed to handle " + request, e);
        }
    }

    private static boolean isSameRoomAsSelf(Integer senderID) {
        try {
            Object senderPlayer = getPlayerMethod.invoke(null, senderID);
            if (senderPlayer == null) {
                return false;
            }
            Object selfPlayer = getSelfMethod.invoke(null);
            if (selfPlayer == null) {
                return false;
            }
            Object senderLoc = locationField.get(senderPlayer);
            Object selfLoc = locationField.get(selfPlayer);
            if (senderLoc == null || selfLoc == null) {
                return false;
            }
            return senderLoc.equals(selfLoc);
        } catch (Exception e) {
            return false;
        }
    }

    private static void applyRemoteAnimation(Integer senderID, String animPath) {
        try {
            Object playerData = getPlayerMethod.invoke(null, senderID);
            if (playerData == null) {
                return;
            }
            Object entity = getEntityMethod.invoke(playerData);
            if (entity == null) {
                return;
            }
            Object sourceObj = sourceField.get(entity);
            if (!(sourceObj instanceof AbstractPlayer)) {
                return;
            }
            AbstractPlayer remotePlayer = (AbstractPlayer) sourceObj;
            if (remotePlayer == AbstractDungeon.player) {
                return;
            }
            if (remotePlayer.chosenClass != KazamiYuuka.Meta.FLOWER_FIELD_TYRANT) {
                return;
            }
            LLSPC98formPower.applyPlayerAnimation(remotePlayer, animPath);
        } catch (Exception e) {
            Yuukamod.logger.warn("TogetherInSpire: failed to apply remote animation", e);
        }
    }
}
