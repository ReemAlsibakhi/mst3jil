package com.reemsib.mosta3ml.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.google.gson.Gson
import com.google.gson.JsonParser
import com.reemsib.mosta3ml.R
import com.reemsib.mosta3ml.model.Advert
import com.reemsib.mosta3ml.model.Reviews
import com.reemsib.mosta3ml.setting.MySession
import com.reemsib.mosta3ml.setting.MySingleton
import com.reemsib.mosta3ml.utils.Constants
import com.reemsib.mosta3ml.utils.URLs
import kotlinx.android.synthetic.main.activity_edit_comment.*
import org.json.JSONException
import org.json.JSONObject
import java.util.*


class EditCommentActivity : AppCompatActivity() , View.OnClickListener{
   var comment_id:Int?=null
   var position :Int ?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_comment)
        val i=intent
        if(i !=null){
            et_comment.setText(i.getStringExtra(Constants.REVIEW_CONTENT))
            comment_id=i.getIntExtra(Constants.REVIEW_Id, -1)
            position=i.getIntExtra(Constants.POSITION,-1)
        }

        tv_cancel.setOnClickListener(this)
        tv_update_comment.setOnClickListener(this)
    }

    override fun onClick(p0: View?) {
     when(p0!!.id){
         R.id.tv_cancel -> {
             finish()
         }
         R.id.tv_update_comment -> {
             var comment = et_comment.text.toString()
             if (comment.isNotEmpty()) {
                 update(comment_id!!, comment,position!!)
             } else {
                 et_comment.error = getString(R.string.fill_feild)
             }

         }
     }
    }

    private fun update(commentId: Int, comment: String,position:Int) {

            val stringRequest = object : StringRequest(
                Method.POST, URLs.URL_EDIT_REVIEW + commentId,
                Response.Listener { response ->
                    try {
                        val obj = JSONObject(response)

                        if (obj.getBoolean("status")) {

                            Toast.makeText(applicationContext, obj.getString("message"), Toast.LENGTH_LONG).show()

                            val jsObj=obj.getJSONObject("data")

                            val mJson = JsonParser().parse(jsObj.toString())
                            val rev = Gson().fromJson<Any>(mJson, Reviews::class.java) as Reviews

                            val returnIntent = Intent()
                            returnIntent.putExtra(Constants.POSITION,position)
                            returnIntent.putExtra(Constants.REVIEW,rev)
                            setResult(RESULT_OK, returnIntent)
                            finish()
                        } else {
                            Toast.makeText(
                                applicationContext,
                                obj.getString("message") + "error",
                                Toast.LENGTH_LONG
                            ).show()

                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                },
                Response.ErrorListener { error ->
                    Toast.makeText(applicationContext, error.message + "", Toast.LENGTH_SHORT)
                        .show()
                }) {
                override fun getParams(): MutableMap<String, String> {
                    val params = HashMap<String, String>()
                    params["content"] =comment
                    return params
                }


                override fun getHeaders(): MutableMap<String, String> {
                    val map = HashMap<String, String>()
                    map["Accept"] = "application/json"
                    map["Authorization"]="Bearer " + MySession.getInstance(applicationContext).getToken()
                    return map
                }


            }
            MySingleton.getInstance(applicationContext).addToRequestQueue(stringRequest)

        }


}