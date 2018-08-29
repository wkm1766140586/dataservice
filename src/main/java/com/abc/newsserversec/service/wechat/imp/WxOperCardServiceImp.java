package com.abc.newsserversec.service.wechat.imp;

import com.abc.newsserversec.mapper.wechat.WxOperCardMapper;
import com.abc.newsserversec.model.wechat.WxOperCard;
import com.abc.newsserversec.service.wechat.WxOperCardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Map;

@Service
public class WxOperCardServiceImp implements WxOperCardService {
    @Autowired
    private WxOperCardMapper wxViewCardMapper;
    @Override
    public ArrayList<WxOperCard> selectOperCardsById(Map<String, Object> map) {
        return wxViewCardMapper.selectOperCardsById(map);
    }

    @Override
    public int selectCountById(Map<String, Object> map) {
        return wxViewCardMapper.selectCountById(map);
    }

    @Override
    public int selectDistinctCountById(Map<String, Object> map) {
        return wxViewCardMapper.selectDistinctCountById(map);
    }

    @Override
    public int insertWxOperCardByMap(Map<String, Object> map) {
        return wxViewCardMapper.insertWxOperCardByMap(map);
    }

    @Override
    public int deleteWxOperCardByMap(Map<String, Object> map) {
        return wxViewCardMapper.deleteWxOperCardByMap(map);
    }

    @Override
    public int updateAllID(Map<String, Object> map){
        return wxViewCardMapper.updateAllID(map);
    }

    @Override
    public int updateWxOperCardByMap(Map<String, Object> map) {
        return wxViewCardMapper.updateWxOperCardByMap(map);
    }
}
