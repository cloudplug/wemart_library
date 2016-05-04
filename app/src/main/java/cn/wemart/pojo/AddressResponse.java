package cn.wemart.pojo;

import java.io.Serializable;

public class AddressResponse implements Serializable {
	public String city, district, mobileNo, name, province, street;
	public int addrNo, cityNo;
	public Boolean isDefault;
	public String streetAddr;

	public AddressResponse() {

	}

	public AddressResponse(int addrNo, String city, int cityNo,
			String district, Boolean isDefault, String mobileNo, String name,
			String province, String street) {
		this.addrNo = addrNo;
		this.city = city;
		this.cityNo = cityNo;
		this.district = district;
		this.isDefault = isDefault;
		this.mobileNo = mobileNo;
		this.name = name;
		this.province = province;
		this.street = street;
	}

	public String getmobileNo() {
		return mobileNo;

	}

	public String getname() {
		return name;

	}

	public String getprovince() {
		return province;

	}

	public String getstreet() {
		return street;

	}

	public int getaddrNo() {
		return addrNo;

	}

	public String getcity() {
		return city;

	}

	public int getcityNo() {
		return cityNo;

	}

	public String getdistrict() {
		return district;

	}

	public Boolean getisDefault() {
		return isDefault;
	}

}
