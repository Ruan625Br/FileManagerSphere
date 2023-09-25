/*
 * Copyright (c)  2023  Juan Nascimento
 * Part of FileManagerSphere - ChanneledViewModel.kt
 * SPDX-License-Identifier: GPL-3.0-or-later
 * More details at: https://www.gnu.org/licenses/
 */

package com.etb.filemanager.compose.core.presentation.common

import android.os.Bundle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.Navigator
import com.etb.filemanager.files.extensions.navigate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChanneledViewModel @Inject constructor() : ViewModel() {

    sealed class Event {
        data class NavigationRouteEvent(val route: String) : Event()
        data class NavigationRouteEventWithArgs(
            val route: String, val args: Bundle, val navOptions: NavOptions? = null,
            val navigatorExtras: Navigator.Extras? = null
        ) : Event()

        object NavigationUpEvent : Event()
    }

    private val eventChannel = Channel<Event>()

    fun initWithNav(navController: NavController) =
        eventChannel.receiveAsFlow().map {
            when (it) {
                is Event.NavigationRouteEvent ->
                    navController.navigate(it.route) {
                        launchSingleTop = true
                        restoreState = true
                    }

                is Event.NavigationRouteEventWithArgs ->
                    navController.navigate(it.route, it.args, it.navOptions, it.navigatorExtras)

                Event.NavigationUpEvent ->
                    navController.navigateUp()
            }
        }

    fun navigate(route: String) {
        viewModelScope.launch {
            eventChannel.send(Event.NavigationRouteEvent(route))
        }
    }

    fun navigate(
        route: String,
        args: Bundle,
        navOptions: NavOptions? = null,
        navigatorExtras: Navigator.Extras? = null
    ) {
        viewModelScope.launch {
            eventChannel.send(
                Event.NavigationRouteEventWithArgs(
                    route,
                    args,
                    navOptions,
                    navigatorExtras
                )
            )
        }
    }

    fun navigateUp() {
        viewModelScope.launch {
            eventChannel.send(Event.NavigationUpEvent)
        }
    }
}