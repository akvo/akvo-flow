package com.gallatinsystems.flowfoundry.server.domain;
import javax.jdo.annotations.PersistenceCapable;

import com.gallatinsystems.framework.domain.BaseDomain;

/**
 * A single configuration value for a single flow instance.
 *
 * @author Christopher Fagiani
 *
 */
@PersistenceCapable
public class FlowConfigValue extends BaseDomain{
	private static final long serialVersionUID = 3948441952167359835L;
	private String name;
	private String value;
	private Long flowInstanceId;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public Long getFlowInstanceId() {
		return flowInstanceId;
	}

	public void setFlowInstanceId(Long flowInstanceId) {
		this.flowInstanceId = flowInstanceId;
	}

}
