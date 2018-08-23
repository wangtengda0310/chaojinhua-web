package com.igame.work.checkpoint.wujinZhiSen.handler;







import net.sf.json.JSONObject;

import com.igame.core.ErrorCode;
import com.igame.core.MProtrol;
import com.igame.core.MessageUtil;
import com.igame.core.SessionManager;
import com.igame.core.handler.BaseHandler;
import com.igame.core.handler.RetVO;
import com.igame.work.checkpoint.guanqia.CheckPointService;
import com.igame.work.fight.dto.MatchMonsterDto;
import com.igame.work.user.dto.Player;
import com.igame.work.user.load.ResourceService;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.data.ISFSObject;

/**
 * 
 * @author Marcus.Z
 *
 */
public class EndlessNaiHandler extends BaseHandler{
	

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

		int ret = 0;
		if(player.getWuNai() >= 1){
			ret = ErrorCode.NAI_ERROR;
		}else{
			if(player.getDiamond() < 50){
				ret = ErrorCode.DIAMOND_NOT_ENOUGH;
			}else{
				if(CheckPointService.isFullWuHp(player)){
					ret = ErrorCode.WUZHENG_HPFULL;
				}else{
					player.setWuNai(1);
					ResourceService.ins().addDiamond(player, -50);
			    	for(MatchMonsterDto mto : player.getWuZheng().values()){
			    		mto.setHp(mto.getHpInit());
			    	}
				}

			}

		}

		if(ret != 0){
			vo.setState(1);
			vo.setErrCode(ret);
		}else{
			MessageUtil.notiyWuNaiChange(player);
			MessageUtil.notiyWuZhengChange(player);
		}

		send(MProtrol.toStringProtrol(MProtrol.WU_NAI), vo, user);
	}

	
}