package com.svend.dab.core.beans.projects;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import models.altermotif.projects.theme.Theme;

import org.springframework.data.annotation.Transient;

import web.utils.Utils;

import com.google.common.base.Strings;
import com.svend.dab.core.beans.profile.UserSummary;

public class Asset {
	
	public enum ASSET_STATUS {
		available("projectAssetAvailable"), missing("projectAssetMissing");

		private final String label;

		private ASSET_STATUS(String label) {
			this.label = label;
		}

		public String getLabel() {
			return label;
		}

	}

	private String id;
	private String name;
	private String description;

	private List<String> assigneeNames;

	@Transient
	private List<UserSummary> assignees;

	private Date dueDate;
	
	@Transient
	private String dueDateStr;

	private ASSET_STATUS status;

	// ////////////////////////////
	//

	/**
	 * Popuplates the list of reference so the {@link UserSummary} contained at the owner Project level, from the list of assignee usernames stored at this level <br/>
	 * 
	 * This is typically called when a {@link Asset} is read from DB => it needs to be prepared in order have a correct display on the GUI
	 * 
	 * @param parentProject
	 */
	public void computeAssigneeSummaries(Project parentProject) {

		assignees = new LinkedList<UserSummary>();
		
		if (assigneeNames != null) {
			for (String username : assigneeNames) {
				Participant assigneeParticipant = parentProject.getConfirmedActiveParticipant(username);
				if (assigneeParticipant != null) {
					assignees.add(assigneeParticipant.getUser());
				}
			}
		}
	}

	/**
	 * This is the reverse operation of the above: typically called when receiving some {@link Asset} from the browser => we have to apply {@link Theme} new list of {@link UserSummary} to the list of
	 * username saved in DB
	 * 
	 */
	public void applyAssigneeSummraiesToAssigneeUsernames() {
		
		assigneeNames = new LinkedList<String>();
		
		if (assignees != null) {
			for (UserSummary usersummary : assignees) {
				assigneeNames.add(usersummary.getUserName());
			}
		}
		
		assignees = null;

	}
	
	
	/////////////////////
	
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<String> getAssigneeNames() {
		return assigneeNames;
	}

	public void setAssigneeNames(List<String> assigneeNames) {
		this.assigneeNames = assigneeNames;
	}

	public List<UserSummary> getAssignees() {
		return assignees;
	}

	public void setAssignees(List<UserSummary> assignees) {
		this.assignees = assignees;
	}

	public Date getDueDate() {
		return dueDate;
	}

	public void setDueDate(Date dueDate) {
		this.dueDate = dueDate;
	}

	public String getDueDateStr() {
		if (Strings.isNullOrEmpty(dueDateStr) && dueDate != null) {
			dueDateStr = Utils.formatDate(dueDate);
		}
		
		return dueDateStr;
	}

	public void setDueDateStr(String dueDateStr) {
		this.dueDateStr = dueDateStr;
		this.dueDate = Utils.convertStringToDate(dueDateStr);
	}

	public ASSET_STATUS getStatus() {
		return status;
	}

	public void setStatus(ASSET_STATUS status) {
		this.status = status;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}


}
