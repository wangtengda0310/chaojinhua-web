package com.igame.work.activity;

import com.igame.work.activity.meiriLiangfa.MeiriLiangfaData;
import com.igame.work.activity.sign.SignData;
import com.igame.work.user.dto.Player;
import org.mongodb.morphia.annotations.Entity;

@Entity(value = "activityData", noClassnameStored = true)
public class PlayerActivityData {
    private SignData signData;
    private MeiriLiangfaData meiriLiangfaData;

    public PlayerActivityData() {

    }
    public PlayerActivityData(Player player) {
            signData = new SignData(player);
            meiriLiangfaData = new MeiriLiangfaData(player);
        }

        public SignData getSignData () {
            return signData;
        }

        public void setSignData (SignData signData){
            this.signData = signData;
        }

        public MeiriLiangfaData getMeiriLiangfaData () {
            return meiriLiangfaData;
        }

        public void setMeiriLiangfaData (MeiriLiangfaData meiriLiangfaData){
            this.meiriLiangfaData = meiriLiangfaData;
        }
    }