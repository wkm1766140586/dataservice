package com.abc.newsserversec.mapper.company;

import com.abc.newsserversec.model.company.CompanyInfo;
import com.abc.newsserversec.model.user.UserInfo;
import org.apache.ibatis.annotations.Mapper;

import java.util.ArrayList;
import java.util.Map;

@Mapper
public interface CompanyInfoMapper {

    int selectCompanyInfoCountByCondition(String company_name);

    ArrayList<CompanyInfo> selectCompanyInfoByCondition(Map<String,Object> map);

}
