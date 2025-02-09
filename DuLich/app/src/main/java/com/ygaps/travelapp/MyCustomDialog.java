package com.ygaps.travelapp;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.DialogFragment;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyCustomDialog extends DialogFragment {
    private static final String TAG = "MyCustomDialog";
    private Button button;
    private TextView textView;
    String token;
    String id;
    int status =-1;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    public interface NoticeDialogListener {
        public void onDialogPositiveClick(DialogFragment dialog);
        public void onDialogNegativeClick(DialogFragment dialog);
    }
    NoticeDialogListener mListener;

    @Override
    public void onAttach(Context context) {
        super.onAttach( context );
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (NoticeDialogListener) context;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(context.toString()
                    + " must implement NoticeDialogListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        //LayoutInflater inflater = getActivity().getLayoutInflater();
        preferences = super.getContext().getSharedPreferences("isLogin", Context.MODE_PRIVATE);
        token = preferences.getString( "token","" );
        id = String.valueOf(preferences.getInt( "id",-1 )) ;
           //Log.i("idddddddddddddddd", id);
           //Log.i("token", token);


        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        //builder.setView(inflater.inflate(R.layout.dialog_historytour,null))
        builder.setMessage(R.string.history_dialog_message)
                .setPositiveButton(R.string.history_dialog_yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialog, int which) {
                        Call<ResponseBody> call = RetrofitClient
                                .getInstance()
                                .getApi()
                                .updateDelTour(token,id,status);
                        call.enqueue(new Callback<ResponseBody>() {
                            @Override
                            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                //Toast.makeText(getContext(),response.code() , Toast.LENGTH_SHORT).show();
                                if (response.code()==200) {
                                      mListener.onDialogPositiveClick(MyCustomDialog.this);

                                    try {
                                        String body = response.body().string();
                                        Log.e("respond",body);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }


                                    //* Intent intent = new Intent(getBaseContext(), MapsActivity.class);
                                }
                                else{
                                    //Toast.makeText( "Vui lòng thử lại!", Toast.LENGTH_SHORT).show();
                                    Log.e("Error update"," :delete tour");
                                }
                            }
                            @Override
                            public void onFailure(Call<ResponseBody> call, Throwable t) {
                                Log.i("Fail","Fail");
                            }
                        });

                    }
                })
                .setNegativeButton(R.string.history_dialog_no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                         mListener.onDialogNegativeClick(MyCustomDialog.this);
                        // Toast.makeText(getContext(), "Canceled!", Toast.LENGTH_SHORT).show();
                    }
                });

        return builder.create();
        /*View view = inflater.inflate(R.layout.dialog_historytour,container,false);
        button = view.findViewById( R.id.deleteTour );
        button.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDialog().dismiss();
            }
        } );
        return view;*/
    }


}
