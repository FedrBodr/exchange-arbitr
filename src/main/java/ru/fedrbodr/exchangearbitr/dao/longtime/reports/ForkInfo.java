package ru.fedrbodr.exchangearbitr.dao.longtime.reports;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.fedrbodr.exchangearbitr.dao.longtime.domain.Fork;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ForkInfo extends Fork {
	private Long id;
	private Date startTime;
	private Date lastUpdatedTime;
}
