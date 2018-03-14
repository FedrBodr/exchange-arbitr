package ru.fedrbodr.exchangearbitr.view;

import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import ru.fedrbodr.exchangearbitr.dao.longtime.domain.User;
import ru.fedrbodr.exchangearbitr.dao.longtime.repo.UserRepository;
import ru.fedrbodr.exchangearbitr.services.UserService;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import java.io.IOException;

@Component
@Scope("session")
@Data
public class LoginController {
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private UserService userService;

	public boolean isAuthenticated() {
		return userService.isAuthenticated();
	}

	public String logOut() throws IOException {
		ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
		externalContext.redirect("/logout");
		return "logout";
	}

	public User getCurrentUser() throws IOException {
		return userService.getCurrentUserOrNull();
	}
}
