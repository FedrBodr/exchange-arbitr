package ru.fedrbodr.exchangearbitr.dao.longtime.repo;

import org.springframework.transaction.annotation.Transactional;
import ru.fedrbodr.exchangearbitr.dao.longtime.reports.ForkInfo;

import java.util.List;

public interface ForkRepositoryCustom {
	@Transactional(readOnly = true)
	List<ForkInfo> selectLatestForksInfo(int forkLastUpdatedSecconds);
}
