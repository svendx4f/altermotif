package controllers;

import models.altermotif.SessionWrapper;
import play.mvc.Before;
import play.mvc.Controller;

/**
 * 
 * Same as {@link DabController} but automatically redirects to index if the user is not logged in
 * 
 * @author Svend
 *
 */
public class DabLoggedController extends Controller {

	@Before
	static void makeSureUserIsLoggedIn() {
		
		DabController.addDefaults();
    	
    	if (!getSessionWrapper().isLoggedIn() ) {
    		Enter.login();
    	}
    	
	}


	
	
	
	
	public static SessionWrapper getSessionWrapper() {
		return DabController.getSessionWrapper();
	}
		

	
}
