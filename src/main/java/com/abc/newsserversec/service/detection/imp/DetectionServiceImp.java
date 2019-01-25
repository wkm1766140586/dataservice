package com.abc.newsserversec.service.detection.imp;

import com.abc.newsserversec.mapper.detection.DetectionMapper;
import com.abc.newsserversec.service.detection.DetectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Map;

@Service
public class DetectionServiceImp implements DetectionService{

    @Autowired
    private DetectionMapper detectionMapper;

    @Override
    public int insertDetectionInfo(Map<String, Object> map) {
        return detectionMapper.insertDetectionInfo(map);
    }

    @Override
    public int insertDetectionDetailInfo(Map<String, Object> map) {
        return detectionMapper.insertDetectionDetailInfo(map);
    }

    @Override
    public ArrayList<Map<String, Object>> selectDetectionInfoByMap(Map<String, Object> map) {
        return detectionMapper.selectDetectionInfoByMap(map);
    }

    @Override
    public int selectDetectionInfoCountByMap(Map<String, Object> map) {
        return detectionMapper.selectDetectionInfoCountByMap(map);
    }

    @Override
    public Map<String, Object> selectDetectionInfoById(String id) {
        return detectionMapper.selectDetectionInfoById(id);
    }
}
