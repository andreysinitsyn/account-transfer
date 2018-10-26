package com.devstomper.account_transfer.dao;

import java.util.List;

/**
 * Generic DAO
 * @param <T> source type
 * @author asinitsyn
 * Date: 24.10.2018
 */
public interface GenericDao<T> {

    List<T> retrieveAll() throws Exception;

    T retrieve(String id) throws Exception;

    void update(T... entities) throws Exception;

}
