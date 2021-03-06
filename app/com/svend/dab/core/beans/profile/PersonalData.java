package com.svend.dab.core.beans.profile;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.data.annotation.Transient;
import org.springframework.security.crypto.password.StandardPasswordEncoder;

/**
 * @author svend
 * 
 */
public class PersonalData implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2171159429855330690L;

	// associates the gender, as stored in persistence, to the localized label to be displayed to user, as defined in the .properties files
	private static Map<String, String> GENDER_LABEL_MAPPING = new HashMap<String, String>();
	static {
		GENDER_LABEL_MAPPING.put("M", "profileMale");
		GENDER_LABEL_MAPPING.put("F", "profileFemale");
		GENDER_LABEL_MAPPING.put("U", "profileGenderUnspecified");
	}

	private static Logger logger = Logger.getLogger(PersonalData.class.getName());

	private final static String FIXED_SALT = "apUPJI!G+[-b,vox#_TUO @r0-NdfB4}9qp\\:HGz!tfS/)5U0e";

	// -------------------
	// basic info

	private String location;

	private String password;
	private String passwd;

	private String firstName;
	private String lastName;
	private String email;
	private String gender = "U";
	private Date dateOfBirth;
	private Set<Language> languages;
	private String website;

	private String locationLat;
	private String locationLong;

	// ------------------------
	// textual details
	private String personalObjective;
	private String personalDescription;
	private String personalPhilosophy;
	private String personalAssets;

	// -------------------------
	// computed fields

	// this isrecomputed each time the date of birth is set (and is not persisted)
	@Transient
	private Integer computedAge;

	// assuming an average year of 365.242199 days...
	public static long MILLIS_IN_YEAR = 31556925993l;

	// -------------------------
	// -------------------------

	public PersonalData() {
	}

	public PersonalData(String location, String password, String passwd, String firstName, String lastName, String email, String gender, Date dateOfBirth, Set<Language> languages, String website,
			String locationLat, String locationLong, String personalObjective, String personalDescription, String personalPhilosophy, String personalAssets) {
		super();
		this.location = location;
		this.password = password;
		this.passwd = passwd;
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
		this.gender = gender;
		this.website = website;
		this.locationLat = locationLat;
		this.locationLong = locationLong;
		this.personalObjective = personalObjective;
		this.personalDescription = personalDescription;
		this.personalPhilosophy = personalPhilosophy;
		this.personalAssets = personalAssets;

		this.languages = new HashSet<Language>();
		if (languages != null) {
			for (Language lg : languages) {
				this.languages.add(new Language(lg));
			}
		}

		this.dateOfBirth = dateOfBirth;
		recomputeAge();
	}

	/**
	 * @param copied
	 */
	public PersonalData(PersonalData copied) {
		this(copied.location, copied.password, copied.passwd, copied.firstName, copied.lastName, copied.email, copied.gender, copied.dateOfBirth, copied.languages, copied.website, copied.locationLat,
				copied.locationLong, copied.personalObjective, copied.personalDescription, copied.personalPhilosophy, copied.personalAssets);
	}

	// -------------------------
	// -------------------------

	public void updatePassword(String password) {
		StandardPasswordEncoder encoder = new StandardPasswordEncoder(FIXED_SALT);
		this.passwd = encoder.encode(password);
	}

	
	public boolean checkPassword(String password) {
		StandardPasswordEncoder encoder = new StandardPasswordEncoder(FIXED_SALT);
		return encoder.matches(password, this.passwd);
	}

	
	
	/**
	 * 
	 */
	private void recomputeAge() {
		if (dateOfBirth != null) {
			long nowInMillis = new Date().getTime();
			long ageInMillis = nowInMillis - dateOfBirth.getTime();
			computedAge = (int) Math.ceil(ageInMillis / MILLIS_IN_YEAR);
		}
	}

	/**
	 * @return the computed age
	 */
	public int getAge() {
		if (computedAge == null) {
			recomputeAge();
		}
		// it could still be null now if the computation has failed (e.g. if null date of birth) => this avoid a NPTR while auto outboxing
		if (computedAge == null) {
			return 0;
		}
		return computedAge;
	}

	/**
	 * @return true if the profile has a minimun information for a minimal profile (used when registering a user)
	 */
	public boolean isComplete() {

		return firstName != null && !"".equals(firstName) && lastName != null && !"".equals(lastName) && dateOfBirth != null && languages != null && languages.size() > 0;

	}

	public int getNumberOfKnownLanguages() {
		if (getLanguages() == null) {
			return 0;
		} else {
			return getLanguages().size();
		}
	}

	/**
	 * @param lg
	 * @return a boolean say if this user knows this langage or not
	 */
	public boolean knowsLanguge(String lg) {
		if (lg == null || getLanguages() == null) {
			return false;
		} else {
			for (Language spokenLg : getLanguages()) {
				if (spokenLg != null && lg.equals(spokenLg.getName())) {
					return true;
				}
			}
			return false;
		}
	}

	/**
	 * @param addedLanguageCode
	 */
	public void addLanguage(String addedLanguageCode) {

		donotadd: if (addedLanguageCode != null) {
			if (languages == null) {
				languages = new HashSet<Language>();
			}

			for (Language existingLg : languages) {
				if (existingLg != null && addedLanguageCode.equals(existingLg.getName())) {
					logger.log(Level.WARNING, "No adding already existing langage to profile: " + addedLanguageCode);
					break donotadd;
				}
			}

			languages.add(new Language(addedLanguageCode, 2));
		}

	}

	/**
	 * @return the label associated to the gender of this {@link UserProfile}
	 */
	public String getGenderLabel() {
		if (gender == null) {
			return null;
		} else {
			return GENDER_LABEL_MAPPING.get(gender);
		}
	}

	/**
	 * @param otherPdata
	 * @return
	 */
	public boolean isLocationDifferent(PersonalData otherPdata) {
		if (otherPdata == null) {
			return false;
		}

		if (location == null) {
			return otherPdata.getLocation() != null;
		}

		return !location.equals(otherPdata.getLocation());

	}

	// -------------------------
	// -------------------------

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public Date getDateOfBirth() {
		return dateOfBirth;
	}

	public void setDateOfBirth(Date dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
		recomputeAge();
	}

	public Set<Language> getLanguages() {
		return languages;
	}

	public void setLanguages(Set<Language> languages) {
		this.languages = languages;
	}

	public String getWebsite() {
		return website;
	}

	public void setWebsite(String website) {
		this.website = website;
	}

	public String getLocationLat() {
		return locationLat;
	}

	public void setLocationLat(String locationLat) {
		this.locationLat = locationLat;
	}

	public String getLocationLong() {
		return locationLong;
	}

	public void setLocationLong(String locationLong) {
		this.locationLong = locationLong;
	}

	public String getPersonalObjective() {
		return personalObjective;
	}

	public void setPersonalObjective(String personalObjective) {
		this.personalObjective = personalObjective;
	}

	public String getPersonalDescription() {
		return personalDescription;
	}

	public void setPersonalDescription(String personalDescription) {
		this.personalDescription = personalDescription;
	}

	public String getPersonalPhilosophy() {
		return personalPhilosophy;
	}

	public void setPersonalPhilosophy(String personalPhilosophy) {
		this.personalPhilosophy = personalPhilosophy;
	}

	public String getPersonalAssets() {
		return personalAssets;
	}

	public void setPersonalAssets(String personalAssets) {
		this.personalAssets = personalAssets;
	}

	public String getPasswd() {
		return passwd;
	}

	public void setPasswd(String passwd) {
		this.passwd = passwd;
	}

}
