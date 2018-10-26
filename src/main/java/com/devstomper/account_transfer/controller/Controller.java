package com.devstomper.account_transfer.controller;


import com.devstomper.account_transfer.model.Account;
import com.devstomper.account_transfer.model.TransactionResult;
import com.devstomper.account_transfer.model.TransferTransaction;
import com.devstomper.account_transfer.service.AccountingService;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

/**
 * Request handler
 * @author asinitsyn
 * Date: 24.10.2018
 */
@Path("accounts")
@Produces(MediaType.APPLICATION_JSON)
public class Controller {

    /** Bank accounting service */
    private AccountingService service;

    @Inject
    public Controller(AccountingService service) {
        this.service = service;
    }

    /**
     * Returns response with list of accounts
     * @return {@link Response} response with list of accounts
     */
    @GET
    @Path(("/all"))
    public Response handleRetrieveAllAccountsRequest() {
        try {
            List<Account> accounts = service.retrieveAllAccounts();
            return Response.ok(accounts).build();
        } catch (Exception e) {
            return Response.serverError().build();
        }
    }

    /**
     * Returns response with requested account details
     * @param accountId requested account ID
     * @return {@link Response} response with requested account details
     */
    @GET
    @Path("/{accountId}")
    public Response handleRetrieveAccountByIdRequest(@PathParam("accountId") String accountId) {
        try {
            Account account = service.retrieveAccountById(accountId);
            return account != null ?
                    Response.ok(account).build() : Response.serverError().status(404).entity("").build();
        } catch (Exception e) {
            return Response.serverError().build();
        }
    }

    /**
     * Handle
     * @return {@link Response} response with requested account details
     */
    @PUT
    @Path(("/transfer"))
    @Consumes(MediaType.APPLICATION_JSON)
    public Response handleTransferTransactionRequest(TransferTransaction transaction) {
        try {
            TransactionResult result = service.transfer(transaction);
            return Response.ok(result).build();
        } catch (Exception e) {
            return Response.serverError().build();
        }
    }

}
