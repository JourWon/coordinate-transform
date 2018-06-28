package com.jourwon.pojo;

import java.io.Serializable;

/**
 * Description: 坐标点
 * 
 * @author JourWon
 * @date Created on 2018年6月19日
 */
public class Point implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3584864663880053897L;

	/**
	 * 经度
	 */
	private double lng;

	/**
	 * 纬度
	 */
	private double lat;

	public Point() {
	}

	public Point(double lng, double lat) {
		super();
		this.lng = lng;
		this.lat = lat;
	}

	public double getLng() {
		return lng;
	}

	public void setLng(double lng) {
		this.lng = lng;
	}

	public double getLat() {
		return lat;
	}

	public void setLat(double lat) {
		this.lat = lat;
	}

	@Override
	public String toString() {
		return lng + "," + lat;
	}

}
