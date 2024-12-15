USE QUANLYDAILY_TEST
---Tu dong cap nhat don gia xuat = 102% don gia nhap, da check

CREATE TRIGGER ThamSo_CapNhatDonGiaXuatMatHang
ON THAMSO AFTER UPDATE
AS
BEGIN
        Declare @TyLeDonGiaXuat float;
            
        SELECT @TyLeDonGiaXuat = TyLeDonGiaXuat FROM THAMSO
        
        UPDATE MatHang
        SET DonGiaXuat = DonGiaNhap * @TyLeDonGiaXuat
END
GO

CREATE TRIGGER MatHang_TuDongCapNhatDonGiaXuat
ON MATHANG AFTER INSERT, UPDATE
AS
BEGIN
        Declare @TyLeDonGiaXuat float;
        Declare @DonGiaNhap money, @ID int;
        
        Select @DonGiaNhap = inserted.DonGiaNhap From inserted;
        Select @ID = inserted.ID From inserted;
        
        SELECT @TyLeDonGiaXuat = TyLeDonGiaXuat FROM THAMSO
        
        UPDATE MatHang
        SET DonGiaXuat = @DonGiaNhap * @TyLeDonGiaXuat
        WHERE ID = @ID;
END
GO

---Tu dong tang so luong mat hang trong kho, da check
CREATE TRIGGER CTPN_TuDongCapNhatSoLuongMatHang
ON CHITIETPHIEUNHAP 
AFTER INSERT
AS
BEGIN
    -- Update the MatHang table with the aggregated SoLuong
    UPDATE mh
    SET mh.SoLuong = mh.SoLuong + i.TotalSoLuong
    FROM MatHang mh
    JOIN (
        SELECT MaMatHang, SUM(SoLuong) AS TotalSoLuong
        FROM inserted
        GROUP BY MaMatHang
    ) i ON mh.ID = i.MaMatHang;
END;
GO

---Kiem tra trong kho co du hang de xuat k, da check
CREATE TRIGGER CTPX_KiemTraCoDuHangTrongKho
ON CHITIETPHIEUXUAT
FOR INSERT
AS
BEGIN
    -- Declare table variables to hold the inserted rows and the rows with insufficient stock
    DECLARE @InsufficientStock TABLE (
        MaMatHang INT,
        SoLuongBot INT,
        SoLuongTrongKho INT
    );

    -- Insert rows into the @InsufficientStock table variable where the stock is insufficient
    INSERT INTO @InsufficientStock (MaMatHang, SoLuongBot, SoLuongTrongKho)
    SELECT i.MaMatHang, i.SoLuong, m.SoLuong
    FROM inserted i
    JOIN MatHang m ON i.MaMatHang = m.ID
    WHERE i.SoLuong > m.SoLuong;

    -- If there are any rows with insufficient stock, create a detailed error message and rollback the transaction
    IF EXISTS (SELECT 1 FROM @InsufficientStock)
    BEGIN
        Throw 50000, N'Không đủ hàng để xuất', 1;
        ROLLBACK TRANSACTION;
    END
END;
GO


CREATE TRIGGER CTPX_TuDongCapNhatSoLuongMatHang
ON CHITIETPHIEUXUAT 
AFTER INSERT
AS
BEGIN
    -- Update the MatHang table with the aggregated SoLuong
    UPDATE mh
    SET mh.SoLuong = mh.SoLuong - i.TotalSoLuong
    FROM MatHang mh
    JOIN (
        SELECT MaMatHang, SUM(SoLuong) AS TotalSoLuong
        FROM inserted
        GROUP BY MaMatHang
    ) i ON mh.ID = i.MaMatHang;
END;
GO


---Tu dong cap nhat thanh tien TRC khi them chi tiet phieu nhap, da check
CREATE TRIGGER CTPN_CapNhatThanhTien
ON CHITIETPHIEUNHAP 
INSTEAD OF INSERT
AS
BEGIN
    -- Temporary table to hold intermediate results
    DECLARE @TempTable TABLE (
        MaPhieuNhap INT,
        MaMatHang INT,
        SoLuong INT,
        DonGiaNhap MONEY,
        ThanhTien MONEY
    );

    -- Insert calculated ThanhTien into the temporary table
    INSERT INTO @TempTable (MaPhieuNhap, MaMatHang, SoLuong, DonGiaNhap, ThanhTien)
    SELECT 
        i.MaPhieuNhap,
        i.MaMatHang,
        i.SoLuong,
        i.DonGiaNhap,
        i.SoLuong * i.DonGiaNhap AS ThanhTien
    FROM 
        inserted i;

    -- Insert the results from the temporary table into ChiTietPhieuNhap
    INSERT INTO ChiTietPhieuNhap (MaPhieuNhap, MaMatHang, SoLuong, DonGiaNhap, ThanhTien)
    SELECT 
        MaPhieuNhap,
        MaMatHang,
        SoLuong,
        DonGiaNhap,
        ThanhTien
    FROM 
        @TempTable;
