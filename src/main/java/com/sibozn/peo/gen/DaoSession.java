package com.sibozn.peo.gen;

import java.util.Map;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.AbstractDaoSession;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.identityscope.IdentityScopeType;
import org.greenrobot.greendao.internal.DaoConfig;

import com.sibozn.peo.bean.Beans;
import com.sibozn.peo.bean.FeatureBean;

import com.sibozn.peo.gen.BeansDao;
import com.sibozn.peo.gen.FeatureBeanDao;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.

/**
 * {@inheritDoc}
 * 
 * @see org.greenrobot.greendao.AbstractDaoSession
 */
public class DaoSession extends AbstractDaoSession {

    private final DaoConfig beansDaoConfig;
    private final DaoConfig featureBeanDaoConfig;

    private final BeansDao beansDao;
    private final FeatureBeanDao featureBeanDao;

    public DaoSession(Database db, IdentityScopeType type, Map<Class<? extends AbstractDao<?, ?>>, DaoConfig>
            daoConfigMap) {
        super(db);

        beansDaoConfig = daoConfigMap.get(BeansDao.class).clone();
        beansDaoConfig.initIdentityScope(type);

        featureBeanDaoConfig = daoConfigMap.get(FeatureBeanDao.class).clone();
        featureBeanDaoConfig.initIdentityScope(type);

        beansDao = new BeansDao(beansDaoConfig, this);
        featureBeanDao = new FeatureBeanDao(featureBeanDaoConfig, this);

        registerDao(Beans.class, beansDao);
        registerDao(FeatureBean.class, featureBeanDao);
    }
    
    public void clear() {
        beansDaoConfig.clearIdentityScope();
        featureBeanDaoConfig.clearIdentityScope();
    }

    public BeansDao getBeansDao() {
        return beansDao;
    }

    public FeatureBeanDao getFeatureBeanDao() {
        return featureBeanDao;
    }

}