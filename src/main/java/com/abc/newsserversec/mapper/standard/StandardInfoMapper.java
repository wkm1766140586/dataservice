package com.abc.newsserversec.mapper.standard;

import com.abc.newsserversec.model.company.CompanyInfo;
import com.abc.newsserversec.model.standard.StandardData;
import org.apache.ibatis.annotations.Mapper;

import java.util.ArrayList;
import java.util.Map;

@Mapper
public interface StandardInfoMapper {

    int insertStandardInfo(StandardData data);

    int updateStandardInfo(Map<String,Object> map);

    ArrayList<StandardData> selectStandardInfo(Map<String,Object> map);

    int selectStandardInfoCountByCondition(Map<String,Object> map);

    StandardData selectStandardInfoByCode(String code);

    ArrayList<StandardData> selectRecentStandardInfo();
}
