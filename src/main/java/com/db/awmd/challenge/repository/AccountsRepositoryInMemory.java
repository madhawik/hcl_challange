package com.db.awmd.challenge.repository;

import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.db.awmd.challenge.domain.Account;
import com.db.awmd.challenge.exception.DuplicateAccountIdException;
import com.db.awmd.challenge.exception.InsufficientBalanceException;
import com.db.awmd.challenge.exception.InvalidTransferAmountException;
import com.db.awmd.challenge.service.EmailNotificationService;

@Repository
public class AccountsRepositoryInMemory implements AccountsRepository {

	private final Map<String, Account> accounts = new ConcurrentHashMap<>();
	private EmailNotificationService emailNotificationService = new EmailNotificationService();

	@Override
	public void createAccount(Account account) throws DuplicateAccountIdException {
		Account previousAccount = accounts.putIfAbsent(account.getAccountId(), account);
		if (previousAccount != null) {
			throw new DuplicateAccountIdException("Account id " + account.getAccountId() + " already exists!");
		}
	}

	@Override
	public Account getAccount(String accountId) {
		return accounts.get(accountId);
	}

	@Override
	public void clearAccounts() {
		accounts.clear();
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED) // if an existing txn context exists continue with it, else create new txn
	public void transfer(String accountFromId, String accountToId, double amount) {
		if(amount <=0){
			throw new InvalidTransferAmountException("The amount to transfer should always be a positive number.");
		}
		String transferDescription = null;
		Account fromAccount = getAccount(accountFromId);
		Account toAccount = getAccount(accountToId);
		BigDecimal fromBalance = fromAccount.getBalance().subtract(BigDecimal.valueOf(amount));
		if (fromBalance.signum() == -1) {
			throw new InsufficientBalanceException("It should not be possible for an account to end up with negative balance");
		}
	
		BigDecimal toBalance = toAccount.getBalance();
		toAccount.setBalance(toBalance.add(BigDecimal.valueOf(amount)));
		transferDescription = "Amount " + amount + "transfred from account id " + accountFromId;
		emailNotificationService.notifyAboutTransfer(toAccount, transferDescription);
		fromAccount.setBalance(fromBalance);
		transferDescription = "Amount " + amount + "transfred to account id " + accountToId;
		emailNotificationService.notifyAboutTransfer(fromAccount, transferDescription);
	}

}
