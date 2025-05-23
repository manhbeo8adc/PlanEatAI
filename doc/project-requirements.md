# PlanEatAI - Tài liệu Yêu cầu Dự án 📋

## Thông tin Dự án
- **Tên dự án**: PlanEatAI
- **Nền tảng**: Android
- **Ngôn ngữ**: Kotlin
- **Framework UI**: Jetpack Compose
- **AI Engine**: Google Gemini API (gemini-1.5-flash)
- **Kiến trúc**: MVVM Pattern với ViewModel

---

## Mô tả Tổng quan
PlanEatAI là ứng dụng Android lập kế hoạch bữa ăn thông minh sử dụng AI để gợi ý thực đơn tuần phù hợp với sở thích cá nhân, nhu cầu dinh dưỡng và ngân sách của người dùng.

---

## Yêu cầu Chức năng Chi tiết

### 1. Quản lý Preferences Người dùng ⚙️ ✅ **HOÀN THÀNH**
**Input**: Thông tin sở thích cá nhân chi tiết
- **Món ăn yêu thích**: Text field để nhập món ăn được ưa chuộng
- **Món ăn không thích**: Text field để nhập món ăn muốn tránh
- **Phong cách ẩm thực**: Multi-select chips (Việt Nam, Trung Hoa, Nhật, Hàn, Thái, Ấn Độ, Ý, Pháp, Mỹ, Địa Trung Hải, Chay)
- **Số khẩu phần**: Slider từ 1-10 người
- **Cài đặt cho từng bữa ăn**:
  - **Bữa sáng**: Thời gian (15 phút mặc định), Calo (400 kcal), Ngân sách (30k VND)
  - **Bữa trưa**: Thời gian (45 phút mặc định), Calo (600 kcal), Ngân sách (60k VND)
  - **Bữa tối**: Thời gian (60 phút mặc định), Calo (500 kcal), Ngân sách (80k VND)

**Output**: Dữ liệu preferences được lưu trong SharedPreferences

**Business Rules**:
- ✅ **Auto-save**: Preferences tự động lưu khi bấm "Lưu cài đặt"
- ✅ **Change detection**: Theo dõi thay đổi và hỏi user khi back
- ✅ **Auto-load**: Tự động load preferences khi khởi động app
- ✅ **Persistence**: Dữ liệu được lưu bền vững trong SharedPreferences
- ✅ **Multi-cuisine**: AI xáo trộn giữa các phong cách ẩm thực trong tuần
- ✅ **Meal-specific constraints**: AI tuân thủ thời gian, calo, budget riêng cho từng bữa

### 2. Sinh Meal Plan bằng AI 🤖
**Input**: User preferences + prompt engineering
**Process**: 
- Gọi Gemini API với prompt được tối ưu
- Retry logic: tối đa 3 lần với delay tăng dần
- Timeout: 30 giây cho mỗi request
- Response parsing: JSON format validation

**Output**: Meal plan 7 ngày với thông tin tổng quát
- Date (yyyy-MM-dd format)
- Breakfast/lunch/dinner (tên món + mô tả ngắn)

**Business Rules**:
- Meal plan chỉ tạo khi user bấm "Tạo mới"
- Không tự động lưu, cần user bấm "Lưu"
- Cache response để tránh duplicate API calls

### 3. Hiển thị Meal Plan 📅 ✅ **HOÀN THÀNH**
**Input**: Meal plan data với format mới (tên thứ thay vì ngày cụ thể)
**UI Components**:
- ✅ **Card layout**: Mỗi thứ hiển thị trong card riêng biệt
- ✅ **Day display**: Hiển thị tên thứ (Thứ Hai, Thứ Ba...) thay vì ngày cụ thể
- ✅ **Meal sections**: 3 section cho sáng/trưa/tối với emoji (☀️🥗🌙)
- ✅ **Color coding**: Màu sắc phân biệt bữa ăn, pink theme
- ✅ **Sorted display**: Hiển thị theo thứ tự từ Thứ Hai đến Chủ Nhật
- ✅ **Empty state**: Hiển thị đúng 7 thứ khi chưa có thực đơn
- ✅ **Circle indicators**: T2, T3...CN cho dễ nhận biết

**Interactions**:
- ✅ **Navigation**: Tap vào món ăn → navigate đến MealDetailScreen
- ✅ **Scroll**: LazyColumn để scroll xem các ngày khác
- ✅ **Responsive**: Tự động adjust theo kích thước màn hình

