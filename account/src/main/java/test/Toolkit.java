package test;

public class Toolkit {

	public static boolean vertifyStrNotNull(String str){
		if(str!=null&&!"".equals(str)){
			return !isChinese(str);
		}
		return false;
	}

	public static boolean isChinese(char c) {
		Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
		if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
				|| ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
				|| ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
				|| ub == Character.UnicodeBlock.GENERAL_PUNCTUATION
				|| ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
				|| ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS){
			return true;
		}
		return false;
	}
	public static boolean isChinese(String strName) {
		char[] ch = strName.toCharArray();
		boolean b=false;
		for (int i = 0; i < ch.length; i++) {
			char c = ch[i];
			if(isChinese(c)==true){
				b=true;
				break;
			}
		}
		return b;
	}
}
