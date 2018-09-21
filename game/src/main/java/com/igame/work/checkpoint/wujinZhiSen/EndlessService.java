package com.igame.work.checkpoint.wujinZhiSen;

import com.google.common.collect.Lists;
import com.igame.core.di.LoadXml;
import com.igame.util.GameMath;
import com.igame.util.MyUtil;
import com.igame.work.ErrorCode;
import com.igame.work.fight.dto.GodsDto;
import com.igame.work.user.dto.Player;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class EndlessService {
    /**
     * 无尽之森
     */
    @LoadXml("endlessdata.xml")public EndlessData endlessData;
    public Map<Long, Integer> tempBufferId = new ConcurrentHashMap<>();//临时ID
    /**
     * 无尽之森刷新
     */
    public int refEndlessRef(Player player){

        int ret = 0;
        int lv = player.getPlayerLevel();
        if(lv < 21){
            lv = 21;
        }
        List<EndlessdataTemplate> ls = Lists.newArrayList();
        for(EndlessdataTemplate et : endlessData.getAll()){
            if(lv >= Integer.parseInt(et.getLvRange().split(",")[0]) && lv <= Integer.parseInt(et.getLvRange().split(",")[1])){
                ls.add(et);
            }
        }
        if(ls.isEmpty()){
            ret = ErrorCode.ERROR;
        }else{
            player.getWuMap().clear();
            player.getWuZheng().clear();
            player.setWuGods(new GodsDto());
            player.setWuNai(0);
            player.getWuEffect().clear();
            for(EndlessdataTemplate et : ls){
                String str = String.valueOf(et.getNum());
                str+=";"+String.valueOf(et.getDifficulty())+";0";
                String[] mons = et.getMonsterId().split(",");
                List<String> temp = Lists.newArrayList();
                List<Integer> lvs = Lists.newArrayList();
                for(int i = 1;i <=5;i++){
                    temp.add(mons[GameMath.getRandInt(mons.length)]);
                    lvs.add(GameMath.getRandomInt(lv+Integer.parseInt(et.getMonsterLv().split(",")[0]), lv+Integer.parseInt(et.getMonsterLv().split(",")[1])));
                }
                str += ";"+ MyUtil.toString(temp, ",");
                str += ";"+MyUtil.toStringInt(lvs, ",");
                str += ";0;0";
                player.getWuMap().put(et.getNum(), str);
            }

        }
        return ret;
    }


    public void afterPlayerLogin(Player player) {
        if(player.getWuMap().isEmpty()){
            refEndlessRef(player);
        }
    }
}