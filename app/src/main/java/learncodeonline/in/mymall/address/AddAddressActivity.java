package learncodeonline.in.mymall.address;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import learncodeonline.in.mymall.DBqueries;
import learncodeonline.in.mymall.R;

public class AddAddressActivity extends AppCompatActivity {

    private EditText city;
    private EditText locality;
    private EditText flatNo;
    private EditText pincode;
    private EditText landmark;
    private EditText name;
    private EditText mobileNo;
    private EditText alternateMobileNo;
    private Spinner stateSpinner;
    private Button saveBtn;
    private Dialog loadingDialog;

    private boolean updateAddress = false;
    private  AdressesModel adressesModel;
    private int position;

    private String[] stateList;
    private String selectedState;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_address);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle("Add a new address");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        /////// loading dialog
        loadingDialog = new Dialog(AddAddressActivity.this);
        loadingDialog.setContentView(R.layout.loading_progress_dialog);
        loadingDialog.setCancelable(false);
        loadingDialog.getWindow().setBackgroundDrawable(AddAddressActivity.this.getDrawable(R.drawable.slider_background));
        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        /////// loading dialog
        stateList = getResources().getStringArray(R.array.india_states);

        city = findViewById(R.id.tv_city_name);
        locality = findViewById(R.id.tv_locality);
        flatNo = findViewById(R.id.tv_flatNum);
        pincode = findViewById(R.id.tv_pin_code);
        landmark = findViewById(R.id.tv_landmark);
        name = findViewById(R.id.name);
        mobileNo = findViewById(R.id.mobile_number);
        alternateMobileNo = findViewById(R.id.alternate_mobile_number);
        stateSpinner = findViewById(R.id.state_spinner);
        saveBtn = findViewById(R.id.save_btn);


        ArrayAdapter spinnerAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, stateList);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        stateSpinner.setAdapter(spinnerAdapter);

        stateSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedState = stateList[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        if(getIntent().getStringExtra("INTENT").equals("update_address")) {
            updateAddress = true;
            position = getIntent().getIntExtra("index", -1);
            adressesModel = DBqueries.adressesModelList.get(position);

            city.setText(adressesModel.getCity());
            pincode.setText(adressesModel.getPincode());
            alternateMobileNo.setText(adressesModel.getAlternateMobileNo());
            landmark.setText(adressesModel.getLandmark());
            locality.setText(adressesModel.getLocality());
            flatNo.setText(adressesModel.getFlatNo());
            name.setText(adressesModel.getName());
            mobileNo.setText(adressesModel.getMobileNo());

            for(int i=0; i<stateList.length; i++) {
               if(stateList[i].equals(adressesModel.getState())){
                   stateSpinner.setSelection(i);
                   break;
               }
            }

            saveBtn.setText("Update");

        }
        else {
            position = DBqueries.adressesModelList.size();
        }

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!TextUtils.isEmpty(city.getText())) {
                    if (!TextUtils.isEmpty(locality.getText())) {
                        if (!TextUtils.isEmpty(flatNo.getText())) {
                            if (!TextUtils.isEmpty(pincode.getText()) && pincode.length() == 6) {
                                if (!TextUtils.isEmpty(name.getText())) {
                                    if (!TextUtils.isEmpty(mobileNo.getText()) && mobileNo.length() == 10) {

                                        loadingDialog.show();

                                        Map<String,Object> addAddress = new HashMap<>();




                                        addAddress.put("mobile_no_" + String.valueOf((long) position + 1), mobileNo.getText().toString() + " or " + alternateMobileNo.getText().toString());
                                        addAddress.put("name_" + String.valueOf((long) position + 1), name.getText().toString());
                                        addAddress.put("pincode_" + String.valueOf((long) position + 1), pincode.getText().toString());
                                        addAddress.put("city_" + String.valueOf((long) position + 1), city.getText().toString());
                                        addAddress.put("locality_" + String.valueOf((long) position + 1), locality.getText().toString());
                                        addAddress.put("flat_no_" + String.valueOf((long) position + 1), flatNo.getText().toString());
                                        addAddress.put("landmark_" + String.valueOf((long) position + 1), landmark.getText().toString());
                                        addAddress.put("alternate_mobile_no_" + String.valueOf((long) position + 1), alternateMobileNo.getText().toString());
                                        addAddress.put("state_" + String.valueOf((long) position + 1), selectedState);

                                        if(!updateAddress) {
                                            addAddress.put("list_size", (long) position+ 1);
                                            if(getIntent().getStringExtra("INTENT").equals("manage")) {
                                                if(DBqueries.adressesModelList.size()==0) {
                                                    addAddress.put("selected_" + String.valueOf((long) position + 1), true);
                                                }else {
                                                    addAddress.put("selected_" + String.valueOf((long) position + 1), false);
                                                }
                                            } else {
                                                addAddress.put("selected_" + String.valueOf((long) position + 1), true);
                                            }


                                            if(DBqueries.adressesModelList.size()>0) {
                                                addAddress.put("selected_" + (DBqueries.selectedAddress + 1), false);
                                            }
                                        }


                                        FirebaseFirestore.getInstance().collection("USERS")
                                                .document(FirebaseAuth.getInstance().getUid())
                                                .collection("USER_DATA").document("MY_ADDRESSES")
                                                .update(addAddress).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if(task.isSuccessful()){
                                                    if(!updateAddress) {
                                                        if(DBqueries.adressesModelList.size()>0) {
                                                            DBqueries.adressesModelList.get(DBqueries.selectedAddress).setSelected(false);
                                                        }
                                                        DBqueries.adressesModelList.add(new AdressesModel(true, city.getText().toString(), locality.getText().toString(), flatNo.getText().toString(), pincode.getText().toString(), landmark.getText().toString(),name.getText().toString(), mobileNo.getText().toString(), alternateMobileNo.getText().toString(), selectedState));

                                                        if(getIntent().getStringExtra("INTENT").equals("manage")) {
                                                            if(DBqueries.adressesModelList.size()==0) {
                                                                DBqueries.selectedAddress = DBqueries.adressesModelList.size() - 1;
                                                            }
                                                        } else {
                                                            DBqueries.selectedAddress = DBqueries.adressesModelList.size() - 1;
                                                        }

                                                        DBqueries.selectedAddress = DBqueries.adressesModelList.size() - 1;
                                                    }
                                                    else {
                                                        DBqueries.adressesModelList.set(position,new AdressesModel(true, city.getText().toString(), locality.getText().toString(), flatNo.getText().toString(), pincode.getText().toString(), landmark.getText().toString(),name.getText().toString(), mobileNo.getText().toString(), alternateMobileNo.getText().toString(), selectedState));

                                                    }



                                                    if(getIntent().getStringExtra("INTENT").equals("deliveryIntent")) {
                                                        Intent deliveryIntent = new Intent(AddAddressActivity.this, DeliveryActivity.class);
                                                        startActivity(deliveryIntent);
                                                    }else {
                                                        MyAddressActivity.refreshItem(DBqueries.selectedAddress,DBqueries.adressesModelList.size()-1);
                                                    }

                                                    finish();
                                                }
                                                else{
                                                    String error = task.getException().getLocalizedMessage();
                                                    Toast.makeText(AddAddressActivity.this,error,Toast.LENGTH_SHORT).show();
                                                }
                                                loadingDialog.dismiss();
                                            }
                                        });

                                    }else{
                                        mobileNo.requestFocus();
                                        Toast.makeText(AddAddressActivity.this,"Please provide valid No.",Toast.LENGTH_SHORT).show();
                                    }
                                }else{
                                    name.requestFocus();
                                }
                            }else{
                                pincode.requestFocus();
                                Toast.makeText(AddAddressActivity.this,"Please provide valid pincode",Toast.LENGTH_SHORT).show();
                            }
                        }else{
                            flatNo.requestFocus();
                        }
                    } else{
                        locality.requestFocus();
                    }
                } else{
                    city.requestFocus();
                }

            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
          if(id==android.R.id.home){
              finish();
              return true;
          }
        return super.onOptionsItemSelected(item);
    }
}
