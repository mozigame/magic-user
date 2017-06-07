package com.magic.user.storage.base;

import com.magic.api.commons.ApiLogger;
import com.magic.api.commons.atlas.core.BaseDao;
import com.magic.api.commons.model.Page;
import com.magic.user.entity.Login;
import org.apache.zookeeper.KeeperException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;


public abstract class BaseDbServiceImpl<T, PK extends Serializable> implements BaseDbService<T, PK> {

    protected Logger logger = LoggerFactory.getLogger(getClass());

    public abstract BaseDao<T, PK> getDao();

    public PK insert(T entity) {
        try {
            return getDao().insert(entity);
        } catch (Exception e) {
            ApiLogger.error("BaseDb insert entity error:", e);
        }
        return null;
    }

    @Override
    public PK insert(PK id, T entity) {
        try {
            return getDao().insert(id, entity);
        } catch (Exception e) {
            ApiLogger.error(String.format("BaseDb shard insert entity error,id:%s",id), e);
        }
        return null;
    }

    public List<PK> insert(List<T> entitys) {
        try {
            return getDao().insert(entitys);
        } catch (Exception e) {
            ApiLogger.error("BaseDb insert entitys error", e);
        }
        return null;
    }

    public Object insert(String ql, final String[] paramNames, final Object... values) {
        try {
            return getDao().insert(ql, paramNames, values);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public int delete(T entity) {
        try {
            return getDao().delete(entity);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int delete(List<T> entitys) {
        try {
            return getDao().delete(entitys);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int delete(String ql, final String[] paramNames, final Object... values) {
        try {
            return getDao().delete(ql, paramNames, values);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int update(T entity) {
        try {
            return getDao().update(entity);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int update(List<T> entitys) {
        try {
            return getDao().update(entitys);
        } catch (Exception e) {
        }
        return 0;
    }

    public int update(String ql, final String[] paramNames, final Object... values) {
        try {
            return getDao().update(ql, paramNames, values);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public T get(PK id) {
        try {
            return getDao().get(id);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public Object get(String ql, final String[] paramNames, final Object... values) {
        try {
            return getDao().get(ql, paramNames, values);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<T> find(T entity) {
        try {
            return getDao().find(entity);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public long findCount(T entity) {
        try {
            return getDao().findCount(entity);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public List<T> find(String ql, final String[] paramNames, final Object... values) {
        try {
            return getDao().find(ql, paramNames, values);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public <X> List<X> findCustom(String hql, String[] paramNames, final Object... values) {
        try {
            return getDao().find(hql, paramNames, values);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public long findCount(String ql, final String[] paramNames, final Object... values) {
        try {
            return getDao().findCount(ql, paramNames, values);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public Page<T> find(Page<T> page, T entity) {
        try {
            return getDao().find(page, entity);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public Page<T> find(Page<T> page, String ql, final String[] paramNames, final Object... values) {
        try {
            return getDao().find(page, ql, paramNames, values);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


}