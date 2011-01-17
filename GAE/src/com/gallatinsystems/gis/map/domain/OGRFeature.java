package com.gallatinsystems.gis.map.domain;

import javax.jdo.annotations.PersistenceCapable;

import com.gallatinsystems.framework.domain.BaseDomain;
import com.google.appengine.api.datastore.Text;

@PersistenceCapable
public class OGRFeature extends BaseDomain {
	private String name;
	private String clname;
	private Integer count;
	private String firstCCNA;
	private String firtDNAM;
	private Integer sumTotal;
	private Integer sumMale;
	private Integer sumFemale;
	private Integer sumHH;
	private String firstCCOD;
	private String firstDCOD;
	private String firstCLCO;
	private String point;
	private Text polygon;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getClname() {
		return clname;
	}

	public void setClname(String clname) {
		this.clname = clname;
	}

	public Integer getCount() {
		return count;
	}

	public void setCount(Integer count) {
		this.count = count;
	}

	public String getFirstCCNA() {
		return firstCCNA;
	}

	public void setFirstCCNA(String firstCCNA) {
		this.firstCCNA = firstCCNA;
	}

	public String getFirtDNAM() {
		return firtDNAM;
	}

	public void setFirtDNAM(String firtDNAM) {
		this.firtDNAM = firtDNAM;
	}

	public Integer getSumTotal() {
		return sumTotal;
	}

	public void setSumTotal(Integer sumTotal) {
		this.sumTotal = sumTotal;
	}

	public Integer getSumMale() {
		return sumMale;
	}

	public void setSumMale(Integer sumMale) {
		this.sumMale = sumMale;
	}

	public Integer getSumFemale() {
		return sumFemale;
	}

	public void setSumFemale(Integer sumFemale) {
		this.sumFemale = sumFemale;
	}

	public Integer getSumHH() {
		return sumHH;
	}

	public void setSumHH(Integer sumHH) {
		this.sumHH = sumHH;
	}

	public String getFirstCCOD() {
		return firstCCOD;
	}

	public void setFirstCCOD(String firstCCOD) {
		this.firstCCOD = firstCCOD;
	}

	public String getFirstDCOD() {
		return firstDCOD;
	}

	public void setFirstDCOD(String firstDCOD) {
		this.firstDCOD = firstDCOD;
	}

	public String getFirstCLCO() {
		return firstCLCO;
	}

	public void setFirstCLCO(String firstCLCO) {
		this.firstCLCO = firstCLCO;
	}

	public Text getPolygon() {
		return polygon;
	}

	public void setPolygon(Text polygon) {
		this.polygon = polygon;
	}

	public void setPoint(String point) {
		this.point = point;
	}

	public String getPoint() {
		return point;
	}

	

}
