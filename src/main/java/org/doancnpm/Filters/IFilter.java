package org.doancnpm.Filters;

import javafx.collections.ObservableList;
import org.doancnpm.Models.MatHang;

public interface IFilter<T> {
    public ObservableList<T> Filter();
}
