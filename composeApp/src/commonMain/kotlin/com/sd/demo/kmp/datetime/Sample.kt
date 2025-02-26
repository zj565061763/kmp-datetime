package com.sd.demo.kmp.datetime

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun Sample(
  onClickBack: () -> Unit,
) {
  RouteScaffold(
    title = "Sample",
    onClickBack = onClickBack,
  ) {
    Text(text = "text")
  }
}