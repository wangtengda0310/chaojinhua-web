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
import com.igame.work.fight.dto.FightData;
import com.igame.work.fight.dto.MatchMonsterDto;
import com.igame.work.monster.dto.Monster;
import com.igame.work.user.dto.Player;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import net.sf.json.JSONObject;

import java.util.List;

/**
 * 
 * @author Marcus.Z
 *
 */
public class GateResetHandler extends BaseHandler{
	

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

		List<GateDto> ls = Lists.newArrayList();
		int ret = 0;
//		if(player.getPlayerLevel() <18){
//			ret = ErrorCode.LEVEL_NOT;
//		}else{
			if(player.getFateData().getGetReward() == 1){
				ret = ErrorCode.GATE_NOT;
			}else{
				player.getFateData().setTodayFateLevel(1);		
				player.getFateData().setTempBoxCount(-1);
				player.getFateData().setTempSpecialCount(0);
				player.getFateData().setAddRate(0);
				ls = GateService.creatGate(player);
				player.getFateData().setGate(ls);
//				MessageUtil.notiyDeInfoChange(player);
				MessageUtil.notiyGateChange(player);
				
				FightData fd = new FightData(player);
				player.getMingZheng().clear();
		    	for(Monster m : fd.getMonsters().values()){
		    		MatchMonsterDto mto = new MatchMonsterDto(m);
					mto.reCalGods(player.callFightGods(),null);
		    		player.getMingZheng().put(mto.getObjectId(),mto);
		    	}
				
			}

//		}

		if(ret != 0){
			vo.setState(1);
			vo.setErrCode(ret);
		}

		send(MProtrol.toStringProtrol(MProtrol.GATE_RESET), vo, user);
	}

	
}