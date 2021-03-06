package com.abc.newsserversec.service.detection;

import java.util.ArrayList;
import java.util.Map;

/**
 * 承检机构
 */
public interface DetectionService {
    int insertDetectionInfo(Map<String,Object> map);

    int insertDetectionDetailInfo(Map<String,Object> map);

    /*根据关键词查承检机构*/
    ArrayList<Map<String,Object>> selectDetectionInfoByMap(Map<String,Object> map);

    /*根据关键词查承检机构数量*/
    int selectDetectionInfoCountByMap(Map<String,Object> map);

    /*通过ID 获取基本信息*/
    Map<String,Object> selectDetectionInfoById(String id);
}