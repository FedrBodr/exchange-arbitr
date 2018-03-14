package ru.fedrbodr.exchangearbitr.view;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import ru.fedrbodr.exchangearbitr.dao.longtime.domain.User;
import ru.fedrbodr.exchangearbitr.services.UserService;

import java.util.TimeZone;

@Component
@Scope("session")
public class EditUserController {
	private String timeZoneName;
	@Autowired
	private UserService userService;

	public String updateTimeSone(){
		TimeZone timeZone = TimeZone.getTimeZone(this.timeZoneName);
		User currentUserOrNull = userService.getCurrentUserOrNull();
		currentUserOrNull.setTimeZone(timeZone);
		userService.save(currentUserOrNull);
		return "user.jsf";
	}

	public String getTimeZoneName() {
		return userService.getCurrentUserOrNull().getTimeZone().getID();
	}

	public void setTimeZoneName(String timeZoneName) {
		this.timeZoneName = timeZoneName;
	}
}
