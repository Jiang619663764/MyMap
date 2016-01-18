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

	// ��λ������
	private boolean isFirstIn = true;
	private LocationClient mLocationClient;
	private MyLocationListener mLocationListener;
	private BitmapDescriptor mBitmapDescriptor;// �Զ�λͼ��

	// ��ǰλ�þ�γ��
	private double mLatitude;
	private double mLongitude;

	// ���������
	private MyOrientationListener mOrientationListener;
	private float mCurrentX;
	
	//�������poi
	private PoiSearch mPoiSearch;
	private PoiNearbySearchOption mNearbySearch;
	private PoiCitySearchOption mCitySearch;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// ��ʹ��SDK�����֮ǰ��ʼ��context��Ϣ������ApplicationContext
		// ע��÷���Ҫ��setContentView����֮ǰʵ��
		SDKInitializer.initialize(getApplicationContext());
		setContentView(R.layout.activity_main);

		// ��ʼ���ؼ�(MapView,BaiduMap)
		initView();
		// ��ʼ����λ���
		initLocation();

	}

	/**
	 * ��ʼ����ͼ���ؼ�
	 */
	private void initView() {
		mMapView = (MapView) findViewById(R.id.mapView);
		mBaiduMap = mMapView.getMap();
		MapStatusUpdate msu = MapStatusUpdateFactory.zoomTo(15f);
	}

	/**
	 * ��ʼ����λ���
	 */
	private void initLocation() {
		mLocationClient = new LocationClient(MainActivity.this);
		mLocationListener = new MyLocationListener();
		// ע��
		mLocationClient.registerLocationListener(mLocationListener);
		// �����趨
		LocationClientOption option = new LocationClientOption();
		option.setCoorType("bd09ll");
		option.setOpenGps(true);
		option.setIsNeedAddress(true);
		option.setScanSpan(1000);
		mLocationClient.setLocOption(option);

		// ��ʼ����λͼ��
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
			// ��ǰλ�þ�γ�ȸ�ֵ
			mLatitude = location.getLatitude();
			mLongitude = location.getLongitude();

			// �����Զ��嶨λͼ��
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
			// ������ͨ��ͼ
			mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
			break;
		case R.id.id_map_site:
			// �������ǵ�ͼ
			mBaiduMap.setMapType(BaiduMap.MAP_TYPE_SATELLITE);
			break;
		case R.id.id_map_traffic:

			// ����ʵʱ��ͨ
			if (mBaiduMap.isTrafficEnabled()) {
				mBaiduMap.setTrafficEnabled(false);
				item.setTitle("ʵʱ��ͨ(off)");
			} else {
				mBaiduMap.setTrafficEnabled(true);
				item.setTitle("ʵʱ��ͨ(on)");
			}
			break;
		case R.id.id_map_mylocation:
			// ��ʾ�ҵ�λ��
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
		// �������򴫸���
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
		// �رն�λ
		mBaiduMap.setMyLocationEnabled(false);
		mLocationClient.stop();
		// �رշ��򴫸�
		mOrientationListener.stopSensor();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		mMapView.onDestroy();
	}

}
