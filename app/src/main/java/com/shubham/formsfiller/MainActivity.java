package com.shubham.formsfiller;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.view.View;
import android.widget.Toast;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    TextInputEditText namet,brancht,emailt,yeart,mobilet,msgt;
    String name,branch,email,year,mobile,message;
    AppCompatButton post;
    ProgressDialog progressDialog;
    public static final MediaType FORM_DATA_TYPE
            = MediaType.parse("application/x-www-form-urlencoded; charset=utf-8");
    public static final String URL="https://docs.google.com/forms/d/e/1FAIpQLSeVFz6mJuILeTVoYNXxno-v6DRCnem4ST5jj2c2GAPZUTW3CA/formResponse";
    //input element ids found from the live form page
    public static final String NAME_KEY="entry.231938939";
    public static final String BRANCH_KEY="entry.930750165";
    public static final String EMAIL_KEY="entry.1736661395";
    public static final String YEAR_KEY="entry.1457720457";
    public static final String MOBILE_KEY="entry.562157188";
    public static final String MESSAGE_KEY="entry.1273253549";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        namet=(TextInputEditText)findViewById(R.id.inp_name);
        brancht=(TextInputEditText)findViewById(R.id.inp_branch);
        emailt=(TextInputEditText)findViewById(R.id.inp_email);
        yeart=(TextInputEditText)findViewById(R.id.inp_year);
        mobilet=(TextInputEditText)findViewById(R.id.inp_number);
        msgt=(TextInputEditText)findViewById(R.id.input_query);
        post=(AppCompatButton) findViewById(R.id.btn_post);
        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PostingData();
            }
        });
    }
    public void PostingData()
    {
        post.setEnabled(false);
        if(validate())
        {
            PostDataTask postDataTask = new PostDataTask();
            progressDialog = new ProgressDialog(MainActivity.this,
                    R.style.AppTheme_Dark_Dialog);
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage("Please Wait...");
            progressDialog.show();
            postDataTask.execute(URL,name,branch,email,year,mobile,message);
        }
        else
            post.setEnabled(true);
    }
    public boolean validate()
    {
        boolean valid = true;
        name=namet.getText().toString();
        branch=brancht.getText().toString();
        email = emailt.getText().toString();
        year=yeart.getText().toString();
        mobile=mobilet.getText().toString();
        message=msgt.getText().toString();
        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailt.setError("enter a valid email address");
            valid = false;
        }
        if(name.isEmpty() || name.length()<4)
        {
            namet.setError("Name?");
            valid=false;
        }
        if(branch.isEmpty())
        {
            brancht.setError("Branch?");
            valid=false;
        }
        if(year.isEmpty())
        {
            yeart.setError("Year?");
            valid=false;
        }
        if(!mobile.isEmpty() && mobile.length()!=10)
        {
            mobilet.setError("Must be 10 digits");
            valid=false;
        }
        if(message.isEmpty())
        {
            msgt.setError("Empty?");
            valid=false;
        }
        return valid;
    }
    private class PostDataTask extends AsyncTask<String, Void, Boolean>
    {
        @Override
        protected Boolean doInBackground(String...contactData) {
            Boolean result = true;
            String url = contactData[0];
            String name = contactData[1];
            String branch = contactData[2];
            String email = contactData[3];
            String year = contactData[4];
            String mobile = contactData[5];
            String message = contactData[6];
            String postBody="";

            try {
                //all values must be URL encoded to make sure that special characters like & | ",etc.
                //do not cause problems
                postBody = NAME_KEY+"=" + URLEncoder.encode(name,"UTF-8") +
                        "&" + BRANCH_KEY + "=" + URLEncoder.encode(branch,"UTF-8") +
                        "&" + EMAIL_KEY + "=" + URLEncoder.encode(email,"UTF-8")+
                        "&" + YEAR_KEY + "=" + URLEncoder.encode(year,"UTF-8")+
                        "&" + MOBILE_KEY + "=" + URLEncoder.encode(mobile,"UTF-8")+
                        "&" + MESSAGE_KEY + "=" + URLEncoder.encode(message,"UTF-8");
            } catch (UnsupportedEncodingException ex) {
                result=false;
            }
            /*
            //If you want to use HttpRequest class from http://stackoverflow.com/a/2253280/1261816
            try {
			HttpRequest httpRequest = new HttpRequest();
			httpRequest.sendPost(url, postBody);
		}catch (Exception exception){
			result = false;
		}
            */
            try{
                //Create OkHttpClient for sending request
                OkHttpClient client = new OkHttpClient();
                //Create the request body with the help of Media Type
                RequestBody body = RequestBody.create(FORM_DATA_TYPE, postBody);
                Request request = new Request.Builder()
                        .url(url)
                        .post(body)
                        .build();
                //Send the request
                Response response = client.newCall(request).execute();
            }catch (IOException exception){
                result=false;
            }
            return result;
        }
        @Override
        protected void onPostExecute(Boolean result){
            //Print Success or failure message accordingly
            progressDialog.dismiss();
            Toast.makeText(getApplicationContext(),result?"Message successfully sent!":"There was some error in sending message. Please try again after some time.", Toast.LENGTH_LONG).show();
            post.setEnabled(true);
        }
    }
}
