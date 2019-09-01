package com.yzs.activitymaster.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtils {
	//根据时间计算出年月份
	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	 private static SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
	 private static SimpleDateFormat formatDate = new SimpleDateFormat("dd天HH时mm分ss秒");
		public static String date(Date d){
			Calendar cal = Calendar.getInstance();
			cal.setTime(d);
			int month = cal.get(Calendar.MONTH);
			int year = cal.get(Calendar.YEAR);
			int i = cal.get(Calendar.DATE);
			String y=year+"年-"+(month+1)+"月-"+i+"日";
			return y;
		}
		public static String times(Date d) {
			String string = sdf.format(d);
			return string;
		}
		public static String timeDay(Date d) {
			String string = format.format(d);
			return string;
		}
		/**
		 * 计算两个时间差
		 * @param startTime 开始时间
		 * @param endTime 结束时间
		 * @return
		 */
		public static String timeShort(Date startTime ,Date endTime) {
			long nd = 1000 * 24 * 60 * 60;
		    long nh = 1000 * 60 * 60;
		    long nm = 1000 * 60;
		    // long ns = 1000;
		    // 获得两个时间的毫秒时间差异
		    long diff = endTime.getTime()-startTime.getTime();
		    // 计算差多少天
		    long day = diff / nd;
		    // 计算差多少小时
		    long hour = diff % nd / nh;
		    // 计算差多少分钟
		    long min = diff % nd % nh / nm;
		    // 计算差多少秒//输出结果
		     long sec = diff % nd % nh % nm / 1000;
		    String days=null;
		    if(day == 0) {
		    	days=hour + "小时" + min + "分钟"+sec+"秒";
		    }
		    if(day ==0 && hour==0) {
		    	days=min + "分钟"+sec+"秒";
		    }
		    if(day ==0 && hour==0 && min ==0) {
		    	days=sec+"秒";
		    }
			return days;
		}
		public static void main(String[] args) throws Exception {
			String i ="2019-02-15 14:16:00";
			String u="2019-02-15 14:16:32";
			Date date = sdf.parse(u);
			Date parse = sdf.parse(i);
			String string = timeShort(parse,date);
			System.out.println(string);
			}
}
