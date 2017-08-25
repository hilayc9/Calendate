package com.calendate.calendate;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;


/**
 * A simple {@link Fragment} subclass.
 */
public class ButtonsFragment extends Fragment {

    ViewPager viewPager;
    PlaceholderFragment.SectionsPagerAdapter mSectionsPagerAdapter;
    FirebaseDatabase mDatabase;
    FirebaseAuth mAuth;
    FirebaseUser user;
    int fragNum = 0;
    String imageRef = "";
    Bitmap image;

    public ButtonsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.buttons_container_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mDatabase = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();

        viewPager = (ViewPager) view.findViewById(R.id.viewPager);
        mSectionsPagerAdapter = new PlaceholderFragment.SectionsPagerAdapter(getChildFragmentManager());
        if (mAuth.getCurrentUser() != null)
            viewPager.setAdapter(mSectionsPagerAdapter);
        int fragNumToGo = getArguments().getInt("fragNum", 0);
        if (getArguments().getParcelable("image") != null){
            image = getArguments().getParcelable("image");
        }
        viewPager.setCurrentItem(fragNumToGo);

        if (user != null) {
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("all_events").child(user.getUid());
            ref.keepSynced(true);
        }
    }

    public static class PlaceholderFragment extends Fragment implements View.OnClickListener, View.OnLongClickListener {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";
        private static final String IVTOPLEFT = "topLeft";
        private static final String IVTOPRIGHT = "topRight";
        private static final String IVMIDDLELEFT = "middleLeft";
        private static final String IVMIDDLERIGHT = "middleRight";
        private static final String IVBOTTOMLEFT = "bottomLeft";
        private static final String IVBOTTOMRIGHT = "bottomRight";

        FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        int fragNum = 0;
        BootstrapButton btnTopLeft;
        BootstrapButton btnTopRight;
        BootstrapButton btnMiddleLeft;
        BootstrapButton btnMiddleRight;
        BootstrapButton btnBottomLeft;
        BootstrapButton btnBottomRight;
        String btnRef = "";
        String btnId = "";
        ImageView ivTopLeft;
        ImageView ivTopRight;
        ImageView ivMiddleLeft;
        ImageView ivMiddleRight;
        ImageView ivBottomLeft;
        ImageView ivBottomRight;
        SharedPreferences prefs;
        Bitmap image;

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_buttons, container, false);
            fragNum = getArguments().getInt(ARG_SECTION_NUMBER);
            btnTopLeft = (BootstrapButton) rootView.findViewById(R.id.btnTopLeft);
            btnTopRight = (BootstrapButton) rootView.findViewById(R.id.btnTopRight);
            btnMiddleLeft = (BootstrapButton) rootView.findViewById(R.id.btnMiddleLeft);
            btnMiddleRight = (BootstrapButton) rootView.findViewById(R.id.btnMiddleRight);
            btnBottomLeft = (BootstrapButton) rootView.findViewById(R.id.btnBottomLeft);
            btnBottomRight = (BootstrapButton) rootView.findViewById(R.id.btnBottomRight);

            ivTopLeft = (ImageView) rootView.findViewById(R.id.ivTopLeft);
            ivTopRight = (ImageView) rootView.findViewById(R.id.ivTopRight);
            ivMiddleLeft = (ImageView) rootView.findViewById(R.id.ivMiddleLeft);
            ivMiddleRight = (ImageView) rootView.findViewById(R.id.ivMiddleRight);
            ivBottomLeft = (ImageView) rootView.findViewById(R.id.ivBottomLeft);
            ivBottomRight = (ImageView) rootView.findViewById(R.id.ivBottomRight);

            btnTopLeft.setBootstrapBrand(new CustomBootstrapStyleTransparent(rootView.getContext()));
            btnTopRight.setBootstrapBrand(new CustomBootstrapStyleDark(rootView.getContext()));
            btnMiddleLeft.setBootstrapBrand(new CustomBootstrapStyleDark(rootView.getContext()));
            btnMiddleRight.setBootstrapBrand(new CustomBootstrapStyleTransparent(rootView.getContext()));
            btnBottomLeft.setBootstrapBrand(new CustomBootstrapStyleTransparent(rootView.getContext()));
            btnBottomRight.setBootstrapBrand(new CustomBootstrapStyleDark(rootView.getContext()));

            prefs = getContext().getSharedPreferences("icons", Context.MODE_PRIVATE);

