package learncodeonline.in.mymall.address;

import android.app.Dialog;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import learncodeonline.in.mymall.DBqueries;
import learncodeonline.in.mymall.R;

import static android.content.Context.POWER_SERVICE;
import static learncodeonline.in.mymall.address.DeliveryActivity.SELECT_ADDRESS;
import static learncodeonline.in.mymall.address.MyAccountFragment.MANAGE_ADDRESS;
import static learncodeonline.in.mymall.address.MyAddressActivity.refreshItem;

public class AddressesAdapter extends RecyclerView.Adapter<AddressesAdapter.Viewholder> {

    private List<AdressesModel> adressesModelList;
    private int MODE;
    private int preSelectedPosition;
    private boolean refresh = false;
    private Dialog loadingDialog;

    public AddressesAdapter(List<AdressesModel> adressesModelList, int MODE, Dialog loadingDialog) {

        this.adressesModelList = adressesModelList;
        this.MODE = MODE;
        preSelectedPosition = DBqueries.selectedAddress;
        this.loadingDialog = loadingDialog;

    }

    @NonNull
    @Override
    public Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.addresses_item_layout,parent,false);
        return new Viewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Viewholder holder, int position) {
        String name = adressesModelList.get(position).getName();
        String mobileNo = adressesModelList.get(position).getMobileNo();
        String alternateMobileNo = adressesModelList.get(position).getAlternateMobileNo();
        String  flatNo = adressesModelList.get(position).getFlatNo();
        String  locality = adressesModelList.get(position).getLocality();
        String  landmark = adressesModelList.get(position).getLandmark();
        String  city = adressesModelList.get(position).getCity();
        String  state = adressesModelList.get(position).getState();
        String  pincode = adressesModelList.get(position).getPincode();
        boolean selected = adressesModelList.get(position).getSelected();

        holder.setdata(name, city, pincode,selected, position, mobileNo, alternateMobileNo, flatNo, locality, state, landmark);

    }

    @Override
    public int getItemCount() {
        return adressesModelList.size();
    }

    public class Viewholder extends RecyclerView.ViewHolder{

        private TextView fullname;
        private TextView address;
        private TextView pincode;
        private ImageView icon;
        private LinearLayout optioncontainer;

        public Viewholder(@NonNull View itemView) {
            super(itemView);

            fullname = itemView.findViewById(R.id.name);
            address = itemView.findViewById(R.id.address);
            pincode = itemView.findViewById(R.id.pin_code);
            icon = itemView.findViewById(R.id.icon_view);
            optioncontainer = itemView.findViewById(R.id.option_container);
        }

        private void setdata(String username, String city, String userpincode, Boolean selected, final int position,
                             String mobileNo,String alternateMobileNo, String  flatNo, String  locality,
                             String state, String landmark){

            if(alternateMobileNo.equals("")) {
                fullname.setText(username + "-" + mobileNo);
            }
            else {
                fullname.setText(username + "-" + mobileNo + " or " + alternateMobileNo);
            }


            if(landmark.equals("")) {
               address.setText(flatNo + " " + locality+ " " +city+ " " +state);
            }
            else {
                address.setText(flatNo + " " + locality+ " " + landmark+ " " +city+ " " +state);
            }


            pincode.setText(userpincode);


            if(MODE == SELECT_ADDRESS){
                    icon.setImageResource(R.drawable.check);
                    if(selected){
                        icon.setVisibility(View.VISIBLE);
                        preSelectedPosition = position;
                    }
                    else{
                        icon.setVisibility(View.GONE);
                    }
                    itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(preSelectedPosition!=position){
                            adressesModelList.get(position).setSelected(true);
                            adressesModelList.get(preSelectedPosition).setSelected(false);
                            refreshItem(preSelectedPosition, position);
                            preSelectedPosition = position;
                            DBqueries.selectedAddress = position;
                        }
                        }
                    });
               }else if(MODE == MANAGE_ADDRESS){
                    optioncontainer.setVisibility(View.GONE);
                    optioncontainer.getChildAt(0).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {//edit address
                            Intent addressIntent = new Intent(itemView.getContext(),AddAddressActivity.class);
                            addressIntent.putExtra("INTENT","update_address");
                            addressIntent.putExtra("index",position);
                            itemView.getContext().startActivity(addressIntent);
                            refresh = false;
                        }
                    });

                optioncontainer.getChildAt(1).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {//remove address

                        loadingDialog.show();

                        Map<String, Object> addresses = new HashMap<>();
                        int x = 0;
                        int selected = -1;
                        for(int i=0; i<adressesModelList.size(); i++) {
                            if(i!=position) {
                                                x++;
                                                addresses.put("city_"+x, adressesModelList.get(i).getCity());
                                                addresses.put("locality_"+x, adressesModelList.get(i).getLocality());
                                                addresses.put("flat_no_"+x,adressesModelList.get(i).getFlatNo());
                                                addresses.put("pincode_"+x,adressesModelList.get(i).getPincode());
                                                addresses.put("landmark_"+x,adressesModelList.get(i).getPincode());
                                                addresses.put("name_"+x, adressesModelList.get(i).getName());
                                                addresses.put("mobile_no_"+x,adressesModelList.get(i).getMobileNo());
                                                addresses.put("alternate_mobile_no_"+x,adressesModelList.get(i).getAlternateMobileNo());
                                                addresses.put("state_"+x,adressesModelList.get(i).getState());

                                                if(adressesModelList.get(position).getSelected()) {
                                                    if(position-1>=0) {
                                                        if(x == position) {
                                                            addresses.put("selected_"+x, true);
                                                            selected = x;
                                                        }
                                                        else {
                                                            addresses.put("selected_"+x, adressesModelList.get(position).getSelected());
                                                        }
                                                    } else {
                                                            if(x==1) {
                                                                addresses.put("selected_"+x, true);
                                                                selected = x;
                                                            } else {
                                                                addresses.put("selected_"+x, adressesModelList.get(position).getSelected());
                                                            }
                                                    }
                                                }
                                                else {
                                                    addresses.put("selected_"+x, adressesModelList.get(position).getSelected());
                                                    if(adressesModelList.get(position).getSelected()) {
                                                        selected = x;
                                                    }
                                                }


                            }
                        }

                        addresses.put("list_size", x);
                        final int finalSelected = x;
                        FirebaseFirestore.getInstance().collection("USERS").document(FirebaseAuth.getInstance().getUid()).collection("USER_DATA").document("MY_ADDRESSES")
                                .set(addresses).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()) {
                                    DBqueries.adressesModelList.remove(position);
                                    if(finalSelected!=-1) {
                                        DBqueries.selectedAddress = finalSelected-1;
                                        DBqueries.adressesModelList.get(finalSelected-1).setSelected(true);
                                    } else if(DBqueries.adressesModelList.size()==0) {
                                        DBqueries.selectedAddress = -1;
                                    }
                                    notifyDataSetChanged();
                                }
                                else {
                                    String error = task.getException().getMessage();
                                    Toast.makeText(itemView.getContext(), error, Toast.LENGTH_SHORT).show();
                                }
                                loadingDialog.dismiss();
                            }
                        });

                        refresh = false;
                    }
                });

                    icon.setImageResource(R.drawable.vertical_dot);
                    icon.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            optioncontainer.setVisibility(View.VISIBLE);
                            if(refresh) {
                                refreshItem(preSelectedPosition,preSelectedPosition);
                            }
                            else {
                                refresh = true;
                            }

                            preSelectedPosition = position;
                        }
                    });
                    itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            refreshItem(preSelectedPosition,preSelectedPosition);
                            preSelectedPosition = -1;
                        }
                    });
               }
        }
    }
}
