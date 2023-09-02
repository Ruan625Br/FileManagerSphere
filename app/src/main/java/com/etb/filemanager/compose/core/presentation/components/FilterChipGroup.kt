/*
 * Copyright (c)  2023  Juan Nascimento
 * Part of FileManagerSphere - FilterChipGroup.kt
 * SPDX-License-Identifier: GPL-3.0-or-later
 * More details at: https://www.gnu.org/licenses/
 */

package com.etb.filemanager.compose.core.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterChipGroup(
    innerPadding: PaddingValues,
    modifier: Modifier,
    chips: List<Pair<String, Boolean>>,
    onChipClick: (Int) -> Unit
) {
    LazyRow(
        contentPadding = innerPadding,
        horizontalArrangement = Arrangement.spacedBy(5.dp),
        modifier = modifier
    ) {
        items(chips.size) { index ->
            val (label, selected) = chips[index]
            FilterChip(selected = selected,
                onClick = {


                    onChipClick(index) },
                label = { Text(label) },
                leadingIcon = if (selected) {
                    {
                        Icon(
                            imageVector = Icons.Filled.Done,
                            contentDescription = null,
                            modifier = Modifier.size(FilterChipDefaults.IconSize)
                        )
                    }
                } else {
                    null
                })
        }
    }
}
@Composable
fun List<Pair<String, Boolean>>.getChipSelectedIndex(): Int {
    var chipIndex = 0
    forEachIndexed { index, pair ->
        if (pair.second){
            chipIndex = index
        }
    }
    return chipIndex
}