### 4. Chi tiết Món ăn (MealDetailScreen) 🍽️ ✅ **HOÀN THÀNH**
**Input**: Tên món ăn từ meal plan + user preferences cho servings
**Process**:
- ✅ **AI Integration**: Gọi Gemini API để lấy chi tiết món ăn real-time
- ✅ **Loading state**: Circular progress indicator với beautiful design
- ✅ **Error handling**: Retry mechanism với exponential backoff
- ✅ **JSON parsing**: Robust parsing với fallback data

**Output**: Thông tin chi tiết với nutrition enhancement
- ✅ **Ingredients**: Danh sách nguyên liệu với số lượng cho đúng số người ăn
- ✅ **Ingredient nutrition**: Thông tin dinh dưỡng chi tiết cho từng nguyên liệu
- ✅ **Cooking steps**: Hướng dẫn nấu từng bước chi tiết
- ✅ **Dual nutrition display**:
  - **Món ăn**: Thông tin dinh dưỡng cho 1 người
  - **Tổng từ nguyên liệu**: Tính tổng từ tất cả nguyên liệu cho số người thực tế
- ✅ **Color-coded nutrition**: Calo (Orange), Protein (Blue), Carbs (Amber), Fat (Red), Fiber (Green), Sugar (Brown)
- ✅ **Serving size display**: Hiển thị đúng số người từ user preferences

**Business Rules**:
- ✅ **On-demand fetch**: Chi tiết chỉ fetch khi user yêu cầu
- ✅ **Retry logic**: Tối đa 3 lần retry với delay tăng dần
- ✅ **Fallback data**: Nutrition mặc định nếu AI không trả về
- ✅ **Clear labeling**: Phân biệt rõ thông tin cho 1 người vs nhiều người

### 5. Lưu/Tải Meal Plan 💾 ✅ **HOÀN THÀNH**
**Storage**: SharedPreferences (JSON format)
- `saved_meal_plan_*`: meal plan data với key theo ngày
- `saved_meal_plans_list`: danh sách các meal plan đã lưu
- `user_preferences`: user preferences data

**Features**:
- ✅ **Auto-load**: Tự động load meal plan khi mở app
- ✅ **Manual save**: User bấm "Lưu thực đơn" để lưu thủ công
- ✅ **Multiple saves**: Hỗ trợ lưu nhiều thực đơn với key khác nhau
- ✅ **Error handling**: Try-catch cho các thao tác I/O
- ✅ **Persistence**: Dữ liệu không bị mất khi đóng app

### 6. Tutorial System 📖
**Components**:
- Onboarding screens cho lần đầu sử dụng
- Step-by-step guide
- Skip option
- Re-access từ Settings

---

## Yêu cầu Kỹ thuật

### Architecture
- **Pattern**: MVVM (Model-View-ViewModel)
- **State Management**: StateFlow, MutableStateFlow
- **Navigation**: Jetpack Navigation Compose
- **Dependency Injection**: Manual injection (có thể upgrade Hilt)

### API Integration
- **Service**: Google Gemini API (gemini-1.5-flash)
- **Authentication**: API Key trong `local.properties`
- **Error Handling**: Retry mechanism + timeout
- **Response Format**: JSON với schema validation

### Data Models
```kotlin
data class PreferencesData(...)
data class MealPlan(
    val date: String,
    val breakfast: String,
    val lunch: String, 
    val dinner: String
)
data class Dish(
    val id: String,
    val name: String,
    val imageUrl: String?,
    val description: String,
    val prepTime: Int,
    val cookTime: Int,
    val nutrition: Nutrition,
    val ingredients: List<Pair<String, String>>,
    val steps: List<String>
)
data class Nutrition(
    val calories: Int,
    val protein: Int,
    val carbs: Int,
    val fat: Int,
    val fiber: Int,
    val sugar: Int
)
```

