package com.igame.work.vip;

import com.igame.core.di.Inject;
import com.igame.core.handler.ReconnectedHandler;
import com.igame.core.handler.RetVO;
import com.igame.util.MyUtil;
import com.igame.work.ErrorCode;
import com.igame.work.MProtrol;
import com.igame.work.gm.service.GMService;
import com.igame.work.user.dto.Player;
import com.igame.work.user.load.ResourceService;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import net.sf.json.JSONObject;

import static com.igame.work.vip.VIPConstants.KEY_FIRST_PACK;

/**
 * @author xym
 *
 * 会员购买礼包
 */
public class VipBuyFristPackHandler extends ReconnectedHandler {
    @Inject private ResourceService resourceService;
    @Inject private GMService gmService;
    @Inject private VIPService vipService;

    @Override
    protected RetVO handleClientRequest(Player player, ISFSObject params) {

        RetVO vo = new RetVO();

        String infor = params.getUtfString("infor");
        JSONObject jsonObject = JSONObject.fromObject(infor);

        int vipLv = jsonObject.getInt("vipLv");
        vo.addData("vipLv",vipLv);

        //校验vip等级
        int vip = player.getVip();
        if (vip < vipLv){
            return error(ErrorCode.VIP_LV_LACK);
        }

        //校验是否已购买
        String s = (String) player.getVipPrivileges().get(KEY_FIRST_PACK);
        if (!s.contains(String.valueOf(vipLv))){
            return error(ErrorCode.PACK_PURCHASED);
        }

        VipPackTemplate template = vipService.vipPackData.getTemplate(vipLv);
        if (template == null){
            return error(ErrorCode.PARAMS_INVALID);
        }

        //校验钻石
        int gem = template.getGem();
        if (player.getDiamond() < gem){
            return error(ErrorCode.DIAMOND_NOT_ENOUGH);
        }

        //扣除钻石
        resourceService.addDiamond(player,-gem);

        //发放礼包
        String firstPack = template.getFirstPack();
        gmService.processGM(player,firstPack);

        //标记已购买
        if (MyUtil.isNullOrEmpty(s)){
            s = String.valueOf(vipLv);
        }else {
            s = s+","+String.valueOf(vipLv);
        }
        player.getVipPrivileges().put(KEY_FIRST_PACK,s);

        vo.addData("checkReward",firstPack);
        return vo;
    }

    @Override
    public int protocolId() {
        return MProtrol.VIP_FRIST_PACK;
    }

}