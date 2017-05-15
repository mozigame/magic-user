package com.magic.user.storage.base;

import com.magic.api.commons.model.Page;

import java.io.Serializable;
import java.util.List;

public abstract interface BaseDbService<T, PK extends Serializable> {
    /**
     * 保存新增对象.
     */
    public PK insert(final T entity);

    /**
     * 保存新增对象.
     */
    public PK insert(final PK id, final T entity);

    /**
     * 保存新增对象列表.
     */
    public List<PK> insert(final List<T> entitys);

    /**
     * 保存新增对象.
     */
    public Object insert(final String ql, final String[] paramNames, final Object... values);

    /**
     * 删除对象.
     *
     * @param entity 对象必须是session中的对象或含id属性的transient对象.
     */
    public int delete(final T entity);

    /**
     * 删除对象列表.
     *
     * @param entity 对象必须是session中的对象或含id属性的transient对象.
     */
    public int delete(final List<T> entitys);

    /**
     * 删除对象.
     *
     * @param paramNames 对象必须是session中的对象或含id属性的transient对象.
     */
    public int delete(final String ql, final String[] paramNames, final Object... values);

    /**
     * 保存修改的对象.
     */
    public int update(final T entity);

    /**
     * 保存修改的对象列表.
     */
    public int update(final List<T> entitys);

    /**
     * 保存修改的对象.
     */
    public int update(final String ql, final String[] paramNames, final Object... values);

    /**
     * 按id获取对象.
     */
    public T get(final PK id);

    /**
     * 获取对象.
     */
    public Object get(final String ql, final String[] paramNames, final Object... values);


    /**
     * 查询对象列表.
     *
     * @param T 参数对象.
     * @return List<T> 查询结果对象列表
     */
    public List<T> find(T entity);

    /**
     * 查询对象列表的数量.
     *
     * @param T 参数对象.
     * @return 查询结果的数量
     */
    public long findCount(T entity);

    /**
     * 查询对象列表.
     *
     * @param ql
     * @param values 参数对象.
     * @return List<X> 查询结果对象列表
     */
    public List<T> find(final String ql, final String[] paramNames, final Object... values);

    /**
     * 查询用户自定义的结果
     *
     * @param hql
     * @param paramNames
     * @param values
     * @param <X>
     * @return
     */
    <X> List<X> findCustom(String hql, String[] paramNames, final Object... values);

    /**
     * 查询对象列表的数量.
     *
     * @param ql
     * @param values 参数对象.
     * @return 查询结果的数量
     */
    public long findCount(final String ql, final String[] paramNames, final Object... values);

    /**
     * 分页查询对象列表.
     *
     * @param page   分页参数对象
     * @param values 查询参数对象.
     * @return Page<T> 查询结果的分页对象
     */
    public Page<T> find(Page<T> page, final T entity);

    /**
     * 分页查询对象列表.
     *
     * @param page   分页参数对象
     * @param ql
     * @param values 查询参数对象.
     * @return Page<T> 查询结果的分页对象
     */
    public Page<T> find(Page<T> page, final String ql, final String[] paramNames, final Object... values);

}
