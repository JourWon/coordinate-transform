package com.jourwon.util;

import com.jourwon.pojo.Point;

/**
 * Description: 各坐标系之间的转换工具类
 * 
 * WGS84坐标系：即地球坐标系，国际上通用的坐标系。设备一般包含GPS芯片或者北斗芯片获取的经纬度为WGS84地理坐标系。
 * 谷歌地图采用的是WGS84地理坐标系（中国范围除外,谷歌中国地图采用的是GCJ02地理坐标系。)
 * 
 * GCJ02坐标系：即火星坐标系，WGS84坐标系经加密后的坐标系。
 * 出于国家安全考虑，国内所有导航电子地图必须使用国家测绘局制定的加密坐标系统，即将一个真实的经纬度坐标加密成一个不正确的经纬度坐标。
 * 
 * BD09坐标系：即百度坐标系，GCJ02坐标系经加密后的坐标系。搜狗坐标系、图吧坐标系等，估计也是在GCJ02基础上加密而成的。
 * 
 * 高德MapABC地图API 火星坐标 
 * 腾讯搜搜地图API 火星坐标 
 * 阿里云地图API 火星坐标 
 * 灵图51ditu地图API 火星坐标 
 * 
 * 百度地图API 百度坐标 
 * 搜狐搜狗地图API 搜狗坐标 
 * 图吧MapBar地图API 图吧坐标
 * 
 * @author JourWon
 * @date Created on 2018年6月19日
 */
public class CoordinateTransformUtils {

	// 圆周率π
	private static final double PI = 3.1415926535897932384626D;
	
	// 火星坐标系与百度坐标系转换的中间量
	private static final double X_PI = 3.14159265358979324 * 3000.0 / 180.0D;

	// Krasovsky 1940
	// 长半轴a = 6378245.0, 1/f = 298.3
	// b = a * (1 - f)
	// 扁率ee = (a^2 - b^2) / a^2;
	
	// 长半轴
	private static final double SEMI_MAJOR = 6378245.0D;
	
	// 扁率
	private static final double FLATTENING = 0.00669342162296594323D;
	
	// WGS84=>GCJ02 地球坐标系=>火星坐标系
	public static Point wgs84ToGcj02(double lng, double lat) {
		if (outOfChina(lng, lat)) {
			return new Point(lng, lat);
		}

		double[] offset = offset(lng, lat);
		double mglng = lng + offset[0];
		double mglat = lat + offset[1];

		return new Point(mglng, mglat);
	}

	// GCJ02=>WGS84 火星坐标系=>地球坐标系(粗略)
	public static Point gcj02ToWgs84(double lng, double lat) {
		if (outOfChina(lng, lat)) {
			return new Point(lng, lat);
		}

		double[] offset = offset(lng, lat);
		double mglng = lng - offset[0];
		double mglat = lat - offset[1];

		return new Point(mglng, mglat);
	}

	// GCJ02=>WGS84 火星坐标系=>地球坐标系（精确）
	public static Point gcj02ToWgs84Exactly(double lng, double lat) {
		if (outOfChina(lng, lat)) {
			return new Point(lng, lat);
		}
		
		double initDelta = 0.01;
		double threshold = 0.000000001;
		double dLat = initDelta, dLon = initDelta;
		double mLat = lat - dLat, mLon = lng - dLon;
		double pLat = lat + dLat, pLon = lng + dLon;
		double wgsLat, wgsLng, i = 0;
		while (true) {
			wgsLat = (mLat + pLat) / 2;
			wgsLng = (mLon + pLon) / 2;
			Point point = wgs84ToGcj02(wgsLng, wgsLat);
			dLon = point.getLng() - lng;
			dLat = point.getLat() - lat;
			if ((Math.abs(dLat) < threshold) && (Math.abs(dLon) < threshold))
				break;

			if (dLat > 0)
				pLat = wgsLat;
			else
				mLat = wgsLat;
			if (dLon > 0)
				pLon = wgsLng;
			else
				mLon = wgsLng;

			if (++i > 10000)
				break;
		}

		return new Point(wgsLng, wgsLat);
	}

