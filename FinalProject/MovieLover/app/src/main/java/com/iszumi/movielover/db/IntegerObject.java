package com.iszumi.movielover.db;

import io.realm.RealmObject;

/**
 * Thanks to my sensei: @hendrawd
 */

public class IntegerObject extends RealmObject {

    public Integer integer;

    public Integer getInteger() {
        return integer;
    }

    public void setInteger(Integer integer) {
        this.integer = integer;
    }
}
