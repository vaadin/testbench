/**
 * Copyright (C) 2000-2026 Vaadin Ltd
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.vaadin.flow.component.virtuallist;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.LongStream;

public final class UserData {
    public static final int USER_COUNT = 10000;

    private final List<User> users;

    // private constructor so can only be instantiated by getInstance
    private UserData() {
        users = LongStream.range(0, USER_COUNT).mapToObj(UserData::createUser)
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

    public static User get(int index) {
        return all().get(index);
    }

    public static User first() {
        return get(0);
    }

    public static User last() {
        return get(all().size() - 1);
    }

    public static User any() {
        return get(getAnyValidIndex());
    }

    public static int getAnyValidIndex() {
        return ThreadLocalRandom.current().nextInt(0, all().size());
    }

}
