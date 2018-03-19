package ru.fedrbodr.exchangearbitr.rest.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import ru.fedrbodr.exchangearbitr.dao.longtime.domain.BulletForkFilter;
import ru.fedrbodr.exchangearbitr.dao.longtime.domain.TgBulletConfig;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
public class TgBulletConfigDto {
	private Long id;
	private UUID uuid;
	private Long tgChatId;
	private Integer minuteInterval;
	private List<BulletForkFilterDto> filters;
	private String error;

	public TgBulletConfigDto(TgBulletConfig configsToSendBullet) {
		this.id = configsToSendBullet.getId();
		this.uuid = configsToSendBullet.getUuid();
		this.tgChatId = configsToSendBullet.getTgChatId();
		this.minuteInterval = configsToSendBullet.getMinuteInterval();
		this.filters = convertToDtoFilters(configsToSendBullet.getFilters());
	}

	public TgBulletConfigDto(String error) {
		this.error = error;
	}

	private List<BulletForkFilterDto> convertToDtoFilters(List<BulletForkFilter> filters) {
		List<BulletForkFilterDto> bulletForkFilterDtos = new LinkedList<>();
		for (BulletForkFilter filter : filters) {
			bulletForkFilterDtos.add(new BulletForkFilterDto(filter));
		}
		return bulletForkFilterDtos;
	}
}
