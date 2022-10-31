package com.longmai.dbsafe.utils;

import com.alibaba.druid.util.StringUtils;
import com.longmai.datakeeper.rest.dto.DBEncryptDto;

import java.util.*;
import java.util.function.Predicate;

public class DBEncryptContext {

    private static DBEncryptDto dbEncryptDto = new DBEncryptDto(null);

    public static void put(DBEncryptDto encryptDto){
        if (Objects.isNull(encryptDto)){
            return;
        }
        dbEncryptDto = encryptDto;
    }

    public static List<DBEncryptDto.EncryptColumnDto> listColumn(String tableName){
        if (dbEncryptDto == null){
            return Collections.emptyList();
        }
        Map<String, List<DBEncryptDto.EncryptColumnDto>> encryptTableColumnMap = dbEncryptDto.getEncryptTableColumnMap();
        List<DBEncryptDto.EncryptColumnDto> encryptColumnDtos = encryptTableColumnMap.get(tableName.toUpperCase());
        return encryptColumnDtos == null? Collections.emptyList() :  encryptColumnDtos;
    }

    public static DBEncryptDto.EncryptColumnDto getEncryptColumn(String tableName, String column){
        if (StringUtils.isEmpty(tableName) || StringUtils.isEmpty(column)){
            throw new IllegalArgumentException("tableName 和 column 不能为空");
        }
        Map<String, List<DBEncryptDto.EncryptColumnDto>> map = dbEncryptDto.getEncryptTableColumnMap();
        List<DBEncryptDto.EncryptColumnDto> encryptColumnDtos = map.get(tableName.toUpperCase());
        Optional<DBEncryptDto.EncryptColumnDto> first = Optional.empty();
        if (Objects.nonNull(encryptColumnDtos)){
            first = encryptColumnDtos.stream().filter(new Predicate<DBEncryptDto.EncryptColumnDto>() {
                @Override
                public boolean test(DBEncryptDto.EncryptColumnDto encryptColumnDto) {
                    return column.toUpperCase().equals(encryptColumnDto.getColumnName().toUpperCase().trim());
                }
            }).findFirst();
        }

        if (first.isPresent()){
            return first.get();
        }else {
            return null;
        }
    }
}
