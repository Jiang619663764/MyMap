package com.jpmph.mybaidumap;


import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.MyLocationConfiguration.LocationMode;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.poi.PoiCitySearchOption;
import com.baidu.mapapi.search.poi.PoiNearbySearchOption;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.jpmph.mybaidumap.listener.MyOrientationListener;
import com.jpmph.mybaidumap.listener.MyOrientationListener.OnOrientationListener;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends Activity {

	private BaiduMap mBaiduMap;
	private MapView mMapView;

	// 定位相关组件
	private boolean isFirstIn = true;
	private LocationClient mLocationClient;
	private MyLocationListener mLocationListener;
	private BitmapDescriptor mBitmapDescriptor;// 自定位图标

	// 当前位置经纬度
	private double mLatitude;
	private double mLongitude;

	// 传感器相关
	private MyOrientationListener mOrientationListener;
	private float mCurrentX;
	
	//搜索相关poi
	private PoiSearch mPoiSearch;
	private PoiNearbySearchOption mNearbySearch;
	private PoiCitySearchOption mCitySearch;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 在使用SDK各组件之前初始化context信息，传入ApplicationContext
		// 注意该方法要再setContentView方法之前实现
		SDKInitializer.initialize(getApplicationContext());
		setContentView(R.layout.activity_main);

		// 初始化控件(MapView,BaiduMap)
		initView();
		// 初始化定位组件
		initLocation();

	}

	/**
	 * 初始化视图，控件
	 */
	private void initView() {
		mMapView = (MapView) findViewById(R.id.mapView);
		mBaiduMap = mMapView.getMap();
		MapStatusUpdate msu = MapStatusUpdateFactory.zoomTo(15f);
	}

	/**
	 * 初始化定位组件
	 */
	private void initLocation() {
		mLocationClient = new LocationClient(MainActivity.this);
		mLocationListener = new MyLocationListener();
		// 注册
		mLocationClient.registerLocationListener(mLocationListener);
		// 参数设定
		LocationClientOption option = new LocationClientOption();
		option.setCoorType("bd09ll");
		option.setOpenGps(true);
		option.setIsNeedAddress(true);
		option.setScanSpan(1000);
		mLocationClient.setLocOption(option);

		// 初始化定位图标
		mBitmapDescriptor = BitmapDescriptorFactory
				.fromResource(R.drawable.marker);

		mOrientationListener = new MyOrientationListener(this);
		mOrientationListener
				.setOnOrientationListener(new OnOrientationListener() {

					@Override
					public void onOrientationChange(float x) {
						// TODO Auto-generated method stub
						mCurrentX = x;
					}
				});

	}

	public class MyLocationListener implements BDLocationListener {

		@Override
		public void onReceiveLocation(BDLocation location) {
			// TODO Auto-generated method stub
			MyLocationData data = new MyLocationData.Builder()//
					.accuracy(location.getRadius())//
					.latitude(location.getLatitude())//
					.longitude(location.getLongitude())//
					.build();
			mBaiduMap.setMyLocationData(data);
			// 当前位置经纬度赋值
			mLatitude = location.getLatitude();
			mLongitude = location.getLongitude();

			// 设置自定义定位图标
			MyLocationConfiguration config = new MyLocationConfiguration(
					LocationMode.NORMAL, true, mBitmapDescriptor);
			mBaiduMap.setMyLocationConfigeration(config);

			if (isFirstIn) {
				LatLng latlng = new LatLng(location.getLatitude(),
						location.getLongitude());
				MapStatusUpdate msu = MapStatusUpdateFactory.newLatLng(latlng);
				mBaiduMap.animateMapStatus(msu);
				isFirstIn = false;
			}

		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
		case R.id.id_map_common:
			// 设置普通地图
			mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
			break;
		case R.id.id_map_site:
			// 设置卫星地图
			mBaiduMap.setMapType(BaiduMap.MAP_TYPE_SATELLITE);
			break;
		case R.id.id_map_traffic:

			// 设置实时交通
			if (mBaiduMap.isTrafficEnabled()) {
				mBaiduMap.setTrafficEnabled(false);
				item.setTitle("实时交通(off)");
			} else {
				mBaiduMap.setTrafficEnabled(true);
				item.setTitle("实时交通(on)");
			}
			break;
		case R.id.id_map_mylocation:
			// 显示我的位置
			LatLng latLng = new LatLng(mLatitude, mLongitude);
			MapStatusUpdate msu = MapStatusUpdateFactory.newLatLng(latLng);
			mBaiduMap.animateMapStatus(msu);
			break;

		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		mBaiduMap.setMyLocationEnabled(true);
		if (!mLocationClient.isStarted()) {
			mLocationClient.start();
		}
		// 开启方向传感器
		mOrientationListener.startSonsor();

	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		mMapView.onResume();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		mMapView.onPause();
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		// 关闭定位
		mBaiduMap.setMyLocationEnabled(false);
		mLocationClient.stop();
		// 关闭方向传感
		mOrientationListener.stopSensor();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		mMapView.onDestroy();
	}

}
