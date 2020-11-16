package com.reemsib.mosta3ml.activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns.EMAIL_ADDRESS
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.orhanobut.hawk.Hawk
import com.reemsib.mosta3ml.R
import com.reemsib.mosta3ml.fragment.CustomDialogCountryFragment
import com.reemsib.mosta3ml.setting.MySingleton
import com.reemsib.mosta3ml.setting.MySession
import com.reemsib.mosta3ml.utils.URLs
import com.reemsib.mosta3ml.utils.Constants
import kotlinx.android.synthetic.main.activity_register.*
import org.json.JSONException
import org.json.JSONObject


class RegisterActivity : AppCompatActivity(),View.OnClickListener{
   var gender :String =""
    var codeId:Int ?=null
   var customDialogCountry=CustomDialogCountryFragment()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        Hawk.init(applicationContext).build()


        tv_country.setOnClickListener(this)
        tv_terms.setOnClickListener(this)
        btn_regiter!!.setOnClickListener(this)
    }



    override fun onClick(v: View?) {
        when(v!!.id){
            R.id.tv_country -> {

                customDialogCountry.setCustomDialogInterface(object :
                    CustomDialogCountryFragment.CountryCustomDialogInterface {
                    override fun sendData(id: Int, calling_code: String) {
                        tv_country.text = calling_code
                        codeId = id
                        Toast.makeText(applicationContext, "$codeId", Toast.LENGTH_LONG).show()
                    }
                })
                customDialogCountry.show(supportFragmentManager, "CustomDialogCountryFragment")

            }
            R.id.tv_terms -> {
                startActivity(Intent(this, TermsActivity::class.java))
            }
            R.id.btn_regiter -> {
                registerUser()
            }
        }
    }

    private fun registerUser(){

        val username=et_userName.text.toString()
        val callingCode=tv_country.text.toString()
        val mobile=et_mobile.text.toString()
        val email=et_email.text.toString()
        val password=et_password.text.toString()
        val confirmPwd=et_confirmPwd.text.toString()
        val genderId=rg_gender.checkedRadioButtonId
        val isAgree=checkbox_agree.isChecked

        when(genderId){
            R.id.rb_female -> {
                gender = "female"
            }
            R.id.rb_male -> {
                gender = "male"
            } }

        if(!validateForm(username, callingCode, mobile, email, password, confirmPwd, isAgree)){

            return
        }
        btn_regiter.showLoading()

        val stringRequest = object : StringRequest(Request.Method.POST, URLs.URL_REGISTER,
            Response.Listener { response ->
                btn_regiter.hideLoading()

                try {
                    val obj = JSONObject(response)

                    if (obj.getBoolean("status")) {

                        //   val userJson = obj.getJSONObject("user")
                        val token = obj.getString("token")
                        val user = obj.getJSONObject("user")
                        val userId=user.getInt("id")

                        MySession.getInstance(applicationContext).setLogin(true)

                        Hawk.put(Constants.TOKEN, token)
                        Hawk.put(Constants.USERID, userId)

                        Toast.makeText(applicationContext, getString(R.string.register_success), Toast.LENGTH_SHORT).show()

                        Log.e("token", "${Hawk.get(Constants.TOKEN, null)}")
                        Log.e("id_user", "${Hawk.get(Constants.USERID, null)}")

                        val intent = Intent(applicationContext, MainActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or (Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(intent)

                    } else {
                        Toast.makeText(
                            applicationContext,
                            obj.getString("message"),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            },
            Response.ErrorListener { error ->
                btn_regiter.hideLoading()
                //  Toast.makeText(applicationContext, R.string.failed_internet, Toast.LENGTH_SHORT).show()
                Toast.makeText(applicationContext, error.message, Toast.LENGTH_SHORT).show()
            }) {

            override fun getParams(): MutableMap<String, String> {
                val params = HashMap<String, String>()
                params["name"] = username
                params["email"] = email
                params["password"] = password
                params["fcm_token"] = "asd"
                params["mobile"] = mobile
                params["sex"] = gender
                params["country_id"] = codeId.toString()
                params["device_type"] = "android"
                return params
            }

            override fun getPriority(): Priority {
                return Priority.HIGH
            }

            override fun getHeaders(): MutableMap<String, String> {
                val map = HashMap<String, String>()
                map["Accept"] = "application/json"

                return map
            }
        }
        MySingleton.getInstance(this).addToRequestQueue(stringRequest)
    }


    private fun validateForm(username: String, callingCode: String, mobile: String, email: String, password: String, confirmPwd: String, isAgree: Boolean):Boolean {
        var valid = true
        when{
            username.isEmpty() -> {
                et_userName.error = getString(R.string.enter_username)
                et_userName.requestFocus()
                valid = false
            }
            username.length<3-> {
                et_userName.error = getString(R.string.enter_username_valid)
                et_userName.requestFocus()
                valid = false
            }
            mobile.isEmpty() -> {
                et_mobile.error=getString(R.string.enter_mobile)
                et_mobile.requestFocus()
                valid = false

            }
            callingCode==getString(R.string.intro)->{
                Toast.makeText(
                    applicationContext,
                    getString(R.string.select_code),
                    Toast.LENGTH_LONG
                ).show()
                valid = false
            }
            email.isEmpty() -> {
                et_email.error=getString(R.string.enter_email)
                et_email.requestFocus()
                valid = false

            }
            !EMAIL_ADDRESS.matcher(email).matches() -> {
                et_email.error=getString(R.string.enter_valid_email)
                et_email.requestFocus()
                valid = false

            }
            password.isEmpty() -> {
                et_password.error=getString(R.string.enter_password)
                et_password.requestFocus()
                valid = false

            }
            password.length<6 -> {
                et_password.error=getString(R.string.enter_password_Length)
                et_password.requestFocus()
                valid = false

            }
            confirmPwd.isEmpty() -> {
                et_confirmPwd.error=getString(R.string.enter_confirm_pass)
                et_confirmPwd.requestFocus()

                valid = false

            }
            password != confirmPwd -> {
                et_confirmPwd.error=getString(R.string.psd_not_match)
                et_confirmPwd.requestFocus()
                valid = false

            }
            gender.equals("") -> {
                Toast.makeText(
                    applicationContext,
                    getString(R.string.choose_gender),
                    Toast.LENGTH_LONG
                ).show()
                valid = false
            }
            !isAgree -> {
                Toast.makeText(
                    applicationContext,
                    getString(R.string.agree_terms),
                    Toast.LENGTH_LONG
                ).show()
                valid = false

            }

        }
        return valid
    }



}


