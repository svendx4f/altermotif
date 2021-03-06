package models.altermotif;

import com.svend.dab.core.beans.projects.Participation;

/**
 * the data model class contains the outcome of an action on some particpant on the "groups view" page <br />
 * 
 * the point is that now the number of participant and/or administrator has changed, maybe the rights of the participants (including the logged in one) has changed
 * 
 * => we provide to the browser the new information
 * 
 * @author svend
 * 
 */
public class GroupsViewParticipantActionOutcome {

	private boolean success;

	private boolean loggedInUser_leaveLinkVisible;
	private boolean loggedInUser_makeMemberLinkVisible;
	private boolean loggedInUser_makeAdminLinkVisible;
	
	private boolean otherUser_leaveLinkVisible;
	private boolean otherUser_makeMemberLinkVisible;
	private boolean otherUser_makeAdminLinkVisible;
	private boolean otherUser_removeUserLinkVisible;
	
	private boolean applyToGroupWithProjectLinkVisisble;
	
	// this is often null. If it is not, it means the action resulted in new project to be taken care of in the list of project the user is admin
	// (this can happen for example if the user is group admin and rejects an application of a project he is also admin of)
	private Participation addedProjectIamAdminOf;

	public GroupsViewParticipantActionOutcome() {
		super();
	}

	public GroupsViewParticipantActionOutcome(boolean success) {
		super();
		this.success = success;
	}
	
	public GroupsViewParticipantActionOutcome(boolean success, Participation addedProjectIamAdminOf) {
		super();
		this.success = success;
		this.addedProjectIamAdminOf = addedProjectIamAdminOf;
	}

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public boolean isLoggedInUser_leaveLinkVisible() {
		return loggedInUser_leaveLinkVisible;
	}

	public void setLoggedInUser_leaveLinkVisible(boolean loggedInUser_leaveLinkVisible) {
		this.loggedInUser_leaveLinkVisible = loggedInUser_leaveLinkVisible;
	}

	public boolean isLoggedInUser_makeMemberLinkVisible() {
		return loggedInUser_makeMemberLinkVisible;
	}

	public void setLoggedInUser_makeMemberLinkVisible(boolean loggedInUser_makeMemberLinkVisible) {
		this.loggedInUser_makeMemberLinkVisible = loggedInUser_makeMemberLinkVisible;
	}

	public boolean isLoggedInUser_makeAdminLinkVisible() {
		return loggedInUser_makeAdminLinkVisible;
	}

	public void setLoggedInUser_makeAdminLinkVisible(boolean loggedInUser_makeAdminLinkVisible) {
		this.loggedInUser_makeAdminLinkVisible = loggedInUser_makeAdminLinkVisible;
	}

	public boolean isOtherUser_leaveLinkVisible() {
		return otherUser_leaveLinkVisible;
	}

	public void setOtherUser_leaveLinkVisible(boolean otherUser_leaveLinkVisible) {
		this.otherUser_leaveLinkVisible = otherUser_leaveLinkVisible;
	}

	public boolean isOtherUser_makeMemberLinkVisible() {
		return otherUser_makeMemberLinkVisible;
	}

	public void setOtherUser_makeMemberLinkVisible(boolean otherUser_makeMemberLinkVisible) {
		this.otherUser_makeMemberLinkVisible = otherUser_makeMemberLinkVisible;
	}

	public boolean isOtherUser_makeAdminLinkVisible() {
		return otherUser_makeAdminLinkVisible;
	}

	public void setOtherUser_makeAdminLinkVisible(boolean otherUser_makeAdminLinkVisible) {
		this.otherUser_makeAdminLinkVisible = otherUser_makeAdminLinkVisible;
	}

	public boolean isOtherUser_removeUserLinkVisible() {
		return otherUser_removeUserLinkVisible;
	}

	public void setOtherUser_removeUserLinkVisible(boolean otherUser_removeUserLinkVisible) {
		this.otherUser_removeUserLinkVisible = otherUser_removeUserLinkVisible;
	}

	public boolean isApplyToGroupWithProjectLinkVisisble() {
		return applyToGroupWithProjectLinkVisisble;
	}

	public void setApplyToGroupWithProjectLinkVisisble(boolean applyToGroupWithProjectLinkVisisble) {
		this.applyToGroupWithProjectLinkVisisble = applyToGroupWithProjectLinkVisisble;
	}

	public Participation getAddedProjectIamAdminOf() {
		return addedProjectIamAdminOf;
	}

	public void setAddedProjectIamAdminOf(Participation addedProjectIamAdminOf) {
		this.addedProjectIamAdminOf = addedProjectIamAdminOf;
	}

}
