/*  Copyright (C) 2014 Stichting Akvo (Akvo Foundation)
 *
 *  This file is part of Akvo FLOW.
 *
 *  Akvo FLOW is free software: you can redistribute it and modify it under the terms of
 *  the GNU Affero General Public License (AGPL) as published by the Free Software Foundation,
 *  either version 3 of the License or any later version.
 *
 *  Akvo FLOW is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 *  without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *  See the GNU Affero General Public License included below for more details.
 *
 *  The full license text can also be seen at <http://www.gnu.org/licenses/agpl.html>.
 */

package org.waterforpeople.mapping.domain;

import java.io.Serializable;
import java.util.List;
import org.waterforpeople.mapping.domain.CaddisflyResult;

public class CaddisflyResource implements Serializable {
	private static final long serialVersionUID = 1L;	
	private String name;
	private String uuid;
	private Long keyId;
	private String subtype;
	private String brand;
	private String[] tags;
	private String description;
	private int numResults;
	private Boolean hasImage;
	private List<CaddisflyResult> results;

	public CaddisflyResource(String name, String uuid, String subtype, String brand, String[] tags, String description, int numResults, Boolean hasImage){
		this.setName(name);
		this.setUuid(uuid);
		this.setSubtype(subtype);
		this.setBrand(brand);
		this.setTags(tags);
		this.setDescription(description);
		this.setNumResults(numResults);
		this.setHasImage(hasImage);
	}

	public CaddisflyResource(){
		
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSubtype() {
		return subtype;
	}

	public void setSubtype(String subtype) {
		this.subtype = subtype;
	}

	public String getBrand() {
		return brand;
	}

	public void setBrand(String brand) {
		this.brand = brand;
	}

	public String[] getTags() {
		return tags;
	}

	public void setTags(String[] tags) {
		this.tags = tags;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getNumResults() {
		return numResults;
	}

	public void setNumResults(int numResults) {
		this.numResults = numResults;
	}

	public List<CaddisflyResult> getResults() {
		return results;
	}

	public void setResults(List<CaddisflyResult> results) {
		this.results = results;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public Long getKeyId() {
		return keyId;
	}

	public void setKeyId(Long keyId) {
		this.keyId = keyId;
	}

	public Boolean getHasImage() {
		return hasImage;
	}

	public void setHasImage(Boolean hasImage) {
		this.hasImage = hasImage;
	}
}
