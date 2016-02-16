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

public class CaddisflyResult implements Serializable {
	private static final long serialVersionUID = 1L;
		private String name;
		private String unit;
		private String id;

		public CaddisflyResult(String name, String unit, String id){
			this.setName(name);
			this.setUnit(unit);
			this.setId(id);
		}

		public CaddisflyResult(){
			
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getUnit() {
			return unit;
		}

		public void setUnit(String unit) {
			this.unit = unit;
		}

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}
}
