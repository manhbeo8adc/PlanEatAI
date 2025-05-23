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

### 1. Quản lý Preferences Người dùng ⚙️
**Input**: Thông tin sở thích cá nhân
- Món ăn yêu thích, không thích, dị ứng
- Nguyên liệu ưa/không ưa thích  
- Phong cách ẩm thực (Bắc/Trung/Nam, Hàn Quốc, Trung Quốc, Eat Clean...)
- Số khẩu phần (servings)
- Thời gian chuẩn bị tối đa cho từng bữa (sáng/trưa/tối)
- Ngân sách cho từng bữa ăn
- Mục tiêu calo cho từng bữa

**Output**: Dữ liệu preferences được lưu trong file `user_preferences.json`

**Business Rules**:
- Preferences không tự động cập nhật meal plan
- Cần bấm "Tạo mới" để áp dụng preferences mới
- Dữ liệu được persist trong bộ nhớ thiết bị

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

### 3. Hiển thị Meal Plan 📅
**Input**: Meal plan data
**UI Components**:
- Card layout cho từng ngày
- Tab hoặc section cho từng bữa (sáng/trưa/tối)
- Màu sắc phân biệt bữa ăn
- Icon minh họa
- Thông tin: tên món, mô tả, thời gian, calo (ước tính)

**Interactions**:
- Tap vào món ăn → navigate đến MealDetailScreen
- Swipe hoặc scroll để xem các ngày khác

### 4. Chi tiết Món ăn (MealDetailScreen) 🍽️
**Input**: Tên món ăn từ meal plan
**Process**:
- Gọi AI để lấy chi tiết món ăn real-time
- Loading state trong quá trình fetch
- Error handling khi API fail

**Output**: Thông tin chi tiết
- Danh sách nguyên liệu với số lượng
- Hướng dẫn nấu từng bước
- Thông tin dinh dưỡng chi tiết (calo, protein, carbs, fat, fiber, sugar)
- Nutrition progress bars/charts

**Business Rules**:
- Chi tiết chỉ fetch khi user yêu cầu (không pre-load)
- Cache chi tiết món để tối ưu performance
- Hiển thị fallback UI khi không lấy được data

### 5. Lưu/Tải Meal Plan 💾
**Storage**: File system (JSON format)
- `mealplans.json`: meal plan data
- `user_preferences.json`: user preferences

**Features**:
- Auto-load meal plan khi mở app
- Manual save khi user bấm "Lưu"
- Preserve ngày tháng để tracking lịch sử

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

## Định hướng Phát triển

### Phase 2 Features
- Cloud sync với Firebase
- Sharing meal plans
- Shopping list generation  
- Nutrition analysis & tracking
- Recipe photos với AI recognition

### Technical Improvements
- Hilt dependency injection
- Room database cho offline
- WorkManager cho background tasks
- Advanced caching strategies

---

*Tài liệu này được cập nhật lần cuối: Tháng 12/2024*