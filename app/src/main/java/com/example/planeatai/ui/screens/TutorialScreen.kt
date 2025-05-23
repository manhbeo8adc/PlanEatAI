package com.example.planeatai.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.Image
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.pager.PagerState
import androidx.compose.ui.text.style.TextAlign

@Composable
fun TutorialScreen(onFinish: () -> Unit) {
    val pages = listOf(
        TutorialPage(
            title = "Chào mừng đến với PlanEatAI!",
            description = "Ứng dụng giúp bạn lên thực đơn cá nhân thông minh, phù hợp sở thích và phong cách ẩm thực.",
            imageRes = android.R.drawable.ic_menu_info_details
        ),
        TutorialPage(
            title = "Nhập sở thích & phong cách",
            description = "Chọn món yêu thích, dị ứng, phong cách ẩm thực (Bắc, Trung, Nam, Hàn, Eat Clean, v.v.) để AI hiểu bạn hơn!",
            imageRes = android.R.drawable.ic_menu_info_details
        ),
        TutorialPage(
            title = "Tạo meal plan tự động",
            description = "Bấm nút + để AI sinh thực đơn tuần mới, đầy đủ sáng, trưa, tối, phù hợp nhu cầu của bạn.",
            imageRes = android.R.drawable.ic_menu_info_details
        ),
        TutorialPage(
            title = "Xem chi tiết bữa ăn",
            description = "Chạm vào từng bữa ăn để xem chi tiết món, thành phần, hướng dẫn nấu, dinh dưỡng...",
            imageRes = android.R.drawable.ic_menu_info_details
        ),
        TutorialPage(
            title = "Lưu & mở lại meal plan",
            description = "Khi hài lòng, bấm Lưu để lưu meal plan. Có thể mở lại bất cứ lúc nào!",
            imageRes = android.R.drawable.ic_menu_info_details
        )
    )
    val pagerState = rememberPagerState(pageCount = { pages.size })
    val scope = rememberCoroutineScope()
    
    Box(Modifier.fillMaxSize().background(Color.White)) {
        Column(
            Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.weight(1f)
            ) { page ->
                TutorialPageView(page = pages[page])
            }
            
            // Custom pager indicator
            PagerIndicator(
                pagerState = pagerState,
                pageCount = pages.size,
                modifier = Modifier.padding(16.dp)
            )
            
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                if (pagerState.currentPage > 0) {
                    TextButton(onClick = {
                        scope.launch { pagerState.animateScrollToPage(pagerState.currentPage - 1) }
                    }) { Text("Quay lại") }
                } else {
                    Spacer(Modifier.width(80.dp))
                }
                if (pagerState.currentPage < pages.size - 1) {
                    Button(onClick = {
                        scope.launch { pagerState.animateScrollToPage(pagerState.currentPage + 1) }
                    }, shape = RoundedCornerShape(16.dp)) {
                        Text("Tiếp tục")
                    }
                } else {
                    Button(onClick = onFinish, shape = RoundedCornerShape(16.dp)) {
                        Text("Bắt đầu sử dụng")
                    }
                }
            }
        }
    }
}

@Composable
fun PagerIndicator(
    pagerState: PagerState,
    pageCount: Int,
    modifier: Modifier = Modifier,
    activeColor: Color = MaterialTheme.colorScheme.primary,
    inactiveColor: Color = Color.Gray
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        repeat(pageCount) { index ->
            val isActive = pagerState.currentPage == index
            Box(
                modifier = Modifier
                    .size(if (isActive) 12.dp else 8.dp)
                    .background(
                        color = if (isActive) activeColor else inactiveColor,
                        shape = androidx.compose.foundation.shape.CircleShape
                    )
            )
        }
    }
}

data class TutorialPage(val title: String, val description: String, val imageRes: Int)

@Composable
fun TutorialPageView(page: TutorialPage) {
    Column(
        Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = page.imageRes),
            contentDescription = page.title,
            modifier = Modifier.size(80.dp)
        )
        Spacer(Modifier.height(24.dp))
        Text(
            text = page.title,
            fontWeight = FontWeight.Bold,
            fontSize = 22.sp,
            color = MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(12.dp))
        Text(
            text = page.description,
            fontSize = 16.sp,
            color = Color(0xFF616161),
            lineHeight = 22.sp,
            textAlign = TextAlign.Center
        )
    }
}