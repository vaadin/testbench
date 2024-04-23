package com.vaadin.flow.component.virtuallist;

import org.instancio.Instancio;

import java.util.List;

public final class UserData {
  public static final int USER_COUNT = 10000;

  private final List<User> users;

  // private constructor so can only be instantiated by getInstance
  private UserData() {
    users = Instancio.ofList(User.class)
            .size(USER_COUNT)
            .create();
  }

  // inner class to provide instance of class
  private static final class SingletonHelper {
    private static final UserData INSTANCE = new UserData();
  }

  private static UserData getInstance() {
    return SingletonHelper.INSTANCE;
  }

  public static List<User> all() {
    return getInstance().users;
  }

  public static User first() {
    return get(0);
  }

  public static User get(int index) {
    return all().get(index);
  }

  public static User last() {
    return get(all().size() - 1);
  }

}
