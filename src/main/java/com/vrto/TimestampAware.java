package com.vrto;

import java.time.ZonedDateTime;

public interface TimestampAware<T> {

    T getId();

    ZonedDateTime getCreatedAt();

    void setCreatedAt(ZonedDateTime createdAt);

    ZonedDateTime getUpdatedAt();

    void setUpdatedAt(ZonedDateTime updatedAt);
}
