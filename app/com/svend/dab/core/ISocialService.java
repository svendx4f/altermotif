package com.svend.dab.core;

/**
 * @author svend
 *
 */
public interface ISocialService {

	
	
	public abstract void sendEmail(String recipient, String replyTo, String subject, String textContent);
	
}


