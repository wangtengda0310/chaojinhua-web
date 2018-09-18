package com.igame.work.monster.handler;


import com.google.common.collect.Lists;
import com.igame.core.di.Inject;
import com.igame.core.handler.ReconnectedHandler;
import com.igame.core.handler.RetVO;
import com.igame.work.ErrorCode;
import com.igame.work.MProtrol;
import com.igame.work.fight.dto.MatchMonsterDto;
import com.igame.work.monster.data.StrengthenmonsterTemplate;
import com.igame.work.monster.dto.Monster;
import com.igame.work.monster.service.MonsterService;
import com.igame.work.user.dto.Player;
import com.igame.work.user.dto.TongHuaDto;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import net.sf.json.JSONObject;

import java.util.List;
import java.util.Map;

/**
 * 
 * @author Marcus.Z
 *
 */
public class TongHuaEnterFightHandler extends ReconnectedHandler {
	@Inject
	private MonsterService monsterService;
	

	@Override
	protected RetVO handleClientRequest(Player player, ISFSObject params) {
		RetVO vo = new RetVO();

		String infor = params.getUtfString("infor");
		JSONObject jsonObject = JSONObject.fromObject(infor);

		int index = jsonObject.getInt("index");
		List<MatchMonsterDto> lb = Lists.newArrayList();
		/*
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
			return error(ErrorCode.ERROR);
		}else{
			String[] tss = tdo.getTongStr().split(";");
			if(index< 1 || index>tss.length){
				return error(ErrorCode.ERROR);
			}else{
				String[] t = tss[index-1].split(",");//"type,state,rewardtype,rewardId,count"
				if(!"1".equals(t[0]) || "-1".equals(t[1]) || "0".equals(t[1]) || "3".equals(t[1])){
					return error(ErrorCode.ERROR);
				}else{
					if(tdo.getTimeIndex() == index){//倒计时
						if(tdo.calLeftTime() > 0){
							return error(ErrorCode.ERROR);
						}else{
							t[1] = "2";
							tdo.setStartTime(0);
							tdo.setTimeCount(0);
							tdo.setTimeIndex(0);
						}						
					}

					StrengthenmonsterTemplate mt = monsterService.strengthenmonsterData.getTemplate(Integer.parseInt(t[3]));

					// todo extract method
					Map<Long, Monster> monster = monsterService.createMonster(String.valueOf(mt.getMonster_id()), String.valueOf(mt.getMonster_lv()), "1","","");
					monster.forEach((mid, m) -> {
						int i = mid.intValue();
						MatchMonsterDto mto = new MatchMonsterDto(m, i);
						mto.reCalGods(player.callFightGods(), null);
						lb.add(mto);
					});


				}
			}
		}

		vo.addData("index", index);
		vo.addData("m", lb);
		
		return vo;
	}

	@Override
    public int protocolId() {
		return MProtrol.TONGHUA_ENTER_FIGHT;
	}
}
