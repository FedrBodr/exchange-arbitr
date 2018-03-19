package ru.fedrbodr.exchangearbitr.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import ru.fedrbodr.exchangearbitr.dao.longtime.domain.TgBulletConfig;
import ru.fedrbodr.exchangearbitr.rest.dto.TgBulletConfigDto;
import ru.fedrbodr.exchangearbitr.services.TgBulletConfigService;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

@RestController
public class TgBotRestController {
	@Autowired
	private TgBulletConfigService tgBulletConfigService;

	@RequestMapping(value = "/api/link/uuid/{uuid}/chatId/{chatId}", method = RequestMethod.GET)
	public String getCurrentForks(@PathVariable() UUID uuid, @PathVariable() Long chatId) {
		TgBulletConfig byUuid = tgBulletConfigService.getByUuid(uuid);
		if (byUuid != null) {
			byUuid.setTgChatId(chatId);
			tgBulletConfigService.save(byUuid);
			return "OK";
		} else {
			return "not found TgBulletConfig by uuid";
		}
	}

	@RequestMapping(value = "/api/tg-bullet-configs-to-send-bullets/{uuid}", method = RequestMethod.GET)
	public List<TgBulletConfigDto> getTgBulletConfigsToSendBullets(@PathVariable() UUID uuid) {
		if ("253423a1-3a03-478c-b807-ac0f81fead61".equals(uuid.toString())) {
			return convertToDto(tgBulletConfigService.getConfigsToSendBullets());
		} else {
			return Arrays.asList(new TgBulletConfigDto("If you want access to api wright tg or gmail - @fedrbodr"));
		}
	}

	private List<TgBulletConfigDto> convertToDto(List<TgBulletConfig> configsToSendBullets) {
		List<TgBulletConfigDto> tgBulletConfigDtos = new LinkedList<>();
		for (TgBulletConfig configsToSendBullet : configsToSendBullets) {
			tgBulletConfigDtos.add(new TgBulletConfigDto(configsToSendBullet));
		}
		return tgBulletConfigDtos;
	}
}