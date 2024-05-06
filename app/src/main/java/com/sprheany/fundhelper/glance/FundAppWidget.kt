package com.sprheany.fundhelper.glance

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.ColorFilter
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.LocalContext
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.appwidget.CircularProgressIndicator
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.appWidgetBackground
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.lazy.LazyColumn
import androidx.glance.appwidget.lazy.items
import androidx.glance.appwidget.provideContent
import androidx.glance.appwidget.updateAll
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.width
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextAlign
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.sprheany.fundhelper.R
import com.sprheany.fundhelper.data.FundUseCase
import com.sprheany.fundhelper.models.FundGrowthState
import com.sprheany.fundhelper.models.FundState
import com.sprheany.fundhelper.models.FundWorth
import com.sprheany.fundhelper.models.growthPercent
import com.sprheany.fundhelper.models.state
import com.sprheany.fundhelper.ui.MainActivity
import com.sprheany.fundhelper.ui.theme.FundGlanceTheme
import com.sprheany.fundhelper.ui.theme.Green
import com.sprheany.fundhelper.ui.theme.Red
import kotlinx.coroutines.launch

class FundAppWidget : GlanceAppWidget() {

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent { Content() }
    }

    @Composable
    fun Content() {
        val context = LocalContext.current
        val scope = rememberCoroutineScope()
        val updateFundState: () -> Unit = { scope.launch { refreshFundState(context) } }

        val fundState by FundUseCase.fundState.collectAsState()

        FundGlanceTheme {
            Column(
                modifier = GlanceModifier
                    .fillMaxSize()
                    .appWidgetBackground()
                    .background(GlanceTheme.colors.background)
                    .cornerRadius(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                TopBar(updateFundState)
                Box(
                    modifier = GlanceModifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(GlanceTheme.colors.inverseOnSurface)
                ) {}
                FundInfoView(fundState)
            }
        }
    }
}

@Composable
fun TopBar(onRefreshClicked: () -> Unit) {
    Row(
        modifier = GlanceModifier
            .fillMaxWidth()
            .background(GlanceTheme.colors.surface)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = LocalContext.current.getString(R.string.app_name),
            style = TextStyle(
                color = GlanceTheme.colors.onBackground,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        )
        Spacer(modifier = GlanceModifier.defaultWeight())

        Image(
            modifier = GlanceModifier
                .clickable(onRefreshClicked)
                .padding(4.dp)
                .cornerRadius(16.dp),
            provider = ImageProvider(R.drawable.baseline_refresh_24),
            contentDescription = null,
            colorFilter = ColorFilter.tint(GlanceTheme.colors.onSurfaceVariant)
        )
        Spacer(modifier = GlanceModifier.width(16.dp))
        Image(
            modifier = GlanceModifier
                .clickable(actionStartActivity<MainActivity>())
                .padding(4.dp)
                .cornerRadius(16.dp),
            provider = ImageProvider(R.drawable.baseline_more_vert_24),
            contentDescription = null,
            colorFilter = ColorFilter.tint(GlanceTheme.colors.onSurfaceVariant)
        )
    }
}

@Composable
fun FundInfoView(
    fundState: FundState,
) {
    when (fundState) {
        is FundState.Loading -> {
            Box(
                modifier = GlanceModifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

        is FundState.Success -> {
            LazyColumn {
                items(fundState.fundWorth) {
                    FundItem(it)
                }
            }
        }

        is FundState.Error -> {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = fundState.message,
                    style = TextStyle(color = GlanceTheme.colors.onBackground)
                )
            }
        }
    }
}

@Composable
fun FundItem(data: FundWorth) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = GlanceModifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp)
    ) {
        Column(modifier = GlanceModifier.defaultWeight()) {
            Text(
                text = data.name,
                maxLines = 1,
                style = TextStyle(
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = GlanceTheme.colors.onBackground,
                ),
            )
            Spacer(modifier = GlanceModifier.height(2.dp))
            Text(
                text = data.code,
                style = TextStyle(
                    fontSize = 12.sp,
                    color = GlanceTheme.colors.inverseSurface
                )
            )
        }
        Spacer(modifier = GlanceModifier.width(8.dp))
        Text(
            modifier = GlanceModifier,
            text = data.exceptWorth,
            style = TextStyle(
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = GlanceTheme.colors.onBackground,
                textAlign = TextAlign.End,
            )
        )
        Spacer(modifier = GlanceModifier.width(8.dp))
        Text(
            text = data.growthPercent,
            modifier = GlanceModifier
                .background(
                    color = when (data.state) {
                        FundGrowthState.Up -> Red
                        FundGrowthState.Down -> Green
                        else -> Color.Gray
                    }
                )
                .padding(horizontal = 6.dp, vertical = 4.dp)
                .cornerRadius(4.dp),
            style = TextStyle(
                textAlign = TextAlign.Center,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = ColorProvider(Color.White)
            )
        )
    }
}

suspend fun refreshFundState(context: Context) {
    FundAppWidget().updateAll(context)
    FundUseCase.refreshFundWorth()
}
