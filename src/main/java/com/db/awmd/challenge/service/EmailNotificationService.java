package com.db.awmd.challenge.service;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.db.awmd.challenge.domain.Account;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class EmailNotificationService implements NotificationService {

  @Override
  @Transactional(propagation=Propagation.MANDATORY) // must be called within an existing txn context
  public void notifyAboutTransfer(Account account, String transferDescription) {
    //THIS METHOD SHOULD NOT BE CHANGED - ASSUME YOUR COLLEAGUE WILL IMPLEMENT IT
    log
      .info("Sending notification to owner of {}: {}", account.getAccountId(), transferDescription);
  }

}
