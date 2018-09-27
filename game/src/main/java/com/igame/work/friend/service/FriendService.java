package com.igame.work.friend.service;

import com.igame.core.SessionManager;
import com.igame.core.di.Inject;
import com.igame.core.event.RemoveOnLogout;
import com.igame.core.handler.RetVO;
import com.igame.work.MProtrol;
import com.igame.work.MessageUtil;
import com.igame.work.checkpoint.guanqia.CheckPointService;
import com.igame.work.checkpoint.tansuo.TansuoDto;
import com.igame.work.checkpoint.tansuo.TansuoTemplate;
import com.igame.work.friend.dao.FriendDAO;
import com.igame.work.friend.dto.Friend;
import com.igame.work.friend.dto.FriendInfo;
import com.igame.work.user.dto.Player;
import com.igame.work.user.service.PlayerCacheService;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static com.igame.work.friend.FriendConstants.FRIEND_STATE_CAN_HELP;
import static com.igame.work.friend.FriendConstants.FRIEND_STATE_NO_HELP;

/**
 * @author xym
 *
 * 好友模块服务
 *
 *      本服务内方法涉及当前角色更新均不推送
 */
public class FriendService {

    @Inject private FriendDAO dao;
    @Inject private SessionManager sessionManager;
    @Inject private PlayerCacheService playerCacheService;
    @Inject public CheckPointService checkPointService;

    @RemoveOnLogout() private Map<Long, FriendInfo> playerFriends= new ConcurrentHashMap<>();    //好友 在Friends表中存储 不在Player表中存储

    /**
     * 获取好友探索列表
     * @param friendPlayer 好友角色对象
     * @return 正在探索中且剩余时间大于0的探索小队
     */
    public List<TansuoDto> getExploreList(Player friendPlayer) {

        //计算剩余时间
        for(TansuoTemplate ts : checkPointService.tansuoData.getAll()){
            if(friendPlayer.hasCheckPoint(String.valueOf(ts.getUnlock())) && friendPlayer.getTangSuo().get(ts.getNum()) == null){
                friendPlayer.getTangSuo().put(ts.getNum(), new TansuoDto(ts));
            }
        }

        long now = System.currentTimeMillis();
        friendPlayer.getTangSuo().values().forEach(e -> e.calLeftTime(now));

        List<TansuoDto> exploreList = new ArrayList<>();
        for (TansuoDto tansuoDto : friendPlayer.getTangSuo().values()) {
            if (tansuoDto.getLeftTime() > 0)
                exploreList.add(tansuoDto);
        }

        return exploreList;
    }

    /**
     * 获取帮助状态
     * @param exploreList 探索列表
     * @return 0等于不可帮助，1等于可帮助
     */
    public int getHelpState(Collection<TansuoDto> exploreList) {

        int helpState = FRIEND_STATE_NO_HELP;  //0等于不可帮助，1等于可帮助

        for (TansuoDto value : exploreList) {
            //如果剩余时间大于0并且还没人帮助
            if (value.getLeftTime() > 0 && value.getIsHelp() == 0)
                helpState = FRIEND_STATE_CAN_HELP;
        }

        return helpState;
    }

    /**
     * 添加好友请求
     *      如果对方在线则更新缓存并推送，不在线则存库
     *      ps:推送对方好友请求更新
     * @param sendPlayer 发送好友请求的角色
     * @param recPlayerId 接收好友请求的角色
     */
    public void addReqFriend(Player sendPlayer, long recPlayerId) {

        Player reqPlayer = sessionManager.getSessionByPlayerId(recPlayerId);
        Player reqPlayerCache = playerCacheService.getPlayerById(recPlayerId);

        if (reqPlayer == null && reqPlayerCache == null){
            return;
        }

        Friend friend = new Friend(sendPlayer);
        if (reqPlayer != null && !playerFriends.get(reqPlayer).getReqFriends().contains(friend)){ //在线 并且自己不存在于对方好友列表中

            playerFriends.get(reqPlayer).getReqFriends().add(friend);

            //推送
            List<Friend> reqFriends = new ArrayList<>();
            reqFriends.add(new Friend(sendPlayer));
            pushReqFriends(reqPlayer,new ArrayList<>(),reqFriends);

        }else{ //不在线

            //存库
            FriendInfo friendInfo = dao.getFriendInfoByPlayerId(recPlayerId);
            if (friendInfo == null){    //todo 处理老数据

                friendInfo = new FriendInfo(recPlayerId);

                friendInfo.getReqFriends().add(friend);

                dao.saveFriendInfo(friendInfo);
            }else {
                if (!friendInfo.getReqFriends().contains(friend)){  //自己不存在于对方好友列表中
                    friendInfo.getReqFriends().add(friend);
                    dao.updateFriends(friendInfo);
                }
            }
        }
    }

