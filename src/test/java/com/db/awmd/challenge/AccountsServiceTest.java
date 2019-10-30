package com.db.awmd.challenge;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

import java.math.BigDecimal;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import com.db.awmd.challenge.domain.Account;
import com.db.awmd.challenge.exception.DuplicateAccountIdException;
import com.db.awmd.challenge.exception.InsufficientBalanceException;
import com.db.awmd.challenge.exception.InvalidTransferAmountException;
import com.db.awmd.challenge.service.AccountsService;

@RunWith(SpringRunner.class)
@SpringBootTest
public class AccountsServiceTest {

	@Autowired
	private AccountsService accountsService;
	private MockMvc mockMvc;

	@Test
	public void addAccount() throws Exception {
		Account account = new Account("Id-123");
		account.setBalance(new BigDecimal(1000));
		this.accountsService.createAccount(account);

		assertThat(this.accountsService.getAccount("Id-123")).isEqualTo(account);
	}

	@Test
	public void addAccount_failsOnDuplicateId() throws Exception {
		String uniqueId = "Id-" + System.currentTimeMillis();
		Account account = new Account(uniqueId);
		this.accountsService.createAccount(account);

		try {
			this.accountsService.createAccount(account);
			fail("Should have failed when adding duplicate account");
		} catch (DuplicateAccountIdException ex) {
			assertThat(ex.getMessage()).isEqualTo("Account id " + uniqueId + " already exists!");
		}

	}

	

	@Before
	public void prepareMockMvc() {
		// Reset the existing accounts before each test.
		accountsService.getAccountsRepository().clearAccounts();

		Account accountFrom = new Account("Id-mk_123");
		accountFrom.setBalance(new BigDecimal(1000));
		this.accountsService.createAccount(accountFrom);

		Account accountTo = new Account("Id-mk_456");
		accountTo.setBalance(new BigDecimal(1000));
		this.accountsService.createAccount(accountTo);
	}
	@Test
	public void transfer_failsOnNegativeAmount() throws Exception {
				
		try {
			this.accountsService.transfer("Id-mk_123", "Id-mk_456", -100);
			fail("Should have failed when trasfering negative balance.");
		} catch (InvalidTransferAmountException ex) {
			assertThat(ex.getMessage()).isEqualTo("The amount to transfer should always be a positive number.");
		}
	}
	@Test
	public void transfer_failsOnMoreAmount() throws Exception {
		
		try {
			this.accountsService.transfer("Id-mk_123", "Id-mk_456", 2000);
			fail("Should not be possible for an account to end up with negative balance");
		} catch (InsufficientBalanceException ex) {
			assertThat(ex.getMessage())
					.isEqualTo("It should not be possible for an account to end up with negative balance");
		}
	}

	@Test
	public void transfer_passOnpositiveAmount() throws Exception {
		//TODO : add test for pass scenarios 
	}
}
