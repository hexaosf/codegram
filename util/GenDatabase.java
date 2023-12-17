package org.electronic.electronicdocumentsystemjava.util;

import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.OutputFile;
import com.baomidou.mybatisplus.generator.config.rules.DbColumnType;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;

import java.sql.Types;
import java.util.Collections;

public class GenDatabase {
    public static final String DB_URL = "jdbc:mysql://localhost:3306/electronic_document_system?useUnicode=true&characterEncoding=utf8";
    public static final String DB_USERNAME = "root";
    public static final String DB_PASSWORD = "root";
    public static final String DRIVER_NAME = "com.mysql.cj.jdbc.Driver";
    public static final String OUTPUT_DIR = "D:\\project\\Work\\ElectronicDocumentSystemJava\\src\\main\\java";
    public static final String XML_OUTPUT_DIR = "D:\\project\\Work\\ElectronicDocumentSystemJava\\src\\main\\resources\\xml";
    public static final String PACKAGE_NAME = "org.electronic.electronicdocumentsystemjava";

    public static void main(String[] args) {
        FastAutoGenerator.create(DB_URL, DB_USERNAME, DB_PASSWORD)
                .globalConfig(builder -> {
                    builder.enableSwagger() // 开启 swagger 模式
                            .outputDir(OUTPUT_DIR); // 指定输出目录
                })
                .dataSourceConfig(builder -> builder.typeConvertHandler((globalConfig, typeRegistry, metaInfo) -> {
                    int typeCode = metaInfo.getJdbcType().TYPE_CODE;
                    if (typeCode == Types.SMALLINT) {
                        // 自定义类型转换
                        return DbColumnType.INTEGER;
                    }
                    return typeRegistry.getColumnType(metaInfo);

                }))
                .packageConfig(builder -> {
                    builder.parent(PACKAGE_NAME) // 设置父包名
                            .moduleName("system") // 设置父包模块名
                            .pathInfo(Collections.singletonMap(OutputFile.xml, XML_OUTPUT_DIR)); // 设置mapperXml生成路径
                })
                .strategyConfig(builder -> {
                    builder.addInclude("user") // 设置需要生成的表名
                            .addTablePrefix("t_", "c_"); // 设置过滤表前缀
                })
                .templateEngine(new FreemarkerTemplateEngine()) // 使用Freemarker引擎模板，默认的是Velocity引擎模板
                .execute();
    }
}
