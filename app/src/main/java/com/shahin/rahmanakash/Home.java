package com.shahin.rahmanakash;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.facebook.ads.AbstractAdListener;
import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AdSize;
import com.facebook.ads.AudienceNetworkAds;
import com.facebook.ads.InterstitialAdListener;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.RequestConfiguration;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class Home extends AppCompatActivity {



    AdView mAdView;
    LinearLayout facebookadsLayout;
    private com.facebook.ads.AdView facebookAdvide;


    public static int CategoryClicked = -10;
    ImageSlider imageSlider;
    ExpandableHeightGridView mainGrid;
    RelativeLayout rLayRateUs;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    FirebaseFirestore firebaseFirestore;
    NestedScrollView scrollView;
    LinearLayout noInternet;
    SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        firebaseDatabase=FirebaseDatabase.getInstance();
        databaseReference=FirebaseDatabase.getInstance().getReference("Slider");
        firebaseFirestore=FirebaseFirestore.getInstance();
        scrollView=findViewById(R.id.scrollView);
        noInternet=findViewById(R.id.noInternet);
        swipeRefreshLayout=findViewById(R.id.seiperrefsh);
        facebookadsLayout=findViewById(R.id.banner_containerfb);

        mAdView = findViewById(R.id.adView);
        imageSlider = findViewById(R.id.image_slider);
        mainGrid = findViewById(R.id.mainGrid);
        rLayRateUs = findViewById(R.id.rLayRateUs);
        AdsremoteControle();
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                if (isNetworkAvailable(getApplicationContext())){

                    noInternet.setVisibility(View.GONE);
                    scrollView.setVisibility(View.VISIBLE);
                    CallDatabase();
                }else {
                    scrollView.setVisibility(View.GONE);
                    noInternet.setVisibility(View.VISIBLE);
                }

                swipeRefreshLayout.setRefreshing(false);

            }

        });

        if (isNetworkAvailable(getApplicationContext())){

            noInternet.setVisibility(View.GONE);
            scrollView.setVisibility(View.VISIBLE);
            CallDatabase();
        }else {
            scrollView.setVisibility(View.GONE);
            noInternet.setVisibility(View.VISIBLE);
        }

    } //------------------------------onCreate (bundle) ENDS here

    private void AdsremoteControle() {

        databaseReference.child("youtubeVideo").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                String AdsNetwork=snapshot.child("AdRemote").getValue().toString();

                String AdsNetWork=snapshot.child("AdRemote").getValue().toString();
                if (AdsNetWork.contains("admob")){
                    OnDestroy();
                    facebookadsLayout.setVisibility(View.GONE);
                    mAdView.setVisibility(View.VISIBLE);
                    loadBannerAd();
                    loadFullscreenAd();



                }else if (AdsNetWork.contains("Start.io")){

                }else if (AdsNetWork.contains("Facebook")){
                    OnDestroy();
                    loadFacebookfullscreenAds();
                    mAdView.setVisibility(View.GONE);
                    facebookadsLayout.setVisibility(View.VISIBLE);
                    InstallFacebook_bannerAds();





                }



            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private com.facebook.ads.InterstitialAd facebookinterstitialAd;
    private void loadFacebookfullscreenAds() {
        AudienceNetworkAds.initialize(this);
         facebookinterstitialAd=new com.facebook.ads.InterstitialAd(this,"IMG_16_9_APP_INSTALL#YOUR_PLACEMENT_ID");
        InterstitialAdListener facebookinterstitialAdListener=new InterstitialAdListener() {
            @Override
            public void onInterstitialDisplayed(Ad ad) {

            }

            @Override
            public void onInterstitialDismissed(Ad ad) {
                loadFacebookfullscreenAds();


            }

            @Override
            public void onError(Ad ad, AdError adError) {
                facebookinterstitialAd=null;

            }

            @Override
            public void onAdLoaded(Ad ad) {


            }

            @Override
            public void onAdClicked(Ad ad) {

            }

            @Override
            public void onLoggingImpression(Ad ad) {

            }
        };
        facebookinterstitialAd.loadAd(facebookinterstitialAd.buildLoadAdConfig().withAdListener(facebookinterstitialAdListener).build());


    }

    private void InstallFacebook_bannerAds() {
        facebookAdvide=new com.facebook.ads.AdView(getApplicationContext(),"IMG_16_9_APP_INSTALL#YOUR_PLACEMENT_ID", AdSize.BANNER_HEIGHT_50);
        facebookadsLayout.addView(facebookAdvide);
        facebookAdvide.loadAd();
        com.facebook.ads.AdListener adListener=new com.facebook.ads.AdListener() {
            @Override
            public void onError(Ad ad, AdError adError) {

            }

            @Override
            public void onAdLoaded(Ad ad) {

            }

            @Override
            public void onAdClicked(Ad ad) {

            }

            @Override
            public void onLoggingImpression(Ad ad) {

            }
        };




    }



    private void OnDestroy(){

        if (facebookAdvide!=null){
            facebookAdvide.destroy();
        }
        if (facebookAdvide!=null){
            facebookAdvide.destroy();
        }

    }

    private void CallDatabase() {
        ProgressDialog dialog = new ProgressDialog(Home.this);
        dialog.setMessage("please wait...");
        dialog.show();
        databaseReference.child("SliderList").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String photo=snapshot.child("photo1").getValue().toString();
                String photoTitile1=snapshot.child("photoTitile1").getValue().toString();
                String photo2=snapshot.child("photo2").getValue().toString();
                String photoTitile2=snapshot.child("photoTitile2").getValue().toString();
                String photo3=snapshot.child("photo3").getValue().toString();
                String photoTitile3=snapshot.child("photoTitile3").getValue().toString();
                String photo4=snapshot.child("photo4").getValue().toString();
                String photoTitile4=snapshot.child("photoTitile4").getValue().toString();
                String photo5=snapshot.child("photo5").getValue().toString();
                String photoTitile5=snapshot.child("photoTitile5").getValue().toString();
                String photo6=snapshot.child("photo6").getValue().toString();
                String photoTitile6=snapshot.child("photoTitile6").getValue().toString();
                String photo7=snapshot.child("photo7").getValue().toString();
                String photoTitile7=snapshot.child("photoTitile7").getValue().toString();
                String photo8=snapshot.child("photo8").getValue().toString();
                String photoTitile8=snapshot.child("photoTitile8").getValue().toString();
                String photo9=snapshot.child("photo9").getValue().toString();
                String photoTitile9=snapshot.child("photoTitile9").getValue().toString();
                String photo10=snapshot.child("photo10").getValue().toString();
                String photoTitile10=snapshot.child("photoTitile10").getValue().toString();



                ArrayList<SlideModel> imageList = new ArrayList<>();
                imageList.add(new SlideModel(photo, photoTitile1, null));
                imageList.add(new SlideModel(photo2, photoTitile2, null));
                imageList.add(new SlideModel(photo3, photoTitile3, null));
                imageList.add(new SlideModel(photo4, photoTitile4, null));
                imageList.add(new SlideModel(photo5, photoTitile5, null));
                imageList.add(new SlideModel(photo6, photoTitile6, null));
                imageList.add(new SlideModel(photo7, photoTitile7, null));
                imageList.add(new SlideModel(photo8, photoTitile8, null));
                imageList.add(new SlideModel(photo9, photoTitile9, null));
                imageList.add(new SlideModel(photo10, photoTitile10, null));
                imageSlider.setImageList(imageList, ScaleTypes.CENTER_CROP);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        databaseReference.child("youtubeVideo").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                rootArrayList = new ArrayList();
                DogArrayList = new ArrayList<>();
                videoArrayList = new ArrayList<>();

                addVideoItem(snapshot.child("Banga_Song").child("Chomok_Hasan").child("1Song").child("1Song_Link").getValue().toString(),snapshot.child("Banga_Song").child("Chomok_Hasan").child("1Song").child("1Song_Tittle").getValue().toString(),snapshot.child("Banga_Song").child("Chomok_Hasan").child("1Song").child("1Song_Description").getValue().toString());
                addVideoItem(snapshot.child("Banga_Song").child("Chomok_Hasan").child("2Song").child("2Song_Link").getValue().toString(),snapshot.child("Banga_Song").child("Chomok_Hasan").child("2Song").child("2Song_Tittle").getValue().toString(),snapshot.child("Banga_Song").child("Chomok_Hasan").child("2Song").child("2song_Description").getValue().toString());
                addVideoItem(snapshot.child("Banga_Song").child("Chomok_Hasan").child("3Song").child("3song_Link").getValue().toString(),snapshot.child("Banga_Song").child("Chomok_Hasan").child("3Song").child("3Song_Tittle").getValue().toString(),snapshot.child("Banga_Song").child("Chomok_Hasan").child("3Song").child("3Song_Description").getValue().toString());
                addVideoItem(snapshot.child("Banga_Song").child("Chomok_Hasan").child("4Song").child("4Song_Link").getValue().toString(),snapshot.child("Banga_Song").child("Chomok_Hasan").child("4Song").child("4Song_Tittle").getValue().toString(),snapshot.child("Banga_Song").child("Chomok_Hasan").child("4Song").child("4Song_Description").getValue().toString());
                addVideoItem(snapshot.child("Banga_Song").child("Chomok_Hasan").child("5Song").child("5Song_Link").getValue().toString(),snapshot.child("Banga_Song").child("Chomok_Hasan").child("5Song").child("5Song_Tittle").getValue().toString(),snapshot.child("Banga_Song").child("Chomok_Hasan").child("5Song").child("5Song_Description").getValue().toString());
                addVideoItem(snapshot.child("Banga_Song").child("Chomok_Hasan").child("6Song").child("6Song_Link").getValue().toString(),snapshot.child("Banga_Song").child("Chomok_Hasan").child("6Song").child("6Song_Tittle").getValue().toString(),snapshot.child("Banga_Song").child("Chomok_Hasan").child("6Song").child("6Song_Description").getValue().toString());
                addVideoItem(snapshot.child("Banga_Song").child("Chomok_Hasan").child("7Song").child("7Song_Link").getValue().toString(),snapshot.child("Banga_Song").child("Chomok_Hasan").child("7Song").child("7Song_Tittle").getValue().toString(),snapshot.child("Banga_Song").child("Chomok_Hasan").child("7Song").child("7Song_Description").getValue().toString());
                addVideoItem(snapshot.child("Banga_Song").child("Chomok_Hasan").child("8Song").child("8Song_Link").getValue().toString(),snapshot.child("Banga_Song").child("Chomok_Hasan").child("8Song").child("8Song_Tittle").getValue().toString(),snapshot.child("Banga_Song").child("Chomok_Hasan").child("8Song").child("8Song_Description").getValue().toString());
                addVideoItem(snapshot.child("Banga_Song").child("Chomok_Hasan").child("9Song").child("9Song_Link").getValue().toString(),snapshot.child("Banga_Song").child("Chomok_Hasan").child("9Song").child("9Song_Tittle").getValue().toString(),snapshot.child("Banga_Song").child("Chomok_Hasan").child("9Song").child("9Song_description").getValue().toString());
                addVideoItem(snapshot.child("Banga_Song").child("Chomok_Hasan").child("10Song").child("10Song_Link").getValue().toString(),snapshot.child("Banga_Song").child("Chomok_Hasan").child("10Song").child("10Song_Tittle").getValue().toString(),snapshot.child("Banga_Song").child("Chomok_Hasan").child("10Song").child("10Song_Description").getValue().toString());
                addVideoItem(snapshot.child("Banga_Song").child("Chomok_Hasan").child("11Song").child("11Song_Link").getValue().toString(),snapshot.child("Banga_Song").child("Chomok_Hasan").child("11Song").child("11Song_Tittle").getValue().toString(),snapshot.child("Banga_Song").child("Chomok_Hasan").child("11Song").child("11Song_Description").getValue().toString());
                addVideoItem(snapshot.child("Banga_Song").child("Chomok_Hasan").child("12Song").child("12Song_Link").getValue().toString(),snapshot.child("Banga_Song").child("Chomok_Hasan").child("12Song").child("12Song_Tittle").getValue().toString(),snapshot.child("Banga_Song").child("Chomok_Hasan").child("12Song").child("12Song_Description").getValue().toString());
                addVideoItem(snapshot.child("Banga_Song").child("Chomok_Hasan").child("13Song").child("13Song-Link").getValue().toString(),snapshot.child("Banga_Song").child("Chomok_Hasan").child("13Song").child("13Song_Tittle").getValue().toString(),snapshot.child("Banga_Song").child("Chomok_Hasan").child("13Song").child("3Song_Description").getValue().toString());
                addVideoItem(snapshot.child("Banga_Song").child("Chomok_Hasan").child("14Song").child("14Song_Link").getValue().toString(),snapshot.child("Banga_Song").child("Chomok_Hasan").child("14Song").child("14Song_Tittle").getValue().toString(),snapshot.child("Banga_Song").child("Chomok_Hasan").child("14Song").child("14Song_Description").getValue().toString());
                addVideoItem(snapshot.child("Banga_Song").child("Chomok_Hasan").child("15Song").child("15Song_Link").getValue().toString(),snapshot.child("Banga_Song").child("Chomok_Hasan").child("15Song").child("15Song_Tittle").getValue().toString(),snapshot.child("Banga_Song").child("Chomok_Hasan").child("15Song").child("15Song_Description").getValue().toString());
                addVideoItem(snapshot.child("Banga_Song").child("Chomok_Hasan").child("16Song").child("16Song_Link").getValue().toString(),snapshot.child("Banga_Song").child("Chomok_Hasan").child("16Song").child("16song_Tittle").getValue().toString(),snapshot.child("Banga_Song").child("Chomok_Hasan").child("16Song").child("16Song_Description").getValue().toString());
                addVideoItem(snapshot.child("Banga_Song").child("Chomok_Hasan").child("17Song").child("17Song_Link").getValue().toString(),snapshot.child("Banga_Song").child("Chomok_Hasan").child("17Song").child("17Song_Tittle").getValue().toString(),snapshot.child("Banga_Song").child("Chomok_Hasan").child("17Song").child("17Song_Description").getValue().toString());
                addVideoItem(snapshot.child("Banga_Song").child("Chomok_Hasan").child("18Song").child("18Song_Link").getValue().toString(),snapshot.child("Banga_Song").child("Chomok_Hasan").child("18Song").child("18Song_Tittle").getValue().toString(),snapshot.child("Banga_Song").child("Chomok_Hasan").child("18Song").child("18Song_Description").getValue().toString());
                addVideoItem(snapshot.child("Banga_Song").child("Chomok_Hasan").child("19Song").child("19Song_Link").getValue().toString(),snapshot.child("Banga_Song").child("Chomok_Hasan").child("19Song").child("19Song_Tittle").getValue().toString(),snapshot.child("Banga_Song").child("Chomok_Hasan").child("19Song").child("19Song_Description").getValue().toString());

                addVideoItem(snapshot.child("Banga_Song").child("Chomok_Hasan").child("20Song").child("20Song_Link").getValue().toString(),snapshot.child("Banga_Song").child("Chomok_Hasan").child("20Song").child("20Song_Tittle").getValue().toString(),snapshot.child("Banga_Song").child("Chomok_Hasan").child("20Song").child("20_Description").getValue().toString());
                addVideoItem(snapshot.child("Banga_Song").child("Tasrif_Khan").child("1Song").child("1Song_Link").getValue().toString(),snapshot.child("Banga_Song").child("Tasrif_Khan").child("1Song").child("1Song_Tittle").getValue().toString(),snapshot.child("Banga_Song").child("Tasrif_Khan").child("1Song").child("1Song_Description").getValue().toString());
                addVideoItem(snapshot.child("Banga_Song").child("Tasrif_Khan").child("2Song").child("2Song_Link").getValue().toString(),snapshot.child("Banga_Song").child("Tasrif_Khan").child("2Song").child("2Song_Tittle").getValue().toString(),snapshot.child("Banga_Song").child("Tasrif_Khan").child("2Song").child("2Song_Description").getValue().toString());
                addVideoItem(snapshot.child("Banga_Song").child("Tasrif_Khan").child("3Song").child("3Song_Link").getValue().toString(),snapshot.child("Banga_Song").child("Tasrif_Khan").child("3Song").child("3Song_Tittle").getValue().toString(),snapshot.child("Banga_Song").child("Tasrif_Khan").child("3Song").child("3Song_Description").getValue().toString());
                addVideoItem(snapshot.child("Banga_Song").child("Tasrif_Khan").child("4Song").child("4Song_Link").getValue().toString(),snapshot.child("Banga_Song").child("Tasrif_Khan").child("4Song").child("4Song_Tittle").getValue().toString(),snapshot.child("Banga_Song").child("Tasrif_Khan").child("4Song").child("4Song_Description").getValue().toString());
                addVideoItem(snapshot.child("Banga_Song").child("Tasrif_Khan").child("5Song").child("5Song_Link").getValue().toString(),snapshot.child("Banga_Song").child("Tasrif_Khan").child("5Song").child("5Song_Tittle").getValue().toString(),snapshot.child("Banga_Song").child("Tasrif_Khan").child("5Song").child("5Song_Description").getValue().toString());
                addVideoItem(snapshot.child("Banga_Song").child("Tasrif_Khan").child("6Song").child("6Song_Link").getValue().toString(),snapshot.child("Banga_Song").child("Tasrif_Khan").child("6Song").child("6Song_Tittle").getValue().toString(),snapshot.child("Banga_Song").child("Tasrif_Khan").child("6Song").child("6Song_Description").getValue().toString());
                addVideoItem(snapshot.child("Banga_Song").child("Tasrif_Khan").child("7Song").child("7Song_Link").getValue().toString(),snapshot.child("Banga_Song").child("Tasrif_Khan").child("7Song").child("7Song_Tittle").getValue().toString(),snapshot.child("Banga_Song").child("Tasrif_Khan").child("7Song").child("7Song_Description").getValue().toString());
                addVideoItem(snapshot.child("Banga_Song").child("Tasrif_Khan").child("8Song").child("8Song_Link").getValue().toString(),snapshot.child("Banga_Song").child("Tasrif_Khan").child("8Song").child("8Song_Tittle").getValue().toString(),snapshot.child("Banga_Song").child("Tasrif_Khan").child("8Song").child("8Song_Description").getValue().toString());
                addVideoItem(snapshot.child("Banga_Song").child("Tasrif_Khan").child("9Song").child("9Song_Link").getValue().toString(),snapshot.child("Banga_Song").child("Tasrif_Khan").child("9Song").child("9Song_Tittle").getValue().toString(),snapshot.child("Banga_Song").child("Tasrif_Khan").child("9Song").child("9Song_Description").getValue().toString());
                addVideoItem(snapshot.child("Banga_Song").child("Tasrif_Khan").child("10Song").child("10Song_Link").getValue().toString(),snapshot.child("Banga_Song").child("Tasrif_Khan").child("10Song").child("10Song_Tittle").getValue().toString(),snapshot.child("Banga_Song").child("Tasrif_Khan").child("10Song").child("10Song_Description").getValue().toString());
                addVideoItem(snapshot.child("Banga_Song").child("Tasrif_Khan").child("11Song").child("11Song_Link").getValue().toString(),snapshot.child("Banga_Song").child("Tasrif_Khan").child("11Song").child("11Song_Tittle").getValue().toString(),snapshot.child("Banga_Song").child("Tasrif_Khan").child("11Song").child("11Song_Description").getValue().toString());
                addVideoItem(snapshot.child("Banga_Song").child("Tasrif_Khan").child("12Song").child("12Song_Link").getValue().toString(),snapshot.child("Banga_Song").child("Tasrif_Khan").child("12Song").child("12Song_Tittle").getValue().toString(),snapshot.child("Banga_Song").child("Tasrif_Khan").child("12Song").child("12Song_Description").getValue().toString());
                addVideoItem(snapshot.child("Banga_Song").child("Tasrif_Khan").child("13Song").child("13Song_Link").getValue().toString(),snapshot.child("Banga_Song").child("Tasrif_Khan").child("13Song").child("13Song_Tittle").getValue().toString(),snapshot.child("Banga_Song").child("Tasrif_Khan").child("13Song").child("13Song_Description").getValue().toString());
                addVideoItem(snapshot.child("Banga_Song").child("Tasrif_Khan").child("14Song").child("14Song_Link").getValue().toString(),snapshot.child("Banga_Song").child("Tasrif_Khan").child("14Song").child("14Song_Tittle").getValue().toString(),snapshot.child("Banga_Song").child("Tasrif_Khan").child("14Song").child("14Song_Descriptio").getValue().toString());
                addVideoItem(snapshot.child("Banga_Song").child("Tasrif_Khan").child("15Song").child("15Song_Link").getValue().toString(),snapshot.child("Banga_Song").child("Tasrif_Khan").child("15Song").child("15Song_Tittle").getValue().toString(),snapshot.child("Banga_Song").child("Tasrif_Khan").child("15Song").child("15Song_description").getValue().toString());
                addVideoItem(snapshot.child("Banga_Song").child("Tasrif_Khan").child("16Song").child("16Song_Link").getValue().toString(),snapshot.child("Banga_Song").child("Tasrif_Khan").child("16Song").child("16Song_Tittle").getValue().toString(),snapshot.child("Banga_Song").child("Tasrif_Khan").child("16Song").child("16Song_description").getValue().toString());
                addVideoItem(snapshot.child("Banga_Song").child("Tasrif_Khan").child("17Song").child("17Song_Link").getValue().toString(),snapshot.child("Banga_Song").child("Tasrif_Khan").child("17Song").child("17Song_Tittle").getValue().toString(),snapshot.child("Banga_Song").child("Tasrif_Khan").child("17Song").child("17Song_description").getValue().toString());
                addVideoItem(snapshot.child("Banga_Song").child("Tasrif_Khan").child("18Song").child("18Song_Link").getValue().toString(),snapshot.child("Banga_Song").child("Tasrif_Khan").child("18Song").child("18song_Tittle").getValue().toString(),snapshot.child("Banga_Song").child("Tasrif_Khan").child("18Song").child("18Song_Description").getValue().toString());
                addVideoItem(snapshot.child("Banga_Song").child("Tasrif_Khan").child("19Song").child("19Song_Link").getValue().toString(),snapshot.child("Banga_Song").child("Tasrif_Khan").child("19Song").child("19Song_Tittle").getValue().toString(),snapshot.child("Banga_Song").child("Tasrif_Khan").child("19Song").child("19Song_Description").getValue().toString());
                addVideoItem(snapshot.child("Banga_Song").child("Tasrif_Khan").child("20Song").child("20Song_Link").getValue().toString(),snapshot.child("Banga_Song").child("Tasrif_Khan").child("20Song").child("20Song_Tittle").getValue().toString(),snapshot.child("Banga_Song").child("Tasrif_Khan").child("20Song").child("20Song_Description").getValue().toString());


                addVideoItem(snapshot.child("Banga_Song").child("James").child("1Song_Link").getValue().toString(),snapshot.child("Banga_Song").child("James").child("1Song_Tittle").getValue().toString(),snapshot.child("Banga_Song").child("James").child("1Song_Description").getValue().toString());
                addVideoItem(snapshot.child("Banga_Song").child("James").child("2Song_Link").getValue().toString(),snapshot.child("Banga_Song").child("James").child("2Song_Tittle").getValue().toString(),snapshot.child("Banga_Song").child("James").child("2Song_Description").getValue().toString());
                addVideoItem(snapshot.child("Banga_Song").child("James").child("3Song_Link").getValue().toString(),snapshot.child("Banga_Song").child("James").child("3Song_Tittle").getValue().toString(),snapshot.child("Banga_Song").child("James").child("3Song_Description").getValue().toString());
                addVideoItem(snapshot.child("Banga_Song").child("James").child("4Song_Link").getValue().toString(),snapshot.child("Banga_Song").child("James").child("4Song_Tittle").getValue().toString(),snapshot.child("Banga_Song").child("James").child("4Song_Description").getValue().toString());
                addVideoItem(snapshot.child("Banga_Song").child("James").child("5Song_Link").getValue().toString(),snapshot.child("Banga_Song").child("James").child("5Song_Tittle").getValue().toString(),snapshot.child("Banga_Song").child("James").child("5Song-Description").getValue().toString());
                addVideoItem(snapshot.child("Banga_Song").child("James").child("6Song_Link").getValue().toString(),snapshot.child("Banga_Song").child("James").child("6Song_Tittle").getValue().toString(),snapshot.child("Banga_Song").child("James").child("6Song_Description").getValue().toString());
                addVideoItem(snapshot.child("Banga_Song").child("James").child("7Song_Link").getValue().toString(),snapshot.child("Banga_Song").child("James").child("7Song_Tittle").getValue().toString(),snapshot.child("Banga_Song").child("James").child("7Song_Description").getValue().toString());
                addVideoItem(snapshot.child("Banga_Song").child("James").child("8Song_Link").getValue().toString(),snapshot.child("Banga_Song").child("James").child("8Song_Tittle").getValue().toString(),snapshot.child("Banga_Song").child("James").child("8Song_Description").getValue().toString());
                addVideoItem(snapshot.child("Banga_Song").child("James").child("9Song_Link").getValue().toString(),snapshot.child("Banga_Song").child("James").child("9Song_Tittle").getValue().toString(),snapshot.child("Banga_Song").child("James").child("9Song-Description").getValue().toString());
                addVideoItem(snapshot.child("Banga_Song").child("James").child("10Song_Link").getValue().toString(),snapshot.child("Banga_Song").child("James").child("10Song_Tittle").getValue().toString(),snapshot.child("Banga_Song").child("James").child("10Song_Description").getValue().toString());
                addVideoItem(snapshot.child("Banga_Song").child("James").child("11Song-Link").getValue().toString(),snapshot.child("Banga_Song").child("James").child("11Song_Tittle").getValue().toString(),snapshot.child("Banga_Song").child("James").child("11Song_Description").getValue().toString());
                addVideoItem(snapshot.child("Banga_Song").child("James").child("12Song-Link").getValue().toString(),snapshot.child("Banga_Song").child("James").child("12Song_Tittle").getValue().toString(),snapshot.child("Banga_Song").child("James").child("12Song_Description").getValue().toString());
                addVideoItem(snapshot.child("Banga_Song").child("James").child("13Song_Link").getValue().toString(),snapshot.child("Banga_Song").child("James").child("13Song-Tittle").getValue().toString(),snapshot.child("Banga_Song").child("James").child("13Song_Description").getValue().toString());
                addVideoItem(snapshot.child("Banga_Song").child("James").child("14Song_Link").getValue().toString(),snapshot.child("Banga_Song").child("James").child("14Song_Tittle").getValue().toString(),snapshot.child("Banga_Song").child("James").child("14song_Description").getValue().toString());
                addVideoItem(snapshot.child("Banga_Song").child("James").child("15Song_Link").getValue().toString(),snapshot.child("Banga_Song").child("James").child("15Song-Tittle").getValue().toString(),snapshot.child("Banga_Song").child("James").child("15Song_Description").getValue().toString());
                addVideoItem(snapshot.child("Banga_Song").child("James").child("16Song_Link").getValue().toString(),snapshot.child("Banga_Song").child("James").child("16Song_Tittle").getValue().toString(),snapshot.child("Banga_Song").child("James").child("16Song_Description").getValue().toString());
                addVideoItem(snapshot.child("Banga_Song").child("James").child("17Song_Link").getValue().toString(),snapshot.child("Banga_Song").child("James").child("17Song_Tittle").getValue().toString(),snapshot.child("Banga_Song").child("James").child("17Song_Description").getValue().toString());
                addVideoItem(snapshot.child("Banga_Song").child("James").child("18Song_Link").getValue().toString(),snapshot.child("Banga_Song").child("James").child("18Song_Tittle").getValue().toString(),snapshot.child("Banga_Song").child("James").child("18Song_Description").getValue().toString());
                addVideoItem(snapshot.child("Banga_Song").child("James").child("19Song_Link").getValue().toString(),snapshot.child("Banga_Song").child("James").child("19Song_Tittle").getValue().toString(),snapshot.child("Banga_Song").child("James").child("19Song_Description").getValue().toString());
                addVideoItem(snapshot.child("Banga_Song").child("James").child("20Song_Link").getValue().toString(),snapshot.child("Banga_Song").child("James").child("20Song_Tittle").getValue().toString(),snapshot.child("Banga_Song").child("James").child("20Song_Description").getValue().toString());


                createPlayListForVideo(snapshot.child("Banga_Song").child("catagoryname").getValue().toString(),R.drawable.category_1);
                addVideoItem(snapshot.child("LiveTv").child("1channel").getValue().toString(),snapshot.child("LiveTv").child("1channelTitle").getValue().toString(),snapshot.child("LiveTv").child("1channelDiscription").getValue().toString());
                addVideoItem(snapshot.child("LiveTv").child("2channel").getValue().toString(),snapshot.child("LiveTv").child("2channelTitle").getValue().toString(),snapshot.child("LiveTv").child("2channeDiscription").getValue().toString());
                addVideoItem(snapshot.child("LiveTv").child("3channel").getValue().toString(),snapshot.child("LiveTv").child("3channelTitle").getValue().toString(),snapshot.child("LiveTv").child("3channelDiscription").getValue().toString());
                addVideoItem(snapshot.child("LiveTv").child("4channel").getValue().toString(),snapshot.child("LiveTv").child("4channelTitle").getValue().toString(),snapshot.child("LiveTv").child("4channelDiscription").getValue().toString());
                addVideoItem(snapshot.child("LiveTv").child("5channel").getValue().toString(),snapshot.child("LiveTv").child("5channelTitle").getValue().toString(),snapshot.child("LiveTv").child("5channelDiscription").getValue().toString());
                addVideoItem(snapshot.child("LiveTv").child("6channel").getValue().toString(),snapshot.child("LiveTv").child("6channelTitle").getValue().toString(),snapshot.child("LiveTv").child("6channelDiscription").getValue().toString());
                addVideoItem(snapshot.child("LiveTv").child("7channel").getValue().toString(),snapshot.child("LiveTv").child("7channelTitle").getValue().toString(),snapshot.child("LiveTv").child("7channelDiscription").getValue().toString());
                addVideoItem(snapshot.child("LiveTv").child("8channel").getValue().toString(),snapshot.child("LiveTv").child("8channelTitle").getValue().toString(),snapshot.child("LiveTv").child("8channelDiscription").getValue().toString());
                addVideoItem(snapshot.child("LiveTv").child("9channel").getValue().toString(),snapshot.child("LiveTv").child("9channelTitle").getValue().toString(),snapshot.child("LiveTv").child("9channelDiscription").getValue().toString());
                addVideoItem(snapshot.child("LiveTv").child("10channel").getValue().toString(),snapshot.child("LiveTv").child("10channelTitle").getValue().toString(),snapshot.child("LiveTv").child("10channelDiscription").getValue().toString());
                addVideoItem(snapshot.child("LiveTv").child("10channel").getValue().toString(),snapshot.child("LiveTv").child("3channelTitle").getValue().toString(),snapshot.child("LiveTv").child("3channelDiscription").getValue().toString());
                createPlayListForVideo(snapshot.child("LiveTv").child("CatogotyName").getValue().toString(),R.drawable.livetv);


                addVideoItem(snapshot.child("BanglaWaz").child("1Waz_Link").getValue().toString(),snapshot.child("BanglaWaz").child("1Waz_Tittle").getValue().toString(),snapshot.child("BanglaWaz").child("1Waz_1Waz_Description").getValue().toString());
                addVideoItem(snapshot.child("BanglaWaz").child("2Waz_Link").getValue().toString(),snapshot.child("BanglaWaz").child("2Waz_Tittle").getValue().toString(),snapshot.child("BanglaWaz").child("2Waz_Description").getValue().toString());
                addVideoItem(snapshot.child("BanglaWaz").child("3Waz_Link").getValue().toString(),snapshot.child("BanglaWaz").child("3Waz_Tittle").getValue().toString(),snapshot.child("BanglaWaz").child("3Waz_Description").getValue().toString());
                addVideoItem(snapshot.child("BanglaWaz").child("4Waz_Link").getValue().toString(),snapshot.child("BanglaWaz").child("4Waz_Tittle").getValue().toString(),snapshot.child("BanglaWaz").child("4Waz_Description").getValue().toString());
                addVideoItem(snapshot.child("BanglaWaz").child("5Waz_Link").getValue().toString(),snapshot.child("BanglaWaz").child("5waz_Tittle").getValue().toString(),snapshot.child("BanglaWaz").child("5Waz_Description").getValue().toString());
                addVideoItem(snapshot.child("BanglaWaz").child("6Waz_Link").getValue().toString(),snapshot.child("BanglaWaz").child("6Waz_Tittle").getValue().toString(),snapshot.child("BanglaWaz").child("6Waz_Description").getValue().toString());
                addVideoItem(snapshot.child("BanglaWaz").child("7Waz_Link").getValue().toString(),snapshot.child("BanglaWaz").child("7Waz_Tittle").getValue().toString(),snapshot.child("BanglaWaz").child("7Waz_Description").getValue().toString());
                addVideoItem(snapshot.child("BanglaWaz").child("8Waz_Link").getValue().toString(),snapshot.child("BanglaWaz").child("8Waz_Tittle").getValue().toString(),snapshot.child("BanglaWaz").child("8Waz_Description").getValue().toString());
                addVideoItem(snapshot.child("BanglaWaz").child("9Waz_Link").getValue().toString(),snapshot.child("BanglaWaz").child("9Waz_Tittle").getValue().toString(),snapshot.child("BanglaWaz").child("9Waz_Description").getValue().toString());
                addVideoItem(snapshot.child("BanglaWaz").child("10Waz_Link").getValue().toString(),snapshot.child("BanglaWaz").child("10Waz_Tittle").getValue().toString(),snapshot.child("BanglaWaz").child("10Waz_Description").getValue().toString());
                addVideoItem(snapshot.child("BanglaWaz").child("11Waz_Link").getValue().toString(),snapshot.child("BanglaWaz").child("11Waz_Tittle").getValue().toString(),snapshot.child("BanglaWaz").child("11Waz_Description").getValue().toString());
                addVideoItem(snapshot.child("BanglaWaz").child("12Waz_Link").getValue().toString(),snapshot.child("BanglaWaz").child("12Waz_Tittle").getValue().toString(),snapshot.child("BanglaWaz").child("12Waz_Description").getValue().toString());
                addVideoItem(snapshot.child("BanglaWaz").child("13Waz_Link").getValue().toString(),snapshot.child("BanglaWaz").child("13Waz_Tittle").getValue().toString(),snapshot.child("BanglaWaz").child("13Waz_Description").getValue().toString());
                addVideoItem(snapshot.child("BanglaWaz").child("14Waz_Link").getValue().toString(),snapshot.child("BanglaWaz").child("14Waz_Tittle").getValue().toString(),snapshot.child("BanglaWaz").child("14Waz_Description").getValue().toString());
                addVideoItem(snapshot.child("BanglaWaz").child("15Waz_Link").getValue().toString(),snapshot.child("BanglaWaz").child("15Waz_Tittle").getValue().toString(),snapshot.child("BanglaWaz").child("15Waz_Description").getValue().toString());
                addVideoItem(snapshot.child("BanglaWaz").child("16Waz_Link").getValue().toString(),snapshot.child("BanglaWaz").child("16Waz_Tittle").getValue().toString(),snapshot.child("BanglaWaz").child("16Waz_Description").getValue().toString());
                addVideoItem(snapshot.child("BanglaWaz").child("17Waz-Link").getValue().toString(),snapshot.child("BanglaWaz").child("17Waz_Tittle").getValue().toString(),snapshot.child("BanglaWaz").child("17Waz_Description").getValue().toString());
                addVideoItem(snapshot.child("BanglaWaz").child("18Waz_Link").getValue().toString(),snapshot.child("BanglaWaz").child("18Waz_Tittle").getValue().toString(),snapshot.child("BanglaWaz").child("18Waz-description").getValue().toString());
                addVideoItem(snapshot.child("BanglaWaz").child("19Waz_Link").getValue().toString(),snapshot.child("BanglaWaz").child("19Waz_Tittle").getValue().toString(),snapshot.child("BanglaWaz").child("19Waz_Description").getValue().toString());
                addVideoItem(snapshot.child("BanglaWaz").child("20Waz_Link").getValue().toString(),snapshot.child("BanglaWaz").child("20Waz_Tittle").getValue().toString(),snapshot.child("BanglaWaz").child("20Waz_Description").getValue().toString());
                createPlayListForVideo(snapshot.child("BanglaWaz").child("catagoryname").getValue().toString(),R.drawable.wazz);




                addVideoItem(snapshot.child("Bangla_Natok").child("1natok_link").getValue().toString(),snapshot.child("Bangla_Natok").child("1natok_tittle").getValue().toString(),snapshot.child("Bangla_Natok").child("1natok_description").getValue().toString());
                addVideoItem(snapshot.child("Bangla_Natok").child("2natok_link").getValue().toString(),snapshot.child("Bangla_Natok").child("2natok_tittle").getValue().toString(),snapshot.child("Bangla_Natok").child("2natok_description").getValue().toString());
                addVideoItem(snapshot.child("Bangla_Natok").child("3natok_link").getValue().toString(),snapshot.child("Bangla_Natok").child("3natok_tittle").getValue().toString(),snapshot.child("Bangla_Natok").child("3natok_description").getValue().toString());
                addVideoItem(snapshot.child("Bangla_Natok").child("4natok_link").getValue().toString(),snapshot.child("Bangla_Natok").child("4natok-tittle").getValue().toString(),snapshot.child("Bangla_Natok").child("4natok_description").getValue().toString());
                addVideoItem(snapshot.child("Bangla_Natok").child("5natok_link").getValue().toString(),snapshot.child("Bangla_Natok").child("5natok_tittle").getValue().toString(),snapshot.child("Bangla_Natok").child("5natok-description").getValue().toString());
                addVideoItem(snapshot.child("Bangla_Natok").child("6natok_link").getValue().toString(),snapshot.child("Bangla_Natok").child("6natok_tittle").getValue().toString(),snapshot.child("Bangla_Natok").child("6natok_description").getValue().toString());
                createPlayListForVideo(snapshot.child("Bangla_Natok").child("catagoryname").getValue().toString(),R.drawable.natok);





                addVideoItem(snapshot.child("Bangla_Movie").child("1_link").getValue().toString(),snapshot.child("Bangla_Movie").child("1_tittle").getValue().toString(),snapshot.child("Bangla_Movie").child("1_description").getValue().toString());
                addVideoItem(snapshot.child("Bangla_Movie").child("2_link").getValue().toString(),snapshot.child("Bangla_Movie").child("2_tittle").getValue().toString(),snapshot.child("Bangla_Movie").child("2_description").getValue().toString());
                addVideoItem(snapshot.child("Bangla_Movie").child("3_link").getValue().toString(),snapshot.child("Bangla_Movie").child("3_tittle").getValue().toString(),snapshot.child("Bangla_Movie").child("3_description").getValue().toString());
                addVideoItem(snapshot.child("Bangla_Movie").child("4_link").getValue().toString(),snapshot.child("Bangla_Movie").child("4_tittle").getValue().toString(),snapshot.child("Bangla_Movie").child("4_description").getValue().toString());
                addVideoItem(snapshot.child("Bangla_Movie").child("5_link").getValue().toString(),snapshot.child("Bangla_Movie").child("5_tittle").getValue().toString(),snapshot.child("Bangla_Movie").child("5_description").getValue().toString());
                addVideoItem(snapshot.child("Bangla_Movie").child("6_link").getValue().toString(),snapshot.child("Bangla_Movie").child("6_tittle").getValue().toString(),snapshot.child("Bangla_Movie").child("6_description").getValue().toString());
                addVideoItem(snapshot.child("Bangla_Movie").child("7_link").getValue().toString(),snapshot.child("Bangla_Movie").child("7_tittle").getValue().toString(),snapshot.child("Bangla_Movie").child("7_description").getValue().toString());
                addVideoItem(snapshot.child("Bangla_Movie").child("8_link").getValue().toString(),snapshot.child("Bangla_Movie").child("8_tittle").getValue().toString(),snapshot.child("Bangla_Movie").child("8_description").getValue().toString());
                addVideoItem(snapshot.child("Bangla_Movie").child("9_link").getValue().toString(),snapshot.child("Bangla_Movie").child("9_tittle").getValue().toString(),snapshot.child("Bangla_Movie").child("9_description").getValue().toString());
                addVideoItem(snapshot.child("Bangla_Movie").child("10_link").getValue().toString(),snapshot.child("Bangla_Movie").child("10_tittle").getValue().toString(),snapshot.child("Bangla_Movie").child("10_description").getValue().toString());
                createPlayListForVideo(snapshot.child("Bangla_Movie").child("CatagoryName").getValue().toString(),R.drawable.natok);




                addVideoItem(snapshot.child("Bangla_Telefilm").child("1_link").getValue().toString(),snapshot.child("Bangla_Telefilm").child("1_tittle").getValue().toString(),snapshot.child("Bangla_Telefilm").child("1_description").getValue().toString());
                addVideoItem(snapshot.child("Bangla_Telefilm").child("2_link").getValue().toString(),snapshot.child("Bangla_Telefilm").child("2_tittle").getValue().toString(),snapshot.child("Bangla_Telefilm").child("2_description").getValue().toString());
                addVideoItem(snapshot.child("Bangla_Telefilm").child("3_link").getValue().toString(),snapshot.child("Bangla_Telefilm").child("3_tittle").getValue().toString(),snapshot.child("Bangla_Telefilm").child("3_description").getValue().toString());
                addVideoItem(snapshot.child("Bangla_Telefilm").child("4_link").getValue().toString(),snapshot.child("Bangla_Telefilm").child("4_tittle").getValue().toString(),snapshot.child("Bangla_Telefilm").child("4_description").getValue().toString());
                addVideoItem(snapshot.child("Bangla_Telefilm").child("5_link").getValue().toString(),snapshot.child("Bangla_Telefilm").child("5_tittle").getValue().toString(),snapshot.child("Bangla_Telefilm").child("5_description").getValue().toString());
                addVideoItem(snapshot.child("Bangla_Telefilm").child("6_link").getValue().toString(),snapshot.child("Bangla_Telefilm").child("6_tittle").getValue().toString(),snapshot.child("Bangla_Telefilm").child("6_description").getValue().toString());
                addVideoItem(snapshot.child("Bangla_Telefilm").child("7_link").getValue().toString(),snapshot.child("Bangla_Telefilm").child("7_tittle").getValue().toString(),snapshot.child("Bangla_Telefilm").child("7_description").getValue().toString());
                addVideoItem(snapshot.child("Bangla_Telefilm").child("8_link").getValue().toString(),snapshot.child("Bangla_Telefilm").child("8_tittle").getValue().toString(),snapshot.child("Bangla_Telefilm").child("8_description").getValue().toString());
                addVideoItem(snapshot.child("Bangla_Telefilm").child("9_link").getValue().toString(),snapshot.child("Bangla_Telefilm").child("9_tittle").getValue().toString(),snapshot.child("Bangla_Telefilm").child("9_description").getValue().toString());
                addVideoItem(snapshot.child("Bangla_Telefilm").child("10_link").getValue().toString(),snapshot.child("Bangla_Telefilm").child("10_tittle").getValue().toString(),snapshot.child("Bangla_Telefilm").child("10_description").getValue().toString());

                createPlayListForVideo(snapshot.child("Bangla_Telefilm").child("Catagory_name").getValue().toString(),R.drawable.natok);


                createCategoryForWebsite(snapshot.child("newsRoom").child("1newsChannelName").getValue().toString(),R.drawable.newico,snapshot.child("newsRoom").child("1newChannelUrl").getValue().toString());
                createCategoryForWebsite(snapshot.child("newsRoom").child("2newChannelName").getValue().toString(),R.drawable.newico,snapshot.child("newsRoom").child("2newChannel1Url").getValue().toString());
                createCategoryForWebsite(snapshot.child("newsRoom").child("3newChannelName").getValue().toString(),R.drawable.newico,snapshot.child("newsRoom").child("3newChannel1Url").getValue().toString());
                createCategoryForWebsite(snapshot.child("newsRoom").child("4newChannelName").getValue().toString(),R.drawable.newico,snapshot.child("newsRoom").child("4newChannel1Url").getValue().toString());
                createCategoryForWebsite(snapshot.child("newsRoom").child("5newChannelName").getValue().toString(),R.drawable.newico,snapshot.child("newsRoom").child("5newChannel1Url").getValue().toString());
                rateUsOnGooglePlay();
                MyAdapter adapter = new MyAdapter();
                mainGrid.setExpanded(true);
                mainGrid.setAdapter(adapter);
                adapter.notifyDataSetChanged();
                dialog.dismiss();            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                dialog.dismiss();
                Toast.makeText(Home.this, "Please Connect The Internet", Toast.LENGTH_SHORT).show();

            }
        });
    }


    public  void createCategoryForWebsite(String category_name,Integer drawable, String url){
        rootArrayList.add(videoArrayList);
        hashMap = new HashMap<>();
        hashMap.put("category_name", category_name);
        hashMap.put("img", String.valueOf(drawable));
        hashMap.put("url", url);
        DogArrayList.add(hashMap);
        videoArrayList = new ArrayList<>();
    }

    ArrayList< ArrayList<HashMap<String,String>> > rootArrayList;
    ArrayList< HashMap<String, String> > DogArrayList;
    ArrayList< HashMap<String, String> > videoArrayList;
    HashMap<String, String> hashMap;
    public  void addVideoItem(String video_id, String title, String desciption){
        hashMap = new HashMap<>();
        hashMap.put("vdo_id",video_id );
        hashMap.put("vdo_title", title);
        hashMap.put("vdo_desciption", desciption);
        videoArrayList.add(hashMap);

    }
    public  void createPlayListForVideo(String category_name, Integer drawable){
        rootArrayList.add(videoArrayList);
        hashMap = new HashMap<>();
        hashMap.put("category_name", category_name);
        hashMap.put("img", String.valueOf(drawable));
        DogArrayList.add(hashMap);
        videoArrayList = new ArrayList<>();
    }
    public static boolean isNetworkAvailable(Context context) {
        return ((ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo() != null;
    }

    private class MyAdapter extends BaseAdapter {
        private LayoutInflater inflater;

        public  MyAdapter(){
            this.inflater = (LayoutInflater) Home.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return DogArrayList.size();
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
            convertView = inflater.inflate(R.layout.grid_item, parent, false);
            ImageView imgIcon = convertView.findViewById(R.id.imgIcon);
            TextView tvTitle = convertView.findViewById(R.id.tvTitle);
            LinearLayout layItem = convertView.findViewById(R.id.layItem);
            HashMap<String, String> mHashMap = DogArrayList.get(position);
            String catName = mHashMap.get("category_name");
            String img = mHashMap.get("img");
            String url = mHashMap.get("url");
            if (tvTitle!=null) tvTitle.setText(catName);
            if (imgIcon!=null && img!=null) {
                int drawable = Integer.parseInt(img);
                imgIcon.setImageResource( drawable );
            }

            Animation animation = AnimationUtils.loadAnimation(Home.this, R.anim.anim_grid);
            animation.setStartOffset(position*300);
            convertView.startAnimation(animation);
            if (layItem!=null){
                layItem.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (url!=null && url.length()>5){
                                WebBrowser.WEBSITE_LINK = url;
                                WebBrowser.WEBSITE_TITLE = catName;
                                startActivity(new Intent(Home.this, WebBrowser.class));
                            databaseReference.child("youtubeVideo").addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {

                                    String AdsNetWork=snapshot.child("AdRemote").getValue().toString();
                                    if (AdsNetWork.contains("admob")){
                                        if (mInterstitialAd!=null){
                                            mInterstitialAd.show(Home.this);
                                        }
                                    }else if (AdsNetWork.contains("Start.io")){

                                    }else if (AdsNetWork.contains("Facebook")){
                                        if (facebookinterstitialAd!=null&&facebookinterstitialAd.isAdLoaded()){
                                            facebookinterstitialAd.show();
                                        }

                                    }



                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        }
                         else{
                            CategoryClicked = position;
                                MainActivity.arrayList =rootArrayList.get(position);
                                startActivity(new Intent(Home.this, MainActivity.class));
                            databaseReference.child("youtubeVideo").addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    String AdsNetWork=snapshot.child("AdRemote").getValue().toString();
                                    if (AdsNetWork.contains("admob")){
                                        if (mInterstitialAd!=null){
                                            mInterstitialAd.show(Home.this);
                                        }
                                    }else if (AdsNetWork.contains("Start.io")){

                                    }else if (AdsNetWork.contains("Facebook")){
                                        if (facebookinterstitialAd!=null&&facebookinterstitialAd.isAdLoaded()){
                                            facebookinterstitialAd.show();
                                        }}}
                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        }
                         }});
            }return convertView;
        }
    }
     int BANNER_AD_CLICK_COUNT =0;
      private void loadBannerAd(){
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        mAdView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                // Code to be executed when an ad finishes loading.
                if (BANNER_AD_CLICK_COUNT >=3){
                    if(mAdView!=null) mAdView.setVisibility(View.GONE);
                }else{
                    if(mAdView!=null) mAdView.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onAdFailedToLoad(LoadAdError adError) {
                // Code to be executed when an ad request fails.
            }

            @Override
            public void onAdOpened() {
                // Code to be executed when an ad opens an overlay that
                // covers the screen.
            }

            @Override
            public void onAdClicked() {
                // Code to be executed when the user clicks on an ad.
                BANNER_AD_CLICK_COUNT++;

                if (BANNER_AD_CLICK_COUNT >=3){
                    if(mAdView!=null) mAdView.setVisibility(View.GONE);
                }

            }

            @Override
            public void onAdClosed() {
                // Code to be executed when the user is about to return
                // to the app after tapping on an ad.
            }
        });

    }
    //>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>

    //>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
    //>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>

    //>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
    //>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>

    // loadFullscreenAd method starts here.....
    InterstitialAd mInterstitialAd;

    private void loadFullscreenAd(){

        //Requesting for a fullscreen Ad
        AdRequest adRequest = new AdRequest.Builder().build();
        InterstitialAd.load(this,getString(R.string.admob_INTERSTITIAL_UNIT_ID), adRequest, new InterstitialAdLoadCallback() {
            @Override
            public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                // The mInterstitialAd reference will be null until
                // an ad is loaded.
                mInterstitialAd = interstitialAd;

                //Fullscreen callback || Requesting again when an ad is shown already
                mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback(){
                    @Override
                    public void onAdDismissedFullScreenContent() {
                        // Called when fullscreen content is dismissed.
                        //User dismissed the previous ad. So we are requesting a new ad here
                        loadFullscreenAd();

                        if (CategoryClicked>=0){
                            MainActivity.arrayList =rootArrayList.get(CategoryClicked);
                            startActivity(new Intent(Home.this, MainActivity.class));
                        }

                    }

                }); // FullScreen Callback Ends here


            }
            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                // Handle the error
                mInterstitialAd = null;
            }

        });

    }


    // loadFullscreenAd method ENDS  here..... >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>







    //>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
    //>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>

    private void rateUsOnGooglePlay(){
        rLayRateUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                } catch (android.content.ActivityNotFoundException anfe) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                }

            }
        });
    }

    //>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
    //>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>



    ///====================================================
    private static final int TIME_INTERVAL = 2000; // # milliseconds, desired
    private long mBackPressed;

    // When user click bakpress button this method is called
    @Override
    public void onBackPressed() {
        // When user press back button

            if (mBackPressed + TIME_INTERVAL > System.currentTimeMillis()) {
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_HOME);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);

            } else {

                Toast.makeText(getBaseContext(), "Press again to exit",
                        Toast.LENGTH_SHORT).show();
            }
            OnDestroy();

            mBackPressed = System.currentTimeMillis();



    } // end of onBackpressed method

    //#############################################################################################







}