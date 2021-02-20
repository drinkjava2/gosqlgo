package com.demo.entity;

import com.github.drinkjava2.jdialects.annotation.jpa.Id;
import com.github.drinkjava2.jsqlbox.ActiveEntity;

/**
 * The account with a certain amount of money
 * 
 * @author Yong Zhu
 */
public class Account implements ActiveEntity<Account> {
	@Id
	String id;

	Integer amount;

	public String getId() {
		return id;
	}

	public Account setId(String id) {
		this.id = id;
		return this;
	}

	public Integer getAmount() {
		return amount;
	}

	public Account setAmount(Integer amount) {
		this.amount = amount;
		return this;
	}

}
