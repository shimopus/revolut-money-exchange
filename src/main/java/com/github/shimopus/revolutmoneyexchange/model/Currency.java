package com.github.shimopus.revolutmoneyexchange.model;

/**
 * The dictionary wrapper related to the database table <code>currency</code>
 */
public enum Currency {
    USD(1), EUR(2), RUB(3);

    private int id;

    Currency(int id) {
        this.id = id;
    }

    public static Currency valueOf(int id) {
        for(Currency e : values()) {
            if(e.id == id) return e;
        }

        return null;
    }

    public int getId() {
        return id;
    }
}
