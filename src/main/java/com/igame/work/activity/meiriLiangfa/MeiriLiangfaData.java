package com.igame.work.activity.meiriLiangfa;

import com.igame.util.DateUtil;
import com.igame.work.activity.Activities;
import com.igame.work.activity.ActivityConfigTemplate;
import com.igame.work.gm.service.GMService;
import com.igame.work.user.dto.Player;
import net.sf.json.JSONObject;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Transient;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;

@Entity(noClassnameStored = true)
public class MeiriLiangfaData implements Activities {
    @Transient
    private static List<ActivityConfigTemplate> configs = new ArrayList<>();
    public static void addActivityConfigTemplate(ActivityConfigTemplate template) {
        if ("1".equals(template.getActivity_show())) {
            configs.add(template);
        }
    }
    @Transient
    Player player;

    private String record;

    public MeiriLiangfaData() {

    }
    public MeiriLiangfaData(Player player) {
        this.player = player;
    }

    public String getRecord() {
        return record;
    }

    public void setRecord(String record) {
        this.record = record;
    }

    public String receive(int index) {
        configs.stream().filter(template -> {
            String configTime = template.getGet_value();
            String[] split = configTime.split(",");
            if(split.length<2){return false;}
            int begin = Integer.parseInt(split[0]);
            int end = Integer.parseInt(split[1]);
            int current = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
            return current>=begin&&current<end;
        }).findAny().ifPresent(template -> {
            if (record == null) {
                record = "0,0";
            }
            String[] split = record.split(",");
            String today = DateUtil.formatToday();
            if (today.equals(split[index - 1])) {
                return;
            }
            if(index==1) {
                record = today + "," + split[1];
            } else {
                record = split[0] + "," + today;
            }
            GMService.processGM(player, template.getActivity_drop());
        });
        return record;
    }

    @Override
    public int getType() {
        return 1004;
    }

    @Override
    public JSONObject toClientData() {
        JSONObject object = new JSONObject();
        String result = configs.stream()
                .map(template -> template.getGet_value()+","+template.getActivity_drop()+","+true)
                .collect(Collectors.joining(";"));
        object.put("type", getType());
        object.put("d",result);
        return object;
    }

    @Override
    public String loadConfig() {
        return null;
    }
}