package com.longmai.dbsafe.masking;

/**
 * 屏蔽脱敏
 * 参数： 起始下标,终止下标,屏蔽符号
 */
public class ShieldingMasking implements IMasking{

    @Override
    public Object masking(Object data, String arg) {

        if (data == null){
            return data;
        }
        String dataStr = data.toString();
        String[] arr = arg.trim().split(",");
        int fromIndex = parseArg(arr[0], Math.min(1,dataStr.length()-1));
        int endIndex = parseArg(arr[1], Math.max(1, dataStr.length()-2));
        String substring = dataStr.substring(fromIndex, endIndex);

        return dataStr.replace(substring, arr[2]);
    }

    private int parseArg(String arg, Integer defaultValue){
        try {
            return Integer.parseInt(arg);
        }catch (Exception e){
            e.printStackTrace();
            return defaultValue;
        }
    }

}
