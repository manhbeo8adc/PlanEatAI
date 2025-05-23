# 📱 Hướng dẫn cài đặt PlanEatAI APK

## 🎉 **APK Mới đã sẵn sàng!**

### 📦 **Thông tin APK**
- **File**: `app-release.apk`
- **Kích thước**: ~18MB
- **Phiên bản**: 1.0
- **Tương thích**: Android 8.0+ (API 26+)
- **Đã signed**: ✅ Có thể cài đặt trên điện thoại thật

---

## 🔧 **Cách cài đặt**

### **Bước 1: Tải APK**
- File APK nằm tại: `app/build/outputs/apk/release/app-release.apk`
- Copy file này vào điện thoại của bạn

### **Bước 2: Cài đặt APK**
1. **Bật cài đặt từ nguồn không xác định**:
   - Vào `Cài đặt` → `Bảo mật` → `Cài đặt ứng dụng từ nguồn không xác định`
   - Hoặc `Cài đặt` → `Ứng dụng` → `Quyền đặc biệt` → `Cài đặt ứng dụng không xác định`
   - Bật quyền cho trình duyệt hoặc file manager

2. **Cài đặt APK**:
   - Mở file manager, tìm file `app-release.apk`
   - Bấm vào file APK
   - Chọn `Cài đặt`
   - Đợi quá trình cài đặt hoàn tất

### **Bước 3: Mở ứng dụng**
- Tìm icon `PlanEatAI` trên màn hình chính
- Bấm vào để khởi động ứng dụng

---

## ✨ **Tính năng mới trong phiên bản này**

### 🍽️ **Quản lý thực đơn cải tiến**
- ✅ Hiển thị thứ từ Thứ 2 đến Chủ nhật (không có ngày cụ thể)
- ✅ Thứ tự luôn đúng từ Thứ Hai → Chủ Nhật
- ✅ Giao diện sạch sẽ, dễ nhìn

### 🥕 **Chi tiết dinh dưỡng nguyên liệu**
- ✅ Thông tin dinh dưỡng chi tiết cho từng nguyên liệu
- ✅ Hiển thị calo, protein, carbs, fat, fiber, sugar
- ✅ Tính tổng dinh dưỡng từ tất cả nguyên liệu
- ✅ Color-coding cho các chất dinh dưỡng

### 🔧 **Cải thiện kỹ thuật**
- ✅ Sửa lỗi JSON parsing từ AI
- ✅ Retry mechanism khi API fail
- ✅ Error handling tốt hơn
- ✅ Signed APK, cài đặt dễ dàng

---

## 🛠️ **Khắc phục sự cố**

### **Lỗi: "Không thể cài đặt ứng dụng"**
**Nguyên nhân**: Chưa bật quyền cài đặt từ nguồn không xác định
**Giải pháp**:
1. Vào `Cài đặt` → `Bảo mật`
2. Tìm `Nguồn không xác định` hoặc `Install unknown apps`
3. Bật quyền cho File Manager hoặc Browser

### **Lỗi: "Ứng dụng không tương thích"**
**Nguyên nhân**: Điện thoại Android < 8.0
**Giải pháp**: Cần Android 8.0 trở lên để chạy ứng dụng

### **Lỗi: "Gói có vẻ không hợp lệ"**
**Nguyên nhân**: File APK bị hỏng khi copy
**Giải pháp**: 
1. Tải lại file APK
2. Kiểm tra kích thước file (~18MB)
3. Sử dụng file `app-release.apk` (đã signed)

---

## 📱 **Yêu cầu hệ thống**

| Thông số | Yêu cầu |
|----------|---------|
| **OS** | Android 8.0+ (API 26+) |
| **RAM** | 2GB+ |
| **Storage** | 50MB trống |
| **Internet** | Cần kết nối để tạo thực đơn |
| **API Key** | Gemini API key (xem hướng dẫn trong app) |

---

## 🎯 **Sử dụng ứng dụng**

### **Tạo thực đơn**
1. Bấm nút `+` ở góc dưới bên phải
2. Đợi AI tạo thực đơn (~10-30 giây)
3. Xem thực đơn 7 ngày từ Thứ 2 đến Chủ nhật

### **Xem chi tiết món ăn**
1. Bấm vào bất kỳ món ăn nào
2. Xem nguyên liệu với thông tin dinh dưỡng chi tiết
3. Xem hướng dẫn nấu ăn từng bước

### **Cài đặt sở thích ăn uống**
1. Vào Settings (⚙️) → Cài đặt sở thích ăn uống
2. Điền thông tin chi tiết:
   - 🍽️ Món ăn yêu thích/không thích
   - 🌍 Chọn phong cách ẩm thực (có thể chọn nhiều)
   - 👥 Số người ăn
   - ⏰ Thời gian/calo/ngân sách cho từng bữa
3. Lưu cài đặt
4. Tạo thực đơn để thấy sự khác biệt

### **Cài đặt API Key**
1. Vào Settings (⚙️)
2. Nhập Gemini API Key
3. Lưu và tạo thực đơn

---

## 📞 **Hỗ trợ**

Nếu gặp vấn đề khi cài đặt hoặc sử dụng:
- Kiểm tra file `doc/bug-fixes.md` để xem các lỗi đã được sửa
- Đảm bảo đã follow đúng các bước trên
- Restart điện thoại nếu cần thiết

---

**🎉 Chúc bạn trải nghiệm tốt với PlanEatAI!** 