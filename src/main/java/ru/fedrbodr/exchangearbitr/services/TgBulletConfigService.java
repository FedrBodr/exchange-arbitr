package ru.fedrbodr.exchangearbitr.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.fedrbodr.exchangearbitr.dao.longtime.domain.TgBulletConfig;
import ru.fedrbodr.exchangearbitr.dao.longtime.repo.TgBulletConfigRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class TgBulletConfigService {
	@Autowired
	private TgBulletConfigRepository tgBulletConfigRepository;
	@Autowired
	private UserService userService;

	public void save(TgBulletConfig tgBulletConfig) {
		tgBulletConfigRepository.saveAndFlush(tgBulletConfig);
	}

	public TgBulletConfig getByUuid(UUID uuid){
		return tgBulletConfigRepository.findOneByUuid(uuid);
	}

	public List<TgBulletConfig> getConfigsToSendBullets(){
		List<TgBulletConfig> byTgChatIdNotNull = tgBulletConfigRepository.findByTgChatIdNotNull();
		List<TgBulletConfig> byTgChatIdNotNullPaied = new ArrayList<>();
		for (TgBulletConfig tgBulletConfig : byTgChatIdNotNull) {
			if(userService.isUserHasRole(tgBulletConfig.getUser(), "ROLE_PAYED")){
				byTgChatIdNotNullPaied.add(tgBulletConfig);
			}
		}
		return byTgChatIdNotNullPaied;
	}
}
