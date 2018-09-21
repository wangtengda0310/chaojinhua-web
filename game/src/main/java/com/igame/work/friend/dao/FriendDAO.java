package com.igame.work.friend.dao;

import com.igame.core.db.AbsDao;
import com.igame.core.di.Inject;
import com.igame.work.friend.dto.Friend;
import com.igame.work.friend.dto.FriendInfo;
import com.igame.work.user.dto.Player;
import com.igame.work.user.service.PlayerCacheService;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.query.UpdateOperations;

public class FriendDAO extends AbsDao {

    @Inject private PlayerCacheService playerCacheService;

    /**
     * 根据 玩家ID 获取 玩家好友信息
     * @param playerId 玩家ID
     */
    public FriendInfo getFriendInfoByPlayerId(long playerId){

        FriendInfo friendInfo = getDatastore().find(FriendInfo.class, "playerId", playerId).get();

        if (friendInfo != null){
            friendInfo.getCurFriends().forEach(this::loadCache);
            friendInfo.getReqFriends().forEach(this::loadCache);
        }else {

            friendInfo = new FriendInfo(playerId);

            saveFriendInfo(friendInfo);
        }

        return friendInfo;
    }

    private void loadCache(Friend friend) {

        Player cacheDto = playerCacheService.getPlayerById(friend.getPlayerId());

        if(cacheDto!=null) {
            friend.setPlayerLevel(cacheDto.getPlayerLevel());    //玩家等级
            friend.setNickName(cacheDto.getNickname());    //玩家昵称
            friend.setPlayerFrameId(cacheDto.getPlayerFrameId());    //玩家头像框
            friend.setPlayerHeadId(cacheDto.getPlayerHeadId());    //玩家头像
            friend.setFightValue(cacheDto.getFightValue());    //战力
            friend.setLoginoutTime(cacheDto.getLoginoutTime());    //战力
        }
    }

    /**
     * 初始化 玩家好友信息
     * @param friendInfo 好友信息
     */
    public FriendInfo saveFriendInfo(FriendInfo friendInfo){

        getDatastore().save(friendInfo);

        return friendInfo;
    }

    /**
     * 更新 玩家好友信息
     * @param friendInfo 好友信息
     */
    public void updateFriends(FriendInfo friendInfo){

        Datastore ds = getDatastore();
        UpdateOperations<FriendInfo> up = ds.createUpdateOperations(FriendInfo.class)
                .set("curFriends",friendInfo.getCurFriends())
                .set("reqFriends",friendInfo.getReqFriends());

        ds.update(friendInfo,up);
    }

}