package com.svend.dab.dao.mongo;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.mongodb.core.CollectionCallback;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.MongoException;
import com.mongodb.WriteResult;
import com.svend.dab.core.beans.PhotoAlbum;
import com.svend.dab.core.beans.groups.GroupParticipation;
import com.svend.dab.core.beans.groups.GroupSummary;
import com.svend.dab.core.beans.profile.Contact;
import com.svend.dab.core.beans.profile.PersonalData;
import com.svend.dab.core.beans.profile.Photo;
import com.svend.dab.core.beans.profile.PrivacySettings;
import com.svend.dab.core.beans.profile.UserProfile;
import com.svend.dab.core.beans.profile.UserReference;
import com.svend.dab.core.beans.profile.UserSummary;
import com.svend.dab.core.beans.projects.Participant.ROLE;
import com.svend.dab.core.beans.projects.Participation;
import com.svend.dab.core.beans.projects.Project.STATUS;
import com.svend.dab.core.dao.IUserProfileDao;

@Service
public class UserProfileRepoImpl implements IUserProfileDao {

	@Autowired
	private MongoTemplate mongoTemplate;

	private static Logger logger = Logger.getLogger(UserProfileRepoImpl.class.getName());

	public Set<String> getAllUsernames() {
		Query query = query(where("username").exists(true));
		query.fields().include("username");
		List<UserProfile> list = mongoTemplate.find(query, UserProfile.class);
		Set<String> names = new HashSet<String>();

		if (list != null) {
			for (UserProfile profile : list) {
				names.add(profile.getUsername());
			}
		}
		return names;
	}

	public List<UserProfile> retrieveUserProfilesByIds(List<String> ids) {

		// for some reason this does not work...
		// Query theQuery = query(where("id").in(ids));

		List<Criteria> criterias = new LinkedList<Criteria>();

		for (String id : ids) {
			criterias.add(where("username").is(id));
		}

		Query theQuery = query(new Criteria().orOperator(criterias.toArray(new Criteria[] {})));
		return mongoTemplate.find(theQuery, UserProfile.class);
	}

	public void replacePrivacySettings(String updatedUserId, PrivacySettings newPrivacySettings) {
		genericUpdateUser(updatedUserId, new Update().set("privacySettings", newPrivacySettings));
	}

	public void replaceLatestLoginAndPermKey(String updatedUserId, String uploadPermKey, Date dateOfLoggin) {
		genericUpdateUser(updatedUserId, new Update().set("uploadPermKey", uploadPermKey).set("dateOfLatestLogin", dateOfLoggin));
	}

	public void replacePersonalData(UserProfile updatedUser, PersonalData pData) {
		genericUpdateUser(updatedUser.getUsername(), new Update().set("pdata", pData));
	}

	
	public void updatePhotoAlbum(String username, PhotoAlbum photoAlbum) {
		genericUpdateUser(username, new Update().set("photoAlbum", photoAlbum));
	}


	public void addOnePhoto(String username, Photo photo) {
		genericUpdateUser(username, new Update().addToSet("photoAlbum.photos", photo));
	}

	public void removeOnePhoto(String username, Photo removed) {
		genericUpdateUser(username, new Update().pull("photoAlbum.photos", removed));
	}

	public void movePhotoToFirstPosition(String username, int photoIndex) {
		genericUpdateUser(username, new Update().set("photoAlbum.mainPhotoIndex", photoIndex));
	}

	public void removeOnePhotoAndResetMainPhotoIndex(String username, Photo removed) {
		genericUpdateUser(username, new Update().pull("photoAlbum.photos", removed).set("photoAlbum.mainPhotoIndex", 0));
	}

	public void removeOnePhotoAndDecrementMainPhotoIndex(String username, Photo removed) {
		genericUpdateUser(username, new Update().pull("photoAlbum.photos", removed).inc("photoAlbum.mainPhotoIndex", -1));
	}

	public void updatePhotoCaption(String username, String s3PhotoKey, String photoCaption) {
		Query query = query(where("username").is(username).and("photoAlbum.photos.normalPhotoLink.s3Key").is(s3PhotoKey));
		Update update = new Update().set("photoAlbum.photos.$.caption", photoCaption);
		mongoTemplate.updateFirst(query, update, UserProfile.class);
	}

