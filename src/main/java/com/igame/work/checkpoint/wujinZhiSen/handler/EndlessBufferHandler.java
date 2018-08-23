package com.igame.work.checkpoint.wujinZhiSen.handler;







import net.sf.json.JSONObject;

import com.igame.core.ErrorCode;
import com.igame.core.MProtrol;
import com.igame.core.MessageUtil;
import com.igame.core.SessionManager;
import com.igame.core.handler.BaseHandler;
import com.igame.core.handler.RetVO;
import com.igame.util.MyUtil;
import com.igame.work.monster.dto.WuEffect;
import com.igame.work.user.dto.Player;
import com.igame.work.user.load.ResourceService;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.data.ISFSObject;

/**
 * 
 * @author Marcus.Z
 *
 */
public class EndlessBufferHandler extends BaseHandler{
	

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
		String buffer = jsonObject.getString("buffer");
		String[] buffers = buffer.split(",");
		if(player.getWuEffect().size() == 0 || !MyUtil.vlaidString(buffer, "101,105,127,107")){
			ret = ErrorCode.ERROR;
		}else{
			if(buffers.length != player.getWuEffect().size()){
				ret = ErrorCode.WUBUFFER_ERROR;
			}else{
				if(player.getDiamond() < 20){
					ret = ErrorCode.DIAMOND_NOT_ENOUGH;
				}else{
					ResourceService.ins().addDiamond(player, -20);
					player.getWuEffect().clear();
					for(String bu : buffers){
						player.getWuEffect().add(new WuEffect(Integer.parseInt(bu)));
					}
				}

			}
		}

		if(ret != 0){
			vo.setState(1);
			vo.setErrCode(ret);
		}else{
			MessageUtil.notiyWuBufferChange(player,player.getWuEffect());
		}

		send(MProtrol.toStringProtrol(MProtrol.WU_BUFFER), vo, user);
	}

	
}