package com.example.iot.dao;

import com.example.iot.dao.Repository.DeviceManagementRepository;
import com.example.iot.vo.AddDeviceResponse;
import com.example.iot.vo.DeviceVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import com.example.iot.po.devices.*;

import java.util.ArrayList;
import java.util.List;

@Repository
public class DeviceManagementDao implements DeviceManagementRepository {
    int latesttime=0;
    ArrayList<device> runningdevices=new ArrayList<>();

    @Autowired
    private JdbcTemplate jdbcTemplate;


    @Override
    public AddDeviceResponse addDevice(String type, String owner) {
        AddDeviceResponse adr=new AddDeviceResponse();
        int id=jdbcTemplate.update("insert into device (`type`,`userId`) values (?,?)",type,owner);
        adr.setId(String.valueOf(id));
        adr.setTypeid(type);
        return adr;
    }

    @Override
    public boolean deleteDevice(String deviceId) {
        jdbcTemplate.update("delete from device deviceid=\""+ deviceId + "\"");
        return true;
    }

    @Override
    public boolean operateDevice(String time, String code, String deviceId) {
        int now=Integer.parseInt(time);
        int gap=now-latesttime;         //上次操作距这次操作的时间，用于决定设备运行的效果
        int flag=0;       //默认运行设备列表中无当前操作设备
        for (com.example.iot.po.devices.device device : runningdevices) {
            device.update(gap);
        }
        for(com.example.iot.po.devices.device device : runningdevices){
            if(device.getId()==Integer.parseInt(deviceId)){
                device.setState(Integer.parseInt(code));
                flag=1;
            }
        }
        if(flag==0){
            device d1=new device();
            String type=jdbcTemplate.queryForObject("select type from device where id= ?",String.class,deviceId);
            assert type != null;
            if(type.startsWith("A")){
                    d1=new AirConditioner(code,deviceId);
                }

                else if(type.startsWith("L")) {
                    d1 = new Light(code, deviceId);
                }
                else if(type.startsWith("C")) {
                    d1 = new Curtain(code, deviceId);
                }
                else if(type.startsWith("H")) {
                    d1 = new Humidifier(code, deviceId);
                }
                else if(type.startsWith("T")) {
                    d1 = new TV(code, deviceId);
                }
                else if(type.startsWith("B")) {
                    d1 = new Box(code, deviceId);
            }
            runningdevices.add(d1);
        }
        int userid=Integer.parseInt(jdbcTemplate.queryForObject("select userId from device where id=?",String.class,deviceId));
        String temperature=jdbcTemplate.queryForObject("select temperature from environment where userid=?",String.class,userid);
        String humidity=jdbcTemplate.queryForObject("select humidity from environment where userid=?",String.class,userid);
        jdbcTemplate.update("insert into operation (deviceid,`time`,code,temperature,humidity)value (?,?,?,?,?)",deviceId,time,code,temperature,humidity);
        return true;
    }
}
