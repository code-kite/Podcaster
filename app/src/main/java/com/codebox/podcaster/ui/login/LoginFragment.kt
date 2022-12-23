package com.codebox.podcaster.ui.login

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.codebox.podcaster.R
import kotlinx.android.synthetic.main.fragment_login.*

class LoginFragment : Fragment(R.layout.fragment_login) {

    private val login = "Login"
    private val logout = "Logout"


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        if (requestCode == 1001) {
            if (resultCode == AppCompatActivity.RESULT_OK) {
                onLoginSuccess()
            }
        } else {
            Toast.makeText(requireContext(), "Login Failed!", Toast.LENGTH_SHORT).show()
        }

        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun onLoginSuccess() {
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()

    }

    private fun initView() {
        setBtnClickListener()
    }

    private fun setBtnClickListener() {
        btnAuth.setOnClickListener {
            val text = (it as Button).text.toString()
            if (text == login)
            else {
            }
        }
    }



}