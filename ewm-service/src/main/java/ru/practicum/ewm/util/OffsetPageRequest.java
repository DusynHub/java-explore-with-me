package ru.practicum.ewm.util;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

public class OffsetPageRequest extends PageRequest {

    private final int fromRow;

    protected OffsetPageRequest(int fromRow, int size, Sort sort) {
        super(fromRow/size, size, sort);
        this.fromRow = fromRow;
    }

    public OffsetPageRequest(int fromRow, int size) {
        super(fromRow / size, size, Sort.unsorted());
        this.fromRow = fromRow;
    }

    public static OffsetPageRequest of(int fromRow, int size, Sort sort) {
        return new OffsetPageRequest(fromRow, size, sort);
    }

    public static OffsetPageRequest of(int page, int size) {
        return new OffsetPageRequest(page, size);
    }

    @Override
    public long getOffset() {
        return fromRow;
    }
}
