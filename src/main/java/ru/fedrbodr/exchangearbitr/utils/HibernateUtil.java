package ru.fedrbodr.exchangearbitr.utils;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class HibernateUtil {
	private static SessionFactory sessionFactory;

	@Autowired
	public HibernateUtil(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	public static SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	public static Session getSession() {
		return sessionFactory.getCurrentSession();
	}
}
