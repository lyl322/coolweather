package com.example.kuou;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.UiThread;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kuou.Json.HttpUtil;
import com.example.kuou.Util.Utility;
import com.example.kuou.db.City;
import com.example.kuou.db.Contry;
import com.example.kuou.db.Province;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;
import okhttp3.internal.Util;

public class ChoseAreaFragment extends Fragment {

    public final int LEVEL_PROVICE=1;
    public final int LEVEL_CITY=2;
    public final int LEVEL_CONTRY=3;
    private int level;
    private TextView textView;
    private Button Button_back;
    private ListView listView;
    private ArrayAdapter<String> adapter;
    private List<String> dataList=new ArrayList<>();
    private Province chose_Province;
    private City chose_City;
    private Contry chose_Contry;
    private ProgressDialog dialog;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.chose_area,container,false);
        textView=(TextView)view.findViewById(R.id.title_text);
        Button_back=view.findViewById(R.id.back_button);
        listView=view.findViewById(R.id.list_view);
        adapter=new ArrayAdapter<>(getContext(),android.R.layout.simple_list_item_1,dataList);
        listView.setAdapter(adapter);
        return view;//必须要是自己加载的view返回
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        level=LEVEL_PROVICE;
        showProvice();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String name=dataList.get(position);
                if(level==LEVEL_PROVICE){
                    List<Province> list=DataSupport.where("proviceName=?",name).find(Province.class);
                    chose_Province=list.get(0);
                    showCity();
                }else if(level==LEVEL_CITY){
                    List<City> list=DataSupport.where("cityName=?",name).find(City.class);
                    chose_City=list.get(0);
                    showContry();
                }else {
                    List<Contry> list=DataSupport.where("contryName=?",name).find(Contry.class);
                    chose_Contry=list.get(0);
                    showWeather();
                }
            }
        });
        Button_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(level==LEVEL_CITY){
                    level=LEVEL_PROVICE;
                    showProvice();
                }else if(level==LEVEL_CONTRY){
                    level=LEVEL_CITY;
                    showCity();
                }
            }
        });
    }

    public void showProvice(){
        textView.setText("请选择省份");
        Button_back.setVisibility(View.GONE);
        dataList.clear();
        Cursor cursor= DataSupport.findBySQL("select * from province");
        if(cursor.moveToFirst()){
            do{
                dataList.add(cursor.getString(cursor.getColumnIndex("provicename")));
            }while (cursor.moveToNext());
            cursor.close();
            adapter.notifyDataSetChanged();
        }else{
            String address="http://guolin.tech/api/china";
            Log.d(getActivity().toString(),"http://guolin.tech/api/china");
            requestHttp(address,"Province");
        }
    }

    public void showCity(){
        textView.setText("请选择城市");
        Button_back.setVisibility(View.VISIBLE);
        dataList.clear();
        level=LEVEL_CITY;
        Cursor cursor=DataSupport.findBySQL("select * from City where provinceCodeId=?",String.valueOf(chose_Province.getProvinceCode()));
        if(cursor.moveToFirst()){
            do {
                dataList.add(cursor.getString(cursor.getColumnIndex("cityname")));
            }while (cursor.moveToNext());
            cursor.close();
            adapter.notifyDataSetChanged();
        }else{
            String address="http://guolin.tech/api/china/"+chose_Province.getProvinceCode();
            requestHttp(address,"City");
        }
    }

    public void showContry(){
        textView.setText("请选择区县");
        Button_back.setVisibility(View.VISIBLE);
        dataList.clear();
        level=LEVEL_CONTRY;
        Cursor cursor=DataSupport.findBySQL("select * from Contry where cityCodeId=?",String.valueOf(chose_City.getCityCode()));
        if(cursor.moveToFirst()){
            do {
                dataList.add(cursor.getString(cursor.getColumnIndex("contryname")));
            }while (cursor.moveToNext());
            cursor.close();
            adapter.notifyDataSetChanged();
        }else{
            String address="http://guolin.tech/api/china/"+chose_Province.getProvinceCode()+"/"+chose_City.getCityCode();
            requestHttp(address,"Contry");
        }

    }

    public void showWeather(){
     //   Log.d(this.toString(),address);
        SharedPreferences.Editor editor= PreferenceManager.getDefaultSharedPreferences(getContext()).edit();
        editor.putString("weather",null);
        editor.putString("image",null);
        editor.apply();

        Activity activity=getActivity();
        if(activity instanceof Weathre_Activity){
            ((Weathre_Activity) activity).drawerLayout.closeDrawer(GravityCompat.START);
        }

        Intent intent=new Intent(getContext(),Weathre_Activity.class);
        String address="http://guolin.tech/api/weather?cityid="+chose_Contry.getWeatherId()+"&key=1f85fb3c0fb34a27b9404409e7b37b0e";
        intent.putExtra("address",address);
        getContext().startActivity(intent);
    }

    public void requestHttp(String address, final String type){
        showDialog();
        HttpUtil.sendHttp(address,new okhttp3.Callback(){
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                boolean res=false;
                try{
                    if(type.equals("Province")){
                        res=Utility.ParseProvince(response.body().string());
                    }else if(type.equals("City")){
                        res=Utility.ParseCity(response.body().string(),chose_Province.getProvinceCode());
                    }else {
                        res=Utility.ParseContry(response.body().string(),chose_City.getCityCode());
                    }
                    final boolean finalRes = res;
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(finalRes){
                                if(type.equals("Province")){
                                    showProvice();
                                }else if(type.equals("City")){
                                    showCity();
                                }else {
                                    showContry();
                                }
                                closeDiglog();
                             //   Log.d(Weathre_Activity.class.toString(),"4444");
                            }else {
                                Toast.makeText(getContext(),"加载错误1",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call call, IOException e) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getContext(),"加载错误2",Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });


    }

    public void showDialog(){
        if(dialog==null){
            dialog=new ProgressDialog(getActivity());
            dialog.setTitle("loading");
            dialog.setCancelable(false);
            dialog.show();
        }
    }

    public void closeDiglog(){
        if(dialog!=null){
            dialog.dismiss();
        }
    }
}