### UI/UX Requirements
- **Design System**: Material 3
- **Color Scheme**: Pink/Purple theme (Primary: #AD1457)
- **Typography**: Quicksand font family
- **Responsive**: Support các màn hình Android khác nhau
- **Accessibility**: Content descriptions, readable text sizes

---

## User Flows

### Flow 1: Lần đầu sử dụng
1. Mở app → Tutorial screens
2. Nhập preferences → Save
3. Generate meal plan → Display
4. Tap món ăn → View details
5. Save meal plan

### Flow 2: Người dùng quay lại
1. Mở app → Auto-load saved meal plan
2. Browse meal plan hiện tại
3. Tạo meal plan mới (nếu muốn)
4. Chỉnh sửa preferences (nếu cần)

### Flow 3: Xem chi tiết món ăn
1. Từ meal plan → Tap món ăn
2. Loading state → Fetch từ AI
3. Display: ingredients + nutrition + cooking steps
4. Tab switching giữa nutrition/instructions

---

## Error Handling

### Lỗi API
- **Network Error**: Hiển thị "Không có kết nối mạng"
- **API Timeout**: "Timeout, vui lòng thử lại"
- **Invalid Response**: "Dữ liệu không hợp lệ"
- **Rate Limit**: "Quá giới hạn API, vui lòng đợi"

### Lỗi Data
- **Empty Meal Plan**: "Chưa có thực đơn, hãy tạo mới"
- **Failed to Save**: "Không thể lưu, kiểm tra bộ nhớ"
- **Corrupted Data**: "Dữ liệu bị lỗi, reset về mặc định"

---

## Performance Requirements

### API Optimization
- Cache dish details để tránh duplicate calls
- Implement retry với exponential backoff
- Timeout reasonable (30s)

### Memory Management
- Lazy loading cho large lists
- Image optimization với Coil
- Clear unused cache định kỳ

### Storage
- Efficient JSON serialization
- File size optimization
- Background save operations

---

## Security Considerations
- API Key protection (không hardcode)
- Local data encryption (tương lai)
- Input validation cho user data
- Safe JSON parsing

---

## Testing Strategy
- Unit tests cho ViewModels
- Integration tests cho API calls
- UI tests cho critical flows
- Error scenario testing

---

## ✅ Các lỗi đã sửa (Tháng 12/2024)

### 🐛 **Bug Fix #1: Preferences không được lưu**
- **Vấn đề**: savePreferences() chỉ cập nhật state, không persist
- **Giải pháp**: Thêm SharedPreferences integration với JSON serialization
- **Kết quả**: Preferences được lưu bền vững, auto-load khi khởi động

### 🐛 **Bug Fix #2: Thiếu confirmation dialog**
- **Vấn đề**: Không hỏi user khi back từ preferences có thay đổi
- **Giải pháp**: Thêm BackHandler với AlertDialog "Lưu & Thoát" vs "Không lưu"
- **Kết quả**: UX tốt hơn, không bị mất thay đổi do vô tình

### 🐛 **Bug Fix #3: Save/Load meal plan không hoạt động**
- **Vấn đề**: Functions chỉ có log statements, không có implementation
- **Giải pháp**: Full SharedPreferences implementation với multiple meal plans support
- **Kết quả**: Lưu/load thực đơn hoạt động đầy đủ

### 🐛 **Bug Fix #4: Serving size hiển thị sai**
- **Vấn đề**: MealDetailScreen hiển thị hardcoded values thay vì user preferences
- **Giải pháp**: Sử dụng userPreferences.servings từ ViewModel state
- **Kết quả**: Hiển thị đúng số người ăn đã cài đặt

### 🐛 **Bug Fix #5: Nutrition information không nhất quán**
- **Vấn đề**: Thông tin dinh dưỡng món ăn vs nguyên liệu tính khác nhau
- **Giải pháp**: Clear labeling - món ăn cho 1 người, nguyên liệu cho số người thực tế
- **Kết quả**: User hiểu rõ cách tính nutrition, không còn confusion

### 🔧 **Technical Enhancements**
- ✅ **ViewModelFactory**: Inject Context để access SharedPreferences
- ✅ **JSON Serialization**: UserPreferences và SavedMealPlan data classes
- ✅ **Error Handling**: Try-catch cho tất cả persistence operations
- ✅ **State Management**: Proper StateFlow để sync UI với data

---

## Định hướng Phát triển

### Phase 2 Features  
- Cloud sync với Firebase
- Sharing meal plans
- Shopping list generation từ ingredients
- Nutrition tracking & analysis theo thời gian
- Recipe photos với AI recognition
- Grocery price integration

### Technical Improvements
- Hilt dependency injection để replace manual injection
- Room database cho offline capability
- WorkManager cho background sync
- Advanced caching strategies cho AI responses
- Unit testing cho ViewModels và business logic

---

*Tài liệu được cập nhật lần cuối: Tháng 12/2024*
*Phiên bản: 1.0 - Release với tất cả tính năng cơ bản hoàn thiện*