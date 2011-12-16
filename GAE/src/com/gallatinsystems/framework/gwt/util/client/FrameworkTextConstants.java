package com.gallatinsystems.framework.gwt.util.client;

import com.google.gwt.i18n.client.Constants;

/**
 * text constants used by the framework classes. This interface is used so
 * framework ui strings can be localized. 
 * 
 * @author Christopher Fagiani
 * 
 */
public interface FrameworkTextConstants extends Constants {
	public String loading();

	public String previous();

	public String next();

	public String pleaseWait();

	public String noMatches();

	public String ok();

	public String cancel();

	public String close();

	public String saving();
}
