package com.igame.sfsAdaptor;

import com.igame.core.event.EventManager;
import com.igame.core.event.PlayerEventObserver;
import com.igame.core.event.ServiceEventListener;
import com.igame.work.PlayerEvents;
import com.igame.work.ServiceEvents;
import com.igame.work.user.dto.Player;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.entities.data.SFSObject;
import com.smartfoxserver.v2.entities.variables.SFSRoomVariable;
import com.smartfoxserver.v2.entities.variables.SFSUserVariable;
import com.smartfoxserver.v2.extensions.BaseServerEventHandler;

import java.util.Collection;
import java.util.Collections;

public abstract class EventDispatcherHandler extends BaseServerEventHandler {
    {
        PlayerEventObserver playerEventObserver = playerObserver();
        if (playerEventObserver != null) {
            EventManager.playerEventObservers.put(getClass().getSimpleName(), playerEventObserver);
        }
        ServiceEventListener playerEventListener = eventListener();
        if (playerEventListener != null) {
            EventManager.serviceEventListeners.put(getClass().getSimpleName(), playerEventListener);
        }

        if (playerObservers() != null) {
            for (PlayerEventObserver observer : playerObservers()) {
                EventManager.playerEventObservers.put(observer.getClass().getName(), observer);
            }
        }
    }

    protected PlayerEventObserver playerObserver() {
        return null;
    }
    protected Collection<PlayerEventObserver> playerObservers() {
        return Collections.emptySet();
    }

    protected ServiceEventListener eventListener() {
        return null;
    }

    /**
     * 发送用户相关的事件，建议使用在玩家状态更新时通知相关观察者
     * 使用的是setUserVariables 所以会受到用户变量限制 SmartFoxServer默认允许每个玩家使用5个变量
     */
    protected void fireEvent(Player player, PlayerEvents eventType, Object event) {
        ISFSObject value = new SFSObject();
        value.putClass("eventType", eventType);
        if(event == null) {
            value.putNull("event");
        } else {
            value.putClass("event", event);
        }
        SFSUserVariable e = new SFSUserVariable("last.event", value, false, true);
        // 如果user为null,SmartFoxServer发布出事件，会报空指针，可以点进去看一下代码
        getApi().setUserVariables(player.getUser(), Collections.singletonList(e), false, true);
    }

    /**
     * 建议使用在异步调用上
     */
    protected void fireEvent(ServiceEvents eventType, Object event) {
        ISFSObject value = new SFSObject();
        value.putClass("eventType", eventType);
        if(event == null) {
            value.putNull("event");
        } else {
            value.putClass("event", event);
        }
        SFSRoomVariable e = new SFSRoomVariable("last.event", value, false, true, false);
        // 如果user为null,SmartFoxServer发布出事件，会报空指针，可以点进去看一下代码
        getApi().setRoomVariables(null, getParentExtension().getParentRoom(),Collections.singletonList(e), false, true,true);
    }

}
