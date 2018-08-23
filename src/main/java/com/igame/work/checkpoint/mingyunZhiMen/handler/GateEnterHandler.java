package com.igame.work.checkpoint.mingyunZhiMen.handler;







import com.google.common.collect.Lists;
import com.igame.core.ErrorCode;
import com.igame.core.MProtrol;
import com.igame.core.MessageUtil;
import com.igame.core.SessionManager;
import com.igame.core.handler.BaseHandler;
import com.igame.core.handler.RetVO;
import com.igame.work.checkpoint.mingyunZhiMen.GateDto;
import com.igame.work.checkpoint.mingyunZhiMen.GateService;
import com.igame.work.fight.dto.FightBase;
import com.igame.work.fight.dto.FightData;
import com.igame.work.fight.dto.MatchMonsterDto;
import com.igame.work.monster.dto.Monster;
import com.igame.work.quest.service.QuestService;
import com.igame.work.user.dto.Player;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import net.sf.json.JSONObject;

import java.util.List;
import java.util.Map;

/**
 * 
 * @author Marcus.Z
 *
 */
public class GateEnterHandler extends BaseHandler{
	

	@Override
	public void handleClientRequest(User user, ISFSObject params) {

		RetVO vo = new RetVO();
		if(reviceMessage(user,params,vo)){
			return;
		}

		Player player = SessionManager.ins().getSession(Long.parseLong(user.getName()));
		if(player == null){
			this.getLogger().error(this.getClass().getSimpleName()," get player failed Name:" +user.getName());
			return;
		}

		String infor = params.getUtfString("infor");
		JSONObject jsonObject = JSONObject.fromObject(infor);

		int gateId = jsonObject.getInt("gateId");

		//校验等级
//		if(player.getPlayerLevel() <18){
//			sendError(ErrorCode.LEVEL_NOT,MProtrol.toStringProtrol(MProtrol.GATE_ENTER), vo, user);
//			return;
//		}

		//校验是否领取
		if(player.getFateData().getGetReward() == 1){
			sendError(ErrorCode.GATE_NOT,MProtrol.toStringProtrol(MProtrol.GATE_ENTER), vo, user);
			return;
		}

		//异常校验
		GateDto gto = player.getFateData().getGate(gateId);
		if(gto == null){
			sendError(ErrorCode.GATE_NOT,MProtrol.toStringProtrol(MProtrol.GATE_ENTER), vo, user);
			return;
		}

		player.getFateData().setGateId(gateId);
		List<GateDto> ls = Lists.newArrayList();
		List<MatchMonsterDto> lb = Lists.newArrayList();
		int act = 0;
		if(player.getFateData().getTodayFateLevel() < player.getFateData().getFateLevel()){//还在快速选门之中，未达到最高门
			for(int level = player.getFateData().getTodayFateLevel();level <= player.getFateData().getFateLevel();level++){
				List<GateDto> temp = GateService.creatGate(player);
				boolean special = false;
				for(GateDto gt : temp){
					if(gt.getType() != 0){//怪物关卡直接获得宝箱
						special = true;//随机到特殊关卡就展示门
						break;
					}
				}
				if(special){
					ls = temp;
				}else{
					player.getFateData().addTempBoxCount(2);
				}
				player.getFateData().setTodayFateLevel(level);
				if(!ls.isEmpty()){//随机到特殊关卡就展示门
					break;
				}
			}
			if(player.getFateData().getTempBoxCount() > 0){
				MessageUtil.notiyDeInfoChange(player);
			}
			if(ls.isEmpty()){//说明已经达到昨天最高门,但都没有随机到特殊门
				ls = GateService.creatGate(player);
			}
			player.getFateData().setGate(ls);
		}else{

			if(gto.getType() != 0){//buffer
				act = 1;
				player.getFateData().addTempBoxCount(gto.getBoxCount());//加宝箱
				player.getFateData().addTodayFateLevel();//到下一层
				List<GateDto> gls = GateService.creatGate(player);//创建新的门
				player.getFateData().setGate(gls);
				MessageUtil.notiyDeInfoChange(player);
				MessageUtil.notiyGateChange(player);
				QuestService.processTask(player, 10, 1);
			}else {//普通怪物
				act = 0;
				Map<Long,Monster> mms = gto.getMons();
				FightBase fb  = new FightBase(player.getPlayerId(),new FightData(player),new FightData(null,mms));
//				player.setFightBase(fb);

				for(Monster m : fb.getFightB().getMonsters().values()){
					MatchMonsterDto mto = new MatchMonsterDto(m);
					mto.reCalGods(player.callFightGods(), null);
					lb.add(mto);
				}
			}
		}

		int type = gto.getType();

		vo.addData("act", act);
		vo.addData("type", type);
		vo.addData("m", lb);
		if(act == 0){
			vo.addData("a", player.getMingZheng().values());
		}else{
			vo.addData("a", Lists.newArrayList());
		}

		sendSucceed(MProtrol.toStringProtrol(MProtrol.GATE_ENTER), vo, user);
	}

	
}