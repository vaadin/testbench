package com.vaadin.flow.component.virtuallist;

import java.util.List;
import java.util.stream.LongStream;

public final class UserData {
  public static final int USER_COUNT = 10000;

  private final List<User> users;

  // private constructor so can only be instantiated by getInstance
  private UserData() {
    users = LongStream.range(0, USER_COUNT)
            .mapToObj(UserData::createUser)
            .toList();
  }

  private static User createUser(long idx) {
    var user = new User();
    user.setKey(idx);
    user.setFirstName("First-" + idx);
    user.setLastName("Last-" + idx);
    user.setActive(idx % 5 != 0);
    return user;
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
