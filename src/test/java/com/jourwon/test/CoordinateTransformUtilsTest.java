package com.jourwon.test;

import org.junit.Test;

import com.jourwon.pojo.Point;
import com.jourwon.util.CoordinateTransformUtils;

/**
 * Description: 坐标系转换工具测试类
 * 
 * @author JourWon
 * @date Created on 2018年6月19日
 */
public class CoordinateTransformUtilsTest {

	/**
	 * Description: 地球坐标系 =>火星坐标系、百度坐标系
	 */
	@Test
	public void test01() {
		// 广州市中大地铁站
		Point point = new Point(113.28749670783887D, 23.094783676708065D);
		System.out.println("地球坐标系 : " + point);

		Point wgs84ToGcj02 = CoordinateTransformUtils.wgs84ToGcj02(point.getLng(), point.getLat());
		System.out.println("火星坐标系 : " + wgs84ToGcj02);

		Point wgs84ToBd09 = CoordinateTransformUtils.wgs84ToBd09(point.getLng(), point.getLat());
		System.out.println("百度坐标系 : " + wgs84ToBd09);
	}
	
	@Test
	public void test0() {
		// 广州市中大地铁站
		Point point = new Point(113.21809623603632D, 23.15056674737269D);
		System.out.println("地球坐标系 : " + point);

		Point wgs84ToGcj02 = CoordinateTransformUtils.wgs84ToGcj02(point.getLng(), point.getLat());
		System.out.println("火星坐标系 : " + wgs84ToGcj02);

		Point wgs84ToBd09 = CoordinateTransformUtils.wgs84ToBd09(point.getLng(), point.getLat());
		System.out.println("百度坐标系 : " + wgs84ToBd09);
	}

	/**
	 * Description: 火星坐标系=>地球坐标系、百度坐标系
	 */
	@Test
	public void test02() {
		// 广州市珠江新城地铁站
		Point point = new Point(113.321171D, 23.119285D);
		System.out.println("火星坐标系 : " + point);

		Point wgs84ToGcj02 = CoordinateTransformUtils.gcj02ToWgs84(point.getLng(), point.getLat());
		System.out.println("地球坐标系 : " + wgs84ToGcj02);

		Point wgs84ToBd09 = CoordinateTransformUtils.gcj02ToBd09(point.getLng(), point.getLat());
		System.out.println("百度坐标系 : " + wgs84ToBd09);
	}

	/**
	 * Description: 百度坐标系=>地球坐标系、火星坐标系
	 */
	@Test
	public void test03() {
		// 广州市体育西路地铁站
		Point point = new Point(113.328035D, 23.136929D);
		System.out.println("百度坐标系 : " + point);

		Point wgs84ToGcj02 = CoordinateTransformUtils.bd09ToWgs84(point.getLng(), point.getLat());
		System.out.println("地球坐标系 : " + wgs84ToGcj02);

		Point wgs84ToBd09 = CoordinateTransformUtils.bd09ToGcj02(point.getLng(), point.getLat());
		System.out.println("火星坐标系 : " + wgs84ToBd09);
	}

}
