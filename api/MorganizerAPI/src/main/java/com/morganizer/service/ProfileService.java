package com.morganizer.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.morganizer.dto.ProfileRequest;
import com.morganizer.dto.ProfileResponse;
import com.morganizer.entity.EventReminderEntity;
import com.morganizer.entity.ProfileEntity;
import com.morganizer.entity.UserDetailsEntity;
import com.morganizer.repository.ProfileRepository;
import com.morganizer.repository.UserDetailsRepository;
import com.morganizer.repository.EventReminderRepository;


@Service
public class ProfileService {

	@Autowired
	public ProfileRepository profileRepository;

	@Autowired
	private UserDetailsRepository userRepo;
	
	@Autowired
	private EventService eventService;

	@Autowired
	public EventReminderRepository eventReminderRepository;

	public void deleteProfile(Long profileId) {

		try {
			ProfileEntity profile = this.profileRepository.getOne(profileId);
			this.eventService.deleteAssignee(profile);
			
			this.profileRepository.deleteById(profileId);
		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

	public List<ProfileResponse> fetchAll(Long userId) {
		
		List<ProfileEntity> profileList =  profileRepository.findByUserId(userId);
		List<ProfileResponse> result = new ArrayList<>();

		for(ProfileEntity profile: profileList) {
			List<Long> reminderList = new ArrayList<>();
			for (EventReminderEntity reminder: profile.getReminderList()) {
				reminderList.add(reminder.getReminderId());
			}
			result.add(new ProfileResponse(profile.getName(),profile.getGender(),profile.getPhoneNumber(), profile.getBirthdate(), profile.getEmail(), profile.getProfileId(), profile.getUser().getId(), profile.getColor(),profile.isSelected(), reminderList));
		}
		return result;
	}

	public ProfileResponse addProfile(ProfileRequest profileRequest) {
		UserDetailsEntity user = userRepo.getOne(profileRequest.getUserId());

		
		List<EventReminderEntity> reminderList = new ArrayList<>();
		for (Long reminder: profileRequest.getReminderList()) {
			reminderList.add(eventReminderRepository.getOne(reminder));
		}

		ProfileEntity profile = new ProfileEntity(profileRequest.getName(), profileRequest.getEmail(), profileRequest.getPhoneNumber(),
				profileRequest.getGender(), profileRequest.getBirthdate(), profileRequest.getColor(), user, profileRequest.isSelected(), reminderList);
		
		if(profileRequest.getProfileId()!=null) {
			profile.setProfileId(profileRequest.getProfileId());
		}
		
		ProfileEntity profileEntity = profileRepository.save(profile);

		List<Long> reminders = new ArrayList<>();
		for (EventReminderEntity reminder: profileEntity.getReminderList()) {
			reminders.add(reminder.getReminderId());
		}
		return new ProfileResponse(profileEntity.getName(),profileEntity.getGender(),profileEntity.getPhoneNumber(), profileEntity.getBirthdate(), profileEntity.getEmail(), profileEntity.getProfileId(), profileEntity.getUser().getId(), profileEntity.getColor(),profileEntity.isSelected(), reminders);

	}
}
