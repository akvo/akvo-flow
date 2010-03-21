package org.waterforpeople.mapping.domain;

import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;

import com.gallatinsystems.framework.domain.BaseDomain;
import com.google.appengine.api.datastore.Text;

@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class KML extends BaseDomain {	
	private static final long serialVersionUID = -9158145563225511073L;
	private Text kmlText;

	public Text getKmlText() {
		return kmlText;
	}

	public void setKmlText(Text kmlText) {
		this.kmlText = kmlText;
	}
}
