package com.team1.paymentsystem.states;

import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

@Getter
@Setter
public abstract class AbstractState {
    protected String name;

    protected AbstractState(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AbstractState that)) return false;

        return Objects.equals(name, that.name);
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public int hashCode() {
        return name != null ? name.hashCode() : 0;
    }
}
