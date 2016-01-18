package com.jpmph.mybaidumap.bean;

import java.io.Serializable;

public class SearchBean implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 6645399635825171676L;
	
	private String address;
	private String name;
	private double latitude;
	private double lontitude;
	private String phone;
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public double getLatitude() {
		return latitude;
	}
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}
	public double getLontitude() {
		return lontitude;
	}
	public void setLontitude(double lontitude) {
		this.lontitude = lontitude;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
}
