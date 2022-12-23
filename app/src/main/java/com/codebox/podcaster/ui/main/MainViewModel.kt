package com.codebox.podcaster.ui.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

/**
 * Created by Codebox on 23/04/21
 */
class MainViewModel : ViewModel() {

    val bottomNavVisibilityData = MutableLiveData<Int>()
    val toastMessageData = MutableLiveData<String>()
    val snackbarMsgData = MutableLiveData<SnackBarMsg>()

}