package me.neilellis.dollar.store;

import me.neilellis.dollar.var;
import org.jetbrains.annotations.NotNull;

/**
 * @author <a href="http://uk.linkedin.com/in/neilellis">Neil Ellis</a>
 */
public interface DollarStore {

    @NotNull
    var get(String location);

    @NotNull
    var pop(String location, int timeoutInMillis);

    void push(String location, var value);

    void set(String location, var value);

    void set(String location, var value, int expiryInMilliseconds);

}