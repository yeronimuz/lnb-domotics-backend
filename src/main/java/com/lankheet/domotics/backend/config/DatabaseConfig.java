package com.lankheet.domotics.backend.config;

import org.hibernate.validator.constraints.NotEmpty;
import com.fasterxml.jackson.annotation.JsonProperty;

public class DatabaseConfig {
	
	@NotEmpty
	private String url;
	
	@NotEmpty
	private String driver;
	
	@NotEmpty
	private String userName;
	
	@NotEmpty
	private String password;
	
	@JsonProperty
	public String getUrl() {
		return url;
	}

	@JsonProperty
	public void setUrl(String url) {
		this.url = url;
	}
	
	@JsonProperty
	public String getUserName() {
		return userName;
	}

	@JsonProperty
	public void setUserName(String userName) {
		this.userName = userName;
	}

	@JsonProperty
	public String getPassword() {
		return password;
	}

	@JsonProperty
	public void setPassword(String passWord) {
		this.password = passWord;
	}
	
	@JsonProperty
	public String getDriver() {
		return driver;
	}
	
	@JsonProperty
	public void setDriver(String driver) {
		this.driver = driver;
	}
}

