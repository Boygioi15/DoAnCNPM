DBCC CHECKIDENT (CHUCVU, RESEED, 0)
Insert into CHUCVU(TenChucVu) values(N'Nhân viên');
Insert into CHUCVU(TenChucVu) values(N'Admin');

SET DATEFORMAT DMY
Insert into NHANVIEN values(N'Lê Nhật Trường',N'Nam','13/04/2024','0756547365','lnt@gmail.com',1,5000000,'',0);
Insert into NHANVIEN values(N'Phạm Tuyết Khả',N'Nữ','14/04/2024','0379856534','ptk@gmail.com',1,5500000,'',0);
Insert into NHANVIEN values(N'Hoàng Gia Phong',N'Nam','15/04/2024','0168765497','winder@gmail.com',2,5550000,'',0);
Insert into NHANVIEN values(N'Lê Đức Thịnh',N'Nam','17/04/2024','0388651421','thinhleduc@gmail.com',2,6000000,'',0);
Insert into NHANVIEN values(N'Nguyễn Anh Quyền',N'Nam','22/05/2024','0373865627','boygioi85@gmail.com',2,66660000,'',0);

Insert into NHANVIEN values(N'Admin test',N'Nam','22/05/2024','0123456789','testAdmin@gmail.com',2,66660000,'',0);
Insert into NHANVIEN values(N'User test',N'Nam','22/05/2024','0987654321','testNhanVien@gmail.com',1,66660000,'',0);

Select * from NHANVIEN
Insert into TAIKHOAN values('admin','8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92',11);  --123456
Insert into TAIKHOAN values('user','8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92',12);  --123456


DBCC CHECKIDENT (QUAN, RESEED, 0)
Insert into QUAN(TenQuan) values(N'Quận 1');
Insert into QUAN(TenQuan) values(N'Quận 2');
Insert into QUAN(TenQuan) values(N'Quận 3');
Insert into QUAN(TenQuan) values(N'Quận 4');
Insert into QUAN(TenQuan) values(N'Quận 5');
Insert into QUAN(TenQuan) values(N'Quận 6');
Insert into QUAN(TenQuan) values(N'Quận 7');
Insert into QUAN(TenQuan) values(N'Quận 8');
Insert into QUAN(TenQuan) values(N'Quận 9');
Insert into QUAN(TenQuan) values(N'Quận 10');


DBCC CHECKIDENT (LOAIDAILY, RESEED, 0)
Insert into LOAIDAILY values(5000000,N'Loại 1',N'Loại 1 nợ tối đa 5 triệu');
Insert into LOAIDAILY values(10000000,N'Loại 2',N'Loại 2 nợ tối đa 10 triệu');

Select * from LOAIDAILY;
--- chuc vu
DBCC CHECKIDENT (CHUCVU, RESEED, 0)
Insert into CHUCVU(TenChucVu) values(N'Nhân viên');
Insert into CHUCVU(TenChucVu) values(N'Admin');

Select * from CHUCVU;
---dvt
--- chuc vu
DBCC CHECKIDENT (DONVITINH, RESEED, 0)
Insert into DONVITINH(TenDonViTinh) values(N'hộp');
Insert into DONVITINH(TenDonViTinh) values(N'cái');
Insert into DONVITINH(TenDonViTinh) values(N'thùng');
Insert into DONVITINH(TenDonViTinh) values(N'chiếc');
Insert into DONVITINH(TenDonViTinh) values(N'kg');
Insert into DONVITINH(TenDonViTinh) values(N'lít');