	public void updateCv(UserProfile userProfile) {
		genericUpdateUser(userProfile.getUsername(), new Update().set("cvLink", userProfile.getCvLink()));
	}

	public void addPendingSentContactRequest(UserProfile userProfile, Contact pendingContact) {
		Contact existingSentContact = userProfile.retrieveSentContactRequestTo(pendingContact.getOtherUser(userProfile.getUsername()).getUserName());
		if (existingSentContact == null) {
			genericUpdateUser(userProfile.getUsername(), new Update().addToSet("pendingSentContactRequests", pendingContact));
		}
	}

	public void addPendingReceivedContactRequest(UserProfile userProfile, Contact pendingContact) {
		Contact existingReceivedContact = userProfile.retrieveReceivedContactRequestFrom(pendingContact.getOtherUser(userProfile.getUsername()).getUserName());
		if (existingReceivedContact == null) {
			genericUpdateUser(userProfile.getUsername(), new Update().addToSet("pendingReceivedContactRequests", pendingContact));
		}
	}

	public void addConfirmedContact(UserProfile userProfile, Contact contact) {
		Contact existingContact = userProfile.retrieveConfirmedContactWith(contact.getOtherUser(userProfile.getUsername()).getUserName());
		if (existingContact == null) {
			genericUpdateUser(userProfile.getUsername(), new Update().addToSet("contacts", contact));
		}
	}

	public void removePendingSentRelationship(UserProfile userProfile, String otherUsername) {
		Contact pendingSentReContact = userProfile.retrieveSentContactRequestTo(otherUsername);
		if (pendingSentReContact != null) {
			genericUpdateUser(userProfile.getUsername(), new Update().pull("pendingSentContactRequests", new Contact(pendingSentReContact.getContactId())));
		}
	}

	public void removedPendingReceivedRelationship(UserProfile userProfile, String otherUsername) {
		Contact pendingReceivedReContact = userProfile.retrieveReceivedContactRequestFrom(otherUsername);
		if (pendingReceivedReContact != null) {
			genericUpdateUser(userProfile.getUsername(),
					new Update().pull("pendingReceivedContactRequests", new Contact(pendingReceivedReContact.getContactId())));
		}
	}

	public void removeConfirmedContact(UserProfile userProfile, String otherUsername) {
		Contact confirmedContact = userProfile.retrieveConfirmedContactWith(otherUsername);
		if (confirmedContact != null) {
			genericUpdateUser(userProfile.getUsername(), new Update().pull("contacts", new Contact(confirmedContact.getContactId())));
		}
	}

	public void updateContact(String updatedUserId, Contact contact, UserSummary updatedUserSummary, boolean updateRequestor) {
		Query query = query(where("username").is(updatedUserId).and("contacts._id").is(contact.getContactId()));
		if (updateRequestor) {
			mongoTemplate.updateFirst(query, new Update().set("contacts.$.requestedByUser", updatedUserSummary), UserProfile.class);
		} else {
			mongoTemplate.updateFirst(query, new Update().set("contacts.$.requestedToUser", updatedUserSummary), UserProfile.class);
		}
	}

	public void updatePendingSentContactRequests(String updatedUserId, Contact contact, UserSummary updatedUserSummary, boolean updateRequestor) {
		logger.log(Level.INFO, "updatePendingSentContactRequests: updatedUserId=" + updatedUserId + " summary: " + updatedUserSummary + " updaterequestor: "
				+ updateRequestor + " contact id: " + contact.getContactId());

		Query query = query(where("username").is(updatedUserId).and("pendingSentContactRequests._id").is(contact.getContactId()));
		if (updateRequestor) {
			mongoTemplate.updateFirst(query, new Update().set("pendingSentContactRequests.$.requestedByUser", updatedUserSummary), UserProfile.class);
		} else {
			mongoTemplate.updateFirst(query, new Update().set("pendingSentContactRequests.$.requestedToUser", updatedUserSummary), UserProfile.class);
		}
	}

