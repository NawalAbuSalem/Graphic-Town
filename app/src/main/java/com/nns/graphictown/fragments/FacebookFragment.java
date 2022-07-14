package com.nns.graphictown.fragments;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.nns.graphictown.Adapters.GalleryImagesAdapter;
import com.nns.graphictown.Helpers.PreferenceHelper;
import com.nns.graphictown.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FacebookFragment extends Fragment {



    public interface FacebookActionsListener{

        void facebookLogin(LoginButton loginButton);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof FacebookActionsListener){
            facebookActionsListener= (FacebookActionsListener) context;
        }
        if (context instanceof OnImageClickListener){
            onImageClickListener= (OnImageClickListener) context;
        }
    }
    private FacebookActionsListener facebookActionsListener;
    private PreferenceHelper preferenceHelper;
    private List<String> imagesList;
    private RecyclerView facebookRecyclerView;
    private OnImageClickListener onImageClickListener;
    private GalleryImagesAdapter facebookImagesAdapter;
    private ProgressBar progressBar;
    private LoginButton loginButton;
    private  int page=1;
    private  int limit=40;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view=inflater.inflate(R.layout.facebook_fragment,container,false);
        facebookRecyclerView = view.findViewById(R.id.facebook_recycler_view);
        addFacebookRecyclerViewAdapter();
        loginButton =view.findViewById(R.id.facebook_login_button);
        progressBar=view.findViewById(R.id.progress_bar_waiting);
        preferenceHelper=new PreferenceHelper(getContext());
        if (preferenceHelper.isFacebookLogin()){
            System.out.println(preferenceHelper.getFacebookUserToken());
            loginButton.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
            loadFacebookImages();
        }
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               facebookActionsListener.facebookLogin(loginButton);
            }
        });
        return view;
    }

    private void addFacebookRecyclerViewAdapter() {
        imagesList=new ArrayList<>();
        facebookRecyclerView.setLayoutManager(new GridLayoutManager(getContext(),3));
        facebookImagesAdapter=new GalleryImagesAdapter(imagesList,getActivity());
        facebookRecyclerView.setAdapter(facebookImagesAdapter);
        facebookImagesAdapter.setOnGalleryImageClickListener(new GalleryImagesAdapter.OnGalleryImageClickListener() {
            @Override
            public void addNewImage(String imageUri,int position) {
                onImageClickListener.onInsertNewImageListener(imageUri);

            }
            @Override
            public void removeImage(String imageUri,int position) {
                onImageClickListener.onDeleteImageListener(imageUri);
            }
        });
    }

    private void loadFacebookImages() {
        String mainUrl="https://graph.facebook.com/"+preferenceHelper.getFacebookUserID()+"/photos?type=uploaded&limit=40&page="+1+"&access_token="+preferenceHelper.getFacebookUserToken();
        RequestQueue requestQueue= Volley.newRequestQueue(getContext());
        StringRequest mainStringRequest=new StringRequest(
                Request.Method.GET,
                mainUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            progressBar.setVisibility(View.GONE);
                            JSONObject mainJson=new JSONObject(response);
                            JSONArray mainJsonJSONArray=mainJson.getJSONArray("data");
                            for (int i = 0; i < mainJsonJSONArray.length(); i++) {
                                String imageId= (String) mainJsonJSONArray.getJSONObject(i).get("id");
                                getImageURL(imageId);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressBar.setVisibility(View.GONE);
                    }
                });
        requestQueue.add(mainStringRequest);
    }

    private void getImageURL(String imageId) {
        String url="https://graph.facebook.com/"+imageId+"?fields=images&access_token="+preferenceHelper.getFacebookUserToken();
        RequestQueue requestQueue= Volley.newRequestQueue(getContext());
        StringRequest stringRequest=new StringRequest(
                Request.Method.GET,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject=new JSONObject(response);
                            JSONArray jsonArray=jsonObject.getJSONArray("images");
                            String imageUrl= (String) jsonArray.getJSONObject(0).get("source");
                            addFacebookImageUrl(imageUrl);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        System.out.println(error.toString());
                    }
                });
        requestQueue.add(stringRequest);
    }

    private void addFacebookImageUrl(String imageUrl) {
        imagesList.add(imageUrl);
        facebookImagesAdapter.notifyItemInserted(imagesList.size()-1);
    }

    public void loadLoginData() {
      loginButton.setVisibility(View.GONE);
       progressBar.setVisibility(View.VISIBLE);
       loadFacebookImages();
    }
}
