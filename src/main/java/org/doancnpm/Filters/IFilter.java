package org.doancnpm.Filters;

import javafx.collections.ObservableList;
import org.doancnpm.Models.DaiLy;

public interface IFilter<T> {
    public ObservableList<DaiLy> Filter();
}