END;
GO

---Tu dong cap nhat tong tien cua phieu thu SAU khi them chi tiet phieu nhap, da check
CREATE TRIGGER CTPN_CapNhatTongTien
ON CHITIETPHIEUNHAP 
AFTER INSERT
AS
BEGIN
    -- Ensure updates are in a set-based operation
    UPDATE PhieuNhapHang
    SET TongTien = pn.TongTien + i.TotalThanhTien
    FROM PhieuNhapHang pn
    JOIN (
        SELECT MaPhieuNhap, SUM(ThanhTien) AS TotalThanhTien
        FROM inserted
        GROUP BY MaPhieuNhap
    ) i ON pn.ID = i.MaPhieuNhap;
END;
GO 

CREATE TRIGGER CTPX_CapNhatThanhTien
ON CHITIETPHIEUXUAT 
INSTEAD OF INSERT
AS
BEGIN
    -- Temporary table to hold intermediate results
    DECLARE @TempTable TABLE (
        MaPhieuXuat INT,
        MaMatHang INT,
        SoLuong INT,
		DonGiaXuat Money,
        ThanhTien MONEY
    );

    -- Insert calculated ThanhTien into the temporary table
    INSERT INTO @TempTable (MaPhieuXuat, MaMatHang, SoLuong,DonGiaXuat,ThanhTien)
    SELECT 
        i.MaPhieuXuat,
        i.MaMatHang,
        i.SoLuong,
		i.DonGiaXuat,
        i.SoLuong * i.DonGiaXuat AS ThanhTien
    FROM 
        inserted i;
     

    -- Insert the results from the temporary table into ChiTietPhieuXuat
    INSERT INTO ChiTietPhieuXuat (MaPhieuXuat, MaMatHang, SoLuong,DonGiaXuat, ThanhTien)
    SELECT 
        MaPhieuXuat,
        MaMatHang,
        SoLuong,
		DonGiaXuat,
        ThanhTien
    FROM 
        @TempTable;
END;
GO



---da check
CREATE TRIGGER CTPX_CapNhatTongTien
ON CHITIETPHIEUXUAT
AFTER INSERT
AS
BEGIN
    -- Ensure updates are in a set-based operation
    UPDATE pxh
    SET pxh.TongTien = pxh.TongTien + i.TotalThanhTien
    FROM PhieuXuatHang pxh
    JOIN (
        SELECT MaPhieuXuat, SUM(ThanhTien) AS TotalThanhTien
        FROM inserted
        GROUP BY MaPhieuXuat
    ) i ON pxh.ID = i.MaPhieuXuat;
END;
GO

---tang no cua dai ly khi xuat hang, da check
CREATE TRIGGER CTPX_TangNoDaiLy
ON CHITIETPHIEUXUAT
FOR INSERT
AS
BEGIN
    -- Ensure updates are in a set-based operation
    DECLARE @TempTable TABLE (
        MaPhieuXuat INT,
        ThanhTien MONEY
    );

    -- Insert the inserted rows into the temporary table
    INSERT INTO @TempTable (MaPhieuXuat, ThanhTien)
    SELECT MaPhieuXuat, ThanhTien
    FROM inserted;

    -- Update the DaiLy table with the aggregated ThanhTien
    UPDATE dl
    SET dl.NoHienTai = dl.NoHienTai + i.TotalThanhTien
    FROM DaiLy dl
    JOIN (
        SELECT pxh.MaDaiLy, SUM(tt.ThanhTien) AS TotalThanhTien
        FROM @TempTable tt
        JOIN PhieuXuatHang pxh ON tt.MaPhieuXuat = pxh.ID
        GROUP BY pxh.MaDaiLy
    ) i ON dl.ID = i.MaDaiLy;
END;
GO


---tang no cua dai ly khi thu tien, da check
CREATE TRIGGER PhieuThuTien_GiamNoDaiLy
ON PhieuThuTien For INSERT
AS
BEGIN
        Declare @MaDaiLy Int, @SoTienThu money, @NoHienTai money;
        Select @MaDaiLy = MaDaiLy From inserted 
        Select @SoTienThu = SoTienThu From inserted
        Select @NoHienTai = NoHienTai from DaiLy Where ID = @MaDaily
        
        Print @NoHienTai
        Print @SoTienThu
        IF(@NoHienTai<@SoTienThu)
        BEGIN
                THROW 54000,N'Không được thu nhiều hơn nợ hiện tại của đại lý!',1;
                ROLLBACK TRANSACTION
        END
        ELSE
        BEGIN
                Update DaiLy
                Set NoHienTai = NoHienTai - @SoTienThu
                Where ID = @MaDaiLy
        END
