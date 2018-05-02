package com.abc.newsserversec.common;

public class StaticVariable {

    public static String esRequest = "";

    public static String esCount = "";

    public static String searchProductIncludeFields = "\"id\",\"product_name_ch\",\"maker_name_ch\",\"agent\",\"src_loc\",\"main_class\",\"class_code\",\"product_state\",\"product_mode\"";

    public static String searchProductExcludeFields = "\"file_id\",\"type\",\"record_number\",\"data_source_code\",\"manage_type\",\"record_main_type\",\"record_sub_type\"," +
            "\"keep_field_first\",\"keep_field_second\",\"keep_field_third\",\"diseaseFlags\",\"deptFlags\",\"import_date\",\"path\",\"host\",\"@version\",\"@timestamp\"";

    public static String productAggsProductName = "{ \"tags\":{ \"terms\":{ \"field\":\"product_name_agg\" }, \"aggs\":{ \"company_name\":{ \"terms\":{ \"field\":\"company_name_agg\" } }," +
            "\"class_code\":{ \"terms\":{ \"field\":\"class_code\" } }, \"max_date\":{ \"max\":{ \"field\":\"approval_date\" } }, \"min_date\":{ \"min\":{ \"field\":\"approval_date\" } } } } }";

    public static String productAggsCompanyName = "{ \"tags\":{ \"terms\":{ \"field\":\"company_name_agg\" }, \"aggs\":{ \"max_date\":{ \"max\":{ \"field\":\"approval_date\" } }," +
            "\"min_date\":{ \"min\":{ \"field\":\"approval_date\" } } } } }";

    public static String productAggsClassCode = "{ \"tags\":{ \"terms\":{ \"field\":\"class_code\" }, \"aggs\":{ \"max_date\":{ \"max\":{ \"field\":\"approval_date\" } }," +
            "\"min_date\":{ \"min\":{ \"field\":\"approval_date\" } } } } }";
}
