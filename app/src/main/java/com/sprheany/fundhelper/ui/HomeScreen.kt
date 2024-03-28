@file:OptIn(ExperimentalMaterial3Api::class)

package com.sprheany.fundhelper.ui

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sprheany.fundhelper.R
import com.sprheany.fundhelper.models.FUND_STATE_DOWN
import com.sprheany.fundhelper.models.FUND_STATE_UP
import com.sprheany.fundhelper.models.FundWorth
import com.sprheany.fundhelper.ui.theme.FundTheme
import com.sprheany.fundhelper.ui.theme.Green
import com.sprheany.fundhelper.ui.theme.Red
import com.sprheany.fundhelper.utils.recomposeHighlighter
import com.sprheany.fundhelper.viewmodel.FundViewModel
import org.burnoutcrew.reorderable.ReorderableItem
import org.burnoutcrew.reorderable.detectReorderAfterLongPress
import org.burnoutcrew.reorderable.rememberReorderableLazyListState
import org.burnoutcrew.reorderable.reorderable

@Composable
fun Home(
    viewModel: FundViewModel = viewModel(),
    navigateToSearch: () -> Unit = {}
) {
    Scaffold(
        topBar = {
            TopAppBar(modifier = Modifier.recomposeHighlighter(),
                title = { Text(text = stringResource(id = R.string.app_name)) },
                actions = {
                    IconButton(onClick = {
                        viewModel.refreshFundWorth()
                    }) {
                        Icon(
                            Icons.Default.Refresh,
                            contentDescription = stringResource(R.string.refresh),
                        )
                    }
                    IconButton(onClick = navigateToSearch) {
                        Icon(
                            Icons.Default.Add,
                            contentDescription = stringResource(R.string.add),
                        )
                    }
                })
        },
    ) {
        HomeContent(modifier = Modifier.padding(it), viewModel = viewModel)
    }
}

@Composable
fun HomeContent(
    modifier: Modifier = Modifier,
    viewModel: FundViewModel,
) {
    val data = viewModel.fundWorthFlow.collectAsState() as MutableState
    val state = rememberReorderableLazyListState(
        onMove = { from, to ->
            data.value = data.value.toMutableList().apply {
                add(to.index, removeAt(from.index))
            }
        },
        onDragEnd = { fromIndex, toIndex ->
            viewModel.onSwiped(fromIndex, toIndex)
        }
    )
    LazyColumn(
        modifier = modifier
            .reorderable(state)
            .detectReorderAfterLongPress(state),
        state = state.listState,
    ) {
        items(data.value, { it.code }) { item ->
            ReorderableItem(reorderableState = state, key = item.code) { isDragging ->
                val elevation = animateDpAsState(if (isDragging) 16.dp else 0.dp, label = "")
                FundItem(
                    data = item,
                    modifier = Modifier
                        .shadow(elevation.value)
                        .background(MaterialTheme.colorScheme.surface)
                )
            }
        }
    }
}

@Composable
fun FundItem(data: FundWorth, modifier: Modifier = Modifier) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.Center,
        ) {
            Text(
                text = data.name,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = data.code,
                style = MaterialTheme.typography.labelMedium,
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            modifier = Modifier.defaultMinSize(minWidth = 60.dp),
            text = data.exceptWorth,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.End,
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = data.growthPercent,
            modifier = Modifier
                .defaultMinSize(minWidth = 60.dp)
                .background(
                    color = when (data.state) {
                        FUND_STATE_UP -> Red
                        FUND_STATE_DOWN -> Green
                        else -> Color.Gray
                    },
                    shape = RoundedCornerShape(4.dp),
                )
                .padding(horizontal = 8.dp, vertical = 4.dp),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
    }
}

@Preview(showBackground = true)
@Composable
fun FundItemPreview() {
    FundTheme {
        Column {
            FundItem(
                FundWorth(
                    code = "000001",
                    name = "华夏成长混合C",
                    netWorth = "1.4109",
                    worthDate = "2023-08-01",
                    exceptWorth = "1.4098",
                    exceptGrowthWorth = "-0.0011",
                    exceptGrowthPercent = "-0.08%",
                    exceptWorthDate = "2023-08-02"
                )
            )
            FundItem(
                FundWorth(
                    code = "000001",
                    name = "华夏成长混合C",
                    netWorth = "1.4109",
                    worthDate = "2023-08-01",
                    exceptWorth = "1.4098",
                    exceptGrowthWorth = "0.0011",
                    exceptGrowthPercent = "1.18%",
                    exceptWorthDate = "2023-08-02"
                )
            )
        }
    }
}