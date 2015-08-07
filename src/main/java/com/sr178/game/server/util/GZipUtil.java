package com.sr178.game.server.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;


public class GZipUtil {
	/***
	  * 压缩GZip
	  * 
	  * @param data
	  * @return
	 * @throws IOException 
	  */
	 public static byte[] compress(byte[] data) throws IOException {
		   byte[] b = null;
		   ByteArrayOutputStream bos = new ByteArrayOutputStream();
		   GZIPOutputStream gzip = new GZIPOutputStream(bos);
		   gzip.write(data);
		   gzip.finish();
		   gzip.close();
		   b = bos.toByteArray();
		   bos.close();
		   return b;
	 }
	 /***
	  * 解压GZip
	  * 
	  * @param data
	  * @return
	 * @throws IOException 
	  */
	 public static byte[] decompress(byte[] data) throws IOException {
		   byte[] b = null;
		   ByteArrayInputStream bis = new ByteArrayInputStream(data);
		   GZIPInputStream gzip = new GZIPInputStream(bis);
		   byte[] buf = new byte[1024];
		   int num = -1;
		   ByteArrayOutputStream baos = new ByteArrayOutputStream();
		   while ((num = gzip.read(buf, 0, buf.length)) != -1) {
		    baos.write(buf, 0, num);
		   }
		   b = baos.toByteArray();
		   baos.flush();
		   baos.close();
		   gzip.close();
		   bis.close();
		   return b;
	 }
	 public static String getStrFromByte(byte[] data){
		 String byteStr ="";
		 for(int i=0;i<data.length;i++){
			 byteStr=byteStr+","+data[i];
		 }
		 return byteStr;
	 }
	 public static void main(String[] args) throws IOException {
		 String str = "123xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx321";
		 byte[] data = str.getBytes("utf-8");
		 System.out.println("压缩的真实内容为：["+str+"]");
		 String byteStr =getStrFromByte(data);
		 System.out.println("字符转化为字节数组为:["+byteStr+"]");
		 byte[] zip = GZipUtil.compress(data);
		 System.out.println("gzip压缩后:["+getStrFromByte(zip)+"]");
		 byte[] unzip = GZipUtil.decompress(zip);
		 System.out.println("gzip还原压缩后变成的数组为数组为:["+getStrFromByte(unzip)+"]");
		 String afterStr = new String(unzip,"utf-8");
		 System.out.println("还原后的内容为:["+afterStr+"]");
	}
  
}
