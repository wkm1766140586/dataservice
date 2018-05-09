package com.abc.newsserversec.common;

public class StaticVariable {

    public static String esRequest = "";

    public static String esCount = "";

    public static String searchProductIncludeFields = "\"id\",\"product_name_ch\",\"maker_name_ch\",\"agent\",\"src_loc\",\"main_class\",\"class_code\",\"product_state\",\"product_mode\",\"register_code\"";

    public static String searchExcludeFields = "\"file_id\",\"type\",\"id\",\"record_number\",\"data_source_code\",\"record_main_type\",\"record_sub_type\"," +
            "\"keep_field_first\",\"keep_field_second\",\"keep_field_third\",\"manage_type\",\"import_date\",\"path\",\"host\",\"@version\",\"@timestamp\"";

    public static String searchProductExcludeFields = "\"diseaseFlags\",\"deptFlags\",\"product_name_agg\",\"company_name_agg\"";

    public static String searchCompanyExcludeFields = "\"administer_level\",\"company_name_agg\"";

    public static String searchTenderbidExcludeFields = "\"path\",\"@timestamp\",\"month\",\"file_id\",\"@version\",\"host\",\"id\",\"type\"";

    public static String productAggsProductName = "{ \"tags\":{ \"terms\":{ \"field\":\"product_name_agg\" }, \"aggs\":{ \"company_name\":{ \"terms\":{ \"field\":\"company_name_agg\" } }," +
            "\"class_code\":{ \"terms\":{ \"field\":\"class_code\" } }, \"max_date\":{ \"max\":{ \"field\":\"approval_date\" } }, \"min_date\":{ \"min\":{ \"field\":\"approval_date\" } } } } }";

    public static String productAggsCompanyName = "{ \"tags\":{ \"terms\":{ \"field\":\"company_name_agg\" }, \"aggs\":{ \"max_date\":{ \"max\":{ \"field\":\"approval_date\" } }," +
            "\"min_date\":{ \"min\":{ \"field\":\"approval_date\" } } } } }";

    public static String productAggsClassCode = "{ \"tags\":{ \"terms\":{ \"field\":\"class_code\" }, \"aggs\":{ \"max_date\":{ \"max\":{ \"field\":\"approval_date\" } }," +
            "\"min_date\":{ \"min\":{ \"field\":\"approval_date\" } } } } }";

    public static String companyAggsCompanyName = "{ \"tags\":{ \"terms\":{ \"field\":\"company_name_agg\" } } }";
}
