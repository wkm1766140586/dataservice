package com.abc.newsserversec.service.standard.imp;

import com.abc.newsserversec.mapper.standard.StandardInfoMapper;
import com.abc.newsserversec.model.standard.StandardData;
import com.abc.newsserversec.service.standard.StandardInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Map;

@Service
public class StandardInfoServiceImp implements StandardInfoService {

    @Autowired
    private StandardInfoMapper standardInfoMapper;


    @Override
    public int insertStandardInfo(StandardData data) {
        return standardInfoMapper.insertStandardInfo(data);
    }

    @Override
    public int updateStandardInfo(Map<String, Object> map) {
        return standardInfoMapper.updateStandardInfo(map);
    }

    @Override
    public ArrayList<StandardData> selectStandardInfo(Map<String, Object> map) {
        return standardInfoMapper.selectStandardInfo(map);
    }

    @Override
    public int selectStandardInfoCountByCondition(Map<String, Object> map) {
        return standardInfoMapper.selectStandardInfoCountByCondition(map);
    }

    @Override
    public StandardData selectStandardInfoByCode(String code) {
        return standardInfoMapper.selectStandardInfoByCode(code);
    }

    @Override
    public ArrayList<StandardData> selectRecentStandardInfo() {
        return standardInfoMapper.selectRecentStandardInfo();
    }
}