END
GO

CREATE TRIGGER QD1_SoDaiLyToiDaMoiQuan
ON DAILY FOR INSERT, UPDATE
AS 
BEGIN
        DECLARE @soDaiLyHienTai int;
        DECLARE @maQuan int;
        DECLARE @soDaiLyToiDa int;

	SELECT @maQuan = inserted.maQuan From inserted;
	SELECT @soDaiLyToiDa = SoDaiLyToiDaMoiQuan FROM THAMSO

	SELECT @soDaiLyHienTai = COUNT(*) FROM DAILY
	WHERE DAILY.MaQuan = @maQuan
        
	IF(@soDaiLyHienTai>@soDaiLyToiDa)
	BEGIN
		THROW 51000,N'Số đại lý trong quận vượt quá mức cho phép',1
		ROLLBACK TRANSACTION
	END
END
---da check
CREATE TRIGGER QD3_SoNoToiDa
ON DAILY 
FOR UPDATE
AS 
BEGIN
	Declare @ChoPhepVuotNo int;
	SELECT @ChoPhepVuotNo = ChoPhepVuotNo From ThamSo;
	IF(@ChoPhepVuotNo != 0)
	BEGIN
		DECLARE @SoNoToiDa money, @MaLoaiDaiLy INT, @NoHienTai MONEY;

		SELECT @MaLoaiDaiLy = inserted.MaLoaiDaiLy, @NoHienTai = inserted.NoHienTai FROM inserted;

		SELECT @SoNoToiDa = SoNoToiDa From LOAIDAILY WHERE ID = @MaLoaiDaiLy;
		IF(@NoHienTai > @SoNoToiDa)
		BEGIN
			THROW 52000, N'Đại lý nợ quá mức cho phép',1;
			ROLLBACK TRANSACTION;
		END
	END
END
GO



--- UNIQUE CHECK ---
CREATE TRIGGER DAILY_UniqueCheck
ON DAILY
AFTER INSERT, UPDATE
AS
BEGIN
    SET NOCOUNT ON;

    DECLARE @ErrorMessage NVARCHAR(4000);
    DECLARE @ViolatingDienThoai CHAR(10);
    DECLARE @ViolatingEmail NVARCHAR(100);

    -- Check for duplicate DienThoai
    SELECT TOP 1 @ViolatingDienThoai = DienThoai
    FROM DAILY
    WHERE DienThoai IN (SELECT DienThoai FROM inserted)
    GROUP BY DienThoai
    HAVING COUNT(*) > 1;

    IF @ViolatingDienThoai IS NOT NULL
    BEGIN
        SET @ErrorMessage = N'Số điện thoại ' + @ViolatingDienThoai + N' đã tồn tại!';
        RAISERROR(@ErrorMessage, 16, 1);
        ROLLBACK TRANSACTION;
        RETURN;
    END

    -- Check for duplicate Email
    SELECT TOP 1 @ViolatingEmail = Email
    FROM DAILY
    WHERE Email IN (SELECT Email FROM inserted)
    GROUP BY Email
    HAVING COUNT(*) > 1;

    IF @ViolatingEmail IS NOT NULL
    BEGIN
        SET @ErrorMessage = N'Email ' + @ViolatingEmail + N' đã tồn tại!';
        RAISERROR(@ErrorMessage, 16, 1);
        ROLLBACK TRANSACTION;
        RETURN;
    END
END
GO

CREATE TRIGGER NHANVIEN_UniqueCheck
ON NHANVIEN
AFTER INSERT, UPDATE
AS
BEGIN
    SET NOCOUNT ON;

    DECLARE @ErrorMessage NVARCHAR(4000);
    DECLARE @ViolatingSDT VARCHAR(11);
    DECLARE @ViolatingEmail VARCHAR(100);

    -- Check for duplicate SDT
    SELECT TOP 1 @ViolatingSDT = SDT
    FROM NHANVIEN
    WHERE SDT IN (SELECT SDT FROM inserted)
    GROUP BY SDT
    HAVING COUNT(*) > 1;

    IF @ViolatingSDT IS NOT NULL
    BEGIN
        SET @ErrorMessage = N'Số điện thoại ' + @ViolatingSDT + N' đã tồn tại!';
        RAISERROR(@ErrorMessage, 16, 1);
        ROLLBACK TRANSACTION;
        RETURN;
    END

    -- Check for duplicate Email
    SELECT TOP 1 @ViolatingEmail = Email
    FROM NHANVIEN
    WHERE Email IN (SELECT Email FROM inserted)
    GROUP BY Email
    HAVING COUNT(*) > 1;

    IF @ViolatingEmail IS NOT NULL
    BEGIN
        SET @ErrorMessage =  N'Email ' + @ViolatingEmail + N' đã tồn tại!';
        RAISERROR(@ErrorMessage, 16, 1);
        ROLLBACK TRANSACTION;
        RETURN;
    END
