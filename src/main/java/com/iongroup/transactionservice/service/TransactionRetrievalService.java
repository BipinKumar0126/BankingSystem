package com.iongroup.transactionservice.service;

import java.time.LocalDate;
import java.util.List;

import com.iongroup.accountservice.dao.IAccountDao;
import com.iongroup.accountservice.exception.AccountNotExistException;
import com.iongroup.accountservice.model.Account;
import com.iongroup.accountservice.service.AccountValidationService;
import com.iongroup.accountservice.service.IAccountValidationService;
import com.iongroup.transactionservice.dao.ITransactionDao;
import com.iongroup.transactionservice.exception.InvalidDateException;
import com.iongroup.transactionservice.model.Transaction;

public class TransactionRetrievalService implements ITransactionRetrievalService {

	private final IAccountValidationService accValidationService;
	private final ITransactionValidationService traxValidationService;
	private final ITransactionDao transectionDao;
	private final IAccountDao accountDao;


	public TransactionRetrievalService(IAccountDao accountDao, ITransactionDao transectionDao) {
		this.transectionDao = transectionDao;
		this.accountDao = accountDao;
		this.accValidationService = new AccountValidationService(accountDao);
		this.traxValidationService = new TransactionValidationService(accountDao);
	}
	
	@Override
	public double getBalance(Long accountNumber) throws AccountNotExistException {
		double balance = 0;
		accValidationService.validateAccount(accountNumber);
		Account account = accountDao.getAccount(accountNumber);
		
		account.getLock().lock();
		try {
			balance = transectionDao.getBalance(accountNumber);
		}finally {
			account.getLock().unlock();
		}
		return balance;
	}

	@Override
	public List<Transaction> getLastTenTrasactions(Long accountNumber) throws AccountNotExistException {
		
		accValidationService.validateAccount(accountNumber);
		List<Transaction> trax = null;
		Account account = accountDao.getAccount(accountNumber);
		
		account.getLock().lock();
		try {
			trax = transectionDao.getLastTenTrasactions(accountNumber);
		}finally {
			account.getLock().unlock();
		}
		return trax;
	}

	@Override
	public List<Transaction> getTrasactionsByTimeIntarval(Long accountNumber, LocalDate fromDate, LocalDate toDate) throws AccountNotExistException, InvalidDateException {

		traxValidationService.validateTimeIntarval(fromDate, toDate);
		accValidationService.validateAccount(accountNumber);

		List<Transaction> trax = null;
		Account account = accountDao.getAccount(accountNumber);
		
		account.getLock().lock();
		try {
			trax = transectionDao.getTrasactionsByTimeIntarval(accountNumber, fromDate, toDate);
		}finally {
			account.getLock().unlock();
		}
		return trax;
	}
}
