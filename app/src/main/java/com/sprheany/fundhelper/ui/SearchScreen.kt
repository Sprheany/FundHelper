@file:OptIn(ExperimentalMaterial3Api::class)

package com.sprheany.fundhelper.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sprheany.fundhelper.R
import com.sprheany.fundhelper.ui.theme.FundTheme
import com.sprheany.fundhelper.viewmodel.SearchViewModel

@Composable
fun Search(viewModel: SearchViewModel = viewModel(), navigateUp: () -> Unit = {}) {
    val keyboard = LocalSoftwareKeyboardController.current
    var inputText by remember { mutableStateOf("") }
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                modifier = Modifier,
                title = {
                    val focusRequester = FocusRequester()
                    SideEffect {
                        focusRequester.requestFocus()
                    }
                    BasicTextField(
                        value = inputText,
                        onValueChange = {
                            inputText = it
                        },
                        modifier = Modifier
                            .height(48.dp)
                            .fillMaxWidth()
                            .padding(end = 40.dp)
                            .focusRequester(focusRequester),
                        decorationBox = { innerTextField ->
                            OutlinedTextFieldDefaults.DecorationBox(
                                value = inputText,
                                innerTextField = innerTextField,
                                enabled = true,
                                singleLine = true,
                                visualTransformation = VisualTransformation.None,
                                interactionSource = remember { MutableInteractionSource() },
                                placeholder = {
                                    Text(
                                        text = stringResource(R.string.search_bar_hint),
                                        color = Color.Gray
                                    )
                                },
                                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "") },
                                colors = OutlinedTextFieldDefaults.colors(),
                                contentPadding = PaddingValues(0.dp),
                            ) {
                                Box(
                                    modifier = Modifier.background(
                                        color = MaterialTheme.colorScheme.inverseOnSurface,
                                        shape = RoundedCornerShape(24.dp)
                                    )
                                )
                            }
                        },
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        keyboard?.hide()
                        navigateUp()
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Default.ArrowBack,
                            contentDescription = stringResource(R.string.back)
                        )
                    }
                }
            )
        },
    ) { contentPadding ->
        val list by viewModel.searchFunds(inputText).collectAsState(emptyList())
        LazyColumn(
            modifier = Modifier
                .padding(contentPadding)
                .navigationBarsPadding()
                .imePadding()
        ) {
            items(list) { fund ->
                SearchFundItem(name = fund.name, code = fund.code, fund.isCollection) {
                    if (it != fund.isCollection) {
                        viewModel.collectFund(fund.code, it)
                    }
                }
            }
        }
    }
}

@Composable
fun SearchFundItem(
    name: String, code: String,
    selected: Boolean,
    onSelectedChange: (Boolean) -> Unit = {}
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.weight(1f)
        ) {
            Text(text = name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = code, style = MaterialTheme.typography.labelMedium)
        }
        Checkbox(checked = selected, onCheckedChange = onSelectedChange)
    }
}

@Preview(showBackground = true)
@Composable
fun SearchPreview() {
    FundTheme {
//        Search()
    }
}


@Preview(showBackground = true)
@Composable
fun SearchItemPreview() {
    FundTheme {
        Column {
            SearchFundItem(name = "华夏成长混合", code = "000001", selected = false)
            SearchFundItem(name = "华夏成长混合", code = "000001", selected = true)
            SearchFundItem(name = "华夏成长混合", code = "000001", selected = false)
        }
    }
}

