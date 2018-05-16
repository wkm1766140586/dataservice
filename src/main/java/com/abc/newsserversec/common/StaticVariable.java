package com.abc.newsserversec.common;

public class StaticVariable {

    public static String esRequest = "";

    public static String esCount = "";

    public static String ExcludeFields = "\"file_id\",\"type\",\"path\",\"@version\",\"host\",\"@timestamp\",\"id\"";

    public static String searchProductIncludeFields = "\"id\",\"product_name_ch\",\"maker_name_ch\",\"agent\",\"src_loc\",\"main_class\",\"class_code\",\"product_state\",\"product_mode\",\"register_code\"";

    public static String searchProAndComExcludeFields = "\"record_number\",\"data_source_code\",\"record_main_type\",\"record_sub_type\"," +
            "\"keep_field_first\",\"keep_field_second\",\"keep_field_third\",\"manage_type\",\"import_date\"";

    public static String searchProductExcludeFields = "\"diseaseFlags\",\"deptFlags\",\"product_name_agg\",\"company_name_agg\"";

    public static String searchCompanyExcludeFields = "\"administer_level\",\"company_name_agg\",\"quality_manage_person\",\"web_manage_person\"," +
            "\"product_info\",\"service_comprehensive_info\",\"appro_date_start\",\"current_state\",\"change_content\",\"change_date\",\"approval_form\"," +
            "\"change_project\",\"cellphone\",\"facsimile\",\"economic_type\",\"province\",\"area\",\"county\",\"region\",\"social_credit_code\"," +
            "\"others\",\"remarks\",\"attachment\",\"apply_date\",\"accept_date\",\"manage_type\"";

    public static String searchTenderbidExcludeFields = "\"month\"";

    public static String productAggsProductName = "{ \"tags\":{ \"terms\":{ \"field\":\"product_name_agg\" }, \"aggs\":{ \"company_name\":{ \"terms\":{ \"field\":\"company_name_agg\" } }," +
            "\"class_code\":{ \"terms\":{ \"field\":\"class_code\" } }, \"max_date\":{ \"max\":{ \"field\":\"approval_date\" } }, \"min_date\":{ \"min\":{ \"field\":\"approval_date\" } } } } }";

    public static String productAggsCompanyName = "{ \"tags\":{ \"terms\":{ \"field\":\"company_name_agg\" }, \"aggs\":{ \"max_date\":{ \"max\":{ \"field\":\"approval_date\" } }," +
            "\"min_date\":{ \"min\":{ \"field\":\"approval_date\" } } } } }";

    public static String productAggsClassCode = "{ \"tags\":{ \"terms\":{ \"field\":\"class_code\" }, \"aggs\":{ \"max_date\":{ \"max\":{ \"field\":\"approval_date\" } }," +
            "\"min_date\":{ \"min\":{ \"field\":\"approval_date\" } } } } }";

}
