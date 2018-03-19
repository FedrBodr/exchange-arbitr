package ru.fedrbodr.exchangearbitr.dao.longtime.domain;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "tg_bullet_config")
@Data
@NoArgsConstructor
public class TgBulletConfig {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	@Column(name = "uuid", unique = true, updatable = false)
	@org.hibernate.annotations.Type(type="org.hibernate.type.PostgresUUIDType")
	private UUID uuid;
	private Long tgChatId;
	@OneToOne(mappedBy = "bulletConfig")
	private User user;

	@Column(name = "minute_interval")
	private Integer minuteInterval;
	@OneToMany(cascade=CascadeType.PERSIST)
	@LazyCollection(LazyCollectionOption.FALSE)
	private List<BulletForkFilter> filters;

	@PrePersist
	public void initializeUUID() {
		if (uuid == null) {
			uuid = UUID.randomUUID();
		}
	}
}
