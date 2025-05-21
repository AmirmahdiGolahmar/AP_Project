package dao;

import entity.User;
import jakarta.persistence.criteria.*;
import org.hibernate.Session;
import util.HibernateUtil;

import java.util.ArrayList;
import java.util.List;

public class GenericUserSearchDao<T extends User> {

    private final Class<T> clazz;
    private final String profileField; // "profile" یا "sellerProfile"

    public GenericUserSearchDao(Class<T> clazz, String profileField) {
        this.clazz = clazz;
        this.profileField = profileField;
    }

    public List<T> search(String username, String firstName, String lastName, String mobile) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        try {
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<T> cq = cb.createQuery(clazz);
            Root<T> root = cq.from(clazz);

            // join با profile یا sellerProfile
            Join<?, ?> profileJoin = root.join(profileField);

            List<Predicate> predicates = new ArrayList<>();

            if (username != null && !username.isEmpty()) {
                predicates.add(cb.like(root.get("username"), "%" + username + "%"));
            }
            if (firstName != null && !firstName.isEmpty()) {
                predicates.add(cb.like(profileJoin.get("firstName"), "%" + firstName + "%"));
            }
            if (lastName != null && !lastName.isEmpty()) {
                predicates.add(cb.like(profileJoin.get("lastName"), "%" + lastName + "%"));
            }
            if (mobile != null && !mobile.isEmpty()) {
                predicates.add(cb.like(profileJoin.get("mobile"), "%" + mobile + "%"));
            }

            cq.where(cb.and(predicates.toArray(new Predicate[0])));
            return session.createQuery(cq).getResultList();

        } finally {
            session.close();
        }
    }
}
