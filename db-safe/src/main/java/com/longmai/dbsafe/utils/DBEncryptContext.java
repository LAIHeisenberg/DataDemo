package com.longmai.dbsafe.utils;

import com.alibaba.druid.util.StringUtils;
import com.longmai.datakeeper.rest.dto.DBEncryptDto;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;

public class DBEncryptContext {

    private static DBEncryptDto dbEncryptDto;

    public static void put(DBEncryptDto encryptDto){
        dbEncryptDto = encryptDto;
    }

    public static List<DBEncryptDto.EncryptColumnDto> listColumn(String tableName){
        Map<String, List<DBEncryptDto.EncryptColumnDto>> encryptTableColumnMap = dbEncryptDto.getEncryptTableColumnMap();
        List<DBEncryptDto.EncryptColumnDto> encryptColumnDtos = encryptTableColumnMap.get(tableName);
        return encryptColumnDtos == null? Collections.emptyList() :  encryptColumnDtos;
    }

    public static DBEncryptDto.EncryptColumnDto getEncryptColumn(String tableName, String column){
        if (StringUtils.isEmpty(tableName) || StringUtils.isEmpty(column)){
            throw new IllegalArgumentException("tableName 和 column 不能为空");
        }
        Map<String, List<DBEncryptDto.EncryptColumnDto>> map = dbEncryptDto.getEncryptTableColumnMap();
        List<DBEncryptDto.EncryptColumnDto> encryptColumnDtos = map.get(tableName);
        Optional<DBEncryptDto.EncryptColumnDto> first = encryptColumnDtos.stream().filter(new Predicate<DBEncryptDto.EncryptColumnDto>() {
            @Override
            public boolean test(DBEncryptDto.EncryptColumnDto encryptColumnDto) {
                return column.equals(encryptColumnDto.getColumnName().trim());
            }
        }).findFirst();
        if (first.isPresent()){
            return first.get();
        }else {
            return null;
        }
    }
}
