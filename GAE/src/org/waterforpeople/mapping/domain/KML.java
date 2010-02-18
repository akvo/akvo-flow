package org.waterforpeople.mapping.domain;

import java.util.Date;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Text;

@PersistenceCapable(identityType = IdentityType.APPLICATION)

public class KML {
		@PrimaryKey
		@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
		private Long id;
		private Text kmlText;
		private Date createDateTime;
		
		public Date getCreateDateTime() {
			return createDateTime;
		}
		public void setCreateDateTime(Date createDateTime) {
			this.createDateTime = createDateTime;
		}
		public Long getId() {
			return id;
		}
		public void setId(Long id) {
			this.id = id;
		}
		public Text getKmlText() {
			return kmlText;
		}
		public void setKmlText(Text kmlText) {
			this.kmlText = kmlText;
		}
}