//            showProgress(true, getString(R.string.loading));

            mDatabase.getReference("buttons/" + user.getUid() + "/" + "topLeft" + fragNum)
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            btnTopLeft.setText(dataSnapshot.getValue(String.class));
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }
                    });
            mDatabase.getReference("buttons/" + user.getUid() + "/" + "topRight" + fragNum)
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            btnTopRight.setText(dataSnapshot.getValue(String.class));
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
            mDatabase.getReference("buttons/" + user.getUid() + "/" + "middleLeft" + fragNum)
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            btnMiddleLeft.setText(dataSnapshot.getValue(String.class));
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
            mDatabase.getReference("buttons/" + user.getUid() + "/" + "middleRight" + fragNum)
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            btnMiddleRight.setText(dataSnapshot.getValue(String.class));
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
            mDatabase.getReference("buttons/" + user.getUid() + "/" + "bottomLeft" + fragNum)
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            btnBottomLeft.setText(dataSnapshot.getValue(String.class));
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
            mDatabase.getReference("buttons/" + user.getUid() + "/" + "bottomRight" + fragNum)
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            btnBottomRight.setText(dataSnapshot.getValue(String.class));
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

            String topLeftImage = prefs.getString(IVTOPLEFT + fragNum, "default");
            if (topLeftImage.equals("default")) {
                ivTopLeft.setImageResource(R.drawable.default_icon);
            } else {
                byte[] b = Base64.decode(topLeftImage, Base64.DEFAULT);
                Bitmap bitmap = BitmapFactory.decodeByteArray(b, 0, b.length);
                ivTopLeft.setImageBitmap(bitmap);
            }