	public void updatePendingReceivedContactRequests(String updatedUserId, Contact contact, UserSummary updatedUserSummary, boolean updateRequestor) {

		logger.log(Level.INFO, "updatePendingReceivedContactRequests: updatedUserId=" + updatedUserId + " summary: " + updatedUserSummary
				+ " updaterequestor: " + updateRequestor + " contact id: " + contact.getContactId());

		Query query = query(where("username").is(updatedUserId).and("pendingReceivedContactRequests._id").is(contact.getContactId()));
		if (updateRequestor) {
			mongoTemplate.updateFirst(query, new Update().set("pendingReceivedContactRequests.$.requestedByUser", updatedUserSummary), UserProfile.class);
		} else {
			mongoTemplate.updateFirst(query, new Update().set("pendingReceivedContactRequests.$.requestedToUser", updatedUserSummary), UserProfile.class);
		}
	}

	// -------------------------------
	// references

	public void addReceivedReference(UserProfile userProfile, UserReference createdReference) {
		genericUpdateUser(userProfile.getUsername(), new Update().addToSet("receivedReferences", createdReference));
	}

	public void addWrittenReference(UserProfile userProfile, UserReference createdReference) {
		genericUpdateUser(userProfile.getUsername(), new Update().addToSet("writtenReferences", createdReference));
	}

	public void removeWrittenReference(UserProfile userProfile, String referenceId) {
		UserReference reference = userProfile.retrieveWrittenReference(referenceId);
		logger.log(Level.INFO, "removeWrittenReference, reference is " + reference);
		if (reference != null) {
			genericUpdateUser(userProfile.getUsername(), new Update().pull("writtenReferences", new UserReference(reference.getId())));
		}
	}

	public void removeReceivedReference(UserProfile userProfile, String referenceId) {
		UserReference reference = userProfile.retrieveReceivedReference(referenceId);
		logger.log(Level.INFO, "removeReceivedReference, reference is " + reference);
		if (reference != null) {
			genericUpdateUser(userProfile.getUsername(), new Update().pull("receivedReferences", new UserReference(reference.getId())));
		}
	}

	public void updateWrittenReferenceFromUser(String updatedUserId, UserReference ref, UserSummary updatedUserSummary) {
		Query query = query(where("username").is(updatedUserId).and("writtenReferences._id").is(ref.getId()));
		Update update = new Update().set("writtenReferences.$.fromUser", updatedUserSummary);
		mongoTemplate.updateFirst(query, update, UserProfile.class);
	}

	public void updateReceivedReferenceFromUser(String updatedUserId, UserReference ref, UserSummary updatedUserSummary) {
		Query query = query(where("username").is(updatedUserId).and("receivedReferences._id").is(ref.getId()));
		Update update = new Update().set("receivedReferences.$.fromUser", updatedUserSummary);
		mongoTemplate.updateFirst(query, update, UserProfile.class);
	}

	public void updateReceivedReferenceToUser(String updatedUserId, UserReference ref, UserSummary updatedUserSummary) {
		Query query = query(where("username").is(updatedUserId).and("receivedReferences._id").is(ref.getId()));
		Update update = new Update().set("receivedReferences.$.toUser", updatedUserSummary);
		mongoTemplate.updateFirst(query, update, UserProfile.class);
	}

	public void updateWrittenReferenceToUser(String updatedUserId, UserReference ref, UserSummary updatedUserSummary) {
		Query query = query(where("username").is(updatedUserId).and("writtenReferences._id").is(ref.getId()));
		Update update = new Update().set("writtenReferences.$.toUser", updatedUserSummary);
		mongoTemplate.updateFirst(query, update, UserProfile.class);
	}

	// ----------------------------------------
	// projects

	public void addProjectParticipation(UserProfile userProfile, Participation part) {
		Participation existingParticipation = userProfile.retrieveParticipation(part);
		if (existingParticipation == null) {
			genericUpdateUser(userProfile.getUsername(), new Update().addToSet("projects", part));
		}
	}

