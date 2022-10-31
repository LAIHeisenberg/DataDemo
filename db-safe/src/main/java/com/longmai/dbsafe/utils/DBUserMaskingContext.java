package com.longmai.dbsafe.utils;

import com.alibaba.druid.util.StringUtils;
import com.longmai.datakeeper.rest.dto.DBUserMaskingDto;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;

public class DBUserMaskingContext {

    private static DBUserMaskingDto dbUserMaskingDto = new DBUserMaskingDto(null,null);

    public static void put(DBUserMaskingDto maskingDto){
        if (Objects.isNull(maskingDto)){
            return;
        }
        dbUserMaskingDto = maskingDto;
    }

    public static DBUserMaskingDto.MaskingColumnDto getMaskingColumn(String tableName, String column){
        if (StringUtils.isEmpty(tableName) || StringUtils.isEmpty(column)){
            throw new IllegalArgumentException("tableName 和 column 不能为空");
        }
        Map<String, List<DBUserMaskingDto.MaskingColumnDto>> map = dbUserMaskingDto.getMaskingTableColumnMap();
        List<DBUserMaskingDto.MaskingColumnDto> encryptColumnDtos = map.get(tableName.toUpperCase());
        if (Objects.isNull(encryptColumnDtos)){
            return null;
        }
        Optional<DBUserMaskingDto.MaskingColumnDto> first = encryptColumnDtos.stream().filter(new Predicate<DBUserMaskingDto.MaskingColumnDto>() {
            @Override
            public boolean test(DBUserMaskingDto.MaskingColumnDto maskingColumnDto) {
                return column.toUpperCase().equals(maskingColumnDto.getColumnName().toUpperCase().trim());
            }
        }).findFirst();
        if (first.isPresent()){
            return first.get();
        }else {
            return null;
        }
    }

}
