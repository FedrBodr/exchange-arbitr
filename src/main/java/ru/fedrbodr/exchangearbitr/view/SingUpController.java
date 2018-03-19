package ru.fedrbodr.exchangearbitr.view;

import lombok.Data;
import org.joda.time.DateTimeZone;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Component;
import ru.fedrbodr.exchangearbitr.dao.longtime.domain.Role;
import ru.fedrbodr.exchangearbitr.dao.longtime.domain.User;
import ru.fedrbodr.exchangearbitr.dao.longtime.repo.UserRepository;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import java.util.*;

@Component
@Scope("session")
@Data
public class SingUpController {
	private String login;
	private String email;
	private String password;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;
	@Autowired
	protected AuthenticationManager authenticationManager;
	private String timeZone;

	public String addNewUser() {
		User newUser = new User(login,
				email,
				passwordEncoder.encode(password),
				Arrays.asList(new Role("ROLE_USER")),
				TimeZone.getTimeZone(timeZone));
		userRepository.saveAndFlush(newUser);

		UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(newUser.getEmail(), password);
		// generate session if one doesn't exist
		HttpServletRequest request = (HttpServletRequest) (FacesContext.getCurrentInstance().getExternalContext().getRequest());
		request.getSession();
		token.setDetails(new WebAuthenticationDetails(request));
		Authentication authenticatedUser = authenticationManager.authenticate(token);
		SecurityContextHolder.getContext().setAuthentication(authenticatedUser);

		return "index.xhtml";
	}

	public Map<String, String> getGmtTimeSones() {
		Set<String> jodaAvailableIDs1 = DateTimeZone.getAvailableIDs();
		Map<String, String> timeSonesMap = new LinkedHashMap<>();
		for (String s : jodaAvailableIDs1) {
			timeSonesMap.put(s, s);
		}
		return timeSonesMap;
	}
}
