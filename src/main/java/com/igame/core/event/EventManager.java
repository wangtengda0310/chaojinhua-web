package com.igame.core.event;

import com.igame.core.SessionManager;
import com.igame.work.PlayerEvents;
import com.igame.work.user.dto.Player;
import com.smartfoxserver.v2.core.ISFSEvent;
import com.smartfoxserver.v2.core.SFSEventParam;
import com.smartfoxserver.v2.core.SFSEventType;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.entities.variables.SFSRoomVariable;
import com.smartfoxserver.v2.entities.variables.SFSUserVariable;
import com.smartfoxserver.v2.extensions.BaseServerEventHandler;

import java.util.HashMap;
import java.util.Map;

public class EventManager {
    public static Map<String, PlayerEventObserver> playerEventObservers = new HashMap<>();
    public static Map<String, ServiceEventListener> serviceEventListeners = new HashMap<>();

    /**
     * 接受用户相关的事件并预处理后传给GameHandler.PlayerEventObserver
     */
    public static BaseServerEventHandler playerEventObserver() {
        return new BaseServerEventHandler() {
            @Override
            public void handleServerEvent(ISFSEvent isfsEvent) {
                if (isfsEvent.getType() == SFSEventType.USER_VARIABLES_UPDATE && isfsEvent.getParameter(SFSEventParam.VARIABLES_MAP) != null) {
                    User user = null;
                    if (isfsEvent.getParameter(SFSEventParam.USER) instanceof User) {
                        user = (User) isfsEvent.getParameter(SFSEventParam.USER);
                    }
                    if (user == null) {
                        // TODO log error
                        return;
                    }
                    if (isfsEvent.getParameter(SFSEventParam.VARIABLES_MAP) instanceof Map) {
                        Map parameters = (Map) isfsEvent.getParameter(SFSEventParam.VARIABLES_MAP);
                        Object parameter = parameters.get("last.event");

                        if (parameter instanceof SFSUserVariable) {
                            Object value = ((SFSUserVariable) parameter).getValue();
                            if (value instanceof ISFSObject) {  // SFS自己封装了ISFSVariable，需要通过ISFSObject存储对象
                                PlayerEvents eventType = (PlayerEvents) ((ISFSObject) value).getClass("eventType");
                                Object param = ((ISFSObject) value).getClass("event");

                                Player player = SessionManager.ins().getSession(Long.parseLong(user.getName()));
                                if (player == null) {
                                    // TODO 是否加载缓存或数据库？
                                }
                                playerEventObservers.values().stream()
                                        .filter(listener-> listener.interestedType() == eventType)
                                        .forEach(listener->listener.observe(player, param));
                            }
                        }
                    }
                }
            }
        };
    }

    public static BaseServerEventHandler serviceEventListener() {
        return new BaseServerEventHandler() {
            @Override
            public void handleServerEvent(ISFSEvent isfsEvent) {
                if (isfsEvent.getType() == SFSEventType.ROOM_VARIABLES_UPDATE && isfsEvent.getParameter(SFSEventParam.VARIABLES_MAP) != null) {

                    if (isfsEvent.getParameter(SFSEventParam.VARIABLES_MAP) instanceof Map) {
                        Map parameters = (Map) isfsEvent.getParameter(SFSEventParam.VARIABLES_MAP);
                        Object parameter = parameters.get("last.event");

                        if (parameter instanceof SFSRoomVariable) {
                            Object value = ((SFSRoomVariable) parameter).getValue();
                            if (value instanceof ISFSObject) {  // SFS自己封装了ISFSVariable，需要通过ISFSObject存储对象
                                EventType eventType = (EventType) ((ISFSObject) value).getClass("eventType");
                                Object param = ((ISFSObject) value).getClass("event");

                                serviceEventListeners.values().stream()
                                        .filter(listener-> listener.interestedType() == eventType)
                                        .forEach(listener->listener.handleEvent(param));
                            }
                        }
                    }
                }
            }
        };
    }

    public static void clearAllListeners() {
        playerEventObservers.clear();
        serviceEventListeners.clear();
    }
}