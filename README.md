# PlanEatAI 🍱🤖

## Giới thiệu
PlanEatAI là ứng dụng lập kế hoạch bữa ăn cá nhân thông minh, sử dụng AI (Gemini API) để gợi ý thực đơn phù hợp với sở thích, phong cách ẩm thực, nhu cầu dinh dưỡng và thói quen của người dùng. Ứng dụng được phát triển trên nền tảng Android với giao diện hiện đại, dễ sử dụng, tích hợp nhiều tính năng tự động hóa và cá nhân hóa trải nghiệm.

---

## Chức năng chính 🌟

### 1. Quản lý preferences cá nhân
- Nhập danh sách món ăn yêu thích, không thích, dị ứng.
- Chọn phong cách ẩm thực (ẩm thực miền Bắc, Trung, Nam, Hàn Quốc, Trung Quốc, Eat Clean, Ăn kiêng...)
- Nhập mục tiêu dinh dưỡng, số người ăn, thời gian chuẩn bị, chi phí mong muốn.

### 2. Sinh meal plan tự động bằng AI
- Sử dụng prompt tuning với Gemini API để sinh thực đơn tuần phù hợp với preferences.
- Meal plan gồm các bữa sáng, trưa, tối cho từng ngày, chỉ chứa thông tin tổng quát: tên món, thời gian chuẩn bị, calo, khẩu phần, chi phí.
- Meal plan chỉ được tạo mới khi người dùng bấm “Tạo mới”.
- Meal plan không tự động lưu, chỉ lưu khi người dùng bấm “Lưu”.

### 3. Hiển thị meal plan khoa học, dễ đọc
- Giao diện trực quan, trình bày từng ngày, từng bữa ăn rõ ràng.
- Thông tin mỗi món: tên, khẩu phần, thời gian chuẩn bị, calo, chi phí.
- Sử dụng icon minh họa, màu sắc phân biệt các bữa ăn.
- Người dùng có thể bấm vào từng bữa ăn để xem chi tiết.

### 4. Xem chi tiết bữa ăn
- Khi bấm vào bữa ăn, app sẽ gửi prompt đến Gemini API để lấy chi tiết món ăn (thành phần, hướng dẫn nấu, dinh dưỡng...)
- Thông tin chi tiết chỉ được lấy khi người dùng yêu cầu, không sinh sẵn khi tạo meal plan.

### 5. Lưu & tải meal plan
- Meal plan được lưu vào bộ nhớ máy, có thể tải lại khi mở app.
- Nếu đã có meal plan cũ, app sẽ giữ nguyên ngày tháng cũ để tiện theo dõi lịch sử.

### 6. Tùy chỉnh & cá nhân hóa
- Người dùng có thể chỉnh sửa meal plan, thêm/xóa món, thay đổi thông tin dinh dưỡng.
- Thay đổi preferences sẽ không tự động cập nhật meal plan, cần bấm “Tạo mới” để sinh lại.

### 7. Kết nối API & mở rộng
- Dễ dàng tích hợp thêm các API AI khác (OpenAI, Gemini, v.v.).
- Có thể mở rộng để đồng bộ với cloud hoặc chia sẻ meal plan.

### 8. Tutorial trực quan trong app
- Khi mở app lần đầu, người dùng sẽ được hướng dẫn từng bước sử dụng app qua các màn hình tutorial trực quan, dễ hiểu, có thể bỏ qua hoặc xem lại trong phần Cài đặt.

---

## Luồng hoạt động tổng quát 💡
1. Người dùng nhập preferences (món yêu thích, dị ứng, phong cách ẩm thực, mục tiêu dinh dưỡng...)
2. App gửi prompt đến Gemini API để sinh meal plan tuần (chỉ thông tin tổng quát).
3. Hiển thị meal plan từng ngày, từng bữa.
4. Người dùng bấm vào bữa ăn để xem chi tiết, app gửi prompt đến Gemini API để lấy chi tiết món ăn.
5. Người dùng có thể chỉnh sửa, lưu meal plan.
6. Khi mở lại app, meal plan cũ sẽ được load lại (ngày tháng giữ nguyên).

---

## Dữ liệu & mô hình dữ liệu
- **Preferences:**
  - Món yêu thích, không thích, dị ứng, phong cách ẩm thực, mục tiêu dinh dưỡng, số người ăn, thời gian, chi phí.
- **MealPlan:**
  - Ngày, danh sách bữa ăn (sáng, trưa, tối), mỗi bữa gồm: tên món, thời gian, calo, khẩu phần, chi phí (tổng quát).
- **Dish (chi tiết món ăn):**
  - Tên, thành phần, hướng dẫn nấu, dinh dưỡng, hình ảnh, nguồn tham khảo...

---

## Công nghệ sử dụng ⚙️
- Android Studio, Kotlin, Jetpack Compose
- Gemini API (Google AI)
- MCP (Model Context Protocol) hỗ trợ tự động hóa code, debug, tài liệu
- Gradle, kotlinx.serialization, Accompanist FlowLayout

---

## Định hướng phát triển 🚀
- Thêm tính năng gợi ý mua sắm, tính toán chi phí
- Đồng bộ cloud, chia sẻ thực đơn
- Tích hợp AI phân tích sức khỏe, lịch sử ăn uống
- Hỗ trợ đa ngôn ngữ
- Tutorial hướng dẫn sử dụng trực quan, sinh động

---

## Đóng góp & phát triển
Bạn có thể fork, tạo pull request hoặc mở issue để đóng góp cho dự án. Mọi ý kiến đóng góp đều rất được hoan nghênh! 🥰

---

Nếu bạn cần giải thích thêm về bất kỳ chức năng nào, hãy liên hệ đội ngũ phát triển nhé! 😊