    /**
     * 删除好友请求
     *      必定在线，更新缓存
     *      ps:不推送
     * @param player 角色
     * @param delReqPlayerId 待删除的好友ID
     */
    public void delReqFriend(Player player, long delReqPlayerId){

        List<Friend> reqFriends = playerFriends.get(player.getPlayerId()).getReqFriends();

        for (int i = 0; i < reqFriends.size(); i++) {
            Friend reqFriend = reqFriends.get(i);
            if (reqFriend.getPlayerId() == delReqPlayerId) {
                reqFriends.remove(reqFriend);
            }
        }

    }

    /**
     * 双向添加好友
     *      更新当前角色缓存，如果对方在线则更新缓存，不在线则存库并更新cache
     *      ps:只推送对方好友更新
     * @param player 角色
     * @param reqPlayerId 好友ID
     */
    public void addFriend(Player player, long reqPlayerId){

        Player reqPlayer = sessionManager.getSessionByPlayerId(reqPlayerId);
        Player reqPlayerCache = playerCacheService.getPlayerById(reqPlayerId);

        if (reqPlayer == null && reqPlayerCache == null){
            return;
        }

        //当前角色添加好友
        Friend reqFriend;
        if (reqPlayer != null) {
            reqFriend = new Friend(reqPlayer);
        }else {
            reqFriend = new Friend(reqPlayerCache);
        }

        playerFriends.get(player.getPlayerId()).getCurFriends().add(reqFriend);

        //对方添加好友
        if (reqPlayer != null){ //在线,更新缓存

            playerFriends.get(reqPlayer.getPlayerId()).getCurFriends().add(new Friend(player));

            //推送对方好友列表更新
            List<Friend> reqAddFriends = new ArrayList<>();
            reqAddFriends.add(new Friend(player));

            pushFriends(reqPlayer,new ArrayList<>(),reqAddFriends);


        }else { //不在线,更新缓存并存库

            FriendInfo friendInfo = dao.getFriendInfoByPlayerId(reqPlayerId);

            if (friendInfo == null){

                friendInfo = new FriendInfo(reqPlayerId);

                friendInfo.getCurFriends().add(new Friend(player));

                dao.saveFriendInfo(friendInfo);
            }else {

                //添加好友并增加当前好友数量
                friendInfo.getCurFriends().add(new Friend(player));

                dao.updateFriends(friendInfo);
            }
        }
    }

    /**
     * 双向删除好友
     *      更新当前角色缓存，如果对方在线则更新缓存，不在线则存库并更新cache
     *      ps:只推送对方好友更新
     * @param player 当前角色
     * @param delPlayerId 好友ID
     *
     */
    public void delFriend(Player player, long delPlayerId) {

        Player delPlayer = sessionManager.getSessionByPlayerId(delPlayerId);
        Player delPlayerCache = playerCacheService.getPlayerById(delPlayerId);

        //如果对方在线，推送好友更新，不在线，则减少好友数量并存库
        if (delPlayer == null && delPlayerCache == null){
            return;
        }

        //当前角色删除好友
        Friend delFriend;
        if (delPlayer != null) {
            delFriend = new Friend(delPlayer);
        }else {
            delFriend = new Friend(delPlayerCache);
        }

        playerFriends.get(player.getPlayerId()).getCurFriends().remove(delFriend);

        //如果对方在线，推送好友更新，不在线，则增加好友数量并存库
        if (delPlayer != null){

            //对方删除好友
            playerFriends.get(delPlayer.getPlayerId()).getCurFriends().remove(new Friend(player));

            //推送对方好友列表更新
            List<Long> reqDelFriends = new ArrayList<>();
            reqDelFriends.add(player.getPlayerId());

            pushFriends(delPlayer,reqDelFriends,new ArrayList<>());

        } else {

            //删除好友并减少当前好友数量
            FriendInfo friendInfo = dao.getFriendInfoByPlayerId(delPlayerId);

            friendInfo.getCurFriends().remove(new Friend(player));
            dao.updateFriends(friendInfo);
        }

    }

