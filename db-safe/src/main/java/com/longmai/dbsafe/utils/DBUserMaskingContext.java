package com.longmai.dbsafe.utils;

import com.alibaba.druid.util.StringUtils;
import com.longmai.datakeeper.rest.dto.DBUserMaskingDto;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;

public class DBUserMaskingContext {

    private static DBUserMaskingDto dbUserMaskingDto;

    public static void put(DBUserMaskingDto encryptDto){
        dbUserMaskingDto = encryptDto;
    }

    public static DBUserMaskingDto.MaskingColumnDto getMaskingColumn(String tableName, String column){
        if (StringUtils.isEmpty(tableName) || StringUtils.isEmpty(column)){
            throw new IllegalArgumentException("tableName 和 column 不能为空");
        }
        Map<String, List<DBUserMaskingDto.MaskingColumnDto>> map = dbUserMaskingDto.getMaskingTableColumnMap();
        List<DBUserMaskingDto.MaskingColumnDto> encryptColumnDtos = map.get(tableName);
        Optional<DBUserMaskingDto.MaskingColumnDto> first = encryptColumnDtos.stream().filter(new Predicate<DBUserMaskingDto.MaskingColumnDto>() {
            @Override
            public boolean test(DBUserMaskingDto.MaskingColumnDto maskingColumnDto) {
                return column.equals(maskingColumnDto.getColumnName().trim());
            }
        }).findFirst();
        if (first.isPresent()){
            return first.get();
        }else {
            return null;
        }
    }

}
