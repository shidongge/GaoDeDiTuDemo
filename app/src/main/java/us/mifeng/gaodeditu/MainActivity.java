package us.mifeng.gaodeditu;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements AMap.InfoWindowAdapter {
    MapView mMapView ;
    AMap aMap=null;

    AMapLocationClient mlocationClient;
    AMapLocationClientOption mLocationOption;
    private View inflate;
    private ListView lv;
    private ActionBar actionBar;
    private Marker currentMarker;
    private AlertDialog alertDialog;
    private boolean value = true;
    private List<String> list;
    private LatLng latLng;
    private LatLng mylatlng;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        inflate = View.inflate(MainActivity.this, R.layout.item, null);
        lv = (ListView) inflate.findViewById(R.id.ls);

        actionBar = getActionBar();

        setContentView(R.layout.activity_main);
        mMapView = (MapView) findViewById(R.id.map);
        //在activity执行onCreate时执行mMapView.onCreate(savedInstanceState)，创建地图
        mMapView.onCreate(savedInstanceState);
        aMap = mMapView.getMap();
        aMap.setOnMapClickListener(new AMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                if (currentMarker!=null&&currentMarker.isInfoWindowShown()){
                    if (alertDialog!=null&&alertDialog.isShowing()){
                        alertDialog.dismiss();
                    }
                    currentMarker.hideInfoWindow();
                }
            }
        });


        UiSettings uiSettings = aMap.getUiSettings();
        uiSettings.setZoomControlsEnabled(false);
        uiSettings.setLogoBottomMargin(-300);

        MarkerOptions markerOptions = new MarkerOptions();
        BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromResource(R.drawable.dog);
        latLng = new LatLng(39.90960456049752, 116.3972282409668, true);
        markerOptions.icon(bitmapDescriptor)
                .alpha(0.6f)
                .visible(true)
                .title("这是高德地图")
                .snippet("这是一个位置")
                .position(latLng);
        aMap.addMarker(markerOptions);
        aMap.setInfoWindowAdapter(this);

        aMap.setOnMarkerClickListener(new AMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                currentMarker=marker;
                return false;
            }
        });
        aMap.setOnInfoWindowClickListener(new AMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                Toast.makeText(MainActivity.this, marker.getTitle(), Toast.LENGTH_SHORT).show();
                initLv(value);
                alertDialog.show();
            }
        });
    }

    private void initLv(boolean value) {
        if (!value){
            return;
        }
        if (value){
            this.value=false;
        }
        boolean installqq = isInstallByread("com.tencent.map");
        boolean installnav = isInstallByread("com.autonavi.minimap");
        boolean installbaidu = isInstallByread("com.baidu.BaiduMap");
        list = new ArrayList<>();
        if (installqq) {
            list.add("腾讯地图");
        } if (installbaidu) {
            list.add("百度地图");
        } if (installnav) {
            list.add("高德地图");
        } else {
            startActivity(new Intent(MainActivity.this,Navitive.class));
            return;
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1, list);
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (list.get(position).equals("腾讯地图")) {
                    //腾讯地图
                    if (mylatlng != null && latLng != null) {
                        // 腾讯地图
                        Intent naviIntent = new Intent("android.intent.action.VIEW", android.net.Uri.parse("qqmap://map/routeplan?type=drive&from=" +"&fromcoord=" + mylatlng.latitude + "," + mylatlng.longitude + "&to=" + null + "&tocoord=" + latLng.latitude + "," + latLng.longitude + "&policy=0&referer=appName"));
                        startActivity(naviIntent);
                    }
                } else if (list.get(position).equals("百度地图")) {
                    if (mylatlng != null && latLng != null) {
                        // 百度地图
                        Intent naviIntent = new Intent("android.intent.action.VIEW", android.net.Uri.parse("baidumap://map/geocoder?location=" + latLng.latitude + "," + latLng.longitude));
                        startActivity(naviIntent);
                    }
                } else if (list.get(position).equals("高德地图")) {
                    if (mylatlng != null && latLng != null) {
                        // 高德地图
                        Intent naviIntent = new Intent("android.intent.action.VIEW", android.net.Uri.parse("androidamap://route?sourceApplication=appName&slat=&slon=&sname=我的位置&dlat=" + latLng.latitude + "&dlon=" + latLng.longitude + "&dname=目的地&dev=0&t=2"));
                        startActivity(naviIntent);
                    }
                }
            }
        });
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setView(lv);
        alertDialog = builder.create();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.actionbar,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.dingwei:
                MyLocationStyle myLocationStyle;
                myLocationStyle = new MyLocationStyle();//初始化定位蓝点样式类myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE);//连续定位、且将视角移动到地图中心点，定位点依照设备方向旋转，并且会跟随设备移动。（1秒1次定位）如果不设置myLocationType，默认也会执行此种模式。
                myLocationStyle.interval(2000000); //设置连续定位模式下的定位间隔，只在连续定位模式下生效，单次定位模式下不会生效。单位为毫秒。
                myLocationStyle.showMyLocation(true);
                BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromResource(R.drawable.dog);
                myLocationStyle.myLocationIcon(bitmapDescriptor);
                //myLocationStyle.anchor(0.0f,0.0f);
                aMap.setMyLocationStyle(myLocationStyle);//设置定位蓝点的Style
//aMap.getUiSettings().setMyLocationButtonEnabled(true);设置默认定位按钮是否显示，非必需设置。
                aMap.setMyLocationEnabled(true);// 设置为true表示启动显示定位蓝点，false表示隐藏定位蓝点并不进行定位，默认是false。
                aMap.setOnMyLocationChangeListener(new AMap.OnMyLocationChangeListener() {
                    @Override
                    public void onMyLocationChange(Location location) {
                        double latitude = location.getLatitude();
                        double longitude = location.getLongitude();
                        mylatlng = new LatLng(latitude, longitude);
                    }
                });
                break;
            case R.id.shinei:
                aMap.showIndoorMap(true);
                break;
            case R.id.daohang:
                aMap.setMapType(AMap.MAP_TYPE_NAVI);
                break;
            case R.id.yejian:
                aMap.setMapType(AMap.MAP_TYPE_NIGHT);// 设置卫星地图模式，aMap是地图控制器对象。
                break;
            case R.id.weixing:
                aMap.setMapType(AMap.MAP_TYPE_SATELLITE);// 设置卫星地图模式，aMap是地图控制器对象。
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，销毁地图
        mMapView.onDestroy();

    }
    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView.onResume ()，重新绘制加载地图
        mMapView.onResume();
    }
    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView.onPause ()，暂停地图的绘制
        mMapView.onPause();
    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //在activity执行onSaveInstanceState时执行mMapView.onSaveInstanceState (outState)，保存地图当前的状态
        mMapView.onSaveInstanceState(outState);
    }


    /**
     * 判断是否安装目标应用
     * @param packageName 目标应用安装后的包名
     * @return 是否已安装目标应用
     */
    private boolean isInstallByread(String packageName) {
        return new File("/data/data/" + packageName).exists();
    }

    @Override
    public View getInfoWindow(Marker marker) {
        View inflate = LayoutInflater.from(MainActivity.this).inflate(R.layout.info_windon, null, false);
        return inflate;
    }

    @Override
    public View getInfoContents(Marker marker) {
        return null;
    }
}
