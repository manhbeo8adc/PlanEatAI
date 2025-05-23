# 🔑 Hướng dẫn cài đặt API Key cho PlanEatAI

## Bước 1: Lấy API Key từ Google AI Studio

1. **Truy cập Google AI Studio**:
   - Mở: https://aistudio.google.com/
   - Đăng nhập bằng tài khoản Google

2. **Tạo API Key**:
   - Nhấn "Get API key" ở góc trên phải
   - Chọn "Create API key"
   - Chọn project (hoặc tạo mới)
   - **Copy API key** (dạng: `AIzaSy...`)

## Bước 2: Cập nhật vào ứng dụng

1. **Mở file**: `app/src/main/java/com/example/planeatai/config/ApiConfig.kt`

2. **Thay thế** dòng:
   ```kotlin
   const val GEMINI_API_KEY = "YOUR_GEMINI_API_KEY_HERE"
   ```
   
   **Thành**:
   ```kotlin
   const val GEMINI_API_KEY = "AIzaSy-API-KEY-BẠN-VỪA-COPY"
   ```

3. **Lưu file** và rebuild ứng dụng

## Bước 3: Test ứng dụng

1. Chạy ứng dụng
2. Nhấn nút "+" để tạo thực đơn
3. Nếu thành công → API key đã hoạt động! ✅
4. Nếu vẫn lỗi → Kiểm tra lại API key

## ⚠️ Lưu ý bảo mật

- **KHÔNG** commit API key lên Git
- **KHÔNG** chia sẻ API key với người khác
- **XÓA** API key cũ nếu không dùng nữa

## 🆘 Khắc phục sự cố

### Lỗi "API key not valid":
- Kiểm tra API key đã copy đúng chưa
- Đảm bảo không có dấu cách thừa
- Tạo API key mới nếu cần

### Lỗi "Quota exceeded":
- API key đã hết quota miễn phí
- Chờ reset quota hoặc upgrade plan

### Lỗi "Permission denied":
- Enable Gemini API trong Google Cloud Console
- Kiểm tra permissions của API key

## 💡 Tips

- API key Gemini thường bắt đầu bằng `AIzaSy`
- Quota miễn phí: ~60 requests/phút
- Để test: tạo thực đơn đơn giản trước

---

**Cần hỗ trợ?** Liên hệ team phát triển! 🤝