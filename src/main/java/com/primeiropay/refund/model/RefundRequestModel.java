package com.primeiropay.refund.model;

import java.math.BigDecimal;

public class RefundRequestModel {
	
	private String entity_id;
	
	private BigDecimal amount;
	
	private String currency;
	
	public String getEntity_id() {
		return entity_id;
	}

	public void setEntity_id(String entity_id) {
		this.entity_id = entity_id;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	@Override
	public String toString() {		
		return "entityId="+getEntity_id()
			+	"&amount="+getAmount()
			+	"&currency="+getCurrency();
	}
}
