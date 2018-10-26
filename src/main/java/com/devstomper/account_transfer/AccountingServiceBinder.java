package com.devstomper.account_transfer;

import com.devstomper.account_transfer.dao.GenericDao;
import com.devstomper.account_transfer.dao.impl.AccountH2DAO;
import com.devstomper.account_transfer.model.Account;
import com.devstomper.account_transfer.service.AccountingService;
import com.devstomper.account_transfer.service.impl.AccountingServiceImpl;
import org.glassfish.hk2.api.TypeLiteral;
import org.glassfish.hk2.utilities.binding.AbstractBinder;

import javax.inject.Singleton;

/**
 * Custom binder
 * @author asinitsyn
 * Date: 24.10.2018
 */
public class AccountingServiceBinder extends AbstractBinder {

    @Override
    protected void configure() {
        bindAsContract(AccountH2DAO.class).to(new TypeLiteral<GenericDao<Account>>() {}).in(Singleton.class);
        bind(AccountingServiceImpl.class).to(AccountingService.class).in(Singleton.class);
    }
}
