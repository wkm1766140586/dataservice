package com.abc.newsserversec.mapper.company;

import com.abc.newsserversec.model.company.CompanyInfo;
import org.apache.ibatis.annotations.Mapper;

import java.util.ArrayList;
import java.util.Map;

@Mapper
public interface CompanyInfoMapper {

    int selectCompanyInfoCountByCondition(Map<String,Object> map);

    ArrayList<CompanyInfo> selectCompanyInfoByCondition(Map<String,Object> map);

}
