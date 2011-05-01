package com.gallatinsystems.user.domain;

import javax.jdo.annotations.PersistenceCapable;

import com.gallatinsystems.framework.domain.BaseDomain;

/**
 * permissions that can be assigned to a user. Code is mandatory and must be unique
 * 
 * @author Christopher Fagiani
 * 
 */
@PersistenceCapable
public class Permission extends BaseDomain {
	private static final long serialVersionUID = 3706308694153467750L;
	private String code;
	private String name;
	
	public Permission(String name, String code){
		this.name = name;
		this.code = code;
	}
	
	public Permission(String name){
		this(name, name.toUpperCase());
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
		
	public boolean equals(Object other){
		if(other != null && other instanceof Permission){
			Permission op = (Permission)other;
			if(getCode()!= null && getCode().equals(op.getCode())){
				return true;
			}else if(op.getCode()== null && getCode() == null){
				return true;				
			}else{
				return false;
			}
		}else{
			return false;
		}
	}

	public int hashCode(){
		if(code != null){
			return code.hashCode();
		}else{
			return 0;
		}
	}
}
