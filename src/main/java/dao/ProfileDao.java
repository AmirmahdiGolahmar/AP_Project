package dao;

import entity.Profile;
import jakarta.persistence.EntityManager;

public class ProfileDao extends GenericDao<Profile> {
    public ProfileDao() {
        super(Profile.class);
    }
}