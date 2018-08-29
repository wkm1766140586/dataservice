package com.abc.newsserversec.service.wechat.imp;

import com.abc.newsserversec.mapper.wechat.WxcardInfoMapper;
import com.abc.newsserversec.model.wechat.WxspUserInfo;
import com.abc.newsserversec.service.wechat.WxcardInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Map;
@Service
public class WxcardInfoServiceImp implements WxcardInfoService{
    @Autowired
    private WxcardInfoMapper wxcardInfoMapper;
    @Override
    public WxspUserInfo selectUserByOpenid(String openid) {
        return wxcardInfoMapper.selectUserByOpenid(openid);
    }

    @Override
    public int insertUserByMap(Map<String, String> map) {
        return wxcardInfoMapper.insertUserByMap(map);
    }

    @Override
    public ArrayList<Map<String, Object>> selectAllRegion(int pid) {
        return wxcardInfoMapper.selectAllRegion(pid);
    }

    @Override
    public Map<String, Object> selectRegionById(int id) {
        return wxcardInfoMapper.selectRegionById(id);
    }
}
