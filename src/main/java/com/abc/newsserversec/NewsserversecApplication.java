package com.abc.newsserversec;


import com.abc.newsserversec.common.StaticVariable;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.springframework.boot.autoconfigure.*;
import org.springframework.stereotype.*;
import org.springframework.web.bind.annotation.*;
import java.io.*;


@SpringBootApplication
@Controller
@MapperScan("com.abc.newsserversec.mapper")
@EnableAutoConfiguration

public class NewsserversecApplication {

    @RequestMapping("/yixiecha")
    @ResponseBody
    String home() {
        return "Hello! This is YiXieCha API service.";
    }

    public static String loadEsRequestFile(String file_name)
            throws IOException
    {
        ClassLoader classLoader = NewsserversecApplication.class.getClassLoader();
        InputStream inputStream = classLoader.getResourceAsStream(file_name);
        BufferedReader reader = null;
        StringBuilder strBuilder = new StringBuilder();
        try {
            reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while((line=reader.readLine())!=null){
                strBuilder.append(line);
            }
            String esRequest = strBuilder.toString();
            return esRequest;
        }finally {
            if(reader!=null)reader.close();
        }
    }

    public static void main(String[] args) throws IOException {
        StaticVariable.esRequest = loadEsRequestFile("es_query_template.json");
        StaticVariable.esCount = loadEsRequestFile("es_query_count.json");
        StaticVariable.acquisitebidChart = loadEsRequestFile("es_query_acquisitebid_chart.json");
        SpringApplication.run(NewsserversecApplication.class, args);
    }
}
