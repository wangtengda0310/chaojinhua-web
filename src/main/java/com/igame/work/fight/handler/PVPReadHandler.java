package com.igame.work.fight.handler;


import com.igame.work.MProtrol;
import com.igame.core.handler.ReconnectedHandler;
import com.igame.core.handler.RetVO;
import com.igame.work.fight.service.PVPFightService;
import com.igame.work.user.dto.Player;
import com.smartfoxserver.v2.entities.data.ISFSObject;

/**
 * 
 * @author Marcus.Z
 *
 */
public class PVPReadHandler extends ReconnectedHandler {
	

	@Override
	protected RetVO handleClientRequest(Player player, ISFSObject params) {

//		String infor = params.getUtfString("infor");
//		JSONObject jsonObject = JSONObject.fromObject(infor);

		PVPFightService.ins().readFight(player);

		return new RetVO();
	}

	@Override
    public int protocolId() {
		return MProtrol.F_P_S;
	}

}