	public void removeParticipation(final String username, final String projectId) {

		// db.userProfile.update({'_id':'toto'},{'$pull':{'projects':{'projectSummary.projectId':'a13784b5bb9b474fa90f0839c7ea356c'}}});

		mongoTemplate.execute("userProfile", new CollectionCallback<WriteResult>() {
			public WriteResult doInCollection(DBCollection collection) throws MongoException, DataAccessException {
				BasicDBObject queryDbo = new BasicDBObject("_id", username);
				BasicDBObject pullDbo = new BasicDBObject("$pull", new BasicDBObject("projects", new BasicDBObject("projectSummary.projectId", projectId)));
				return collection.update(queryDbo, pullDbo);
			}
		});
	}

	public void updateProjectMainPhoto(String userName, String projectId, Photo mainPhoto) {
		Query query = query(where("username").is(userName).and("projects.projectSummary.projectId").is(projectId));

		Update update;
		if (mainPhoto == null) {
			logger.log(Level.INFO, "removing photo of " + userName + " of project " + projectId);
			update = new Update().unset("projects.$.projectSummary.mainPhoto");
		} else {
			logger.log(Level.INFO, "add photo of " + userName + " of project " + projectId + " with photo" + mainPhoto);
			update = new Update().set("projects.$.projectSummary.mainPhoto", mainPhoto);
		}
		mongoTemplate.updateFirst(query, update, UserProfile.class);
	}

	public void markParticipationHasAccepted(String userName, String projectId) {
		Query query = query(where("username").is(userName).and("projects.projectSummary.projectId").is(projectId));
		Update update = new Update().set("projects.$.accepted", true);
		mongoTemplate.updateFirst(query, update, UserProfile.class);
	}

	public void updateProjectRole(String userName, String projectId, ROLE role) {
		Query query = query(where("username").is(userName).and("projects.projectSummary.projectId").is(projectId));
		Update update = new Update().set("projects.$.role", role);
		mongoTemplate.updateFirst(query, update, UserProfile.class);
	}

	public void updateProjectStatus(String userName, String projectId, STATUS newStatus) {
		Query query = query(where("username").is(userName).and("projects.projectSummary.projectId").is(projectId));
		Update update = new Update().set("projects.$.projectSummary.status", newStatus);
		mongoTemplate.updateFirst(query, update, UserProfile.class);
	}

	// ----------------------------------------
	// groups

	public void addParticipationInGroup(String userName, GroupParticipation groupParticipation) {
		genericUpdateUser(userName, new Update().addToSet("groups", groupParticipation));
	}

	public void removeParticipationInGroup(final String username, final String groupId) {
		mongoTemplate.execute("userProfile", new CollectionCallback<WriteResult>() {
			public WriteResult doInCollection(DBCollection collection) throws MongoException, DataAccessException {
				BasicDBObject queryDbo = new BasicDBObject("_id", username);
				BasicDBObject pullDbo = new BasicDBObject("$pull", new BasicDBObject("groups", new BasicDBObject("groupSummary.groupId", groupId)));
				return collection.update(queryDbo, pullDbo);
			}
		});
	}

	public void updateGroupSummaryOfAllUsersPartOf(GroupSummary updatedSummary) {
		if (updatedSummary != null) {
			mongoTemplate.updateMulti(query(where("groups.groupSummary.groupId").is(updatedSummary.getGroupId())),
					new Update().set("groups.$.groupSummary", updatedSummary), UserProfile.class);
		}
	}

	public void updateGroupParticipationRole(String userName, String groupId, com.svend.dab.core.beans.groups.GroupParticipant.ROLE role) {
		mongoTemplate.updateFirst(query(where("username").is(userName).and("groups.groupSummary.groupId").is(groupId)),
				new Update().set("groups.$.role", role), UserProfile.class);
	}

	// -------------------------------------
	// -------------------------------------

	/**
	 * Applies this update operation to this user
	 * 
	 * @param username
	 * @param update
	 */
	protected void genericUpdateUser(String username, Update update) {
		Query query = query(where("username").is(username));
		mongoTemplate.updateFirst(query, update, UserProfile.class);
	}

	public UserProfile retrieveUserProfileById(String userId) {
		return mongoTemplate.findOne(query(where("username").is(userId)), UserProfile.class);
	}

	public void save(UserProfile createdUserProfile) {
		mongoTemplate.save(createdUserProfile);
	}


}