	// GCJ-02=>BD09 火星坐标系=>百度坐标系
	public static Point gcj02ToBd09(double lng, double lat) {
		double z = Math.sqrt(lng * lng + lat * lat) + 0.00002 * Math.sin(lat * X_PI);
		double theta = Math.atan2(lat, lng) + 0.000003 * Math.cos(lng * X_PI);
		double bd_lng = z * Math.cos(theta) + 0.0065;
		double bd_lat = z * Math.sin(theta) + 0.006;
		return new Point(bd_lng, bd_lat);
	}

	// BD09=>GCJ-02 百度坐标系=>火星坐标系
	public static Point bd09ToGcj02(double lng, double lat) {
		double x = lng - 0.0065;
		double y = lat - 0.006;
		double z = Math.sqrt(x * x + y * y) - 0.00002 * Math.sin(y * X_PI);
		double theta = Math.atan2(y, x) - 0.000003 * Math.cos(x * X_PI);
		double gcj_lng = z * Math.cos(theta);
		double gcj_lat = z * Math.sin(theta);
		return new Point(gcj_lng, gcj_lat);
	}
	
	// WGS84=>BD09 地球坐标系=>百度坐标系
	public static Point wgs84ToBd09(double lng, double lat) {
		Point point = wgs84ToGcj02(lng, lat);
		return gcj02ToBd09(point.getLng(), point.getLat());
	}

	// BD09=>WGS84 百度坐标系=>地球坐标系
	public static Point bd09ToWgs84(double lng, double lat) {
		Point point = bd09ToGcj02(lng, lat);
		return gcj02ToWgs84(point.getLng(), point.getLat());
	}

	/**
	 * Description: 中国境外返回true,境内返回false
	 * @param lng 	经度
	 * @param lat	纬度
	 * @return
	 */
	public static boolean outOfChina(double lng, double lat) {
		if (lng < 72.004 || lng > 137.8347)
			return true;
		if (lat < 0.8293 || lat > 55.8271)
			return true;
		return false;
	}

	// 经度偏移量
	private static double transformLng(double lng, double lat) {
		double ret = 300.0 + lng + 2.0 * lat + 0.1 * lng * lng + 0.1 * lng * lat + 0.1 * Math.sqrt(Math.abs(lng));
		ret += (20.0 * Math.sin(6.0 * lng * PI) + 20.0 * Math.sin(2.0 * lng * PI)) * 2.0 / 3.0;
		ret += (20.0 * Math.sin(lng * PI) + 40.0 * Math.sin(lng / 3.0 * PI)) * 2.0 / 3.0;
		ret += (150.0 * Math.sin(lng / 12.0 * PI) + 300.0 * Math.sin(lng / 30.0 * PI)) * 2.0 / 3.0;
		return ret;
	}
	
	// 纬度偏移量
	private static double transformLat(double lng, double lat) {
		double ret = -100.0 + 2.0 * lng + 3.0 * lat + 0.2 * lat * lat + 0.1 * lng * lat
				+ 0.2 * Math.sqrt(Math.abs(lng));
		ret += (20.0 * Math.sin(6.0 * lng * PI) + 20.0 * Math.sin(2.0 * lng * PI)) * 2.0 / 3.0;
		ret += (20.0 * Math.sin(lat * PI) + 40.0 * Math.sin(lat / 3.0 * PI)) * 2.0 / 3.0;
		ret += (160.0 * Math.sin(lat / 12.0 * PI) + 320 * Math.sin(lat * PI / 30.0)) * 2.0 / 3.0;
		return ret;
	}
	
	// 偏移量
	public static double[] offset(double lng, double lat) {
		double[] lngLat = new double[2];
		double dlng = transformLng(lng - 105.0, lat - 35.0);
		double dlat = transformLat(lng - 105.0, lat - 35.0);
		double radlat = lat / 180.0 * PI;
		double magic = Math.sin(radlat);
		magic = 1 - FLATTENING * magic * magic;
		double sqrtmagic = Math.sqrt(magic);
		dlng = (dlng * 180.0) / (SEMI_MAJOR / sqrtmagic * Math.cos(radlat) * PI);
		dlat = (dlat * 180.0) / ((SEMI_MAJOR * (1 - FLATTENING)) / (magic * sqrtmagic) * PI);
		lngLat[0] = dlng;
		lngLat[1] = dlat;
		return lngLat;
	}

}
