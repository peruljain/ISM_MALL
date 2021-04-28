package learncodeonline.in.mymall.address;


import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;

import de.hdodenhof.circleimageview.CircleImageView;
import learncodeonline.in.mymall.DBqueries;
import learncodeonline.in.mymall.R;
import learncodeonline.in.mymall.authentication.RegisterActivity;
import learncodeonline.in.mymall.order.MyOrderItemModel;


/**
 * A simple {@link Fragment} subclass.
 */
public class MyAccountFragment extends Fragment {


    public MyAccountFragment() {
        // Required empty public constructor
    }

    private FloatingActionButton settings_btn;
    private Button viewAllAddressBtn, signOutBtn;
    public static final int MANAGE_ADDRESS = 1;
    private CircleImageView profileView, currentOrderImage;
    private TextView name,email,tvCurrentOrderStatus;
    private LinearLayout layoutContainer;
    private Dialog loadingDialog;
    private ImageView orderIndicator, packedIndicator,shippedIndicator, deliverIndicator;
    private ProgressBar O_P_progress, P_S_progress, S_D_progress;
    private TextView yourRecentOrdersTitle;
    private LinearLayout recentOrdersContainer;
    private TextView addressName,address, pinCode;





    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_my_account, container, false);

        /////// loading dialog
        loadingDialog = new Dialog(getContext());
        loadingDialog.setContentView(R.layout.loading_progress_dialog);
        loadingDialog.setCancelable(false);
        loadingDialog.getWindow().setBackgroundDrawable(getContext().getDrawable(R.drawable.slider_background));
        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        loadingDialog.show();
        /////// loading dialog

        profileView = view.findViewById(R.id.profile_images);
        name = view.findViewById(R.id.user_name);
        email = view.findViewById(R.id.user_email);
        layoutContainer = view.findViewById(R.id.layout_container);
        currentOrderImage = view.findViewById(R.id.current_order_image);
        tvCurrentOrderStatus = view.findViewById(R.id.tv_current_order_status);
        orderIndicator = view.findViewById(R.id.ordered_indicator);
        packedIndicator = view.findViewById(R.id.packed_indicator);
        shippedIndicator = view.findViewById(R.id.shipped_indicator);
        deliverIndicator = view.findViewById(R.id.delivered_indicator);
        O_P_progress = view.findViewById(R.id.order_packed_progress);
        P_S_progress = view.findViewById(R.id.packed_shipped_progress);
        S_D_progress = view.findViewById(R.id.shipped_delivered_progress);
        recentOrdersContainer = view.findViewById(R.id.recent_orders_container);
        yourRecentOrdersTitle = view.findViewById(R.id.your_recent_orders_title);
        address = view.findViewById(R.id.address);
        addressName = view.findViewById(R.id.address_full_name);
        pinCode = view.findViewById(R.id.address_pincode);
        signOutBtn = view.findViewById(R.id.sign_out_btn);
        settings_btn = view.findViewById(R.id.setting_btn);


        name.setText(DBqueries.fullname);
        email.setText(DBqueries.email);

         if(!DBqueries.profile.equals("")) {
             Glide.with(getContext()).load(DBqueries.profile).apply(new RequestOptions().placeholder(R.mipmap.account)).into(profileView);
         }

        layoutContainer.getChildAt(1).setVisibility(View.GONE);
          loadingDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
              @Override
              public void onDismiss(DialogInterface dialog) {
                  for(MyOrderItemModel myOrderItemModel : DBqueries.myOrderItemModelList) {
                      if(!myOrderItemModel.isCancellationRequested()) {
                          if(!myOrderItemModel.getOrderStatus().equals("Delivered") && !myOrderItemModel.getOrderStatus().equals("Cancelled")) {
                              layoutContainer.getChildAt(1).setVisibility(View.VISIBLE);
                              Glide.with(getContext()).load(myOrderItemModel.getProductImage()).apply(new RequestOptions().placeholder(R.mipmap.ic_launcher)).into(profileView);
                              tvCurrentOrderStatus.setText(myOrderItemModel.getOrderStatus());
                              switch (myOrderItemModel.getOrderStatus()) {
                                  case "Ordered":
                                      orderIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.successGreen)));
                                      break;

                                  case "Packed":
                                      orderIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.successGreen)));
                                      packedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.successGreen)));
                                      O_P_progress.setProgress(100);
                                      break;

                                  case "Shipped":
                                      orderIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.successGreen)));
                                      packedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.successGreen)));
                                      shippedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.successGreen)));
                                      O_P_progress.setProgress(100);
                                      P_S_progress.setProgress(100);
                                      break;

                                  case "Out for Delivery":
                                      orderIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.successGreen)));
                                      packedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.successGreen)));
                                      shippedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.successGreen)));
                                      deliverIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.successGreen)));
                                      O_P_progress.setProgress(100);
                                      P_S_progress.setProgress(100);
                                      S_D_progress.setProgress(100);
                                      break;

                                  case "Cancelled":
                                      break;
                              }


                          }
                      }
                  }

                  int i = 0;
                  for(MyOrderItemModel myOrderItemModels : DBqueries.myOrderItemModelList) {
                      if(i<4) {
                          if(myOrderItemModels.getOrderStatus().equals("Delivered")) {
                              Glide.with(getContext()).load(myOrderItemModels.getProductImage()).apply(new RequestOptions().placeholder(R.mipmap.account)).into((CircleImageView)recentOrdersContainer.getChildAt(i));
                              i++;
                          }
                      }
                      else {
                          break;
                      }

                  }

                  if(i<3) {
                      for(int x = i; x<4; x++) {
                          recentOrdersContainer.getChildAt(x).setVisibility(View.GONE);
                      }
                  }
                  loadingDialog.show();
                  loadingDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                      @Override
                      public void onDismiss(DialogInterface dialog) {
                          loadingDialog.setOnDismissListener(null);
                          if(DBqueries.adressesModelList.size()==0) {
                              addressName.setText("No Address");
                              address.setText("-");
                              pinCode.setText("-");
                          }
                          else {
                              setAddress();
                          }
                      }
                  });

                  DBqueries.loadAddresses(getContext(), loadingDialog, false);


              }
          });
          DBqueries.loadOrders(getContext(), null, loadingDialog);

        viewAllAddressBtn = view.findViewById(R.id.view_all_address_btn);
        viewAllAddressBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myaddressesIntent = new Intent(getContext(), MyAddressActivity.class);
                myaddressesIntent.putExtra("MODE",MANAGE_ADDRESS);
                startActivity(myaddressesIntent);
            }
        });

        signOutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                DBqueries.clearData();
                Intent registerIntent = new Intent(getContext(), RegisterActivity.class);
                startActivity(registerIntent);
                getActivity().finish();
            }
        });

        settings_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent updateUserInfo = new Intent(getContext(), UpdateUserInfoActivity.class);
                updateUserInfo.putExtra("Name", name.getText());
                updateUserInfo.putExtra("Email", email.getText());
                updateUserInfo.putExtra("Photo", DBqueries.profile);
                startActivity(updateUserInfo);
            }
        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        name.setText(DBqueries.fullname);
        email.setText(DBqueries.email);

        if(!DBqueries.profile.equals("")) {
            Glide.with(getContext()).load(DBqueries.profile).apply(new RequestOptions().placeholder(R.mipmap.account)).into(profileView);
        } else {
            profileView.setImageResource(R.mipmap.account);
        }

        if(!loadingDialog.isShowing()) {
            if(DBqueries.adressesModelList.size()==0) {
                addressName.setText("No Address");
                address.setText("-");
                pinCode.setText("-");
            }
            else {
                setAddress();
            }
        }
    }

    private void setAddress() {
        String name = DBqueries.adressesModelList.get(DBqueries.selectedAddress).getName();
        String mobileNo = DBqueries.adressesModelList.get(DBqueries.selectedAddress).getMobileNo();
        if(DBqueries.adressesModelList.get(DBqueries.selectedAddress).getAlternateMobileNo().equals("")) {
            addressName.setText(name + "-" + mobileNo);
        }
        else {
            addressName.setText(name + "-" + mobileNo + " or " + DBqueries.adressesModelList.get(DBqueries.selectedAddress).getAlternateMobileNo());
        }

        String  flatNo = DBqueries.adressesModelList.get(DBqueries.selectedAddress).getFlatNo();
        String  locality = DBqueries.adressesModelList.get(DBqueries.selectedAddress).getLocality();
        String  landmark = DBqueries.adressesModelList.get(DBqueries.selectedAddress).getLandmark();
        String  city = DBqueries.adressesModelList.get(DBqueries.selectedAddress).getCity();
        String  state = DBqueries.adressesModelList.get(DBqueries.selectedAddress).getState();

        if(landmark.equals("")) {
            address.setText(flatNo + " " + locality+ " " +city+ " " +state);
        }
        else {
            address.setText(flatNo + " " + locality+ " " + landmark+ " " +city+ " " +state);
        }


        pinCode.setText(DBqueries.adressesModelList.get(DBqueries.selectedAddress).getPincode());

    }

}