END
GO


CREATE TRIGGER trg_QUAN_UniqueCheck
ON QUAN
AFTER INSERT, UPDATE
AS
BEGIN
    SET NOCOUNT ON;

    DECLARE @ErrorMessage NVARCHAR(4000);
    DECLARE @ViolatingTenQuan NVARCHAR(100);

    -- Check for duplicate TenQuan
    SELECT TOP 1 @ViolatingTenQuan = TenQuan
    FROM QUAN
    WHERE TenQuan IN (SELECT TenQuan FROM inserted)
    GROUP BY TenQuan
    HAVING COUNT(*) > 1;

    IF @ViolatingTenQuan IS NOT NULL
    BEGIN
        SET @ErrorMessage = 'Quận ' + @ViolatingTenQuan + ' đã tồn tại';
        RAISERROR(@ErrorMessage, 16, 1);
        ROLLBACK TRANSACTION;
        RETURN;
    END
END
GO



CREATE TRIGGER trg_LOAIDAILY_UniqueCheck
ON LOAIDAILY
AFTER INSERT, UPDATE
AS
BEGIN
    SET NOCOUNT ON;

    DECLARE @ErrorMessage NVARCHAR(4000);
    DECLARE @ViolatingTenLoai NVARCHAR(100);

    -- Check for duplicate TenLoai
    SELECT TOP 1 @ViolatingTenLoai = TenLoai
    FROM LOAIDAILY
    WHERE TenLoai IN (SELECT TenLoai FROM inserted)
    GROUP BY TenLoai
    HAVING COUNT(*) > 1;

    IF @ViolatingTenLoai IS NOT NULL
    BEGIN
        SET @ErrorMessage = 'Loại đại lý ' + @ViolatingTenLoai + ' đã tồn tại';
        RAISERROR(@ErrorMessage, 16, 1);
        ROLLBACK TRANSACTION;
        RETURN;
    END
END
GO



CREATE TRIGGER trg_CHUCVU_UniqueCheck
ON CHUCVU
AFTER INSERT, UPDATE
AS
BEGIN
    SET NOCOUNT ON;

    DECLARE @ErrorMessage NVARCHAR(4000);
    DECLARE @ViolatingTenChucVu NVARCHAR(100);

    -- Check for duplicate TenChucVu
    SELECT TOP 1 @ViolatingTenChucVu = TenChucVu
    FROM CHUCVU
    WHERE TenChucVu IN (SELECT TenChucVu FROM inserted)
    GROUP BY TenChucVu
    HAVING COUNT(*) > 1;

    IF @ViolatingTenChucVu IS NOT NULL
    BEGIN
        SET @ErrorMessage = 'Chức vụ ' + @ViolatingTenChucVu + ' đã tồn tại';
        RAISERROR(@ErrorMessage, 16, 1);
        ROLLBACK TRANSACTION;
        RETURN;
    END
END
GO


CREATE TRIGGER trg_DONVITINH_UniqueCheck
ON DONVITINH
AFTER INSERT, UPDATE
AS
BEGIN
    SET NOCOUNT ON;

    DECLARE @ErrorMessage NVARCHAR(4000);
    DECLARE @ViolatingTenDonViTinh NVARCHAR(100);

    -- Check for duplicate TenDonViTinh
    SELECT TOP 1 @ViolatingTenDonViTinh = TenDonViTinh
    FROM DONVITINH
    WHERE TenDonViTinh IN (SELECT TenDonViTinh FROM inserted)
    GROUP BY TenDonViTinh
    HAVING COUNT(*) > 1;

    IF @ViolatingTenDonViTinh IS NOT NULL
    BEGIN
        SET @ErrorMessage = 'Đơn vị tính ' + @ViolatingTenDonViTinh + ' đã tồn tại';
        RAISERROR(@ErrorMessage, 16, 1);
        ROLLBACK TRANSACTION;
        RETURN;
    END
END
GO

