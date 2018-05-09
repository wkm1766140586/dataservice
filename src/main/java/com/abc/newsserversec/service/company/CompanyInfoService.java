package com.abc.newsserversec.service.company;


import com.abc.newsserversec.model.company.CompanyInfo;
import com.abc.newsserversec.model.user.UserInfo;

import java.util.ArrayList;
import java.util.Map;

public interface CompanyInfoService {

    //根据条件查询企业数量
    int selectCompanyInfoCountByCondition(String company_name);

    //根据条件查询企业列表信息
    ArrayList<CompanyInfo> selectCompanyInfoByCondition(Map<String,Object> map);


}