    /**
     * 推送好友列表更新
     * @param player 推送给谁
     * @param delFriends 删除的好友ID
     * @param addFriends 增加的好友
     */
    public void pushFriends(Player player,List<Long> delFriends,List<Friend> addFriends){
        RetVO vo = new RetVO();
        vo.addData("delFriend",delFriends);
        vo.addData("addFriend",addFriends);
        MessageUtil.sendMessageToPlayer(player, MProtrol.FRIEND_LIST_UPDATE, vo);
    }

    /**
     * 推送好友请求更新
     * @param player 角色
     * @param delReqFriends 删除的好友请求
     * @param addReqFriends 增加的好友请求
     */
    public void pushReqFriends(Player player,List<Long> delReqFriends,List<Friend> addReqFriends){
        RetVO vo = new RetVO();
        vo.addData("delReqFriend",delReqFriends);
        vo.addData("addReqFriend",addReqFriends);
        MessageUtil.sendMessageToPlayer(player, MProtrol.FRIEND_REQ_UPDATE, vo);
    }

    /**
     * 推送体力赠送
     * @param player 角色
     * @param playerId 赠送体力的好友角色ID
     */
    public void pushFriendPhy(Player player,long playerId){
        RetVO vo = new RetVO();
        vo.addData("fromPlayerId",playerId);
        MessageUtil.sendMessageToPlayer(player, MProtrol.FRIEND_PHY_UPDATE, vo);
    }

    /**
     * 初始化玩家好友信息
     * @param player 角色
     */
    public void newPlayer(Player player) {

        FriendInfo friendInfo = new FriendInfo(player.getPlayerId());

        playerFriends.put(player.getPlayerId(),friendInfo);
    }

    /**
     * 零点执行
     */
    public void zero(Player player) {

        //刷新时间，如更改为每日4点执行，需将此值改为4
        int hour = 12;

        //计算刷新时间
        Calendar instance = Calendar.getInstance();
        instance.set(Calendar.HOUR_OF_DAY,hour);
        instance.set(Calendar.MINUTE,0);
        instance.set(Calendar.SECOND,0);
        instance.set(Calendar.MILLISECOND,0);

        FriendInfo friendInfo = playerFriends.get(player.getPlayerId());

        List<Friend> curFriends = friendInfo.getCurFriends();
        for (Friend curFriend : curFriends) {
            curFriend.setGivePhy(0);    //重置体力赠送状态

            Date givePhyDate = curFriend.getGivePhyDate();
            if (givePhyDate == null){   //处理老数据
                curFriend.setReceivePhy(0);
            }else if (givePhyDate.before(instance.getTime())){  //如果赠送时间在刷新时间之前，则重置体力领取状态
                curFriend.setReceivePhy(0);
            }
        }
    }

    public void loadPlayer(Player player) {
        playerFriends.put(player.getPlayerId(), dao.getFriendInfoByPlayerId(player.getPlayerId()));
        FriendInfo friendInfo = playerFriends.get(player.getPlayerId());
        long explorerCount = friendInfo.getCurFriends().stream().filter(friend -> friend.getHelpAcc() == 1).count();
        friendInfo.setExploreCount((int) explorerCount);
        friendInfo.setMaxFriendCount(20);
        long phyCount = friendInfo.getCurFriends().stream().filter(friend -> friend.getReceivePhy() == 2).count();
        friendInfo.setPhysicalCount((int) phyCount);
    }

    public void setFriends(Player player, FriendInfo friendInfo) {
        playerFriends.put(player.getPlayerId(),friendInfo);
    }

    public FriendInfo getFriends(Player player) {
        return playerFriends.get(player.getPlayerId());
    }
    /**
     * 推送玩家好友更新
     */
    public void notifyFriendInfo(Player player) {

        //推送
        RetVO vo = new RetVO();

        vo.addData("friends", playerFriends.get(player));
        MessageUtil.sendMessageToPlayer(player, MProtrol.FRIENDS_UPDATE, vo);

    }
}
