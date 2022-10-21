package com.longmai.dbsafe.masking;

public class DBMaskingFactory {

    public static IMasking getMaskingInstance(String maskingMethod){
        switch (maskingMethod){
            case "shieldMasking":
                return new ShieldingMasking();
            default:
                return null;
        }
    }

}
