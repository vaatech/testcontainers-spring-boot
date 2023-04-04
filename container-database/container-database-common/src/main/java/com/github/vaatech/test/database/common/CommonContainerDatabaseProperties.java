package com.github.vaatech.test.database.common;

import com.github.vaatech.test.common.properties.CommonContainerProperties;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class CommonContainerDatabaseProperties extends CommonContainerProperties {

  protected ConnectionInfo connectionInfo;

  public record ConnectionInfo(
      String username, String password, String database, String host, Integer... exposedPorts) {
    public ConnectionInfo {
      if (username == null || username.isBlank()) {
        username = "test";
      }
      if (password == null || password.isBlank()) {
        password = "test";
      }
      if (database == null || database.isBlank()) {
        database = "test_db";
      }
      if (host == null || host.isBlank()) {
        host = "localhost";
      }
      if (exposedPorts == null || exposedPorts.length == 0) {
        exposedPorts = new Integer[0];
      }
    }
  }
}
