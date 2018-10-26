package com.abc.newsserversec.service.wechat.imp;

import com.abc.newsserversec.mapper.wechat.ExhibitionMapper;
import com.abc.newsserversec.service.wechat.ExhibitionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Map;

@Service
public class ExhibitionServiceImpl implements ExhibitionService {
    @Autowired
    private ExhibitionMapper exhibitionMapper;
    @Override
    public ArrayList<Map<String, Object>> selectExhibition(Map<String,Object> map) {
        return exhibitionMapper.selectExhibition(map);
    }

    @Override
    public int selectCountByName(Map<String, Object> map) {
        return exhibitionMapper.selectCountByName(map);
    }

    @Override
    public Map<String, Object> selectExhibitionByName(String name) {
        return exhibitionMapper.selectExhibitionByName(name);
    }
}
