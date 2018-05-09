package com.abc.newsserversec.service.company.imp;

import com.abc.newsserversec.mapper.company.CompanyInfoMapper;
import com.abc.newsserversec.model.company.CompanyInfo;
import com.abc.newsserversec.service.company.CompanyInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Map;

@Service
public class CompanyInfoServiceImp implements CompanyInfoService {

    @Autowired
    private CompanyInfoMapper companyInfoMapper;


    @Override
    public int selectCompanyInfoCountByCondition(String company_name) {
        return companyInfoMapper.selectCompanyInfoCountByCondition(company_name);
    }

    @Override
    public ArrayList<CompanyInfo> selectCompanyInfoByCondition(Map<String,Object> map) {
        return companyInfoMapper.selectCompanyInfoByCondition(map);
    }
}
