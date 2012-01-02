package com.gallatinsystems.standards.domain;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;

import javax.jdo.annotations.PersistenceCapable;

import com.gallatinsystems.framework.domain.BaseDomain;
import com.gallatinsystems.standards.domain.Standard.StandardType;
import com.gallatinsystems.survey.domain.Translation;

@PersistenceCapable
public class LOSScoreToStatusMapping extends BaseDomain {

	/**
	 * 
	 */
	public enum LOSColor {
		Black, Red, Yellow, Green
	};

	private static final long serialVersionUID = -5962041217191538747L;
	private StandardType levelOfServiceScoreType = null;
	private Integer floor = null;
	private Integer ceiling = null;
	private LOSColor color = null;
	private String description = null;
	private ArrayList<Translation> trans = null;
	private String iconLargeUrl = null;
	private String iconSmallUrl = null;
	private String iconStyle = null;

	public StandardType getLevelOfServiceScoreType() {
		return levelOfServiceScoreType;
	}

	public void setLevelOfServiceScoreType(StandardType levelOfServiceScoreType) {
		this.levelOfServiceScoreType = levelOfServiceScoreType;
	}

	public Integer getFloor() {
		return floor;
	}

	public void setFloor(Integer floor) {
		this.floor = floor;
	}

	public Integer getCeiling() {
		return ceiling;
	}

	public void setCeiling(Integer ceiling) {
		this.ceiling = ceiling;
	}

	public void setColor(LOSColor color) {
		this.color = color;
	}

	public LOSColor getColor() {
		return color;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getDescription() {
		return description;
	}

	public void setTrans(ArrayList<Translation> trans) {
		this.trans = trans;
	}

	public ArrayList<Translation> getTrans() {
		return trans;
	}

	public void addTrans(Translation trans) {
		if (trans == null) {
			this.trans = new ArrayList<Translation>();
		}
		this.trans.add(trans);
	}
	public void setIconLargeUrl(String iconUrl) {
		this.iconLargeUrl = iconUrl;
	}

	public String getIconLargeUrl() {
		return iconLargeUrl;
	}

	public void setIconSmallUrl(String iconSmallUrl) {
		this.iconSmallUrl = iconSmallUrl;
	}

	public String getIconSmallUrl() {
		return iconSmallUrl;
	}

	@Override
	public String toString() {
		StringBuilder result = new StringBuilder();
		String newLine = System.getProperty("line.separator");

		result.append(this.getClass().getName());
		result.append(" Object {");
		result.append(newLine);

		// determine fields declared in this class only (no fields of
		// superclass)
		Field[] fields = this.getClass().getDeclaredFields();

		// print field names paired with their values
		for (Field field : fields) {
			field.setAccessible(true);
			result.append("  ");
			try {
				result.append(field.getName());
				result.append(": ");
				// requires access to private field:
				result.append(field.get(this));
			} catch (IllegalAccessException ex) {
				System.out.println(ex);
			}
			result.append(newLine);
		}
		result.append("}");

		return result.toString();
	}

	public void setIconStyle(String iconStyle) {
		this.iconStyle = iconStyle;
	}

	public String getIconStyle() {
		return iconStyle;
	}
}
