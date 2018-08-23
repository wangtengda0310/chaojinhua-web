package com.igame.work.checkpoint.xinmo;



import com.igame.core.ErrorCode;
import com.igame.core.MProtrol;
import com.igame.core.SessionManager;
import com.igame.core.handler.BaseHandler;
import com.igame.core.handler.RetVO;
import com.igame.work.user.dto.Player;
import com.igame.work.user.dto.RobotDto;
import com.igame.work.user.service.RobotService;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import net.sf.json.JSONObject;

import java.util.Map;

/**
 * 
 * @author Marcus.Z
 *
 */
public class XinmoEnterHandler extends BaseHandler{
	

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
		int chapterId = jsonObject.getInt("chapterId");
		if(player.getXinMo().get(chapterId) == null){
			ret = ErrorCode.ERROR;
		}else if(player.getXinMo().get(chapterId) != null && player.getXinMo().get(chapterId).calLeftTime(System.currentTimeMillis()) <= 0){
			ret = ErrorCode.XINGMO_LEAVEL;	
		}else{
			Map<String,RobotDto> ro = RobotService.ins().getRobot().get(player.getSeverId());
			if(ro == null || ro.get(player.getXinMo().get(chapterId).getMid()) == null){
				ret = ErrorCode.ERROR;
			}			
		}
		
		if(ret != 0){
			vo.setState(1);
			vo.setErrCode(ret);
		}
		vo.addData("chapterId", chapterId);

		send(MProtrol.toStringProtrol(MProtrol.XINGMO_ENTER), vo, user);
	}

	
}