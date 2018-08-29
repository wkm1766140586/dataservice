package com.abc.newsserversec.service.wechat;

import com.abc.newsserversec.model.wechat.WxOperCard;
import java.util.ArrayList;
import java.util.Map;

public interface WxOperCardService {
    ArrayList<WxOperCard> selectOperCardsById(Map<String, Object> map);

    int selectCountById(Map<String, Object> map);//不去重复的数量

    int selectDistinctCountById(Map<String, Object> map);//不去重复的数量

    int insertWxOperCardByMap(Map<String, Object> map);

    int deleteWxOperCardByMap(Map<String, Object> map);

    int updateAllID(Map<String, Object> map);

    int updateWxOperCardByMap(Map<String, Object> map);
}