/*
            mDatabase.getReference("button_images/" + user.getUid() + "/" + "topRight" + fragNum)
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            String ref = dataSnapshot.getValue(String.class);
                            StorageReference mStorage;
                            if (ref == null) {
                                return;
                            } else {
                                mStorage = FirebaseStorage.getInstance().getReference(ref);

                            }
                            mStorage.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {
                                    if (task.isSuccessful()) {
                                        Glide.with(getContext()).load(task.getResult()).into(ivTopRight);
                                    }
                                }
                            });
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
            mDatabase.getReference("button_images/" + user.getUid() + "/" + "middleLeft" + fragNum)
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            String ref = dataSnapshot.getValue(String.class);
                            StorageReference mStorage;
                            if (ref == null) {
                                mStorage = FirebaseStorage.getInstance().getReference("button-icons/Default.png");
                            } else {
                                mStorage = FirebaseStorage.getInstance().getReference(ref);

                            }
                            mStorage.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {
                                    if (task.isSuccessful()) {
                                        Glide.with(getContext()).load(task.getResult()).into(ivMiddleLeft);
                                    }
                                }
                            });
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
            mDatabase.getReference("button_images/" + user.getUid() + "/" + "middleRight" + fragNum)
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            String ref = dataSnapshot.getValue(String.class);
                            StorageReference mStorage;
                            if (ref == null) {
                                mStorage = FirebaseStorage.getInstance().getReference("button-icons/Default.png");
                            } else {
                                mStorage = FirebaseStorage.getInstance().getReference(ref);
                            }
                            mStorage.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {
                                    if (task.isSuccessful()) {
                                        Glide.with(getContext()).load(task.getResult()).into(ivMiddleRight);
                                    }
                                }
                            });
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
            mDatabase.getReference("button_images/" + user.getUid() + "/" + "bottomLeft" + fragNum)
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            String ref = dataSnapshot.getValue(String.class);
                            StorageReference mStorage;
                            if (ref == null) {
                                mStorage = FirebaseStorage.getInstance().getReference("button-icons/Default.png");
                            } else {
                                mStorage = FirebaseStorage.getInstance().getReference(ref);

                            }
                            mStorage.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {
                                    if (task.isSuccessful()) {
                                        Glide.with(getContext()).load(task.getResult()).into(ivBottomLeft);
                                    }
                                }
                            });
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
            mDatabase.getReference("button_images/" + user.getUid() + "/" + "bottomRight" + fragNum)
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            String ref = dataSnapshot.getValue(String.class);
                            StorageReference mStorage;
                            if (ref == null) {
                                mStorage = FirebaseStorage.getInstance().getReference("button-icons/Default.png");
                            } else {
                                mStorage = FirebaseStorage.getInstance().getReference(ref);
                            }
                            mStorage.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {
                                    if (task.isSuccessful()) {
                                        Glide.with(getContext()).load(task.getResult()).into(ivBottomRight);
                                        showProgress(false, "");
                                    }
                                }
                            });
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
*/
            btnTopLeft.setOnClickListener(this);
            btnTopRight.setOnClickListener(this);
            btnMiddleLeft.setOnClickListener(this);
            btnMiddleRight.setOnClickListener(this);
            btnBottomLeft.setOnClickListener(this);
            btnBottomRight.setOnClickListener(this);

            btnTopLeft.setOnLongClickListener(this);
            btnTopRight.setOnLongClickListener(this);
            btnMiddleLeft.setOnLongClickListener(this);
            btnMiddleRight.setOnLongClickListener(this);
            btnBottomLeft.setOnLongClickListener(this);
            btnBottomRight.setOnLongClickListener(this);

            return rootView;
        }

        @Override
        public void onClick(View v) {
            int id = v.getId();
            switch (id) {
                case R.id.btnTopLeft:
                    btnRef = "topLeft";
                    break;
                case R.id.btnTopRight:
                    btnRef = "topRight";
                    break;
                case R.id.btnMiddleLeft:
                    btnRef = "middleLeft";
                    break;
                case R.id.btnMiddleRight:
                    btnRef = "middleRight";
                    break;
                case R.id.btnBottomLeft:
                    btnRef = "bottomLeft";
                    break;
                case R.id.btnBottomRight:
                    btnRef = "bottomRight";
                    break;
            }
            onButtonPressed(btnRef, fragNum);
        }

        @Override
        public boolean onLongClick(View v) {
            int id = v.getId();
            switch (id) {
                case R.id.btnTopLeft:
                    btnRef = "topLeft";
                    break;
                case R.id.btnTopRight:
                    btnRef = "topRight";
                    break;
                case R.id.btnMiddleLeft:
                    btnRef = "middleLeft";
                    break;
                case R.id.btnMiddleRight:
                    btnRef = "middleRight";
                    break;
                case R.id.btnBottomLeft:
                    btnRef = "bottomLeft";
                    break;
                case R.id.btnBottomRight:
                    btnRef = "bottomRight";
                    break;
            }
            showButtonOptionsDialog(v, btnRef, fragNum);
            return false;
        }

        public void showButtonOptionsDialog(final View v, final String btnRef, final int fragNum) {
            AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
            builder.setTitle(R.string.change_button_dialog_title);
            builder.setItems(R.array.buttonOptions, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which) {
                        case 0:
                            //Change title
                            btnId = btnRef + fragNum;
                            SetButtonTitleDialog f = new SetButtonTitleDialog();
                            Bundle args = new Bundle();
                            args.putString("btnId", btnId);
                            f.setArguments(args);
                            f.show(getFragmentManager(), "setButtonTitleDialog");
                            break;
                        case 1:
                            //Change image
                            StorageReference mStorage = FirebaseStorage.getInstance().getReference("button-icons/Approval.png");
//                            Glide.with(getContext()).using(new FirebaseImageLoader()).load(mStorage).into(ivTopLeft);

                            btnId = btnRef + fragNum;
                            PickImageFragment d = new PickImageFragment();
                            Bundle arg = new Bundle();
                            arg.putString("btnId", btnId);
                            d.setArguments(arg);
                            d.show(getChildFragmentManager(), "f");
                            break;
                    }
                }
            });
            builder.show();
        }

        public static PlaceholderFragment newInstance(int fragNum, Bitmap image) {

            Bundle args = new Bundle();

            PlaceholderFragment fragment = new PlaceholderFragment();
            fragment.setArguments(args);
            return fragment;
        }

        public void setButtonText(String text, String btnId) {
            mDatabase.getReference("buttons/" + user.getUid() + "/" + btnId).setValue(text);
        }

        public void setButtonImage(StorageReference mStorage, String btnId, Bitmap image) {
            mDatabase.getReference("button_images/" + user.getUid() + "/" + btnId).setValue(mStorage.getPath().replaceFirst("/", ""));
//            ivTopLeft.setImageBitmap(image);
        }

        public void onButtonPressed(String btnRef, int fragNum) {
            String btnId = btnRef + fragNum;
            Intent intent = new Intent(getContext(), DetailActivity.class);
            intent.putExtra("btnId", btnId);
            startActivity(intent);
        }

        private ProgressDialog dialog;

        private void showProgress(boolean show, String msg) {
            if (dialog == null) {
                dialog = new ProgressDialog(getContext());
                dialog.setMessage(msg);
                dialog.setCancelable(false);
                dialog.setCanceledOnTouchOutside(false);
            }
            if (show)
                dialog.show();
            else
                dialog.dismiss();
        }


        public static class SectionsPagerAdapter extends FragmentPagerAdapter {

            public SectionsPagerAdapter(FragmentManager fm) {
                super(fm);
            }

            @Override
            public Fragment getItem(int position) {
                // getItem is called to instantiate the fragment for the given page.
                // Return a PlaceholderFragment (defined as a static inner class below).
                return PlaceholderFragment.newInstance(position + 1);
            }

            @Override
            public int getCount() {
                // Show 2 total pages.
                return 2;
            }

            @Override
            public CharSequence getPageTitle(int position) {
                switch (position) {
                    case 0:
                        return "1";
                    case 1:
                        return "2";
                }
                return null;
            }
        }
    }

}
