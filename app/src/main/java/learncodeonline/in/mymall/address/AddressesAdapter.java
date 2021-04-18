package learncodeonline.in.mymall.address;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import learncodeonline.in.mymall.DBqueries;
import learncodeonline.in.mymall.R;

import static learncodeonline.in.mymall.address.DeliveryActivity.SELECT_ADDRESS;
import static learncodeonline.in.mymall.address.MyAccountFragment.MANAGE_ADDRESS;
import static learncodeonline.in.mymall.address.MyAddressActivity.refreshItem;

public class AddressesAdapter extends RecyclerView.Adapter<AddressesAdapter.Viewholder> {

    private List<AdressesModel> adressesModelList;
    private int MODE;
    private int preSelectedPosition;
    private boolean refresh = false;

    public AddressesAdapter(List<AdressesModel> adressesModelList, int MODE) {
        this.adressesModelList = adressesModelList;
        this.MODE = MODE;
        preSelectedPosition = DBqueries.selectedAddress;
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
                        }
                    });

                optioncontainer.getChildAt(1).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {//remove address

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
