package org.doancnpm.DAO;

import org.doancnpm.Models.DatabaseDriver;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ThamSoDAO {
    private static ThamSoDAO singleton = null;

    public static ThamSoDAO getInstance() {
        if (singleton == null) {
            singleton = new ThamSoDAO();
        }
        return singleton;
    }

    public void UpdateDLToiDaMoiQuan(int newQuan) throws SQLException {
        Connection conn = DatabaseDriver.getConnect();
        String sql = "Update ThamSo Set SoDaiLyToiDaMoiQuan = ?";

        assert conn != null;
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setInt(1,newQuan);
        pstmt.executeUpdate();
        pstmt.close();
        
    }
    public int GetDLToiDaMoiQuan() throws SQLException {
        Connection conn = DatabaseDriver.getConnect();
        String sql = "Select SoDaiLyToiDaMoiQuan From ThamSo";

        assert conn != null;
        PreparedStatement pstmt = conn.prepareStatement(sql);

        ResultSet resultSet = pstmt.executeQuery();
        int rs = -1;
        if(resultSet.next()){
            rs = resultSet.getInt("SoDaiLyToiDaMoiQuan");
        }

        pstmt.close();
        return rs;
    }
    public void UpdateTyLeNhapXuat(double newTiLe) throws SQLException {
        Connection conn = DatabaseDriver.getConnect();
        String sql = "Update ThamSo Set TyLeDonGiaXuat = ?";

        assert conn != null;
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setDouble(1,newTiLe);
        pstmt.executeUpdate();
        pstmt.close();
    }
    public void UpdateChoPhepVuotNo(Boolean newChoPhepVuotNo) throws SQLException {
        Connection conn = DatabaseDriver.getConnect();
        String sql = "Update ThamSo Set ChoPhepVuotNo = ?";

        assert conn != null;
        PreparedStatement pstmt = conn.prepareStatement(sql);
        if(newChoPhepVuotNo){
            pstmt.setInt(1,1);
        }else{
            pstmt.setInt(1,0);
        }

        pstmt.executeUpdate();
        pstmt.close();
    }
}
