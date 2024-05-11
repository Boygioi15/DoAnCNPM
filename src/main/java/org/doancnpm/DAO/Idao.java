package org.doancnpm.DAO;

import javafx.beans.InvalidationListener;
import org.doancnpm.Models.DaiLy;

import java.sql.SQLException;
import java.util.ArrayList;

public interface Idao<T>{
    public int Insert(T t) throws SQLException;

    public int Update(int ID, T t) throws SQLException;

    public int Delete(int ID) throws SQLException;

    public T QueryID(int ID) throws SQLException;

    public ArrayList<T> QueryAll() throws SQLException;

    public void AddDatabaseListener(InvalidationListener listener);


}
