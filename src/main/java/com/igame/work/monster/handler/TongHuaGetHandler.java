package com.igame.work.monster.handler;



import net.sf.json.JSONObject;

import com.igame.core.ErrorCode;
import com.igame.core.MProtrol;
import com.igame.core.SessionManager;
import com.igame.core.handler.BaseHandler;
import com.igame.core.log.GoldLog;
import com.igame.core.handler.RetVO;
import com.igame.util.MyUtil;
import com.igame.work.checkpoint.guanqia.RewardDto;
import com.igame.work.user.dto.Player;
import com.igame.work.user.dto.TongHuaDto;
import com.igame.work.user.load.ResourceService;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.data.ISFSObject;

/**
 * 
 * @author Marcus.Z
 *
 */
public class TongHuaGetHandler extends BaseHandler{
	

	@Override
	public void handleClientRequest(User user, ISFSObject params) {
		RetVO vo = new RetVO();
		if(reviceMessage(user,params,vo)){
			return;
		}
		String infor = params.getUtfString("infor");
		JSONObject jsonObject = JSONObject.fromObject(infor);
		Player player = SessionManager.ins().getSession(Long.parseLong(user.getName()));
		if(player == null){
			this.getLogger().error(this.getClass().getSimpleName()," get player failed Name:" +user.getName());
			return;
		}
		long now = System.currentTimeMillis();
		int index = jsonObject.getInt("index");
		String info = "";
		int timeIndex = 0;
		long leftTime = 0;
		String reward = null;
		int ret = 0;
		/**
		 *"type,state,rewardtype,rewardId,count;";//如1,0,1,1,1;2,-1,3,200005,100
		 *
		 * type:0-普通 1-怪物 2-强化袍子 3-进化袍子 4-神性袍子
	     * state:-1-未解锁 0-已解锁/可开启 1-倒计时/进行中 2-可领取 3-已领取/已完结	
		 * rewardtype:1-怪物关卡  3-道具 5-同化经验值
		 * rewardId:当rewardtype为1 对应strengthenmonster.xml中的num 
		 *  当rewardtype为3 对应道具ID
		 *  当rewardtype为5 为5
		 * count:对应的数量
		 */
		TongHuaDto tdo = player.getTonghua();
		if(tdo == null){
			ret = ErrorCode.ERROR;
		}else{
			String[] tss = tdo.getTongStr().split(";");
			if(index< 1 || index>tss.length){
				ret = ErrorCode.ERROR;
			}else{
				String[] t = tss[index-1].split(",");//"type,state,rewardtype,rewardId,count"
				if("1".equals(t[0]) || "-1".equals(t[1]) || "0".equals(t[1]) || "3".equals(t[1])){
					ret = ErrorCode.TONGHUA_NOTGET;
				}else{
					timeIndex = tdo.getTimeIndex();
					leftTime = tdo.calLeftTime();
					if(index == tdo.getTimeIndex() && tdo.calLeftTime() > 0){
						ret = ErrorCode.TONGHUA_NOTGET;
					}else{
						RewardDto rt = new RewardDto();
						if("3".equals(t[2])){//道具
							rt.addItem(Integer.parseInt(t[3]), Integer.parseInt(t[4]));
						}else if("5".equals(t[2])){//5-同化经验值
							rt.setTongExp(Integer.parseInt(t[4]));
						}
						ResourceService.ins().addRewarToPlayer(player, rt);
						reward = ResourceService.ins().getRewardString(rt);
						t[1] = "3";
						tdo.setStartTime(0);
						tdo.setTimeCount(0);
						tdo.setTimeIndex(0);
						tss[index-1] = MyUtil.toString(t, ",");
						info = tss[index-1];
						tdo.setTongStr(MyUtil.toString(tss, ";"));
						timeIndex = tdo.getTimeIndex();
						leftTime = tdo.calLeftTime();
						boolean allOver = true;
						for(String tt : tss){
							if(!"3".equals(tt.split(",")[1])){
								allOver = false;
								break;
							}
						}
						if(allOver && tdo.calRefLeftTime() > 0){
							tdo.setStartRefTime(0);
							tdo.calRefLeftTime();
						}
						GoldLog.info(player.getSeverId(), player.getUserId(), player.getPlayerId(), GoldLog.TONGHUAGET, "#index:" + index+"#info:"+tss[index-1]);
					}
				}

			}
		}
		
		if(ret != 0){
			vo.setState(1);
			vo.setErrCode(ret);
		}
		vo.addData("index", index);
		vo.addData("info", info);
		vo.addData("timeIndex", timeIndex);
		vo.addData("leftTime", leftTime);
		vo.addData("reward", reward);

		send(MProtrol.toStringProtrol(MProtrol.TONGHUA_GET), vo, user);
	}

	
}
