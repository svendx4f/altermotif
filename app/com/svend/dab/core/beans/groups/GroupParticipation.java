package com.svend.dab.core.beans.groups;

import java.util.Date;

import com.svend.dab.core.beans.groups.GroupParticipant.ROLE;

public class GroupParticipation {

	private ROLE role;
	private GroupSummary groupSummary;

	public GroupParticipation() {
		super();
	}

	public GroupParticipation(ROLE role, GroupSummary groupSummary) {
		super();
		this.role = role;
		this.groupSummary = groupSummary;
	}
	
	public void generatePhotoLink(Date expirationdate) {
		if (groupSummary != null) {
			groupSummary.generatePhotoLink(expirationdate);
		}
	}

	public ROLE getRole() {
		return role;
	}

	public void setRole(ROLE role) {
		this.role = role;
	}

	public GroupSummary getGroupSummary() {
		return groupSummary;
	}

	public void setGroupSummary(GroupSummary groupSummary) {
		this.groupSummary = groupSummary;
	}


}
