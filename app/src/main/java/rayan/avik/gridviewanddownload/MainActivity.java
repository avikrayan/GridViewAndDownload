package rayan.avik.gridviewanddownload;

import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    GridView gridView;
    ArrayList<Pojo> pojoArrayList = new ArrayList<Pojo>();
    Pojo pojoDetails;
    private String DATA_URL;
    private ProgressDialog progressDialog;
    private DownloadManager downloadManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        gridView = (GridView) findViewById(R.id.mygridview);
        loadGridContain();

    }

    private void loadGridContain() {

        DATA_URL = "https://api.myjson.com/bins/g5nrp";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, DATA_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject reader = new JSONObject(response);
                    JSONObject responseJSON = reader.getJSONObject("response");
                    String resultSuccess = responseJSON.getString("result");
                    if (resultSuccess.equals("success")) {
                        JSONArray jsonarray = responseJSON.getJSONArray("subject");
                        for (int i = 0; i < jsonarray.length(); i++) {
                            JSONObject c = jsonarray.getJSONObject(i);
                            pojoDetails = new Pojo();
                            pojoDetails.setName(c.getString("name"));
                            pojoDetails.setImage(c.getString("image"));
                            pojoArrayList.add(pojoDetails);
                        }
                        progressDialog.dismiss();
                        gridView.setAdapter(new myGridAdapter());
                    }
                }
                catch (final JSONException e) {
                    progressDialog.dismiss();
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Override
            protected Map<String,String> getParams() {
                Map<String,String>params = new HashMap<String,String>();
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
        progressDialog = ProgressDialog.show(MainActivity.this, "Please Wait...", null, true, true);
        progressDialog.setMessage("Fetching Your Data ! Please wait...!");
        progressDialog.setCancelable(false);

    }

    private class myGridAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return pojoArrayList.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            LayoutInflater inflater=getLayoutInflater();
            View row=inflater.inflate(R.layout.custome_grid_layout,parent,false);

            TextView name = (TextView) row.findViewById(R.id.child_textView);
            ImageView image = (ImageView) row.findViewById(R.id.child_imageView);
            Button downloadButton = (Button) row.findViewById(R.id.child_btn_download);

            name.setText(pojoArrayList.get(position).getName());

            Picasso.with(MainActivity.this)
                    .load(pojoArrayList.get(position).getImage())
                    .fit()
                    .centerCrop()
                    //.placeholder(R.drawable.user_placeholder)
                    //.error(R.drawable.user_placeholder_error)
                    .into(image);

            downloadButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    downloadManager = (DownloadManager)getSystemService(Context.DOWNLOAD_SERVICE);
                    Uri uri = Uri.parse(pojoArrayList.get(position).getImage());
                    DownloadManager.Request request = new DownloadManager.Request(uri);
                    request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                    Long referance = downloadManager.enqueue(request);

                }
            });

            return row;
        }
    }
}
