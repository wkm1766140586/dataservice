package com.abc.newsserversec.service.standard;



import com.abc.newsserversec.model.standard.StandardData;

import java.util.ArrayList;
import java.util.Map;

public interface StandardInfoService {

    //添加信息
    int insertStandardInfo(StandardData data);

    //更新信息
    int updateStandardInfo(Map<String,Object> map);

    //查询信息
    ArrayList<StandardData> selectStandardInfo(Map<String,Object> map);

    //计数
    int selectStandardInfoCountByCondition(Map<String,Object> map);

    //通过code找信息
    StandardData selectStandardInfoByCode(String code);

    /*首页查找最新的六条*/
    ArrayList<StandardData> selectRecentStandardInfo();
}
