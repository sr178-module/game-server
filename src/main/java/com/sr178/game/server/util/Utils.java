package com.sr178.game.server.util;

public class Utils {
	
    public static int getClosestPowerOf2(int x){
        x--;
        x |= x >> 1;
        x |= x >> 2;
        x |= x >> 4;
        x |= x >> 8;
        x |= x >> 16;
        x++;
        return x;
    }
    
    
    public static final int CORE_NUM = Runtime.getRuntime()
            .availableProcessors();
    
    
	public static long getUserHashCode(String str) {
		long h = 0;
		char val[] = str.toCharArray();
		for (int i = 0; i < val.length; i++) {
			h = 31 + h + val[i];
		}
		return Math.abs((long) h);
	}
	
	public static String classNameToTableName(String className){
		char[] chs = className.toCharArray();
		StringBuffer tableName = new StringBuffer();
		tableName.append(chs[0]);
		for (int i = 1; i < chs.length; i++) {
			byte bt = (byte) chs[i];
			if (bt >= 65 && bt <= 90) {
				tableName.append("_");
				tableName.append(chs[i]);
			} else {
				tableName.append(chs[i]);
			}
		}
		return tableName.toString().toLowerCase();
	}
}
